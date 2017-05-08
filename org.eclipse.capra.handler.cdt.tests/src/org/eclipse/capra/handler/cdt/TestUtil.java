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
package org.eclipse.capra.handler.cdt;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IManagedProject;
import org.eclipse.cdt.managedbuilder.core.IProjectType;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class TestUtil {

	public static IProject setupTestProject(String name) throws CoreException, BuildException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject(name);

		IProjectDescription prjDescription = workspace.newProjectDescription(project.getName());
		IProject cdtProject = CCorePlugin.getDefault().createCDTProject(prjDescription, project, null);

		ICProjectDescription cdtDescription = CoreModel.getDefault().createProjectDescription(project, false);

		IManagedBuildInfo info = ManagedBuildManager.createBuildInfo(cdtProject);
		IProjectType projectType = ManagedBuildManager
				.getExtensionProjectType("cdt.managedbuild.target.gnu.cygwin.exe");

		assertNotNull(projectType); // Fail early if required bundles not
									// present

		System.out.println("Owner: " + cdtProject.getName());
		System.out.println("ID: " + projectType.getId());

		IManagedProject managedProject = ManagedBuildManager.createManagedProject(cdtProject, projectType);
		info.setManagedProject(managedProject);

		for (IConfiguration cfg : projectType.getConfigurations()) {
			String id = ManagedBuildManager.calculateChildId(cfg.getId(), null);

			// clone the configuration and set the artifact name
			IConfiguration config = managedProject.createConfiguration(cfg, id);
			config.setArtifactName(project.getName());

			
			System.out.println("Create config: " + cfg.getId());
			// creates/add the configuration to the project description
			cdtDescription.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, config.getConfigurationData());
		}
		CoreModel.getDefault().setProjectDescription(project, cdtDescription);
		return project;
	}

	public static void deleteTestProject(IProject project) throws CoreException {
		project.delete(true, null);
	}

	public static EObject setupModel() {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		ResourceSet resourceSet = new ResourceSetImpl();
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
		return adapter.createArtifact(artifactModel, "org.eclipse.capra.handler.cdt.CDTHandler", uri, name);
	}

}
