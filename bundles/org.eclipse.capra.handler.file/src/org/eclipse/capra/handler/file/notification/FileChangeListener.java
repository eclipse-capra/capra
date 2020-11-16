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
package org.eclipse.capra.handler.file.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.handler.file.FileHandler;
import org.eclipse.capra.ui.notification.CapraNotificationHelper;
import org.eclipse.capra.ui.notification.CapraNotificationHelper.IssueType;
import org.eclipse.core.resources.IFile;
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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Checks for changes in workspace files and produces a Capra warning marker if
 * the changes affect a file that is traced.
 *
 * @author Michael Warne
 */
public class FileChangeListener implements IResourceChangeListener {

	private final ArtifactMetaModelAdapter artifactAdapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter()
			.orElseThrow();

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		TracePersistenceAdapter tracePersistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter()
				.orElseThrow();
		EObject artifactModel = tracePersistenceAdapter.getArtifactWrappers(EditingDomainHelper.getResourceSet());

		// get all artifacts
		List<EObject> allArtifacts = artifactAdapter.getAllArtifacts(artifactModel);
		List<EObject> fileArtifacts = allArtifacts.stream()
				.filter(p -> artifactAdapter.getArtifactHandler(p).equals(FileHandler.class.getName()))
				.collect(Collectors.toList());

		if (fileArtifacts.isEmpty()) {
			return;
		}

		IPath path = new Path(EcoreUtil.getURI(artifactModel).toPlatformString(false));
		IFile wrapperContainer = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {

			@Override
			public boolean visit(IResourceDelta delta) throws CoreException {
				WorkspaceJob job = new WorkspaceJob(CapraNotificationHelper.NOTIFICATION_JOB) {
					@Override
					public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
						handleDelta(delta, fileArtifacts, wrapperContainer);
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
	 * Checks if the provided delta affects any of the artifacts and if it does,
	 * produces a marker and attaches it to the wrapperContainer.
	 *
	 * @param delta            represents changes in the state of the file
	 * @param fileArtifacts    the artifacts that were created by the file handler
	 * @param wrapperContainer the file that contains the ArtifactWrapper model
	 */
	private void handleDelta(IResourceDelta delta, List<EObject> fileArtifacts, IFile wrapperContainer) {
		for (EObject aw : fileArtifacts) {

			if (artifactAdapter.getArtifactUri(aw).equals(delta.getFullPath().toString())) {
				int changeType = delta.getKind();
				IssueType issueType = null;

				if (changeType == IResourceDelta.REMOVED) {
					IPath toPath = delta.getMovedToPath();

					if (toPath == null) {
						issueType = IssueType.DELETED;
					} else if (delta.getFullPath().toFile().getName().equals(toPath.toFile().getName())) {
						issueType = IssueType.MOVED;
					} else {
						issueType = IssueType.RENAMED;
					}

				} else if (changeType == IResourceDelta.CHANGED) {
					issueType = IssueType.CHANGED;
				} else if (changeType == IResourceDelta.ADDED) {
					issueType = IssueType.ADDED;
				}
				if (issueType == IssueType.ADDED) {
					CapraNotificationHelper.deleteCapraMarker(artifactAdapter.getArtifactUri(aw),
							new IssueType[] { IssueType.MOVED, IssueType.RENAMED, IssueType.DELETED },
							wrapperContainer);
				} else if (issueType != null) {
					Map<String, String> markerInfo = generateMarkerInfo(delta, issueType);
					CapraNotificationHelper.createCapraMarker(markerInfo, wrapperContainer);
				}
			}
		}
	}

	/**
	 * Generates the attributes that will be assigned (in the createMarker method)
	 * to a Capra warning marker.
	 *
	 * @param delta     represents changes in the state of the file
	 * @param issueType the type of change that occurred
	 * @return a key value HashMap, containing the attributes to be assigned to a
	 *         Capra change marker and their keys (IDs).
	 */
	private Map<String, String> generateMarkerInfo(IResourceDelta delta, IssueType issueType) {
		Map<String, String> markerInfo = new HashMap<>();

		String oldArtifactUri = delta.getFullPath().toString();
		IPath newArtifactUri = delta.getMovedToPath();

		String message = "";
		switch (issueType) {
		case RENAMED:
			message = oldArtifactUri + " has been renamed to " + newArtifactUri + ".";
			break;
		case MOVED:
			message = oldArtifactUri + " has been moved to " + newArtifactUri + ".";
			break;
		case DELETED:
			message = oldArtifactUri + " has been deleted.";
			break;
		case CHANGED:
			message = oldArtifactUri + " has been changed. Please check if associated trace links are still valid.";
			break;
		case ADDED:
			break;
		}

		markerInfo.put(CapraNotificationHelper.ISSUE_TYPE, issueType.getValue());
		markerInfo.put(CapraNotificationHelper.MESSAGE, message);
		markerInfo.put(CapraNotificationHelper.OLD_URI, oldArtifactUri);
		if (newArtifactUri != null) {
			markerInfo.put(CapraNotificationHelper.NEW_URI, newArtifactUri.toString());
			markerInfo.put(CapraNotificationHelper.NEW_NAME, newArtifactUri.toFile().getName());
		}

		return markerInfo;
	}
}
