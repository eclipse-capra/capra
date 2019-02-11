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
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage;
import org.eclipse.capra.testsuite.TestHelper;
import org.eclipse.capra.testsuite.TestRetry;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Contains tests to check the functioning of the Capra notification system when
 * deleting, renaming, or changing a Java method that is referenced in the trace
 * model.
 * 
 * @author Dusan Kalanj
 *
 */
public class TestNotificationJavaMethod {

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
	}

	@Rule
	public TestRetry retry = new TestRetry(5);

	/**
	 * Tests if a marker appears after deleting a Java method that is referenced
	 * in the trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testDeleteMethod() throws CoreException, IOException, InterruptedException {

		// Create a project with a Java source file
		IType javaClass = createJavaProjectWithASingleJavaClass("TestProject");
		String classSource = javaClass.getSource();
		IMethod method = (IMethod) javaClass.getChildren()[0];
		assertTrue(projectExists("TestProject"));

		// Create a model and persist
		IProject testProject = getProject("TestProject");
		EPackage a = TestHelper.createEcoreModel("modelA");
		createEClassInEPackage(a, "A");
		save(testProject, a);
		EClass A = (EClass) a.getEClassifier("A");

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(A);
		SelectionView.getOpenedView().dropToSelection(method);
		assertFalse(thereIsATraceBetween(A, method));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(A, method));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkerLength = markers.length;

		// Delete method and wait a bit for the
		// ResourceChangedListener to trigger
		method.delete(true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength + 1, markers.length);
		assertEquals(markers[0].getAttribute("issueType"), "deleted");
		currMarkerLength = markers.length;

		// Undo operation
		File classFile = javaClass.getResource().getLocation().makeAbsolute().toFile();
		PrintWriter writer = new PrintWriter(classFile);
		writer.write(classSource);
		writer.close();		
		root.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(3000);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength - 1, markers.length);
	}

	/**
	 * Tests if a marker appears after renaming a Java method that is referenced
	 * in the trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testRenameMethod() throws CoreException, IOException, InterruptedException {

		// Create a project with a Java source file
		IType javaClass = createJavaProjectWithASingleJavaClass("TestProject");
		IMethod method = (IMethod) javaClass.getChildren()[0];
		assertTrue(projectExists("TestProject"));

		// Create a model and persist
		IProject testProject = getProject("TestProject");
		EPackage a = TestHelper.createEcoreModel("modelA");
		createEClassInEPackage(a, "A");
		save(testProject, a);
		EClass A = (EClass) a.getEClassifier("A");

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(A);
		SelectionView.getOpenedView().dropToSelection(method);
		assertFalse(thereIsATraceBetween(A, method));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(A, method));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkerLength = markers.length;

		// Rename method and wait a bit for the
		// ResourceChangedListener to trigger
		String oldName = method.getElementName();
		method.rename("stillDoNothing", true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength + 1, markers.length);
		assertEquals(markers[0].getAttribute("issueType"), "deleted");
		currMarkerLength = markers.length;
		
		// Undo operation
		method = (IMethod) javaClass.getChildren()[0];
		method.rename(oldName, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength - 1, markers.length);
	}

	/**
	 * Tests if a marker appears after editing a Java method that is referenced
	 * in the trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testDeleteProjectOfMethod() throws CoreException, IOException, InterruptedException {

		// Create a project with a Java source file
		IType javaClass = createJavaProjectWithASingleJavaClass("TestProject");
		IMethod method = (IMethod) javaClass.getChildren()[0];
		assertTrue(projectExists("TestProject"));

		// Create a model and persist
		IProject testProject = getProject("TestProject");
		EPackage a = TestHelper.createEcoreModel("modelA");
		createEClassInEPackage(a, "A");
		save(testProject, a);
		EClass A = (EClass) a.getEClassifier("A");

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(A);
		SelectionView.getOpenedView().dropToSelection(method);
		assertFalse(thereIsATraceBetween(A, method));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(A, method));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkerLength = markers.length;

		// Delete method and wait a bit for the
		// ResourceChangedListener to trigger
		testProject.delete(true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength + 1, markers.length);
		assertEquals(markers[0].getAttribute("issueType"), "deleted");
		currMarkerLength = markers.length;
		
		// Undo operation
		createJavaProjectWithASingleJavaClass("TestProject");
		TimeUnit.MILLISECONDS.sleep(300);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength - 1, markers.length);
	}
}
