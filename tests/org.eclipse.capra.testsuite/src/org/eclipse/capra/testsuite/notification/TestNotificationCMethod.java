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
import static org.eclipse.capra.testsuite.TestHelper.createCDTProject;
import static org.eclipse.capra.testsuite.TestHelper.createCSourceFileInProject;
import static org.eclipse.capra.testsuite.TestHelper.createEClassInEPackage;
import static org.eclipse.capra.testsuite.TestHelper.createTraceForCurrentSelectionOfType;
import static org.eclipse.capra.testsuite.TestHelper.getProject;
import static org.eclipse.capra.testsuite.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsuite.TestHelper.save;
import static org.eclipse.capra.testsuite.TestHelper.thereIsATraceBetween;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage;
import org.eclipse.capra.testsuite.TestHelper;
import org.eclipse.capra.testsuite.TestRetry;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.IFunction;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Contains tests to check the functioning of the Capra C notification system.
 * 
 * @author Dusan Kalanj
 *
 */
public class TestNotificationCMethod {

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
	}

	@Rule
	public TestRetry retry = new TestRetry(5);

	/**
	 * Tests if a marker appears after deleting a C method that is referenced in
	 * the trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws BuildException
	 */
	@Test
	public void testDeleteMethod() throws CoreException, IOException, InterruptedException, BuildException {

		// Create a C project with a single source file
		ICProject cProject = createCDTProject("TestProject");
		ITranslationUnit cSourceFile = createCSourceFileInProject("CSource.c", cProject);
		IFunction method = (IFunction) cSourceFile.getChildrenOfType(74).get(0);

		// Create a model
		IProject testProject = getProject("TestProject");
		EPackage a = TestHelper.createEcoreModel("modelA");
		createEClassInEPackage(a, "A");
		save(testProject, a);
		EClass A = (EClass) a.getEClassifier("A");

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(method);
		SelectionView.getOpenedView().dropToSelection(A);
		assertFalse(thereIsATraceBetween(A, method));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(A, method));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkerLength = markers.length;

		// Delete method and wait a bit for the ResourceChangedListener to
		// trigger
		method.delete(true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength + 1, markers.length);
		assertEquals(markers[0].getAttribute("issueType"), "deleted");
		currMarkerLength = markers.length;

		// Undo operation
		cSourceFile.delete(true, new NullProgressMonitor());
		createCSourceFileInProject("CSource.c", cProject);
		TimeUnit.MILLISECONDS.sleep(300);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength - 1, markers.length);
	}

	/**
	 * Tests if a marker appears after renaming a C method that is referenced in
	 * the trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws BuildException
	 */
	@Test
	public void testRenameMethod() throws CoreException, IOException, InterruptedException, BuildException {

		// Create a C project with a single source file
		ICProject cProject = createCDTProject("TestProject");
		ITranslationUnit cSourceFile = createCSourceFileInProject("CSource.c", cProject);
		IFunction method = (IFunction) cSourceFile.getChildrenOfType(74).get(0);

		// Create a model
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

		// Rename method and wait a bit for the ResourceChangedListener to
		// trigger
		method.rename("noLongerMain", true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength + 1, markers.length);
		assertEquals(markers[0].getAttribute("issueType"), "changed");
		currMarkerLength = markers.length;

		// Undo operation
		cSourceFile.delete(true, new NullProgressMonitor());
		createCSourceFileInProject("CSource.c", cProject);
		TimeUnit.MILLISECONDS.sleep(300);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength - 1, markers.length);
	}

	/**
	 * Tests if a marker appears after deleting a project that is referenced by
	 * the trace model and contains a method that is referenced as well. Two
	 * markers should appear, one for each element.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws BuildException
	 */
	@Test
	public void testDeleteProjectWithMethod() throws CoreException, InterruptedException, BuildException, IOException {

		// Create a C project with a single source file that contains a method
		// declaration
		ICProject cProject = createCDTProject("TestProject");
		ITranslationUnit cSourceFile = createCSourceFileInProject("CSource.c", cProject);
		IFunction method = (IFunction) cSourceFile.getChildrenOfType(74).get(0);

		// Create a model
		EPackage a = TestHelper.createEcoreModel("modelA");
		createEClassInEPackage(a, "A");
		save(cProject.getProject(), a);
		EClass A = (EClass) a.getEClassifier("A");

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(A);
		SelectionView.getOpenedView().dropToSelection(method);
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkerLength = markers.length;

		// Delete project and wait a bit for the changeListener to trigger
		getProject("TestProject").delete(true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if there are two new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength + 1, markers.length);
		assertEquals(markers[0].getAttribute("issueType"), "deleted");
		currMarkerLength = markers.length;

		// Undo operation
		cProject = createCDTProject("TestProject");
		createCSourceFileInProject("CSource.c", cProject);
		TimeUnit.MILLISECONDS.sleep(300);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength - 1, markers.length);
	}
}