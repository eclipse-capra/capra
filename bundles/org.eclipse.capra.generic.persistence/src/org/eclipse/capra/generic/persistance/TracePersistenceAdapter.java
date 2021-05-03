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
package org.eclipse.capra.generic.persistance;

import java.io.IOException;
import java.util.Optional;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.IMetadataAdapter;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.core.preferences.CapraPreferences;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This generic implementation of
 * {@link org.eclipse.capra.core.adapters.TracePersistenceAdapter} creates a
 * special project in the workspace to house the trace link model and the
 * artifact model.
 */
public class TracePersistenceAdapter implements org.eclipse.capra.core.adapters.TracePersistenceAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(TracePersistenceAdapter.class);

	private static String DEFAULT_PROJECT_NAME = "__WorkspaceTraceModels";
	private static final String DEFAULT_TRACE_MODEL_NAME = "traceModel.xmi";
	private static final String DEFAULT_ARTIFACT_WRAPPER_MODEL_NAME = "artifactWrappers.xmi";
	private static final String DEFAULT_METADATA_MODEL_NAME = "metadata.xmi";

	private Optional<EObject> loadModel(ResourceSet resourceSet, String modelName) {

		return this.loadModel(this.getProjectNamePreference(), resourceSet, modelName);
	}

	private Optional<EObject> loadModel(String projectName, ResourceSet resourceSet, String modelName) {
		URI uri = URI.createPlatformResourceURI(projectName + "/" + modelName, true);

		Resource resource = resourceSet.getResource(uri, false) != null ? resourceSet.getResource(uri, false)
				: resourceSet.createResource(uri);
		if (!resource.isLoaded()) {
			try {
				resource.load(null);
			} catch (IOException e) {
				LOG.error("Could not load trace model", e);
				return Optional.empty();
			}
		}

		// execute the read operation as a read-only transaction
		EList<EObject> contents;
		try {
			contents = TransactionUtil.runExclusive(EditingDomainHelper.getEditingDomain(),
					new RunnableWithResult.Impl<EList<EObject>>() {
						@Override
						public void run() {
							setResult(resource.getContents());
						}
					});
			return contents.isEmpty() ? Optional.empty() : Optional.of(contents.get(0));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public EObject getTraceModel(ResourceSet resourceSet) {
		TraceMetaModelAdapter adapter = ExtensionPointHelper.getTraceMetamodelAdapter().orElseThrow();
		return loadModel(resourceSet, DEFAULT_TRACE_MODEL_NAME).orElse(adapter.createModel());
	}

	private IProject ensureProjectExists(String defaultProjectName) throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(defaultProjectName);
		if (!project.exists()) {
			project.create(new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		}
		return project;
	}

	@Override
	public void saveModels(EObject traceModel, EObject artifactModel, EObject metadataModel) {
		try {
			ensureProjectExists(this.getProjectNamePreference());
			ResourceSet resourceSet = EditingDomainHelper.getResourceSet();

			URI traceURI = URI.createPlatformResourceURI(getProjectNamePreference() + "/" + DEFAULT_TRACE_MODEL_NAME,
					true);
			URI artifactURI = URI.createPlatformResourceURI(
					getProjectNamePreference() + "/" + DEFAULT_ARTIFACT_WRAPPER_MODEL_NAME, true);
			URI metadataURI = URI
					.createPlatformResourceURI(getProjectNamePreference() + "/" + DEFAULT_METADATA_MODEL_NAME, true);

			Resource resourceForTraces = resourceSet.getResource(traceURI, false) != null
					? resourceSet.getResource(traceURI, false)
					: resourceSet.createResource(traceURI);

			Resource resourceForArtifacts = resourceSet.getResource(artifactURI, false) != null
					? resourceSet.getResource(artifactURI, false)
					: resourceSet.createResource(artifactURI);

			Resource resourceForMetadata = resourceSet.getResource(metadataURI, false) != null
					? resourceSet.getResource(metadataURI, false)
					: resourceSet.createResource(metadataURI);

			if (resourceForTraces.getContents().isEmpty() || resourceForTraces.getContents().isEmpty()
					|| resourceForMetadata.getContents().isEmpty()) {

				// The trace model or the artifact model have never been saved before and we
				// need to add it to the resource set.
				TransactionalEditingDomain editingDomain = EditingDomainHelper.getEditingDomain();
				// We're saving the trace model and the artifact model in the same transaction
				Command cmd = new RecordingCommand(editingDomain, "Save Trace Model") {
					@Override
					protected void doExecute() {
						if (resourceForTraces.getContents().isEmpty()) {
							resourceForTraces.getContents().add(traceModel);
						}
						if (resourceForArtifacts.getContents().isEmpty()) {
							resourceForArtifacts.getContents().add(artifactModel);
						}
						if (resourceForMetadata.getContents().isEmpty()) {
							resourceForMetadata.getContents().add(metadataModel);
						}
					}
				};

				try {
					((TransactionalCommandStack) editingDomain.getCommandStack()).execute(cmd, null); // default options
				} catch (RollbackException rbe) {
					LOG.warn("Saving trace model has been rolled back.", rbe);
				}
			}

			resourceForArtifacts.save(null);
			resourceForTraces.save(null);
			resourceForMetadata.save(null);
		} catch (Exception e) {
			LOG.error("Unable to save trace model!", e);
		}
	}

	@Override
	public EObject getArtifactWrappers(ResourceSet resourceSet) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();
		return loadModel(resourceSet, DEFAULT_ARTIFACT_WRAPPER_MODEL_NAME).orElse(adapter.createModel());
	}

	@Override
	public EObject getMetadataContainer(ResourceSet resourceSet) {
		IMetadataAdapter adapter = ExtensionPointHelper.getTraceMetadataAdapter().orElseThrow();
		return loadModel(resourceSet, DEFAULT_METADATA_MODEL_NAME).orElse(adapter.createModel());
	}

	// get preference for project name
	private String getProjectNamePreference() {
		IEclipsePreferences pref = CapraPreferences.getPreferences();
		String projectName = pref.get(CapraPreferences.CAPRA_PERSISTENCE_PROJECT_NAME, DEFAULT_PROJECT_NAME);
		if (projectName.isBlank() || projectName.isEmpty()) {
			projectName = DEFAULT_PROJECT_NAME;
		}
		return projectName;
	}
}
