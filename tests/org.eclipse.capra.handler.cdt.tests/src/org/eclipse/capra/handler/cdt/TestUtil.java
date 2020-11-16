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
package org.eclipse.capra.handler.cdt;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class TestUtil {

	@SuppressWarnings("restriction")
	public static ICProject setupTestProject(String name) throws CoreException, BuildException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject(name);

		IProjectDescription prjDescription = workspace.newProjectDescription(project.getName());
		project = CCorePlugin.getDefault().createCDTProject(prjDescription, project, new NullProgressMonitor());

		// Create build info and managed project
		ICProjectDescription cProjectDescription = CoreModel.getDefault().createProjectDescription(project, false);
		ManagedBuildManager.createBuildInfo(project);

		Configuration config = new Configuration(new ManagedProject(cProjectDescription), null, "myId", "myName");
		config.getEditableBuilder().setManagedBuildOn(false);
		cProjectDescription.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID,
				config.getConfigurationData());

		CProjectNature.addCNature(project, new NullProgressMonitor());

		CoreModel.getDefault().setProjectDescription(project, cProjectDescription);
		return CoreModel.getDefault().create(project);
	}

	public static void deleteTestProject(ICProject project) throws CoreException {
		project.getProject().delete(true, null);
	}

	public static EObject setupModel() {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		return artifactModel;
	}

	public static IFile createFile(IFile file, String contents) throws CoreException {
		if (contents == null) {
			contents = "";
		}

		System.out.println("Creating file: " + file.getName());

		InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
		file.create(inputStream, true, null);

		System.out.println("Created file: " + file.getName());
		return file;
	}

	public static ITranslationUnit createTranslationUnit(IProject project, String name, String source)
			throws CoreException {
		IFile file = project.getFile(name);
		createFile(file, source);
		return (ITranslationUnit) CoreModel.getDefault().create(file);
	}

	public static EObject createWrapper(EObject artifactModel, String uri, String name) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		return adapter.createArtifact(artifactModel, "org.eclipse.capra.handler.cdt.CDTHandler", uri, name, uri);
	}

}
