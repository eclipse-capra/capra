/*******************************************************************************
 *  Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *      Chalmers|Gothenburg University and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.testsuite;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapper;
import org.eclipse.capra.GenericTraceMetaModel.GenericTraceModel;
import org.eclipse.capra.GenericTraceMetaModel.RelatedTo;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.handler.cdt.CDTHandler;
import org.eclipse.capra.handler.jdt.JavaElementHandler;
import org.eclipse.capra.ui.handlers.TraceCreationHandler;
import org.eclipse.capra.ui.plantuml.DisplayTracesHandler;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * A helper class for writing JUnit tests for the Capra tool.
 */
@SuppressWarnings("restriction")
public class TestHelper {

	/**
	 * ID of Capra custom marker for reporting a generic problem.
	 */
	public static final String CAPRA_PROBLEM_MARKER_ID = "org.eclipse.capra.ui.notification.capraProblemMarker";

	/**
	 * Creates an empty project
	 * 
	 * @param projectName
	 *            the name of the project
	 * @throws CoreException
	 */
	public static void createSimpleProject(String projectName) throws CoreException {
		IProject project = getProject(projectName);

		IProgressMonitor progressMonitor = new NullProgressMonitor();
		project.create(progressMonitor);
		project.open(progressMonitor);
	}

	/**
	 * Creates a Java project and a Java class declaration inside it.
	 * 
	 * @param projectName
	 *            the name of the project
	 * @return the created Java class
	 * @throws CoreException
	 */
	public static IType createJavaProjectWithASingleJavaClass(String projectName) throws CoreException {
		IProject project = getProject(projectName);

		// Create project
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		project.create(progressMonitor);
		project.open(progressMonitor);

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

		// Create a src file
		IFolder sourceFolder = project.getFolder("src");
		sourceFolder.create(false, true, null);
		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(sourceFolder);
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = JavaCore.newSourceEntry(root.getPath());
		javaProject.setRawClasspath(newEntries, null);

		IPackageFragment pack = javaProject.getPackageFragmentRoot(sourceFolder)
				.createPackageFragment("org.eclipse.capra.test", false, null);

		StringBuffer buffer = new StringBuffer();
		buffer.append("package " + pack.getElementName() + ";\n");
		buffer.append("\n");
		buffer.append("public class TestClass { public void doNothing(){ } }");

		ICompilationUnit icu = pack.createCompilationUnit("TestClass.java", buffer.toString(), false, null);
		return icu.getType("TestClass");
	}

	/**
	 * Clears the active workspace by deleting all the contents.
	 * 
	 * @throws CoreException
	 */
	public static void clearWorkspace() throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (IProject p : root.getProjects())
			p.delete(true, new NullProgressMonitor());
	}

	/**
	 * Checks if the project with the provided name exists.
	 * 
	 * @param projectName
	 *            the name of the project
	 * @return true if the project exists in the active workspace, false
	 *         otherwise
	 */
	public static boolean projectExists(String projectName) {
		return getProject(projectName).exists();
	}

	/**
	 * Returns a handle to the project resource with the given name.
	 * 
	 * @param projectName
	 *            the name of the project
	 * @return a handle to the project resource
	 */
	public static IProject getProject(String projectName) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		return root.getProject(projectName);
	}

	/**
	 * Creates an empty Ecore model.
	 * 
	 * @param name
	 *            the name of the model
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
	 * @param p
	 *            an Ecore model
	 * @param name
	 *            the name of the created EClass entity
	 */
	public static void createEClassInEPackage(EPackage p, String name) {
		EClass c = EcoreFactory.eINSTANCE.createEClass();
		c.setName(name);
		p.getEClassifiers().add(c);
	}

	/**
	 * Persists (saves) the provided Ecore model in the specified project.
	 * 
	 * @param project
	 *            a handle to the project in which the model is to be persisted
	 * @param pack
	 *            the Ecore model to be persisted
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
	 * @param project
	 *            the project containing the model
	 * @param p
	 *            the name of the model
	 * @param rs
	 *            the provided ResourceSet instance
	 * @return an Ecore model entity
	 * @throws IOException
	 */
	public static EPackage load(IProject project, String p, ResourceSet rs) throws IOException {
		URI path = URI.createFileURI(project.getLocation().toString() + "/" + p);
		return (EPackage) rs.getResource(path, true).getContents().get(0);
	}

	/**
	 * Creates a trace between the objects that are in the Selection view.
	 * 
	 * @param traceType
	 *            the type of the trace that is to connect the objects
	 */
	public static void createTraceForCurrentSelectionOfType(EClass traceType) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		TraceCreationHandler handler = new TraceCreationHandler();
		handler.createTrace(window, (traceTypes, selection) -> {
			if (traceTypes.contains(traceType))
				return Optional.of(traceType);
			else
				return Optional.empty();
		});
	}

	/**
	 * Checks if there is a trace between the provided EObjects.
	 * 
	 * @param a
	 *            first EObject
	 * @param b
	 *            second EObject
	 * @return true if a trace exists between the two objects, false otherwise
	 */
	public static boolean thereIsATraceBetween(EObject a, EObject b) {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
		return traceAdapter.isThereATraceBetween(a, b,
				persistenceAdapter.getTraceModel(a.eResource().getResourceSet()));
	}

	/**
	 * Checks if there is a trace between the provided EObject and Java element.
	 * 
	 * @param a
	 *            EObject element
	 * @param b
	 *            Java element
	 * @return true if a trace exists between the two objects, false otherwise
	 */
	public static boolean thereIsATraceBetween(EObject a, IJavaElement b) {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();

		List<Connection> connected = traceAdapter.getConnectedElements(a,
				persistenceAdapter.getTraceModel(a.eResource().getResourceSet()));

		return connected.stream().filter(o -> {
			List<EObject> objects = o.getTargets();
			for (EObject obj : objects) {
				if (obj instanceof ArtifactWrapper) {
					ArtifactWrapper wrapper = (ArtifactWrapper) obj;
					if ((wrapper.getArtifactHandler().equals(JavaElementHandler.class.getName()))) {
						return (wrapper.getUri().equals(b.getHandleIdentifier()));
					}
				}
			}

			return false;
		}).findAny().isPresent();

	}

	/**
	 * Checks if there is a trace between the provided EObject and C or C++
	 * element.
	 * 
	 * @param a
	 *            EObject element
	 * @param b
	 *            C or C++ element
	 * @return true if a trace exists between the two objects, false otherwise
	 */
	public static boolean thereIsATraceBetween(EObject a, ICElement b) {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();

		List<Connection> connected = traceAdapter.getConnectedElements(a,
				persistenceAdapter.getTraceModel(a.eResource().getResourceSet()));

		return connected.stream().filter(o -> {
			List<EObject> objects = o.getTargets();
			for (EObject obj : objects) {
				if (obj instanceof ArtifactWrapper) {
					ArtifactWrapper wrapper = (ArtifactWrapper) obj;
					if ((wrapper.getArtifactHandler().equals(CDTHandler.class.getName()))) {
						return (wrapper.getUri().equals(b.getHandleIdentifier()));
					}
				}
			}
			return false;
		}).findAny().isPresent();

	}

	/**
	 * Checks if there is a trace between the provided resources.
	 * 
	 * @param r1
	 *            first resource
	 * @param r2
	 *            second resource
	 * @return true, if there is a trace link between the resources, false
	 *         otherwise
	 */
	public static boolean thereIsATraceBetween(IResource r1, IResource r2) {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();

		EObject tracemodel = persistenceAdapter.getTraceModel(new ResourceSetImpl());
		GenericTraceModel gtm = (GenericTraceModel) tracemodel;

		EList<RelatedTo> traces = gtm.getTraces();
		if (traces.isEmpty())
			return false;

		IPath pathR1 = r1.getFullPath();
		IPath pathR2 = r2.getFullPath();

		for (RelatedTo trace : traces) {
			EList<EObject> items = trace.getItem();
			List<IPath> uris = new ArrayList<IPath>();
			items.forEach(item -> {
				EList<EStructuralFeature> structFeatures = item.eClass().getEAllStructuralFeatures();
				for (EStructuralFeature esf : structFeatures) {
					if (esf.getName().equals("uri")) {
						uris.add(new Path((String) item.eGet(esf)));
					}
				}
			});

			if (uris.contains(pathR1) && uris.contains(pathR2))
				return true;
		}
		return false;
	}

	/**
	 * Creates an empty C or C++ project.
	 * 
	 * @param projectName
	 *            the name of the project to be created
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
	 * @param fileName
	 *            the name of the C source file to be created in the project
	 * @param cProject
	 *            the project in which the file is to be created
	 * @return the created TranslationUnit
	 * @throws CoreException
	 */
	public static ITranslationUnit createCSourceFileInProject(String fileName, ICProject cProject)
			throws CoreException {

		StringBuffer buffer = new StringBuffer();
		buffer.append("#include <stdio.h>\n");
		buffer.append("\n");
		buffer.append("int main() {\n");
		buffer.append("\tprintf(\"Hello, World!\");\n");
		buffer.append("\treturn 0;\n");
		buffer.append("}\n");
		IFile cSourceFile = cProject.getProject().getFile(fileName);
		cSourceFile.create(new ByteArrayInputStream(buffer.toString().getBytes()), true, new NullProgressMonitor());

		return ((ISourceRoot) (cProject.getChildren()[0])).getTranslationUnits()[0];
	}

	/**
	 * Creates an empty file in the project with the provided name.
	 * 
	 * @param fileName
	 *            the name of the created file
	 * @param projectName
	 *            the name of the project in which the file is to be created
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
		DisplayTracesHandler.setTraceViewTransitive(true);
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
	}
}
