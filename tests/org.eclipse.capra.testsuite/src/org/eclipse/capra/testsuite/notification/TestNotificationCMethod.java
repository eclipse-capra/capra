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

package org.eclipse.capra.testsuite.notification;

import static org.eclipse.capra.testsupport.TestHelper.UI_REACTION_WAITING_TIME;
import static org.eclipse.capra.testsupport.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsupport.TestHelper.createCDTProject;
import static org.eclipse.capra.testsupport.TestHelper.createCSourceFileInProject;
import static org.eclipse.capra.testsupport.TestHelper.createEClassInEPackage;
import static org.eclipse.capra.testsupport.TestHelper.createTraceForCurrentSelectionOfType;
import static org.eclipse.capra.testsupport.TestHelper.getProject;
import static org.eclipse.capra.testsupport.TestHelper.purgeModels;
import static org.eclipse.capra.testsupport.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsupport.TestHelper.save;
import static org.eclipse.capra.testsupport.TestHelper.thereIsATraceBetween;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.testsupport.TestHelper;
import org.eclipse.capra.testsupport.TestRetry;
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

	private static final String MARKER_ATTRIBUTE_ISSUE_TYPE = "issueType";
	private static final int IFUNCTION_TYPE_ID = 74;

	private static final String CLASS_A_NAME = "A";
	private static final String MODEL_A_NAME = "modelA";
	private static final String FILE_NAME = "CSource.c";
	private static final String TEST_PROJECT_NAME = "TestProject";

	private static final int NUMBER_OF_RETRIES = 5;

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
		purgeModels();
	}

	@Rule
	public TestRetry retry = new TestRetry(NUMBER_OF_RETRIES);

	/**
	 * Tests if a marker appears after deleting a C method that is referenced in the
	 * trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws BuildException
	 */
	@Test
	public void testDeleteMethod() throws CoreException, IOException, InterruptedException, BuildException {

		// Create a C project with a single source file
		ICProject cProject = createCDTProject(TEST_PROJECT_NAME);
		ITranslationUnit cSourceFile = createCSourceFileInProject(FILE_NAME, cProject);
		IFunction method = (IFunction) cSourceFile.getChildrenOfType(IFUNCTION_TYPE_ID).get(0);

		// Create a model
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		save(testProject, a);
		EClass A = (EClass) a.getEClassifier(CLASS_A_NAME);

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(method);
		SelectionView.getOpenedView().dropToSelection(A);
		assertFalse(thereIsATraceBetween(method, A));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(method, A));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkerLength = markers.length;

		// Delete method and wait a bit for the ResourceChangedListener to
		// trigger
		method.delete(true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength + 1, markers.length);
		assertEquals(markers[0].getAttribute(MARKER_ATTRIBUTE_ISSUE_TYPE), "deleted");
		currMarkerLength = markers.length;

		// Undo operation
		cSourceFile.delete(true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);
		createCSourceFileInProject(FILE_NAME, cProject);
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength - 1, markers.length);
	}

	/**
	 * Tests if a marker appears after renaming a C method that is referenced in the
	 * trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws BuildException
	 */
	@Test
	public void testRenameMethod() throws CoreException, IOException, InterruptedException, BuildException {

		// Create a C project with a single source file
		ICProject cProject = createCDTProject(TEST_PROJECT_NAME);
		ITranslationUnit cSourceFile = createCSourceFileInProject(FILE_NAME, cProject);
		IFunction method = (IFunction) cSourceFile.getChildrenOfType(74).get(0);

		// Create a model
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		save(testProject, a);
		EClass A = (EClass) a.getEClassifier(CLASS_A_NAME);

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(A);
		SelectionView.getOpenedView().dropToSelection(method);
		assertFalse(thereIsATraceBetween(A, method));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(A, method));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkerLength = markers.length;

		// Rename method and wait a bit for the ResourceChangedListener to
		// trigger
		method.rename("noLongerMain", true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength + 1, markers.length);
		assertEquals(markers[0].getAttribute(MARKER_ATTRIBUTE_ISSUE_TYPE), "changed");
		currMarkerLength = markers.length;

		// Undo operation
		cSourceFile.delete(true, new NullProgressMonitor());
		createCSourceFileInProject(FILE_NAME, cProject);
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength - 1, markers.length);
	}

	/**
	 * Tests if a marker appears after deleting a project that is referenced by the
	 * trace model and contains a method that is referenced as well. Two markers
	 * should appear, one for each element.
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
		ICProject cProject = createCDTProject(TEST_PROJECT_NAME);
		ITranslationUnit cSourceFile = createCSourceFileInProject(FILE_NAME, cProject);
		IFunction method = (IFunction) cSourceFile.getChildrenOfType(74).get(0);

		// Create a model
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		save(cProject.getProject(), a);
		EClass A = (EClass) a.getEClassifier(CLASS_A_NAME);

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(A);
		SelectionView.getOpenedView().dropToSelection(method);
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkerLength = markers.length;

		// Delete project and wait a bit for the changeListener to trigger
		getProject(TEST_PROJECT_NAME).delete(true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);

		// Check if there are two new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength + 1, markers.length);
		assertEquals(markers[0].getAttribute(MARKER_ATTRIBUTE_ISSUE_TYPE), "deleted");
		currMarkerLength = markers.length;

		// Undo operation
		cProject = createCDTProject(TEST_PROJECT_NAME);
		createCSourceFileInProject(FILE_NAME, cProject);
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkerLength - 1, markers.length);
	}
}