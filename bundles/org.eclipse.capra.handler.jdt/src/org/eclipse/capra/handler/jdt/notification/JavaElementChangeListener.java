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
package org.eclipse.capra.handler.jdt.notification;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.handler.jdt.JavaElementHandler;
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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaCore;

/**
 * Checks for changes of JavaElements to determine if they affect the trace
 * model. Creates markers on the artifact model if the changes affect artifact
 * wrappers.
 *
 * @author Dusan Kalanj
 */
public class JavaElementChangeListener implements IElementChangedListener {
	ArtifactMetaModelAdapter artifactAdapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();

	@Override
	public void elementChanged(ElementChangedEvent event) {
		TracePersistenceAdapter tracePersistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter()
				.orElseThrow();
		EObject artifactModel = tracePersistenceAdapter.getArtifactWrappers(EditingDomainHelper.getResourceSet());
		// get all artifacts
		List<EObject> allArtifacts = artifactAdapter.getAllArtifacts(artifactModel);
		List<EObject> javaArtifacts = allArtifacts.stream()
				.filter(p -> artifactAdapter.getArtifactHandler(p).equals(JavaElementHandler.class.getName()))
				.collect(Collectors.toList());

		if (javaArtifacts.isEmpty())
			return;

		IPath path = new Path(EcoreUtil.getURI(artifactModel).toPlatformString(false));
		IFile wrapperContainer = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

		new WorkspaceJob(CapraNotificationHelper.NOTIFICATION_JOB) {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				handleDelta(event.getDelta(), javaArtifacts, wrapperContainer);
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	private void handleDelta(IJavaElementDelta delta, List<EObject> javaArtifacts, IFile wrapperContainer) {

		// If the changes are not made manually (by typing in the
		// editor) this recursion only goes as far as the source file. The
		// changes to the child elements of the source file have to be
		// additionally processed. Furthermore, if deletion/moving/renaming is
		// performed, the change only traverses up to the first element that was
		// affected. Example: if a package is moved, this won't reach the files
		// in the package.
		if (!(delta.getElement() instanceof ICompilationUnit)) {
			// Only go as far as the source file
			for (IJavaElementDelta subDelta : delta.getAffectedChildren()) {
				handleDelta(subDelta, javaArtifacts, wrapperContainer);
			}
		}

		int flags = delta.getFlags();
		int changeType = delta.getKind();
		IssueType issueType = null;

		if (changeType == IJavaElementDelta.ADDED) {
			issueType = IssueType.ADDED;
		} else if (changeType == IJavaElementDelta.REMOVED) {

			if ((flags & IJavaElementDelta.F_MOVED_TO) != 0) {
				if (delta.getMovedToElement().getElementName().equals(delta.getElement().getElementName())) {
					issueType = IssueType.MOVED;
				} else {
					issueType = IssueType.RENAMED;
				}
			} else {
				issueType = IssueType.DELETED;
			}
		} else if (changeType == IJavaElementDelta.CHANGED && (flags & IJavaElementDelta.F_PRIMARY_RESOURCE) != 0) {
			// This is only true if a source file changes.
			issueType = IssueType.CHANGED;
		}

		if (issueType != null) {
			String affectedElementUri = delta.getElement().getHandleIdentifier();
			if (affectedElementUri != null) {
				for (EObject aw : javaArtifacts) {
					String artifactId = artifactAdapter.getArtifactIdentifier(aw);
					// Only create a marker if a signature of a
					// method/variable/class... has changed inside of a source
					// file.

					IssueType[] markersToDelete = null;
					String deleteMarkerUri = "";
					IJavaElement element = JavaCore.create(artifactId);
					if (element != null && element.exists()) {
						deleteMarkerUri = artifactId;
						markersToDelete = new IssueType[] { IssueType.MOVED, IssueType.RENAMED, IssueType.DELETED };
					} else if (issueType == IssueType.DELETED) {
						markersToDelete = new IssueType[] { IssueType.MOVED, IssueType.RENAMED, IssueType.CHANGED };
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
	 * method) to a Capra change marker.
	 *
	 * @param aw    ArtifactWrapper that links to the element in the delta or to a
	 *              child of the element in the delta
	 * @param delta represents changes in the state of a JavaElement
	 * @param issue the type of change that occurred
	 * @return a key value HashMap, containing the attributes to be assigned to a
	 *         Capra change marker and their keys (IDs).
	 */
	// This works OK for all levels above the class level. For variables,
	// methods, classes, if the signature is changed, it will say that the
	// element has been deleted. That is because, if the listener even
	// recognizes that a method/variable has been changed (which it doesn't
	// always do), it doesn't provide a "movedToElement", as it does for
	// everything on file level. Is there some way to go around this or does it
	// even make sense to do it? One way to do it would be to get the previous
	// version of the file and compare the body of the method/variable. If it is
	// the same, compare the signature. The problem is that there can be methods
	// and variables with identical bodies, although highly unlikely. Either
	// way, I don't think there is a completely foolproof solution for this.
	private HashMap<String, String> generateMarkerInfo(EObject aw, IJavaElementDelta delta, IssueType issueType) {
		HashMap<String, String> markerInfo = new HashMap<>();

		// Properties from the Java element in the wrapper (all elements)
		String oldArtifactUri = artifactAdapter.getArtifactIdentifier(aw);
		String oldArtifactName = artifactAdapter.getArtifactName(aw);

		// Properties from the affected Java element with its former path
		// (files, folder, packages... not variables, methods or classes)
		String oldAffectedElementUri = delta.getElement().getHandleIdentifier();
		String oldAffectedElementName = delta.getElement().getElementName();
		String newAffectedElementUri = null;
		String newAffectedElementName = null;

		// Affected element with its new path (null if not renamed or moved)
		IJavaElement newAffectedElement = delta.getMovedToElement();
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
			IJavaElement el = JavaCore.create(oldArtifactUri);
			if (el == null || !el.exists()) {
				issueType = IssueType.DELETED;
				message = artifactAdapter.getArtifactUri(aw);

				if (el instanceof ISourceReference && !(el instanceof ICompilationUnit))
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

				// Check if artifact belongs to the public class (because
				// the
				// public class changes with the file name)
				if (newAffectedElement instanceof ICompilationUnit) {
					String oldPublicClassName = oldAffectedElementName.replace(".java", "");
					String newPublicClassName = newAffectedElementName.replace(".java", "");

					int classStartIndex = newAffectedElementUri.length() + 1;
					int classEndIndex = classStartIndex + oldPublicClassName.length();

					if (newArtifactUri.substring(classStartIndex, classEndIndex).equals(oldPublicClassName))
						newArtifactUri = newArtifactUri.substring(0, classStartIndex) + newPublicClassName
								+ newArtifactUri.substring(classEndIndex);

					// The object in the artifact is the public class
					// declaration.
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
