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
package org.eclipse.capra.handler.jdt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.junit.Before;

public class TestUtil {

	private static final String SRC_FOLDER = "src";

	public static IProject createTestProject(String name) throws CoreException {
		// Create the test project
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(name);
		project.create(null);
		project.open(null);

		// Add Java nature
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);

		// Create Java project
		IJavaProject javaProject = JavaCore.create(project);

		// Add output folder
		IFolder binFolder = project.getFolder("bin");
		javaProject.setOutputLocation(binFolder.getFullPath(), null);

		// Set classpath
		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
		for (LibraryLocation element : locations) {
			entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
		}
		// add libs to project class path
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);

		// Create source folder
		IFolder sourceFolder = project.getFolder(SRC_FOLDER);
		sourceFolder.create(false, true, null);

		// Add source folder to classpath entries
		IPackageFragmentRoot srcRoot = javaProject.getPackageFragmentRoot(sourceFolder);
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = JavaCore.newSourceEntry(srcRoot.getPath());
		javaProject.setRawClasspath(newEntries, null);

		// Create default package
		javaProject.getPackageFragmentRoot(sourceFolder).createPackageFragment(name, false, null);
		return project;
	}

	public static void deleteTestProject(IProject project) throws CoreException {
		project.delete(true, null);
	}

	@Before
	public static EObject setupModel() {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		ResourceSet resourceSet = new ResourceSetImpl();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		return artifactModel;
	}

	public static ICompilationUnit createCompilationUnit(IProject project, String name, String source)
			throws JavaModelException {
		IFolder sourceFolder = project.getFolder(SRC_FOLDER);
		IJavaProject javaProject = JavaCore.create(project);
		IPackageFragment pack = javaProject.getPackageFragmentRoot(sourceFolder).getPackageFragment(project.getName());
		return pack.createCompilationUnit(name, source, false, null);
	}

	public static ICompilationUnit createCompilationUnit(IProject project, String name, StringBuffer source)
			throws JavaModelException {
		return createCompilationUnit(project, name, source.toString());
	}

	public static EObject createWrapper(EObject artifactModel, String uri, String name) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		return adapter.createArtifact(artifactModel, "org.eclipse.capra.handler.jdt.JavaElementHandler", uri, name);
	}

}
