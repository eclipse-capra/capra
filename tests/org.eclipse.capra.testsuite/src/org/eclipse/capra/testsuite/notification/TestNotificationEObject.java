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

package org.eclipse.capra.testsuite.notification;

import static org.eclipse.capra.testsuite.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsuite.TestHelper.createEClassInEPackage;
import static org.eclipse.capra.testsuite.TestHelper.createEPackageInEPackage;
import static org.eclipse.capra.testsuite.TestHelper.createEcoreModel;
import static org.eclipse.capra.testsuite.TestHelper.createJavaProjectWithASingleJavaClass;
import static org.eclipse.capra.testsuite.TestHelper.createTraceForCurrentSelectionOfType;
import static org.eclipse.capra.testsuite.TestHelper.getProject;
import static org.eclipse.capra.testsuite.TestHelper.projectExists;
import static org.eclipse.capra.testsuite.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsuite.TestHelper.save;
import static org.eclipse.capra.testsuite.TestHelper.thereIsATraceBetween;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage;
import org.eclipse.capra.testsuite.TestHelper;
import org.eclipse.capra.testsuite.TestRetry;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Contains tests to check the functioning of the Capra notification system when
 * deleting, renaming or moving a model element that is referenced in the trace
 * model.
 * 
 * @author Dusan Kalanj
 *
 */
public class TestNotificationEObject {

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
	}

	@Rule
	public TestRetry retry = new TestRetry(5);

	/**
	 * Tests if a marker appears after deleting a model element that is
	 * referenced in the trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testDeleteModelElement() throws CoreException, IOException, InterruptedException {

		// Create a project
		IType javaClass = createJavaProjectWithASingleJavaClass("TestProject");
		assertTrue(projectExists("TestProject"));

		// Create a model and persist
		IProject testProject = getProject("TestProject");
		EPackage modelA = createEcoreModel("modelA");
		createEClassInEPackage(modelA, "classA");
		save(testProject, modelA);
		EClass classA = (EClass) modelA.getEClassifier("classA");

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(classA);
		SelectionView.getOpenedView().dropToSelection(javaClass);
		assertFalse(thereIsATraceBetween(classA, javaClass));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(classA, javaClass));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkerLength = markers.length;

		// Open an EMF editor
		URI resourceUri = modelA.eResource().getURI();
		File fileToOpen = new File(resourceUri.toFileString());
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
		IWorkbenchPage wp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		wp.closeAllEditors(false);
		IEditorPart editor = IDE.openEditorOnFileStore(wp, fileStore);

		// Obtain the resource of the editor and trace model
		IEditingDomainProvider domainProvider = editor.getAdapter(IEditingDomainProvider.class);
		Resource editorResource = domainProvider.getEditingDomain().getResourceSet().getResources().get(0);
		Resource traceModelResource = modelA.eResource();

		// Find the traced item from the traceModel in the editor's resource
		EClass tracedItem = (EClass) editorResource.getEObject(traceModelResource.getURIFragment(classA));

		// Delete the item and wait a bit for the ModelChangeListener to trigger
		EcoreUtil.delete(tracedItem);
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength + 1, markers.length);

		// Check if the marker signals the correct change
		IMarker marker = markers[markers.length - 1];
		String issue = (String) marker.getAttribute("issueType");
		assertEquals(issue, "deleted");

		// Undo the operation
		EPackage tracedItemParent = (EPackage) editorResource.getEObject(traceModelResource.getURIFragment(modelA));
		tracedItemParent.getEClassifiers().add(tracedItem);
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if marker is gone
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength, markers.length);
	}

	/**
	 * Tests if a marker appears after renaming a model element that is
	 * referenced in the trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	// TODO Strange bug if the name of the item is just one character. In that
	// case, EMF treats a rename operation as a delete+add operation. If it is
	// more characters, it treats it as a normal property set operation.
	public void testRenameModelElement() throws CoreException, IOException, InterruptedException {

		// Create a project
		IType javaClass = createJavaProjectWithASingleJavaClass("TestProject");
		assertTrue(projectExists("TestProject"));

		// Create a model and persist
		IProject testProject = getProject("TestProject");
		EPackage modelA = createEcoreModel("modelA");
		createEClassInEPackage(modelA, "classA");
		save(testProject, modelA);
		EClass classA = (EClass) modelA.getEClassifier("classA");

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(classA);
		SelectionView.getOpenedView().dropToSelection(javaClass);
		assertFalse(thereIsATraceBetween(classA, javaClass));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(classA, javaClass));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkerLength = markers.length;

		// Open an EMF editor
		URI resourceUri = modelA.eResource().getURI();
		File fileToOpen = new File(resourceUri.toFileString());
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
		IWorkbenchPage wp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		wp.closeAllEditors(false);
		IEditorPart editor = IDE.openEditorOnFileStore(wp, fileStore);

		// Obtain the resource of the editor and trace model
		IEditingDomainProvider domainProvider = editor.getAdapter(IEditingDomainProvider.class);
		Resource editorResource = domainProvider.getEditingDomain().getResourceSet().getResources().get(0);
		Resource traceModelResource = modelA.eResource();

		// Find the traced item from the traceModel in the editor's resource
		EObject tracedItem = editorResource.getEObject(traceModelResource.getURIFragment(classA));

		// Rename the item and wait a bit for the ModelChangeListener to trigger
		((EClass) tracedItem).setName("classB");
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength + 1, markers.length);

		// Check if the marker signals the correct change
		IMarker marker = markers[markers.length - 1];
		String issue = (String) marker.getAttribute("issueType");
		assertEquals(issue, "renamed");

		// Undo the operation
		((EClass) tracedItem).setName("classA");
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if marker is gone
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength, markers.length);
	}

	/**
	 * Tests if a marker appears after moving a model element that is referenced
	 * in the trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testMoveModelElement() throws CoreException, IOException, InterruptedException {

		// Create a project
		IType javaClass = createJavaProjectWithASingleJavaClass("TestProject");
		assertTrue(projectExists("TestProject"));

		// Create a model and persist
		IProject testProject = getProject("TestProject");
		EPackage modelA = createEcoreModel("modelA");
		createEClassInEPackage(modelA, "classA");
		createEPackageInEPackage(modelA, "packageA");
		save(testProject, modelA);
		EClass classA = (EClass) modelA.getEClassifier("classA");
		EPackage packageA = (EPackage) modelA.getESubpackages().get(0);

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(classA);
		SelectionView.getOpenedView().dropToSelection(javaClass);
		assertFalse(thereIsATraceBetween(classA, javaClass));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(classA, javaClass));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkerLength = markers.length;

		// Open an EMF editor
		URI resourceUri = modelA.eResource().getURI();
		File fileToOpen = new File(resourceUri.toFileString());
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
		IWorkbenchPage wp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		wp.closeAllEditors(false);
		IEditorPart editor = IDE.openEditorOnFileStore(wp, fileStore);

		// Obtain the resource of the editor and trace model
		IEditingDomainProvider domainProvider = editor.getAdapter(IEditingDomainProvider.class);
		Resource editorResource = domainProvider.getEditingDomain().getResourceSet().getResources().get(0);
		Resource traceModelResource = modelA.eResource();

		// Find the items from the traceModel in the editor's resource
		EClass tracedItem = (EClass) editorResource.getEObject(traceModelResource.getURIFragment(classA));
		EPackage oldPackage = (EPackage) editorResource.getEObject(traceModelResource.getURIFragment(modelA));
		EPackage newPackage = (EPackage) editorResource.getEObject(traceModelResource.getURIFragment(packageA));

		// Move the item and wait a bit for the ModelChangeListener to trigger
		newPackage.getEClassifiers().add(tracedItem);
		oldPackage.getEClassifiers().remove(tracedItem);
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength + 1, markers.length);

		// Check if the marker signals the correct change
		IMarker marker = markers[markers.length - 1];
		String issue = (String) marker.getAttribute("issueType");
		assertEquals(issue, "moved");

		// Undo the operation
		oldPackage.getEClassifiers().add(tracedItem);
		newPackage.getEClassifiers().remove(tracedItem);
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if marker is gone
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength, markers.length);
	}

	/**
	 * Tests if a marker appears after deleting a parent of the item that is
	 * referenced in the trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testDeleteParentModelElement() throws CoreException, IOException, InterruptedException {

		// Create a project
		IType javaClass = createJavaProjectWithASingleJavaClass("TestProject");
		assertTrue(projectExists("TestProject"));

		// Create a model and persist
		IProject testProject = getProject("TestProject");
		EPackage modelA = createEcoreModel("modelA");
		createEPackageInEPackage(modelA, "packageA");
		EPackage packageA = (EPackage) modelA.getESubpackages().get(0);
		createEClassInEPackage(packageA, "classA");
		EClass classA = (EClass) packageA.getEClassifier("classA");
		save(testProject, modelA);

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(classA);
		SelectionView.getOpenedView().dropToSelection(javaClass);
		assertFalse(thereIsATraceBetween(classA, javaClass));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(classA, javaClass));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkerLength = markers.length;

		// Open an EMF editor
		URI resourceUri = modelA.eResource().getURI();
		File fileToOpen = new File(resourceUri.toFileString());
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
		IWorkbenchPage wp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		wp.closeAllEditors(false);
		IEditorPart editor = IDE.openEditorOnFileStore(wp, fileStore);

		// Obtain the resource of the editor and trace model
		IEditingDomainProvider domainProvider = editor.getAdapter(IEditingDomainProvider.class);
		Resource editorResource = domainProvider.getEditingDomain().getResourceSet().getResources().get(0);
		Resource traceModelResource = modelA.eResource();

		// Find the parent of the traced item from the traceModel in the
		// editor's resource
		EPackage tracedItemParent = (EPackage) editorResource.getEObject(traceModelResource.getURIFragment(packageA));

		// Delete parent and wait a bit for the ModelChangeListener to trigger
		EcoreUtil.delete(tracedItemParent);
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength + 1, markers.length);

		// Check if the marker signals the correct change
		IMarker marker = markers[markers.length - 1];
		String issue = (String) marker.getAttribute("issueType");
		assertEquals(issue, "deleted");

		// Undo the operation
		EPackage parentOfTracedItemParent = (EPackage) editorResource
				.getEObject(traceModelResource.getURIFragment(modelA));
		parentOfTracedItemParent.getESubpackages().add(tracedItemParent);
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if marker is gone
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength, markers.length);
	}

	/**
	 * Tests if a marker appears after moving a parent of the item that is
	 * referenced in the trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testMoveParentModelElement() throws CoreException, IOException, InterruptedException {

		// Create a project
		IType javaClass = createJavaProjectWithASingleJavaClass("TestProject");
		assertTrue(projectExists("TestProject"));

		// Create a model and persist
		IProject testProject = getProject("TestProject");
		EPackage modelA = createEcoreModel("modelA");
		createEPackageInEPackage(modelA, "packageA");
		createEPackageInEPackage(modelA, "packageB");
		EPackage packageA = (EPackage) modelA.getESubpackages().get(0);
		EPackage packageB = (EPackage) modelA.getESubpackages().get(1);
		createEClassInEPackage(packageA, "A");
		EClass classA = (EClass) packageA.getEClassifier("A");
		save(testProject, modelA);

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(classA);
		SelectionView.getOpenedView().dropToSelection(javaClass);
		assertFalse(thereIsATraceBetween(classA, javaClass));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(classA, javaClass));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkerLength = markers.length;

		// Open an EMF editor
		URI resourceUri = modelA.eResource().getURI();
		File fileToOpen = new File(resourceUri.toFileString());
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
		IWorkbenchPage wp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		wp.closeAllEditors(false);
		IEditorPart editor = IDE.openEditorOnFileStore(wp, fileStore);

		// Obtain the resource of the editor and trace model
		IEditingDomainProvider domainProvider = editor.getAdapter(IEditingDomainProvider.class);
		Resource editorResource = domainProvider.getEditingDomain().getResourceSet().getResources().get(0);
		Resource traceModelResource = modelA.eResource();

		// Find the packages from the traceModel in the editor's resource
		EPackage tracedItemParent = (EPackage) editorResource.getEObject(traceModelResource.getURIFragment(packageA));
		EPackage oldParentOfParent = (EPackage) editorResource.getEObject(traceModelResource.getURIFragment(modelA));
		EPackage newParentOfParent = (EPackage) editorResource.getEObject(traceModelResource.getURIFragment(packageB));

		// Move the parent and wait a bit for the ModelChangeListener to trigger
		newParentOfParent.getESubpackages().add(tracedItemParent);
		oldParentOfParent.getESubpackages().remove(tracedItemParent);
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength + 1, markers.length);

		// Check if the marker signals the correct change
		IMarker marker = markers[markers.length - 1];
		String issue = (String) marker.getAttribute("issueType");
		assertEquals(issue, "moved");

		// Undo the operation
		oldParentOfParent.getESubpackages().add(tracedItemParent);
		newParentOfParent.getESubpackages().remove(tracedItemParent);
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if marker is gone
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength, markers.length);
	}
}
