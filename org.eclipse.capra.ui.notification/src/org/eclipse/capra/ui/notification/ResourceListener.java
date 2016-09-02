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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapper;
import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapperContainer;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
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
import org.eclipse.emf.ecore.resource.ResourceSet;
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
	// TODO Change into enumeration

	final static int ARTIFACT_RENAMED = 0;
	final static int ARTIFACT_MOVED = 1;
	final static int ARTIFACT_DELETED = 2;
	final static int ARTIFACT_CHANGED = 3;

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		URI uri;
		TracePersistenceAdapter tracePersistenceAdapter;
		ResourceSet resourceSet = new ResourceSetImpl();
		EObject awc;

		tracePersistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		awc = tracePersistenceAdapter.getArtifactWrappers(resourceSet);
		uri = EcoreUtil.getURI(awc);
		EList<ArtifactWrapper> artifactlist = ((ArtifactWrapperContainer) awc).getArtifacts();

		// check if artifact model contains anything

		if (artifactlist.size() > 0) {
			uri = EcoreUtil.getURI(awc);
			IPath path = new Path(uri.toPlatformString(false));
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

			IResourceDelta delta = event.getDelta();
			IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {

				@Override
				public boolean visit(IResourceDelta delta) throws CoreException {

					IPath toPath = delta.getMovedToPath();

					if (delta.getKind() == IResourceDelta.REMOVED && toPath != null) {

						if (delta.getFullPath().toFile().getName().equalsIgnoreCase(toPath.toFile().getName()))
							markupJob(delta, ARTIFACT_MOVED, file, awc);
						else
							markupJob(delta, ARTIFACT_RENAMED, file, awc);
					}
					if (delta.getKind() == IResourceDelta.REMOVED && toPath == null) {
						markupJob(delta, ARTIFACT_DELETED, file, awc);
					}

					if (delta.getKind() == IResourceDelta.CHANGED) {
						markupJob(delta, ARTIFACT_CHANGED, file, awc);
					}
					return true;
				}
			};
			try {
				delta.accept(visitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * The job responsible for adding problem markers to the artifact wrapper
	 * model in case of changes affecting the trace model
	 * 
	 * @param delta
	 *            The changes from the workspace
	 * 
	 * @param issueType
	 *            Type of change that occurred. Can be file changed, deleted,
	 *            moved or renamed.
	 * @param file
	 *            The file containing the artifact wrapper model
	 * @param awc
	 *            The artifact wrapper model
	 */
	public void markupJob(IResourceDelta delta, int issueType, IFile file, EObject awc) {

		WorkspaceJob job = new WorkspaceJob("CapraNotificationJob") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {

				try {
					EList<ArtifactWrapper> list = ((ArtifactWrapperContainer) awc).getArtifacts();
					for (ArtifactWrapper aw : list) {

						if (aw.getName().equals(delta.getResource().getName())) {

							if (issueType == ARTIFACT_RENAMED) {
								IMarker marker = file
										.createMarker("org.eclipse.capra.ui.notification.capraproblemmarker");
								marker.setAttribute("DeltaFullPath", delta.getFullPath().toString());
								marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);

								marker.setAttribute(IMarker.MESSAGE,
										delta.getFullPath() + " has been renamed to " + delta.getMovedToPath());
								marker.setAttribute("DeltaMovedToPath", delta.getMovedToPath().toString());
								marker.setAttribute("oldFileName", delta.getResource().getName());
								marker.setAttribute("newFileName", delta.getMovedToPath().toFile().getName());
								marker.setAttribute("issueType", "Rename");
							} else if (issueType == ARTIFACT_MOVED) {
								IMarker marker = file
										.createMarker("org.eclipse.capra.ui.notification.capraproblemmarker");
								marker.setAttribute("DeltaFullPath", delta.getFullPath().toString());
								marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);

								marker.setAttribute(IMarker.MESSAGE,
										delta.getResource().getName() + " has been moved from " + delta.getFullPath()
												+ " to " + delta.getMovedToPath());
								marker.setAttribute("DeltaMovedToPath", delta.getMovedToPath().toString());
								marker.setAttribute("oldFileName", delta.getResource().getName());
								marker.setAttribute("newFileName", delta.getMovedToPath().toFile().getName());
								marker.setAttribute("issueType", "Move");
							} else if (issueType == ARTIFACT_DELETED) {
								IMarker marker = file
										.createMarker("org.eclipse.capra.ui.notification.capraproblemmarker");
								marker.setAttribute("DeltaFullPath", delta.getFullPath().toString());
								marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);

								marker.setAttribute(IMarker.MESSAGE, delta.getResource().getName()
										+ " has been deleted from " + delta.getFullPath());
								marker.setAttribute("issueType", "Delete");
								marker.setAttribute("fileName", delta.getResource().getName());
							} else if (issueType == ARTIFACT_CHANGED) {
								addFileChangedMarker(file, delta);

							}

						}
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();

	}

	/**
	 * Checks if the marker for file changes already exists in the same file.To
	 * make sure a new marker is not generated every time a change is made.
	 * 
	 * @param file
	 *            The file containing artifact wrappers
	 *
	 * @param delta
	 *            The change that occurred in the workspace
	 * 
	 */
	protected void addFileChangedMarker(IFile file, IResourceDelta delta) throws CoreException {

		IMarker[] markers = file.findMarkers("org.eclipse.capra.ui.notifcation.capraFileChangedMarker", false, 0);
		List<IMarker> markerList = Arrays.asList(markers);
		List<String> changedFiles = new ArrayList<>();

		if (!markerList.isEmpty()) {

			markerList.stream().forEach(m -> {
				changedFiles.add((String) m.getAttribute("fileName", null));
			});

			String changedFile = delta.getResource().getName();
			if (!changedFiles.contains(changedFile)) {
				IMarker fileChangedMarker = file
						.createMarker("org.eclipse.capra.ui.notifcation.capraFileChangedMarker");
				fileChangedMarker.setAttribute("DeltaFullPath", delta.getFullPath().toString());
				fileChangedMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);

				fileChangedMarker.setAttribute("changedFile", delta.getResource().getName());
				fileChangedMarker.setAttribute(IMarker.MESSAGE, delta.getResource().getName()
						+ "has been changed. Please check if associated trace links are still valid");
				fileChangedMarker.setAttribute("issueType", "fileChanged");
			}
		} else {
			IMarker fileChangedMarker = file.createMarker("org.eclipse.capra.ui.notifcation.capraFileChangedMarker");
			fileChangedMarker.setAttribute("DeltaFullPath", delta.getFullPath().toString());
			fileChangedMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);

			fileChangedMarker.setAttribute("fileName", delta.getResource().getName());
			fileChangedMarker.setAttribute(IMarker.MESSAGE, delta.getResource().getName()
					+ "has been changed. Please check if associated trace links are still valid");
			fileChangedMarker.setAttribute("issueType", "fileChanged");
		}

	}
}
