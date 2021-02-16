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
package org.eclipse.capra.testsupport;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.generic.artifactmodel.ArtifactWrapperContainer;
import org.eclipse.capra.ui.operations.CreateTraceOperation;
import org.eclipse.capra.ui.plantuml.ToggleTransitivityHandler;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * A helper class for writing JUnit tests for the Capra tool.
 */
@SuppressWarnings("restriction")
public class TestHelper {

	/**
	 * The package in which new Java classes will be created.
	 */
	private static final String JAVA_PACKAGE_NAME = "org.eclipse.capra.test";

	/**
	 * A standard waiting time that can be used whenever the IDE needs to
	 * asynchronously complete a task that the test should wait for, e.g., create or
	 * delete a resource, generate a marker, etc.
	 */
	public static final int UI_REACTION_WAITING_TIME = 1000;

	private TestHelper() {
		// Deliberately do nothing
	}

	/**
	 * ID of Capra custom marker for reporting a generic problem.
	 */
	public static final String CAPRA_PROBLEM_MARKER_ID = "org.eclipse.capra.ui.notification.capraProblemMarker";

	/**
	 * Creates an empty project
	 *
	 * @param projectName the name of the project
	 * @throws CoreException
	 */
	public static IProject createSimpleProject(String projectName) throws CoreException {
		IProject project = getProject(projectName);

		// Create project
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		project.create(progressMonitor);
		project.open(progressMonitor);
		return project;
	}

	/**
	 * Creates a Java project and a Java class declaration inside it.
	 *
	 * @param projectName the name of the project
	 * @return the created Java class
	 * @throws CoreException
	 */
	public static IType createJavaProjectWithASingleJavaClass(String projectName) throws CoreException {
		IProject project = createSimpleProject(projectName);
		IJavaProject javaProject = createJavaProject(project);
		IFolder sourceFolder = createSourceFolder(project);
		return createJavaClassInFolder(javaProject, sourceFolder, "TestClass");
	}

	/**
	 * Creates a &quot;src&quot; folder inside the given project.
	 * 
	 * @param project the project for which to create a &quot;src&quot;
	 * @return the created folder
	 * @throws CoreException
	 */
	public static IFolder createSourceFolder(IProject project) throws CoreException {
		// Create a src file
		IFolder sourceFolder = project.getFolder("src");
		sourceFolder.create(false, true, null);
		return sourceFolder;
	}

	/**
	 * Turns the given project into a {@link IJavaProject} instance by adding the
	 * Java nature and creating and setting the build path.
	 * 
	 * @param project the project to turn into a {@code IJavaProject}
	 * @return the newly created Java project
	 * @throws CoreException
	 * @throws JavaModelException
	 */
	public static IJavaProject createJavaProject(IProject project) throws CoreException, JavaModelException {
		// Add Java nature
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);

		// Create as Java project and set up build path etc.
		IJavaProject javaProject = JavaCore.create(project);
		IFolder binFolder = project.getFolder("bin");
		binFolder.create(false, true, null);
		javaProject.setOutputLocation(binFolder.getFullPath(), null);
		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();

		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
		return javaProject;
	}

	/**
	 * Creates a java class with the specified name in the provided folder of the
	 * provided project. The new class will be created in a package specified by
	 * (@code JAVA_PACKAGE_NAME}.
	 * 
	 * @param javaProject  the project in which to create the class
	 * @param sourceFolder the folder in which to create the class
	 * @return the newly created class
	 * @throws JavaModelException
	 */
	public static IType createJavaClassInFolder(IJavaProject javaProject, IFolder sourceFolder, String className)
			throws JavaModelException {
		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(sourceFolder);
		Set<IClasspathEntry> classPathEntries = new HashSet<>(Arrays.asList(javaProject.getRawClasspath()));
		classPathEntries.add(JavaCore.newSourceEntry(root.getPath()));
		IClasspathEntry[] classPathEntryArray = new IClasspathEntry[classPathEntries.size()];
		classPathEntries.toArray(classPathEntryArray);
		javaProject.setRawClasspath(classPathEntryArray, null);

		IPackageFragment pack = javaProject.getPackageFragmentRoot(sourceFolder)
				.createPackageFragment(JAVA_PACKAGE_NAME, false, null);

		StringBuffer buffer = new StringBuffer();
		buffer.append("package " + pack.getElementName() + ";\n");
		buffer.append("\n");
		buffer.append("public class " + className + " { public void doNothing(){ } }");

		ICompilationUnit icu = pack.createCompilationUnit(className + ".java", buffer.toString(), false, null);
		return icu.getType(className);
	}

	/**
	 * Clears the active workspace by deleting all the contents.
	 *
	 * @throws CoreException
	 */
	public static void clearWorkspace() throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (IProject p : root.getProjects()) {
			p.delete(true, new NullProgressMonitor());
		}
	}

	/**
	 * Checks if the project with the provided name exists.
	 *
	 * @param projectName the name of the project
	 * @return true if the project exists in the active workspace, false otherwise
	 */
	public static boolean projectExists(String projectName) {
		return getProject(projectName).exists();
	}

	/**
	 * Returns a handle to the project resource with the given name.
	 *
	 * @param projectName the name of the project
	 * @return a handle to the project resource
	 */
	public static IProject getProject(String projectName) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		return root.getProject(projectName);
	}

	/**
	 * Creates an empty Ecore model.
	 *
	 * @param name the name of the model
	 * @return
	 */
	public static EPackage createEcoreModel(String name) {
		EPackage p = EcoreFactory.eINSTANCE.createEPackage();
		p.setName(name);
		return p;
	}

	/**
	 * Creates an EClass entity in the provided model.
	 *
	 * @param p    an Ecore model
	 * @param name the name of the created EClass entity
	 */
	public static void createEClassInEPackage(EPackage p, String name) {
		EClass c = EcoreFactory.eINSTANCE.createEClass();
		c.setName(name);
		p.getEClassifiers().add(c);
	}

	/**
	 * Nests a new EPackage inside the provided EPackage.
	 *
	 * @param p    a new EPackage
	 * @param name the name of the created EPackage
	 */
	public static void createEPackageInEPackage(EPackage p, String name) {
		EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
		pkg.setName(name);
		p.getESubpackages().add(pkg);
	}

	/**
	 * Persists (saves) the provided Ecore model in the specified project.
	 *
	 * @param project a handle to the project in which the model is to be persisted
	 * @param pack    the Ecore model to be persisted
	 * @throws IOException
	 */
	public static void save(IProject project, EPackage pack) throws IOException {
		ResourceSet rs = new ResourceSetImpl();
		URI path = URI.createFileURI(project.getLocation().toString() + "/" + pack.getName() + ".ecore");
		Resource r = rs.createResource(path);
		r.getContents().add(pack);
		r.save(null);
	}

	/**
	 * Returns an Ecore model entity from the specified project.
	 *
	 * @param project the project containing the model
	 * @param p       the name of the model
	 * @param rs      the provided ResourceSet instance
	 * @return an Ecore model entity
	 * @throws IOException
	 */
	public static EPackage load(IProject project, String p, ResourceSet rs) throws IOException {
		URI path = URI.createFileURI(project.getLocation().toString() + "/" + p);
		return (EPackage) rs.getResource(path, true).getContents().get(0);
	}

	public static CreateTraceOperation prepareCreateTraceOperationForCurrentSelectionOfType(EClass traceType) {
		CreateTraceOperation operation = new CreateTraceOperation("Create trace link",
				Arrays.asList(SelectionView.getOpenedView().getSelection().get(0)), SelectionView.getOpenedView()
						.getSelection().subList(1, SelectionView.getOpenedView().getSelection().size()));
		operation.setChooseTraceType((traceTypes, selection) -> {
			if (traceTypes.contains(traceType)) {
				return Optional.of(traceType);
			} else {
				return Optional.empty();
			}
		});
		return operation;
	}

	/**
	 * Creates a trace between the objects that are in the Selection view.
	 *
	 * @param traceType the type of the trace that is to connect the objects
	 */
	public static void createTraceForCurrentSelectionOfType(EClass traceType) {
		CreateTraceOperation operation = prepareCreateTraceOperationForCurrentSelectionOfType(traceType);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		operation.createTrace(shell);
	}

	/**
	 * Checks if there is a trace between the provided {@link EObject} instances.
	 *
	 * @param firstObject  first {@code EObject}
	 * @param secondObject second {@code EObject}
	 * @return {@code true} if a trace exists between the two objects, {@code false}
	 *         otherwise
	 */
	public static boolean thereIsATraceBetween(Object firstObject, Object secondObject) {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(persistenceAdapter.getArtifactWrappers(resourceSet));
		if (firstObject instanceof EObject && secondObject instanceof EObject) {
			if (traceModel != null) {
				return traceAdapter.isThereATraceBetween((EObject) firstObject, (EObject) secondObject, traceModel);
			}
		}
		if (firstObject instanceof EObject && !(secondObject instanceof EObject)) {
			EObject wrapper_b = artifactHelper.createWrapper(secondObject);
			if (traceModel != null) {
				return traceAdapter.isThereATraceBetween((EObject) firstObject, wrapper_b, traceModel);
			}
		}
		if (!(firstObject instanceof EObject) && secondObject instanceof EObject) {
			EObject wrapper_a = artifactHelper.createWrapper(firstObject);
			if (traceModel != null) {
				return traceAdapter.isThereATraceBetween(wrapper_a, (EObject) secondObject, traceModel);
			}
		}
		if (!(firstObject instanceof EObject) && !(secondObject instanceof EObject)) {
			EObject wrapper_a = artifactHelper.createWrapper(firstObject);
			EObject wrapper_b = artifactHelper.createWrapper(secondObject);
			if (traceModel != null) {
				return traceAdapter.isThereATraceBetween(wrapper_a, wrapper_b, traceModel);
			}
		}
		return false;
	}

	/**
	 * Gets the first trace between the two given objects that can be found in the
	 * trace model. Returns {@code null} if no such trace exists.
	 * 
	 * @param origin the origin of the trace
	 * @param target the target of the trace
	 * @return a trace between {@code origin} and {@code target} or {@code null}
	 */
	public static EObject getTraceBetween(Object origin, Object target) {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(persistenceAdapter.getArtifactWrappers(resourceSet));
		List<Connection> connections = traceAdapter.getConnectedElements(artifactHelper.createWrapper(origin),
				traceModel);
		for (Connection conn : connections) {
			if (conn.getTargets().contains(artifactHelper.createWrapper(target))) {
				return conn.getTlink();
			}
		}
		return null;
	}

	/**
	 * Creates an empty C or C++ project.
	 *
	 * @param projectName the name of the project to be created
	 * @return a handle to the created project
	 * @throws CoreException
	 * @throws BuildException
	 */
	public static ICProject createCDTProject(String projectName) throws CoreException, BuildException {
		IProject project = getProject(projectName);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProjectDescription description = workspace.newProjectDescription(projectName);
		project = CCorePlugin.getDefault().createCDTProject(description, project, new NullProgressMonitor());

		// Create build info and managed project
		ICProjectDescription cProjectDescription = CoreModel.getDefault().createProjectDescription(project, false);
		ManagedBuildManager.createBuildInfo(project);
		Configuration config = new Configuration(new ManagedProject(cProjectDescription), null, "myId", "myName");
		config.getEditableBuilder().setManagedBuildOn(false);
		cProjectDescription.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID,
				config.getConfigurationData());

		CoreModel.getDefault().setProjectDescription(project, cProjectDescription);
		CProjectNature.addCNature(project, new NullProgressMonitor());

		return CoreModel.getDefault().create(project);
	}

	/**
	 * Creates a C source file in the provided C project.
	 *
	 * @param fileName the name of the C source file to be created in the project
	 * @param cProject the project in which the file is to be created
	 * @return the created TranslationUnit
	 * @throws CoreException
	 */
	public static ITranslationUnit createCSourceFileInProject(String fileName, ICProject cProject)
			throws CoreException, InterruptedException {

		StringBuffer buffer = new StringBuffer();
		buffer.append("#include <stdio.h>\n");
		buffer.append("\n");
		buffer.append("int main() {\n");
		buffer.append("\tprintf(\"Hello, World!\");\n");
		buffer.append("\treturn 0;\n");
		buffer.append("}\n");
		IFile cSourceFile = cProject.getProject().getFile(fileName);

		cSourceFile.create(new ByteArrayInputStream(buffer.toString().getBytes()), true, new NullProgressMonitor());

		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);
		return ((ISourceRoot) (cProject.getChildren()[0])).getTranslationUnits()[0];
	}

	/**
	 * Creates an empty file in the project with the provided name.
	 *
	 * @param fileName    the name of the created file
	 * @param projectName the name of the project in which the file is to be created
	 * @return a handle to the created file
	 * @throws CoreException
	 */
	public static IFile createEmptyFileInProject(String fileName, String projectName) throws CoreException {
		IProject project = getProject(projectName);
		IFile f = project.getFile(fileName);
		f.create(new ByteArrayInputStream("hello world!".getBytes()), true, new NullProgressMonitor());

		return f;
	}

	/**
	 * Resets the selection view by emptying it.
	 */
	public static void resetSelectionView() {
		SelectionView.getOpenedView().clearSelection();
		ToggleTransitivityHandler.setTraceViewTransitive(true);
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
	}

	/**
	 * Removes all existing trace links and artifacts from the trace model and the
	 * artifact model.
	 */
	public static void purgeModels() {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);

		// Purge traces
		TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
		traceAdapter.deleteTrace(traceAdapter.getAllTraceLinks(traceModel), traceModel);

		// Purge artifacts
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactMetaModelAdapter artifactAdapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		List<EObject> allArtifacts = artifactAdapter.getAllArtifacts(artifactModel);
		ArtifactWrapperContainer container = (ArtifactWrapperContainer) artifactModel;
		TransactionalEditingDomain editingDomain = EditingDomainHelper.getEditingDomain();
		// We're saving the trace model and the artifact model in the same transaction
		Command cmd = new RecordingCommand(editingDomain, "Purge artifacts") {
			@Override
			protected void doExecute() {
				container.getArtifacts().removeAll(allArtifacts);
			}
		};

		try {
			((TransactionalCommandStack) editingDomain.getCommandStack()).execute(cmd, null); // default options
		} catch (RollbackException e) {
			throw new IllegalStateException("Adding a trace link was rolled back.", e);
		} catch (InterruptedException e) {
			throw new IllegalStateException("Adding a trace link was interrupted.", e);
		}
	}
}
