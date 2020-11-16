/*******************************************************************************
 * Copyright (c) 2016, 2019 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *  
 * SPDX-License-Identifier: EPL-2.0
 *  
 * Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *      Chalmers | University of Gothenburg - additional features, updated API
 *******************************************************************************/
package org.eclipse.capra.handler.cdt.notification;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.handler.cdt.CDTHandler;
import org.eclipse.capra.ui.notification.CapraNotificationHelper;
import org.eclipse.capra.ui.notification.CapraNotificationHelper.IssueType;
import org.eclipse.cdt.core.model.ElementChangedEvent;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICElementDelta;
import org.eclipse.cdt.core.model.IElementChangedListener;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Checks for changes of C/C++ elements to determine if they affect the trace
 * model. Creates markers on the artifact model if the changes affect artifact
 * wrappers.
 *
 * @author Dusan Kalanj
 */
public class CElementChangeListener implements IElementChangedListener {
	ArtifactMetaModelAdapter artifactAdapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();

	@Override
	public void elementChanged(ElementChangedEvent event) {
		TracePersistenceAdapter tracePersistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter()
				.orElseThrow();
		EObject artifactModel = tracePersistenceAdapter.getArtifactWrappers(EditingDomainHelper.getResourceSet());
		List<EObject> allArtifacts = artifactAdapter.getAllArtifacts(artifactModel);
		List<EObject> cArtifacts = allArtifacts.stream()
				.filter(p -> artifactAdapter.getArtifactHandler(p).equals(CDTHandler.class.getName()))
				.collect(Collectors.toList());

		if (cArtifacts.isEmpty())
			return;

		IPath path = new Path(EcoreUtil.getURI(artifactModel).toPlatformString(false));
		IFile wrapperContainer = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

		new WorkspaceJob(CapraNotificationHelper.NOTIFICATION_JOB) {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				handleDelta(event.getDelta(), cArtifacts, wrapperContainer);
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	private void handleDelta(ICElementDelta delta, List<EObject> cArtifacts, IFile wrapperContainer) {

		// Visit all the affected children of affected element
		for (ICElementDelta subDelta : delta.getAffectedChildren())
			handleDelta(subDelta, cArtifacts, wrapperContainer);

		int flags = delta.getFlags();
		int changeType = delta.getKind();
		ICElement affectedElement = delta.getElement();
		IssueType issueType = null;

		if (changeType == ICElementDelta.ADDED)
			issueType = IssueType.ADDED;
		// TODO doesn't work if a source folder is renamed - it says that the
		// children have been deleted, instead of that their ancestor was
		// renamed. But renaming the src folder isn't good practice anyway.
		else if (changeType == ICElementDelta.REMOVED) {

			if ((flags & ICElementDelta.F_MOVED_TO) != 0) {
				if (delta.getMovedToElement().getElementName().equals(affectedElement.getElementName())) {
					issueType = IssueType.MOVED;
				} else {
					issueType = IssueType.RENAMED;
				}
			} else {
				if (!(affectedElement instanceof ITranslationUnit
						&& ((ITranslationUnit) affectedElement).isWorkingCopy())) {
					issueType = IssueType.DELETED;
				}
			}

		} else if (changeType == ICElementDelta.CHANGED)
			issueType = IssueType.CHANGED;

		if (issueType != null) {
			String affectedElementUri = affectedElement.getHandleIdentifier();
			if (affectedElementUri != null) {
				for (EObject aw : cArtifacts) {
					String artifactId = artifactAdapter.getArtifactIdentifier(aw);
					// If the change type is "CHANGED", meaning that the element
					// wasn't deleted, renamed, added or moved, only consider
					// making a marker if the URI of the affected element is the
					// same as the URI in the wrapper.
					if (issueType == IssueType.CHANGED && !artifactId.equals(affectedElementUri))
						continue;
					// Otherwise (the change is either "delete", "move" or
					// "rename"), consider making the marker for the affected
					// element as well as its children, who's URIs have changed
					// and need updating.
					IssueType[] markersToDelete = null;
					String deleteMarkerUri = "";
					if (issueType == IssueType.MOVED || issueType == IssueType.RENAMED) {
						deleteMarkerUri = delta.getMovedToElement().getHandleIdentifier();
						markersToDelete = new IssueType[] { IssueType.MOVED, IssueType.RENAMED, IssueType.DELETED };
					} else if (issueType == IssueType.DELETED) {
						markersToDelete = new IssueType[] { IssueType.MOVED, IssueType.RENAMED, IssueType.CHANGED };
						deleteMarkerUri = affectedElementUri;
					} else if (issueType == IssueType.ADDED) {
						markersToDelete = new IssueType[] { IssueType.MOVED, IssueType.RENAMED, IssueType.DELETED };
						deleteMarkerUri = affectedElementUri;
					}

					if (!deleteMarkerUri.isEmpty() && artifactId.contains(deleteMarkerUri))
						CapraNotificationHelper.deleteCapraMarker(artifactId, markersToDelete, wrapperContainer);

					if (artifactId.contains(affectedElementUri)) {
						HashMap<String, String> markerInfo = generateMarkerInfo(aw, delta, issueType);
						CapraNotificationHelper.createCapraMarker(markerInfo, wrapperContainer);
					}
				}
			}
		}
	}

	/**
	 * Generates the attributes that will later be assigned (in the createMarker
	 * method in the CapraNotificationHelper) to a Capra change marker.
	 *
	 * @param aw        ArtifactWrapper that links to the element in the delta or to
	 *                  a child of the element in the delta
	 * @param delta     represents changes in the state of a C/C++ element
	 * @param issueType the type of change that occurred
	 * @return a key value HashMap, containing the attributes to be assigned to a
	 *         Capra change marker and their keys (IDs).
	 */
	private HashMap<String, String> generateMarkerInfo(EObject aw, ICElementDelta delta, IssueType issueType) {
		HashMap<String, String> markerInfo = new HashMap<>();

		// Properties from the C/C++ element in the wrapper (all elements)
		String oldArtifactUri = artifactAdapter.getArtifactIdentifier(aw);
		String oldArtifactName = artifactAdapter.getArtifactName(aw);

		// Properties from the affected C/C++ element before the change.
		ICElement oldAffectedElement = delta.getElement();
		String oldAffectedElementUri = oldAffectedElement.getHandleIdentifier();
		String oldAffectedElementName = oldAffectedElement.getElementName();

		// Properties to be assigned to the marker so that they can be later
		// used in quick fix solutions.
		String newAffectedElementUri = null;
		String newAffectedElementName = null;

		// Affected element in it's new state, if it has been renamed or moved,
		// otherwise null.
		ICElement newAffectedElement = delta.getMovedToElement();
		if (newAffectedElement != null) {
			newAffectedElementUri = newAffectedElement.getHandleIdentifier();
			newAffectedElementName = newAffectedElement.getElementName();
		}

		// Generate a message for the marker based on the change.
		// TODO Make messages more readable (replaces uris with names?).
		String message = "";
		switch (issueType) {
		case RENAMED:
			if (oldArtifactUri.equals(oldAffectedElementUri))
				// The element in the wrapper is the renamed element.
				message = oldAffectedElementUri + " has been renamed to " + newAffectedElementUri + ".";
			else
				// The element in the wrapper is a child of the renamed element.
				message = oldAffectedElementName + ", an ancestor of " + oldArtifactUri + ", has been renamed to "
						+ newAffectedElementName + ".";
			break;
		case MOVED:
			if (oldArtifactUri.equals(oldAffectedElementUri))
				// The element in the wrapper is the moved element.
				message = oldAffectedElementUri + " has been moved to " + newAffectedElementUri + ".";
			else
				// The element in the wrapper is a child of the moved element.
				message = oldAffectedElementName + ", an ancestor of " + oldArtifactUri + ", has been moved to "
						+ newAffectedElementUri;
			break;
		case DELETED:
			message = artifactAdapter.getArtifactUri(aw) + " has been deleted or has had its signature changed.";
			break;
		case CHANGED:
			message = artifactAdapter.getArtifactUri(aw)
					+ " has been edited. Please check if associated trace links are still valid.";
			break;
		case ADDED:
			break;
		}

		if (newAffectedElementUri != null) {
			// The affected element has been renamed or moved.
			String newArtifactUri;
			String newArtifactName;

			if (oldArtifactUri.equals(oldAffectedElementUri)) {
				// The element in the wrapper is the affected element.
				newArtifactUri = newAffectedElementUri;
				newArtifactName = newAffectedElementName;
			} else {
				// The element in the wrapper is a child of the affected
				// element. Update the relevant part of the URI.
				newArtifactUri = oldArtifactUri.replace(oldAffectedElementUri, newAffectedElementUri);
				newArtifactName = oldArtifactName;
			}

			markerInfo.put(CapraNotificationHelper.NEW_URI, newArtifactUri);
			markerInfo.put(CapraNotificationHelper.NEW_NAME, newArtifactName);
		}

		markerInfo.put(CapraNotificationHelper.ISSUE_TYPE, issueType.getValue());
		markerInfo.put(CapraNotificationHelper.OLD_URI, oldArtifactUri);
		markerInfo.put(CapraNotificationHelper.MESSAGE, message);

		return markerInfo;
	}
}
