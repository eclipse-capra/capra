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
 * Listens to resource changes to detect if the trace links are still
 * consistent.
 * 
 * @author Michael Warne
 */
public class ResourceListener implements IResourceChangeListener {

	// TODO Change this into an enumeration
	final static int ARTIFACT_RENAMED = 0;
	final static int ARTIFACT_MOVED = 1;
	final static int ARTIFACT_DELETED = 2;

	private URI uri;
	private TracePersistenceAdapter tracePersistenceAdapter;
	private ResourceSet resourceSet;
	private EObject awc;

	@Override
	public void resourceChanged(IResourceChangeEvent event) {

		IResourceDelta delta = event.getDelta();
		IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {

			@Override
			public boolean visit(IResourceDelta delta) throws CoreException {

				IPath toPath = delta.getMovedToPath();

				if (delta.getKind() == IResourceDelta.REMOVED && toPath != null) {

					if (delta.getFullPath().toFile().getName().equalsIgnoreCase(toPath.toFile().getName()))
						markupJob(delta, ARTIFACT_MOVED);
					else
						markupJob(delta, ARTIFACT_RENAMED);
				}
				if (delta.getKind() == IResourceDelta.REMOVED && toPath == null) {
					markupJob(delta, ARTIFACT_DELETED);
				}
				return true;
			}
		};
		try {
			delta.accept(visitor);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Creates and schedules the job to create error markers for any
	 * inconsistencies detected.
	 * 
	 * @param delta
	 *            the changes in the resources that have been detected
	 * @param issueType
	 *            the kind of change that has taken place
	 */
	public void markupJob(IResourceDelta delta, int issueType) {

		WorkspaceJob job = new WorkspaceJob("myJob") {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				try {
					resourceSet = new ResourceSetImpl();
					tracePersistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
					tracePersistenceAdapter.getTraceModel(resourceSet);
					awc = tracePersistenceAdapter.getArtifactWrappers(resourceSet);
					// if(! tracemodel.isPresent() || ! awc.isPresent()) return
					// Status.OK_STATUS;
					uri = EcoreUtil.getURI(awc);
					IPath path = new Path(uri.toPlatformString(false));
					IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
					EList<ArtifactWrapper> list = ((ArtifactWrapperContainer) awc).getArtifacts();
					for (ArtifactWrapper aw : list) {

						if (aw.getName().equals(delta.getResource().getName())) {

							IMarker marker = file.createMarker("org.eclipse.capra.ui.notification.capraproblemmarker");
							if (issueType == ARTIFACT_RENAMED) {
								marker.setAttribute(IMarker.MESSAGE,
										delta.getFullPath() + " has been renamed to " + delta.getMovedToPath());
								marker.setAttribute("DeltaMovedToPath", delta.getMovedToPath().toString());
								marker.setAttribute("FileName", delta.getMovedToPath().toFile().getName().toString());
								marker.setAttribute("IssueType", "Rename");
							} else if (issueType == ARTIFACT_MOVED) {
								marker.setAttribute(IMarker.MESSAGE,
										delta.getResource().getName() + " has been moved from " + delta.getFullPath()
												+ " to " + delta.getMovedToPath());
								marker.setAttribute("DeltaMovedToPath", delta.getMovedToPath().toString());
								marker.setAttribute("FileName", delta.getMovedToPath().toFile().getName().toString());
								marker.setAttribute("IssueType", "Move");
							} else if (issueType == ARTIFACT_DELETED) {
								marker.setAttribute(IMarker.MESSAGE, delta.getResource().getName()
										+ " has been deleted from " + delta.getFullPath());
								marker.setAttribute("IssueType", "Delete");
							}
							marker.setAttribute("DeltaFullPath", delta.getFullPath().toString());
							marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
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
}
