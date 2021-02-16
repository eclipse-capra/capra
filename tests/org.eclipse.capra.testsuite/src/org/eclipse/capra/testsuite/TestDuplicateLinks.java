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
package org.eclipse.capra.testsuite;

import static org.eclipse.capra.testsupport.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsupport.TestHelper.createEClassInEPackage;
import static org.eclipse.capra.testsupport.TestHelper.createEcoreModel;
import static org.eclipse.capra.testsupport.TestHelper.createSimpleProject;
import static org.eclipse.capra.testsupport.TestHelper.createTraceForCurrentSelectionOfType;
import static org.eclipse.capra.testsupport.TestHelper.getProject;
import static org.eclipse.capra.testsupport.TestHelper.load;
import static org.eclipse.capra.testsupport.TestHelper.projectExists;
import static org.eclipse.capra.testsupport.TestHelper.purgeModels;
import static org.eclipse.capra.testsupport.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsupport.TestHelper.save;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.core.helpers.TraceHelper;
import org.eclipse.capra.generic.tracemodel.RelatedTo;
import org.eclipse.capra.generic.tracemodel.TracemodelFactory;
import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.testsupport.TestHelper;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Before;
import org.junit.Test;

public class TestDuplicateLinks {
	private static final String CLASS_A_NAME = "A";
	private static final String CLASS_B_NAME = "B";

	private static final String MODEL_A_FILENAME = "modelA.ecore";
	private static final String MODEL_B_FILENAME = "modelB.ecore";
	private static final String MODEL_A_NAME = "modelA";
	private static final String MODEL_B_NAME = "modelB";

	private static final String TEST_PROJECT_NAME = "TestProject";
	private static final String T_LINK_NAME_A = "tlinkA";
	private static final String CLASS_C_NAME = "C";
	private static final String TRACE_MODEL_NAME = "traceModel";
	private static final String TRACE_MODEL_FILENAME = "traceModel.ecore";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
		purgeModels();
	}

	@Test
	public void testConnectionEqualsMethod() throws CoreException, IOException {
		// Create a project
		createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// Create an Ecore model with five three classes
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		createEClassInEPackage(a, CLASS_B_NAME);
		createEClassInEPackage(a, CLASS_C_NAME);

		// create a mock trace model with one trace type
		EPackage tm = TestHelper.createEcoreModel(TRACE_MODEL_NAME);
		createEClassInEPackage(tm, T_LINK_NAME_A);
		save(testProject, a);
		save(testProject, tm);

		// get existing trace type from trace model
		RelatedTo tlinkB = TracemodelFactory.eINSTANCE.createRelatedTo();

		// Load the models and select the classes
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, MODEL_A_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);

		EPackage _tm = load(testProject, TRACE_MODEL_FILENAME, rs);
		assertEquals(_tm.getName(), TRACE_MODEL_NAME);

		EClass classA = (EClass) _a.getEClassifier(CLASS_A_NAME);

		EClass classB = (EClass) _a.getEClassifier(CLASS_B_NAME);

		EClass classC = (EClass) _a.getEClassifier(CLASS_C_NAME);

		EClass tlinkA = (EClass) _tm.getEClassifier(T_LINK_NAME_A);

		List<EObject> origins = Arrays.asList(classA);
		List<EObject> targets = Arrays.asList(classB);

		// create a connection
		Connection con1 = new Connection(origins, targets, tlinkA);

		// create a second connection with the same content
		Connection con2 = new Connection(origins, targets, tlinkA);

		// check if the connections are equal
		assertEquals(con1, con2);
		assertEquals(con1.hashCode(), con2.hashCode());

		// change the order of artifacts
		Connection con3 = new Connection(targets, origins, tlinkA);

		// check if the connections are equal
		assertEquals(con1, con3);
		assertEquals(con2, con3);

		// create a new connection with the second trace type
		Connection con4 = new Connection(targets, origins, tlinkB);

		// check that the connections are not equal
		assertNotEquals(con3, con4);
		assertNotEquals(con3.hashCode(), con4.hashCode());

		// create a connection with differesnt artifact
		Connection con5 = new Connection(Arrays.asList(classC), targets, tlinkA);

		// check that the conenctions are not equal
		assertNotEquals(con5, con1);
		assertNotEquals(con5.hashCode(), con1.hashCode());
		assertNotEquals(con5, con2);
		assertNotEquals(con5.hashCode(), con2.hashCode());
		assertNotEquals(con5, con3);
		assertNotEquals(con5.hashCode(), con3.hashCode());
		assertNotEquals(con5, con4);
		assertNotEquals(con5.hashCode(), con4.hashCode());
	}

	@Test
	public void testTraceExistsMethod() throws CoreException, IOException {
		// Create a project
		createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// Create two models and persist them
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		save(testProject, a);

		EPackage b = createEcoreModel(MODEL_B_NAME);
		createEClassInEPackage(b, CLASS_B_NAME);
		createEClassInEPackage(b, CLASS_C_NAME);
		save(testProject, b);

		// Load them, choose two elements
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, MODEL_A_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);
		EClass _A = (EClass) _a.getEClassifier(CLASS_A_NAME);

		EPackage _b = load(testProject, MODEL_B_FILENAME, rs);
		assertEquals(_b.getName(), MODEL_B_NAME);
		EClass _B = (EClass) _b.getEClassifier(CLASS_B_NAME);
		EClass _C = (EClass) _b.getEClassifier(CLASS_C_NAME);

		// Add them to the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(_B);
		assertFalse(SelectionView.getOpenedView().getSelection().isEmpty());

		// Test the trace exists method
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		TraceHelper traceHelper = new TraceHelper(
				persistenceAdapter.getTraceModel(EditingDomainHelper.getResourceSet()));
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(EditingDomainHelper.getResourceSet());
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		EClass traceType = TracemodelPackage.eINSTANCE.getRelatedTo();
		List<EObject> selection = artifactHelper.createWrappers(SelectionView.getOpenedView().getSelection());

		assertFalse(traceHelper.traceExists(selection, traceType));

		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		traceHelper = new TraceHelper(persistenceAdapter.getTraceModel(EditingDomainHelper.getResourceSet()));
		assertTrue(traceHelper.traceExists(selection, traceType));

		// Change the order of the selection in the selection view
		SelectionView.getOpenedView().clearSelection();
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(_B);
		SelectionView.getOpenedView().dropToSelection(_A);
		assertFalse(SelectionView.getOpenedView().getSelection().isEmpty());

		// Check that the trace exists
		selection = artifactHelper.createWrappers(SelectionView.getOpenedView().getSelection());
		traceHelper = new TraceHelper(persistenceAdapter.getTraceModel(EditingDomainHelper.getResourceSet()));
		assertTrue(traceHelper.traceExists(selection, traceType));

		// Add another class to the selection view
		SelectionView.getOpenedView().dropToSelection(_C);

		// Check that the trace does not exist
		selection = artifactHelper.createWrappers(SelectionView.getOpenedView().getSelection());
		traceHelper = new TraceHelper(persistenceAdapter.getTraceModel(EditingDomainHelper.getResourceSet()));
		assertFalse(traceHelper.traceExists(selection, traceType));
	}

}
