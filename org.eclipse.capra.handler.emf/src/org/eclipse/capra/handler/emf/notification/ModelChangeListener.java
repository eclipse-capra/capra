/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.handler.emf.notification;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.capra.GenericTraceMetaModel.GenericTraceModel;
import org.eclipse.capra.GenericTraceMetaModel.RelatedTo;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.notification.CapraNotificationHelper;
import org.eclipse.capra.ui.notification.CapraNotificationHelper.IssueType;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Listens to changes in the target model and produces a notification, if they
 * affect the Capra trace model.
 * 
 * @author Dusan Kalanj
 *
 */
public class ModelChangeListener extends EContentAdapter {
	List<Integer> eventsToProcess = Arrays.asList(new Integer[] { 1, 3, 4 });

	@Override
	public void notifyChanged(Notification notification) {
		Object notifier = notification.getNotifier();
		// Only process the notification if it signifies a set, add or remove
		// operation on an EObject.
		if (!eventsToProcess.contains(notification.getEventType()) || !(notifier instanceof EObject))
			return;

		Resource changedResource = ((EObject) notifier).eResource();
		if (changedResource != null)
			// Compare the current state of the model (changedResource) with the
			// previously saved state of the same model.
			compareTracedItems(changedResource);
	}

	private void compareTracedItems(Resource changedResource) {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		ResourceSetImpl newResourceSet = new ResourceSetImpl();
		EObject traceModel = persistenceAdapter.getTraceModel(newResourceSet);
		EList<RelatedTo> traces = ((GenericTraceModel) traceModel).getTraces();

		if (traces.size() == 0)
			return;

		IPath path = new Path(EcoreUtil.getURI(traceModel).toPlatformString(false));
		IFile traceContainer = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

		// Traverse the traces and the items that they are referencing and
		// check, if the items are from the same resource as the one that was
		// changed. If yes, check if the changes affect the items.
		for (RelatedTo trace : traces) {
			for (EObject tracedItem : trace.getItem()) {
				Resource oldResource = tracedItem.eResource();
				if (oldResource == null)
					oldResource = newResourceSet.getResource(EcoreUtil.getURI(tracedItem).trimFragment(), true);

				if (oldResource != null) {
					if (CapraNotificationHelper.getFileUri(oldResource)
							.equals(CapraNotificationHelper.getFileUri(changedResource))) {
						IComparisonScope scope = new DefaultComparisonScope(changedResource, oldResource, null);
						// Build a comparison object with EMFCompare and
						// recursively resolve the Match elements.
						Comparison comparison = EMFCompare.builder().build().compare(scope);
						for (Match match : comparison.getMatches())
							resolveMatch(tracedItem, match, null, traceContainer);
					}
				}
			}
		}
	}

	private void resolveMatch(EObject tracedItem, Match match, DifferenceKind diffKind, IFile traceContainer) {
		EList<Diff> differences = match.getDifferences();
		if (!differences.isEmpty())
			diffKind = differences.get(0).getKind();

		for (Match subMatch : match.getSubmatches())
			// Recursively resolve the subMatches, if they exist.
			resolveMatch(tracedItem, subMatch, diffKind, traceContainer);

		EObject newObject = match.getLeft();
		if (newObject != null) {
			// Potentially deletes an existing marker, if the state of the
			// newObject is the same as the state of the same object in the
			// previously saved resource.
			URI oldUri = CapraNotificationHelper.getFileUri(tracedItem);
			if (CapraNotificationHelper.getFileUri(newObject).equals(oldUri))
				CapraNotificationHelper.deleteCapraMarker(oldUri.toString(), null, traceContainer);
		}

		if (diffKind != null) {
			EObject oldObject = match.getRight();
			if (oldObject != null && oldObject.equals(tracedItem)) {
				// If the match or a parent match contains a difference that
				// affects the tracedItem, produce a marker.

				IssueType issueType = null;
				if (diffKind.equals(DifferenceKind.DELETE))
					issueType = IssueType.DELETED;
				else if (diffKind.equals(DifferenceKind.MOVE))
					issueType = IssueType.MOVED;
				else if (diffKind.equals(DifferenceKind.ADD))
					issueType = IssueType.ADDED;
				else if (diffKind.equals(DifferenceKind.CHANGE))
					issueType = IssueType.RENAMED;

				if (issueType != null)
					createCapraMarker(tracedItem, match, issueType, traceContainer);
			}
		}
	}

	/**
	 * Generates the info for the to-be-created marker which notifies the user
	 * that the tracedItem has been affected - and calls the method from the
	 * CapraNotificationHelper class that actually creates the marker.
	 * 
	 * @param tracedItem
	 *            the item from the Capra trace model that was affected by the
	 *            change
	 * @param match
	 *            the Match element generated by EMF Compare from which the
	 *            marker info will be extracted
	 * @param diffKind
	 *            the difference type that describes the change that happened to
	 *            the tracedItem
	 * @param file
	 *            the file to which the marker will be attached
	 */
	private void createCapraMarker(EObject tracedItem, Match match, IssueType issueType, IFile file) {
		String oldUri = CapraNotificationHelper.getFileUri(tracedItem).toString();
		String oldName = (String) tracedItem.eGet(tracedItem.eClass().getEStructuralFeature("name"));

		EObject changedObject = match.getLeft();
		String newUri = "";
		String newName = "";
		if (changedObject != null) {
			newUri = CapraNotificationHelper.getFileUri(changedObject).toString();
			newName = (String) changedObject.eGet(changedObject.eClass().getEStructuralFeature("name"));
		}

		String message = "";
		switch (issueType) {
		case DELETED:
			if (changedObject == null)
				message = oldUri + " has been deleted.";
			break;
		case MOVED:
			if (!oldUri.equals(newUri))
				message = oldName + " has been moved to " + newUri + ".";
			break;
		case RENAMED:
			if (!newName.equals(oldName))
				message = oldUri + " has been renamed to " + newName + ".";
			else
				message = "An ancestor of " + oldName + " has been renamed." + " The new URI of the element is "
						+ newUri;
			break;
		case ADDED:
			break;
		case CHANGED:
			break;
		}

		if (issueType != null) {
			HashMap<String, String> markerInfo = new HashMap<String, String>();
			markerInfo.put(CapraNotificationHelper.ISSUE_TYPE, issueType.getValue());
			markerInfo.put(CapraNotificationHelper.MESSAGE, message);
			markerInfo.put(CapraNotificationHelper.OLD_URI, oldUri);
			markerInfo.put(CapraNotificationHelper.NEW_URI, newUri);
			markerInfo.put(CapraNotificationHelper.NEW_NAME, newName);
			CapraNotificationHelper.createCapraMarker(markerInfo, file);
		}
	}
}
