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
import static org.eclipse.capra.testsupport.TestHelper.createCDTProject;
import static org.eclipse.capra.testsupport.TestHelper.createEClassInEPackage;
import static org.eclipse.capra.testsupport.TestHelper.createEcoreModel;
import static org.eclipse.capra.testsupport.TestHelper.createJavaProjectWithASingleJavaClass;
import static org.eclipse.capra.testsupport.TestHelper.createSimpleProject;
import static org.eclipse.capra.testsupport.TestHelper.createTraceForCurrentSelectionOfType;
import static org.eclipse.capra.testsupport.TestHelper.getProject;
import static org.eclipse.capra.testsupport.TestHelper.load;
import static org.eclipse.capra.testsupport.TestHelper.projectExists;
import static org.eclipse.capra.testsupport.TestHelper.purgeModels;
import static org.eclipse.capra.testsupport.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsupport.TestHelper.save;
import static org.eclipse.capra.testsupport.TestHelper.thereIsATraceBetween;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.testsupport.TestHelper;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IType;
import org.junit.Before;
import org.junit.Test;

public class TestCreateTrace {

	private static final String CLASS_A_NAME = "A";
	private static final String CLASS_B_NAME = "B";

	private static final String MODEL_A_FILENAME = "modelA.ecore";
	private static final String MODEL_B_FILENAME = "modelB.ecore";

	private static final String MODEL_A_NAME = "modelA";
	private static final String MODEL_B_NAME = "modelB";

	private static final String TEST_PROJECT_NAME = "TestProject";
	private static final String TEST_PROJECT_NAME_JAVA = "TestProject_java";
	private static final String TEST_PROJECT_NAME_C = "TestProject_C";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
		purgeModels();
	}

	@Test
	public void testLinkCreationEClassToEClass() throws CoreException, IOException {
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
		save(testProject, b);

		// Load them, choose two elements
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, MODEL_A_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);
		EClass _A = (EClass) _a.getEClassifier(CLASS_A_NAME);

		EPackage _b = load(testProject, MODEL_B_FILENAME, rs);
		assertEquals(_b.getName(), MODEL_B_NAME);
		EClass _B = (EClass) _b.getEClassifier(CLASS_B_NAME);

		// Add them to the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(_B);
		assertFalse(SelectionView.getOpenedView().getSelection().isEmpty());

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(_A, _B));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(_A, _B));

	}

	@Test
	public void testLinkCreationJavaEltToEClass() throws CoreException, IOException {
		// Create a project
		IType javaClass = createJavaProjectWithASingleJavaClass(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// Create a model and persist
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		save(testProject, a);

		// Choose the EClass
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, MODEL_A_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);
		EClass _A = (EClass) _a.getEClassifier(CLASS_A_NAME);

		// Drop the EClass in the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(_A);

		// Drop the JavaClass in the selection view
		SelectionView.getOpenedView().dropToSelection(javaClass);

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(_A, javaClass));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(_A, javaClass));
	}

	@Test
	public void testLinkCreationCElementToEClass()
			throws OperationCanceledException, CoreException, IOException, BuildException {
		// Create a project
		ICProject cFile = createCDTProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// Create a model and persist
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		save(testProject, a);

		// Choose the EClass
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, MODEL_A_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);
		EClass _A = (EClass) _a.getEClassifier(CLASS_A_NAME);

		// Drop the EClass in the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(_A);

		// Drop the c File in the selection view
		SelectionView.getOpenedView().dropToSelection(cFile);

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(_A, cFile));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(_A, cFile));
	}

	@Test
	public void testLinkCreationJavaClassToJavaClass() throws CoreException, BuildException {
		// Create a java project
		IType javaClass = createJavaProjectWithASingleJavaClass(TEST_PROJECT_NAME_JAVA);
		assertTrue(projectExists(TEST_PROJECT_NAME_JAVA));

		// Create a C project
		ICProject cFile = createCDTProject(TEST_PROJECT_NAME_C);
		assertTrue(projectExists(TEST_PROJECT_NAME_C));

		// Drop the JavaClass in the selection view
		SelectionView.getOpenedView().dropToSelection(javaClass);
		// Drop the c File in the selection view
		SelectionView.getOpenedView().dropToSelection(cFile);

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(javaClass, cFile));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(javaClass, cFile));

	}

}
