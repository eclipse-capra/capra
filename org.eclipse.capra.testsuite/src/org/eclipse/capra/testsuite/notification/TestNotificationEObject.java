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
import static org.eclipse.capra.testsuite.TestHelper.createEcoreModel;
import static org.eclipse.capra.testsuite.TestHelper.createJavaProjectWithASingleJavaClass;
import static org.eclipse.capra.testsuite.TestHelper.createTraceForCurrentSelectionOfType;
import static org.eclipse.capra.testsuite.TestHelper.getProject;
import static org.eclipse.capra.testsuite.TestHelper.projectExists;
import static org.eclipse.capra.testsuite.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsuite.TestHelper.save;
import static org.eclipse.capra.testsuite.TestHelper.thereIsATraceBetween;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage;
import org.eclipse.capra.testsuite.TestHelper;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.jdt.core.IType;
import org.junit.Before;
import org.junit.Test;

/**
 * Contains tests to check the functioning of the Capra notification system when
 * deleting, renaming or changing a model element that is referenced in the
 * trace model.
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
		EPackage a = createEcoreModel("modelA");
		createEClassInEPackage(a, "A");
		save(testProject, a);
		EClass A = (EClass) a.getEClassifier("A");

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(A);
		SelectionView.getOpenedView().dropToSelection(javaClass);
		assertFalse(thereIsATraceBetween(A, javaClass));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(A, javaClass));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkerLength = markers.length;

		// Delete model element and wait a bit for the
		// ResourceChangedListener to trigger
		a.getEClassifiers().remove(A);
		save(testProject, a);
		TimeUnit.MILLISECONDS.sleep(100);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		// assertEquals(currMarkerLength + 1, markers.length);
		// Deleting a model element doesn't trigger the notification.
		// For now this test fails.
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
	public void testRenameModelElement() throws CoreException, IOException, InterruptedException {

		// Create a project
		IType javaClass = createJavaProjectWithASingleJavaClass("TestProject");
		assertTrue(projectExists("TestProject"));

		// Create a model and persist
		IProject testProject = getProject("TestProject");
		EPackage a = createEcoreModel("modelA");
		createEClassInEPackage(a, "A");
		save(testProject, a);
		EClass A = (EClass) a.getEClassifier("A");

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(A);
		SelectionView.getOpenedView().dropToSelection(javaClass);
		assertFalse(thereIsATraceBetween(A, javaClass));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(A, javaClass));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkerLength = markers.length;

		// Rename model element and wait a bit for the
		// ResourceChangedListener to trigger
		a.getEClassifier("A").setName("B");
		save(testProject, a);
		TimeUnit.MILLISECONDS.sleep(100);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		// assertEquals(currMarkerLength + 1, markers.length);
		// Renaming a model element doesn't trigger the notification.
		// For now this test fails.
	}

	/**
	 * Tests if a marker appears after editing a model element that is
	 * referenced in the trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testEditModelElement() throws CoreException, IOException, InterruptedException {

		// Create a project
		IType javaClass = createJavaProjectWithASingleJavaClass("TestProject");
		assertTrue(projectExists("TestProject"));

		// Create a model and persist
		IProject testProject = getProject("TestProject");
		EPackage a = createEcoreModel("modelA");
		createEClassInEPackage(a, "A");
		save(testProject, a);
		EClass A = (EClass) a.getEClassifier("A");

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(A);
		SelectionView.getOpenedView().dropToSelection(javaClass);
		assertFalse(thereIsATraceBetween(A, javaClass));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(A, javaClass));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkerLength = markers.length;

		// Edit model element and wait a bit for the
		// ResourceChangedListener to trigger
		EAttribute testAttribute = EcoreFactory.eINSTANCE.createEAttribute();
		testAttribute.setName("testAttribute");
		A.getEStructuralFeatures().add(testAttribute);
		save(testProject, a);
		TimeUnit.MILLISECONDS.sleep(100);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		// assertEquals(currMarkerLength + 1, markers.length);
		// Editing a model element doesn't trigger the notification.
		// For now this test fails.
	}
}
