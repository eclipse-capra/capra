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
package org.eclipse.capra.ui.notification;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapper;
import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapperContainer;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.handlers.ArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Checks for workspace changes to determine if the changes made to resources
 * affect the trace model. Creates markers on the artifact model if the changes
 * affect artifact wrappers.
 * 
 * @author Michael Warne
 * 
 *
 */

public class ResourceListener implements IResourceChangeListener {

	/**
	 * ID of Capra custom marker for reporting a generic problem.
	 */
	public static final String CAPRA_PROBLEM_MARKER_ID = "org.eclipse.capra.ui.notification.capraProblemMarker";

	public enum IssueType {
		ARTIFACT_RENAMED, ARTIFACT_MOVED, ARTIFACT_DELETED, ARTIFACT_CHANGED
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {

		TracePersistenceAdapter tracePersistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		EObject awc = tracePersistenceAdapter.getArtifactWrappers(new ResourceSetImpl());
		EList<ArtifactWrapper> artifactList = ((ArtifactWrapperContainer) awc).getArtifacts();

		if (artifactList.size() == 0)
			return;

		URI uri = EcoreUtil.getURI(awc);
		IPath path = new Path(uri.toPlatformString(false));
		IFile wrapperContainer = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

		IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {

			@Override
			public boolean visit(IResourceDelta delta) throws CoreException {

				WorkspaceJob job = new WorkspaceJob("CapraNotificationJob") {

					@Override
					public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {

						EList<ArtifactWrapper> list = ((ArtifactWrapperContainer) awc).getArtifacts();
						for (ArtifactWrapper aw : list) {

							Optional<ArtifactHandler> artifactHandler = ExtensionPointHelper
									.getArtifactHandler(aw.getArtifactHandler());
							if (!artifactHandler.isPresent())
								return Status.CANCEL_STATUS;

							if (aw.getPath().equals(delta.getFullPath().toString())) {
								HashMap<String, String> markerInfo = null;
								int changeType = delta.getKind();

								if (changeType == IResourceDelta.REMOVED) {
									IPath toPath = delta.getMovedToPath();

									if (toPath == null)
										markerInfo = generateMarkerInfo(delta, IssueType.ARTIFACT_DELETED);
									else {
										if (delta.getFullPath().toFile().getName()
												.equalsIgnoreCase(toPath.toFile().getName()))
											markerInfo = generateMarkerInfo(delta, IssueType.ARTIFACT_MOVED);
										else
											markerInfo = generateMarkerInfo(delta, IssueType.ARTIFACT_RENAMED);
									}

									createMarker(wrapperContainer, aw.getUri(), markerInfo);
									break;
								} else if (changeType == IResourceDelta.CHANGED) {
									markerInfo = new HashMap<String, String>();
									String message = artifactHandler.get().generateMarkerMessage(delta, aw.getUri());

									if (message != null && !message.isEmpty()) {
										markerInfo.put("issueType", "fileChanged");
										markerInfo.put(IMarker.MESSAGE, message);
										createMarker(wrapperContainer, aw.getUri(), markerInfo);
									}
								}
							}
						}
						return Status.OK_STATUS;
					}
				};
				job.schedule();
				return true;
			}
		};
		try {
			IResourceDelta delta = event.getDelta();
			if (delta != null)
				delta.accept(visitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a Capra marker from the provided information about the artifact
	 * and the change that occurred.
	 * 
	 * @param wContainer
	 *            file that holds the artifact model
	 * @param wUri
	 *            uri of the associated artifact
	 * @param markerInfo
	 *            contains attributes that are to be assigned to the created
	 *            marker
	 */
	private void createMarker(IFile wContainer, String wUri, HashMap<String, String> markerInfo) {
		if (markerInfo == null || markerInfo.isEmpty())
			return;

		try {
			IMarker[] markers = wContainer.findMarkers(CAPRA_PROBLEM_MARKER_ID, false, 0);
			for (IMarker marker : markers)
				if (marker.getAttribute("elementIdentifier", null).contentEquals(wUri)
						&& marker.getAttribute(IMarker.MESSAGE, null).contentEquals(markerInfo.get(IMarker.MESSAGE)))
					return;

			IMarker marker = wContainer.createMarker(CAPRA_PROBLEM_MARKER_ID);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			marker.setAttribute("elementIdentifier", wUri);

			for (Entry<String, String> entry : markerInfo.entrySet())
				marker.setAttribute(entry.getKey(), entry.getValue());

		} catch (CoreException e) {
			if (wContainer.exists())
				e.printStackTrace();
		}
	}

	/**
	 * Generates the attributes that will later be assigned (in the createMarker
	 * method) to a Capra change marker.
	 * 
	 * @param delta
	 *            represents changes in the state of a resource
	 * @param issue
	 *            the type of change that occurred
	 * @return a key value HashMap, containing the attributes to be assigned to
	 *         a Capra change marker and their keys (IDs).
	 */
	private HashMap<String, String> generateMarkerInfo(IResourceDelta delta, IssueType issue) {
		HashMap<String, String> markerInfo = new HashMap<String, String>();

		if (issue == IssueType.ARTIFACT_RENAMED) {
			markerInfo.put("DeltaMovedToPath", delta.getMovedToPath().toString());
			markerInfo.put("newFileName", delta.getMovedToPath().toFile().getName());
			markerInfo.put("issueType", "Rename");
			markerInfo.put(IMarker.MESSAGE,
					delta.getFullPath() + " has been renamed to " + delta.getMovedToPath() + ".");

		} else if (issue == IssueType.ARTIFACT_MOVED) {
			markerInfo.put("DeltaMovedToPath", delta.getMovedToPath().toString());
			markerInfo.put("newFileName", delta.getMovedToPath().toFile().getName());
			markerInfo.put("issueType", "Move");
			markerInfo.put(IMarker.MESSAGE, delta.getResource().getName() + " has been moved from "
					+ delta.getFullPath() + " to " + delta.getMovedToPath() + ".");
		} else if (issue == IssueType.ARTIFACT_DELETED) {
			markerInfo.put("issueType", "Delete");
			markerInfo.put(IMarker.MESSAGE,
					delta.getResource().getName() + " has been deleted from " + delta.getFullPath() + ".");
		}

		if (!markerInfo.isEmpty()) {
			markerInfo.put("oldFileName", delta.getResource().getName());
		}

		return markerInfo;
	}
}
