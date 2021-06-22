/*******************************************************************************
 * Copyright (c) 2016, 2021 Chalmers | University of Gothenburg, rt-labs and others.
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
package org.eclipse.capra.generic.persistence;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.capra.core.adapters.IArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.IMetadataAdapter;
import org.eclipse.capra.core.adapters.ITraceabilityInformationModelAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.core.preferences.CapraPreferences;
import org.eclipse.core.resources.IFile;
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
 * {@link org.eclipse.capra.core.adapters.IPersistenceAdapter} creates a special
 * project in the workspace to house the trace link model and the artifact
 * model.
 */
public class GenericPersistenceAdapter implements org.eclipse.capra.core.adapters.IPersistenceAdapter {

	private static final String NAMESPACE_VERSION_REGEX = "http://www.eclipse.org/.*(\\d.\\d.\\d)";
	private static final String NEW_NAMESPACE_IN_TRACES_XCORE = "http://www.eclipse.org/capra/traces/0.7.0";
	private static final String NEW_NAMESPACE_IN_ARTIFACTS_XCORE = "http://www.eclipse.org/capra/artifacts/0.7.0";
	private static final String OLD_NAMESPACE_IN_TRACES_XCORE = "org.eclipse.capra.generic.tracemodel";
	private static final String OLD_NAMESPACE_IN_ARTIFACTS_XCORE = "org.eclipse.capra.generic.artifactmodel";

	private static final Logger LOG = LoggerFactory.getLogger(GenericPersistenceAdapter.class);

	private static String DEFAULT_PROJECT_NAME = "__WorkspaceTraceModels";
	private static final String DEFAULT_TRACE_MODEL_NAME = "traceModel.xmi";
	private static final String DEFAULT_ARTIFACT_WRAPPER_MODEL_NAME = "artifactWrappers.xmi";
	private static final String DEFAULT_METADATA_MODEL_NAME = "metadata.xmi";

	/**
	 * Get the second matching group of the regex in a specific string
	 */
	private String getTargetString(String strOrigin, Pattern pattern) {
		if (null == strOrigin || null == pattern)
			return null;

		Matcher matcher = pattern.matcher(strOrigin);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	private Optional<EObject> loadModel(ResourceSet resourceSet, String modelName) {
		return this.loadModel(this.getProjectNamePreference(), resourceSet, modelName);
	}

	/**
	 * Set a default version of 0.7.0 into an xmi file which has no version
	 */
	private void setDefaultVersion(IFile file, String modelName) throws IOException {
		// BufferedReader bufferedReader;
		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(file.getContents(), file.getCharset()))) {
			StringBuffer inputBuffer = new StringBuffer();
			String line = null;
			String strRegex = null;
			String strReplace = null;

			// Determine which string replace which string
			switch (modelName) {
			case DEFAULT_TRACE_MODEL_NAME:
				strRegex = OLD_NAMESPACE_IN_TRACES_XCORE;
				strReplace = NEW_NAMESPACE_IN_TRACES_XCORE;
				break;
			case DEFAULT_ARTIFACT_WRAPPER_MODEL_NAME:
				strRegex = OLD_NAMESPACE_IN_ARTIFACTS_XCORE;
				strReplace = NEW_NAMESPACE_IN_ARTIFACTS_XCORE;
			default:
				break;
			}

			Pattern pattern = Pattern.compile(strRegex);
			boolean bStopSearching = false;

			while ((line = bufferedReader.readLine()) != null) {
				if (!bStopSearching) {
					Matcher matcher = pattern.matcher(line);

					// Find and replace old ns with a new ns contains version
					if (matcher.find()) {
						line = matcher.replaceAll(strReplace);
						bStopSearching = true;
					}
				}

				inputBuffer.append(line);
				inputBuffer.append(System.lineSeparator());
			}

			// Write the new string into the same file
			try (ByteArrayInputStream newContent = new ByteArrayInputStream(inputBuffer.toString().getBytes())) {
				file.setContents(newContent, true, true, null);
				LOG.debug("Updated namespace of file {}", modelName);
			} catch (CoreException e) {
				LOG.error("Failed to set contents for file {}", modelName, e);
			}
		} catch (CoreException e) {
			LOG.error("Failed to get contents from file {}", modelName, e);
		}
	}

	/**
	 * Get the version string from the namespace in an xmi file
	 */
	private String getModelVersion(String projectName, String modelName) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IFile file = project.getFile(modelName);

		String strVersion = null;

		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(file.getContents(), file.getCharset()))) {
			String line = null;
			String namespaceRegexString = NAMESPACE_VERSION_REGEX;
			Pattern pattern = Pattern.compile(namespaceRegexString);

			// read the text of the .xmi file line by line
			while ((line = bufferedReader.readLine()) != null) {
				// get version string by using regular expression
				strVersion = getTargetString(line, pattern);

				// jump out of the loop if we get version string
				if (null != strVersion && !strVersion.isEmpty()) {
					LOG.debug("Got version {} in {}.", strVersion, modelName);
					break;
				}
			}
		} catch (IOException | CoreException e) {
			LOG.warn("Failed to get contents from file {}", modelName, e);
		}
		return strVersion;
	}

	/**
	 * Transform the model to the version adhere to current meta-model
	 */
	private void transformModel(String strVersion, String projectName, String modelName) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IFile file = project.getFile(modelName);

		if (null == strVersion || strVersion.isEmpty()) {
			LOG.debug("Could not find version string in {}, thus assuming version 0.7.0.", modelName);

			// If we failed to find version, write a namespace with a default version to the
			// model
			try {
				setDefaultVersion(file, modelName);
			} catch (IOException e) {
				LOG.warn("Failed to set default version in XMI file.", e);
			}
		}

		// TODO: transform the model to from version to current

	}

	/**
	 * Load model
	 */
	private synchronized Optional<EObject> loadModel(String projectName, ResourceSet resourceSet, String modelName) {
		URI uri = URI.createPlatformResourceURI(projectName + "/" + modelName, true);

		Resource resource = resourceSet.getResource(uri, false) != null ? resourceSet.getResource(uri, false)
				: resourceSet.createResource(uri);

		if (!resource.isLoaded()) {
			// 1. Get version of model (from .xmi file)
			String strVersion = getModelVersion(projectName, modelName);

			// 2. Transform model (if necessary)
			transformModel(strVersion, projectName, modelName);

			// 3. Load model that now corresponds to current meta-model
			try {
				resource.load(null);
			} catch (IOException e) {
				LOG.warn("Could not load {}: {}", modelName, e.getMessage());
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
		} catch (

		InterruptedException e) {
			LOG.error("Got interrupted when reading the model {}", modelName, e);
		}
		return Optional.empty();
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
	public void saveModels(ResourceSet resourceSet) {
		EObject traceModel = this.getTraceModel(resourceSet);
		EObject artifactModel = this.getArtifactWrappers(resourceSet);
		EObject metadataModel = this.getMetadataContainer(resourceSet);
		this.saveModels(traceModel, artifactModel, metadataModel);
	}

	@Override
	public EObject getArtifactWrappers(ResourceSet resourceSet) {
		IArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactMetaModelAdapter().orElseThrow();
		return loadModel(resourceSet, DEFAULT_ARTIFACT_WRAPPER_MODEL_NAME).orElse(adapter.createModel());
	}

	@Override
	public EObject getMetadataContainer(ResourceSet resourceSet) {
		IMetadataAdapter adapter = ExtensionPointHelper.getTraceMetadataAdapter().orElseThrow();
		return loadModel(resourceSet, DEFAULT_METADATA_MODEL_NAME).orElse(adapter.createModel());
	}

	@Override
	public EObject getTraceModel(ResourceSet resourceSet) {
		ITraceabilityInformationModelAdapter adapter = ExtensionPointHelper.getTraceabilityInformationModelAdapter()
				.orElseThrow();
		return loadModel(resourceSet, DEFAULT_TRACE_MODEL_NAME).orElse(adapter.createModel());
	}
}
