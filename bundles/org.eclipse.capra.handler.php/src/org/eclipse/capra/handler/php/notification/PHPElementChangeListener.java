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
package org.eclipse.capra.handler.php.notification;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.handler.php.PhpHandler;
import org.eclipse.capra.ui.notification.CapraNotificationHelper;
import org.eclipse.capra.ui.notification.CapraNotificationHelper.IssueType;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IElementChangedListener;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementDelta;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Checks for changes of PHP Elements to determine if they affect the trace
 * model. Creates markers on the artifact model if the changes affect artifact
 * wrappers.
 *
 * @author Dusan Kalanj, Salome Maro
 */
public class PHPElementChangeListener implements IElementChangedListener {

	ArtifactMetaModelAdapter artifactAdapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();

	@Override
	public void elementChanged(ElementChangedEvent event) {
		TracePersistenceAdapter tracePersistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter()
				.orElseThrow();
		EObject artifactModel = tracePersistenceAdapter.getArtifactWrappers(EditingDomainHelper.getResourceSet());
		// get all artifacts
		List<EObject> allArtifacts = artifactAdapter.getAllArtifacts(artifactModel);
		List<EObject> phpArtifacts = allArtifacts.stream()
				.filter(p -> artifactAdapter.getArtifactHandler(p).equals(PhpHandler.class.getName()))
				.collect(Collectors.toList());

		if (phpArtifacts.isEmpty())
			return;

		IPath path = new Path(EcoreUtil.getURI(artifactModel).toPlatformString(false));
		IFile wrapperContainer = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

		new WorkspaceJob(CapraNotificationHelper.NOTIFICATION_JOB) {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				handleDelta(event.getDelta(), phpArtifacts, wrapperContainer);
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	private void handleDelta(IModelElementDelta delta, List<EObject> phpArtifacts, IFile wrapperContainer) {
		if (!(delta.getElement() instanceof ISourceModule)) {
			// Only go as far as the source file
			for (IModelElementDelta subDelta : delta.getAffectedChildren()) {
				handleDelta(subDelta, phpArtifacts, wrapperContainer);
			}
		}

		int flags = delta.getFlags();
		int changeType = delta.getKind();
		IssueType issueType = null;

		if (changeType == IModelElementDelta.ADDED) {
			issueType = IssueType.ADDED;
		} else if (changeType == IModelElementDelta.REMOVED) {
			if ((flags & IModelElementDelta.F_MOVED_TO) != 0) {
				if (delta.getMovedToElement().getElementName().equals(delta.getElement().getElementName())) {
					issueType = IssueType.MOVED;
				} else {
					issueType = IssueType.RENAMED;
				}
			} else {
				issueType = IssueType.DELETED;
			}
		} else if (changeType == IModelElementDelta.CHANGED && (flags & IModelElementDelta.F_PRIMARY_RESOURCE) != 0) {
			// This is only true if a source file changes.
			issueType = IssueType.CHANGED;
		}
		if (issueType != null) {
			String affectedElementUri = delta.getElement().getHandleIdentifier();
			if (affectedElementUri != null) {
				for (EObject aw : phpArtifacts) {
					String artifactUri = artifactAdapter.getArtifactUri(aw);
					// Only create a marker if a signature of a
					// method/variable/class... has changed inside of a source
					// file.

					IssueType[] markersToDelete = null;
					String deleteMarkerUri = "";
					IModelElement element = DLTKCore.create(artifactUri);
					if (element != null && element.exists()) {
						deleteMarkerUri = artifactUri;
						markersToDelete = new IssueType[] { IssueType.MOVED, IssueType.RENAMED, IssueType.DELETED };
					} else if (issueType == IssueType.DELETED) {
						markersToDelete = new IssueType[] { IssueType.MOVED, IssueType.RENAMED, IssueType.CHANGED };
						deleteMarkerUri = affectedElementUri;
					}

					if (!deleteMarkerUri.isEmpty() && artifactUri.contains(deleteMarkerUri))
						CapraNotificationHelper.deleteCapraMarker(artifactUri, markersToDelete, wrapperContainer);

					if (artifactUri.contains(affectedElementUri)) {
						HashMap<String, String> markerInfo = generateMarkerInfo(aw, delta, issueType);
						CapraNotificationHelper.createCapraMarker(markerInfo, wrapperContainer);
					}
				}
			}
		}
	}

	/**
	 * Generates the attributes that will later be assigned (in the createMarker
	 * method) to a Capra change marker.
	 *
	 * @param aw    ArtifactWrapper that links to the element in the delta or to a
	 *              child of the element in the delta
	 * @param delta represents changes in the state of a PHP Element
	 * @param issue the type of change that occurred
	 * @return a key value HashMap, containing the attributes to be assigned to a
	 *         Capra change marker and their keys (IDs).
	 */
	// This works well for child elements if files are renamed or deleted.
	// Renaming classes/methods/variables inside a class
	// translates to a deleted classes/methods/variables because the delta does
	// not keep track of
	// the new name of the class. It just knows about the old name.
	private HashMap<String, String> generateMarkerInfo(EObject aw, IModelElementDelta delta, IssueType issueType) {
		HashMap<String, String> markerInfo = new HashMap<>();

		// Properties from the PHP element in the wrapper
		String oldArtifactUri = artifactAdapter.getArtifactUri(aw);
		String oldArtifactName = artifactAdapter.getArtifactName(aw);

		// Properties from the affected PHP element with its former path
		String oldAffectedElementUri = delta.getElement().getHandleIdentifier();
		String oldAffectedElementName = delta.getElement().getElementName();
		String newAffectedElementUri = null;
		String newAffectedElementName = null;

		// Affected element with its new path (null if not renamed or moved)
		IModelElement newAffectedElement = delta.getMovedToElement();
		if (newAffectedElement != null) {
			newAffectedElementUri = newAffectedElement.getHandleIdentifier();
			newAffectedElementName = newAffectedElement.getElementName();
		}

		String message = "";
		// TODO Make messages more readable (replaces uris with names?).
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
		default:
			IModelElement el = DLTKCore.create(oldArtifactUri);
			if (el == null || !el.exists()) {
				issueType = IssueType.DELETED;
				message = artifactAdapter.getArtifactUri(aw);

				if (el instanceof ISourceReference && !(el instanceof ISourceModule))
					message += " has been deleted or has had its signature changed.";
				else
					message += " has been deleted.";
			}
			break;
		}

		// The affected element has been renamed or moved.
		if (newAffectedElementUri != null) {

			String newArtifactUri;
			String newArtifactName;
			if (oldArtifactUri.equals(oldAffectedElementUri)) {
				// The element in the wrapper is the affected element.
				newArtifactUri = newAffectedElementUri;
				newArtifactName = newAffectedElementName;
			} else {
				// The element in the wrapper is a child of the affected
				// element.

				// Build new uri for the moved class
				newArtifactUri = oldArtifactUri.replace(oldAffectedElementUri, newAffectedElementUri);
				newArtifactName = oldArtifactName;

				// Check if artifact belongs to the PHP files (because
				// inner elements in the PHP file change with the file name)
				if (newAffectedElement instanceof ISourceModule) {
					String oldPublicClassName = oldAffectedElementName.replace(".php", "");
					String newPublicClassName = newAffectedElementName.replace(".php", "");

					int classStartIndex = newAffectedElementUri.length();
					int classEndIndex = classStartIndex + oldPublicClassName.length();

					if (newArtifactUri.substring(classStartIndex, classEndIndex).equals(oldPublicClassName))
						newArtifactUri = newArtifactUri.substring(0, classStartIndex) + newPublicClassName
								+ newArtifactUri.substring(classEndIndex);

					if (newArtifactName.contentEquals(oldPublicClassName))
						newArtifactName = newPublicClassName;
				}
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
