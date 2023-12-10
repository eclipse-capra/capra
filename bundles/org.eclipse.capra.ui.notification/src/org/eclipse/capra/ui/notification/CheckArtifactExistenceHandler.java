/*******************************************************************************
 * Copyright (c) 2023 Jan-Philipp Steghöfer
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *   
 * SPDX-License-Identifier: EPL-2.0
 *   
 * Contributors:
 *     Jan-Philipp Steghöfer - initial implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.ui.notification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.IPersistenceAdapter;
import org.eclipse.capra.core.adapters.ITraceabilityInformationModelAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.ArtifactStatus;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * When executed, goes through all trace links and determines if the linked
 * artifacts still exist. Creates a problem marker if that is not the case.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class CheckArtifactExistenceHandler extends AbstractHandler {

	private final ITraceabilityInformationModelAdapter traceAdapter = ExtensionPointHelper
			.getTraceabilityInformationModelAdapter().get();
	private IPersistenceAdapter persistenceAdapter = ExtensionPointHelper.getPersistenceAdapter().get();
	private ResourceSet resourceSet = EditingDomainHelper.getResourceSet();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Job job = new Job("Check if linked artifacts exist") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Set<EObject> artifacts = new HashSet<>();
				EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);
				EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
				Path path = new Path(EcoreUtil.getURI(artifactModel).toPlatformString(false));
				IFile wrapperContainer = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

				List<Connection> connections = traceAdapter.getAllTraceLinks(traceModel);
				for (Connection connection : connections) {
					artifacts.addAll(connection.getOrigins());
					artifacts.addAll(connection.getTargets());
					for (EObject obj : artifacts) {
						ArtifactHelper artifactHelper = new ArtifactHelper(
								persistenceAdapter.getArtifactWrappers(resourceSet));
						Optional<IArtifactHandler<?>> handler = artifactHelper
								.getHandler(artifactHelper.unwrapWrapper(obj));
						if (handler.isPresent()) {
							ArtifactStatus artifactStatus = handler.get().getArtifactStatus(obj);
							if (artifactStatus.getStatus() == ArtifactStatus.Status.REMOVED) {
								HashMap<String, String> markerInfo = new HashMap<>();
								markerInfo.put(CapraNotificationHelper.ISSUE_TYPE,
										CapraNotificationHelper.IssueType.DELETED.getValue());
								markerInfo.put(CapraNotificationHelper.MESSAGE, "Linked artifact "
										+ artifactHelper.getArtifactLabel(obj) + " does not exist anymore");
								markerInfo.put(CapraNotificationHelper.OLD_URI,
										artifactHelper.getArtifactLocation(obj));
								markerInfo.put(CapraNotificationHelper.NEW_URI, "");
								markerInfo.put(CapraNotificationHelper.NEW_NAME, "");
								CapraNotificationHelper.createCapraMarker(markerInfo, wrapperContainer);
							} else if (artifactStatus.getStatus() == ArtifactStatus.Status.RENAMED) {
								HashMap<String, String> markerInfo = new HashMap<>();
								markerInfo.put(CapraNotificationHelper.ISSUE_TYPE,
										CapraNotificationHelper.IssueType.RENAMED.getValue());
								markerInfo.put(CapraNotificationHelper.MESSAGE,
										"Linked artifact \"" + artifactStatus.getOldName() + "\" has been renamed to \""
												+ artifactStatus.getNewName() + "\"");
								markerInfo.put(CapraNotificationHelper.OLD_URI,
										artifactHelper.getArtifactLocation(obj));
								markerInfo.put(CapraNotificationHelper.OLD_NAME, artifactHelper.getArtifactLabel(obj));
								markerInfo.put(CapraNotificationHelper.NEW_URI,
										CapraNotificationHelper.getFileUri(obj).toPlatformString(false));
								markerInfo.put(CapraNotificationHelper.NEW_NAME, artifactStatus.getNewName());
								CapraNotificationHelper.createCapraMarker(markerInfo, wrapperContainer);
							}
						}
					}
				}
				return Status.OK_STATUS;
			}

		};
		job.setPriority(Job.BUILD);
		job.setUser(true);
		job.schedule();
		return null;
	}

}
