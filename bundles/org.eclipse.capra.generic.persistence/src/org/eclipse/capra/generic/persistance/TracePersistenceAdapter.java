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
package org.eclipse.capra.generic.persistance;

import java.io.IOException;
import java.util.Optional;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * This generic implementation of
 * {@link org.eclipse.capra.core.adapters.TracePersistenceAdapter} creates a
 * special project in the workspace to house the trace link model and the
 * artifact model.
 */
public class TracePersistenceAdapter implements org.eclipse.capra.core.adapters.TracePersistenceAdapter {

	private static final String DEFAULT_PROJECT_NAME = "__WorkspaceTraceModels";
	private static final String DEFAULT_TRACE_MODEL_NAME = "traceModel.xmi";
	private static final String DEFAULT_ARTIFACT_WRAPPER_MODEL_NAME = "artifactWrappers.xmi";

	private Optional<EObject> loadModel(ResourceSet resourceSet, String modelName) {
		if (projectExist(DEFAULT_PROJECT_NAME) && fileExists(DEFAULT_PROJECT_NAME + "/" + modelName)) {
			try {
				URI uri = URI.createPlatformResourceURI(DEFAULT_PROJECT_NAME + "/" + modelName, true);
				Resource resource = resourceSet.getResource(uri, true);
				resource.load(null);

				return Optional.of(resource.getContents().get(0));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return Optional.empty();
	}

	@Override
	public EObject getTraceModel(ResourceSet resourceSet) {
		TraceMetaModelAdapter adapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
		return loadModel(resourceSet, DEFAULT_TRACE_MODEL_NAME).orElse(adapter.createModel());
	}

	private boolean fileExists(String path) {
		return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path)).exists();
	}

	private boolean projectExist(String defaultProjectName) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(defaultProjectName).exists();
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
	public void saveTracesAndArtifacts(EObject traceModel, EObject artifactModel) {
		try {
			ResourceSet resourceSet = new ResourceSetImpl();

			URI uri = URI.createPlatformResourceURI(DEFAULT_PROJECT_NAME + "/" + DEFAULT_TRACE_MODEL_NAME, true);
			Resource resource = resourceSet.createResource(uri);
			resource.getContents().add(traceModel);

			uri = URI.createPlatformResourceURI(DEFAULT_PROJECT_NAME + "/" + DEFAULT_ARTIFACT_WRAPPER_MODEL_NAME, true);
			Resource resourceForArtifacts = resourceSet.createResource(uri);
			resourceForArtifacts.getContents().add(artifactModel);

			ensureProjectExists(DEFAULT_PROJECT_NAME);

			resourceForArtifacts.save(null);
			resource.save(null);
		} catch (Exception e) {
			System.err.println("Unable to save trace model!");
			e.printStackTrace();
		}
	}

	@Override
	public EObject getArtifactWrappers(ResourceSet resourceSet) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		return loadModel(resourceSet, DEFAULT_ARTIFACT_WRAPPER_MODEL_NAME).orElse(adapter.createModel());
	}

	@Override
	public EObject getTraceModel(EObject object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EObject getArtifactWrappers(EObject object) {
		// TODO Auto-generated method stub
		return null;
	}

}
