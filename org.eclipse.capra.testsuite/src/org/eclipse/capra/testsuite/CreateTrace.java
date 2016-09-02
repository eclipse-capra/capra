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

import static org.eclipse.capra.testsuite.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsuite.TestHelper.createCDTProject;
import static org.eclipse.capra.testsuite.TestHelper.createEClassInEPackage;
import static org.eclipse.capra.testsuite.TestHelper.createEcoreModel;
import static org.eclipse.capra.testsuite.TestHelper.createJavaProjectWithASingleJavaClass;
import static org.eclipse.capra.testsuite.TestHelper.createSimpleProject;
import static org.eclipse.capra.testsuite.TestHelper.createTraceForCurrentSelectionOfType;
import static org.eclipse.capra.testsuite.TestHelper.getProject;
import static org.eclipse.capra.testsuite.TestHelper.load;
import static org.eclipse.capra.testsuite.TestHelper.projectExists;
import static org.eclipse.capra.testsuite.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsuite.TestHelper.save;
import static org.eclipse.capra.testsuite.TestHelper.thereIsATraceBetween;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.cdt.core.model.ICProject;
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

public class CreateTrace {

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
	}

	@Test
	public void testLinkCreationEClassToEClass() throws CoreException, IOException {
		// Create a project
		createSimpleProject("TestProject");
		assertTrue(projectExists("TestProject"));

		// Create two models and persist them
		IProject testProject = getProject("TestProject");
		EPackage a = TestHelper.createEcoreModel("modelA");
		createEClassInEPackage(a, "A");
		save(testProject, a);

		EPackage b = createEcoreModel("modelB");
		createEClassInEPackage(b, "B");
		save(testProject, b);

		// Load them, choose two elements
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, "modelA.ecore", rs);
		assertEquals(_a.getName(), "modelA");
		EClass _A = (EClass) _a.getEClassifier("A");

		EPackage _b = load(testProject, "modelB.ecore", rs);
		assertEquals(_b.getName(), "modelB");
		EClass _B = (EClass) _b.getEClassifier("B");

		// Add them to the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(_B);
		assertFalse(SelectionView.getOpenedView().getSelection().isEmpty());

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(_A, _B));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(_A, _B));
	}

	@Test
	public void testLinkCreationJavaEltToEClass() throws CoreException, IOException {
		// Create a project
		IType javaClass = createJavaProjectWithASingleJavaClass("TestProject");
		assertTrue(projectExists("TestProject"));

		// Create a model and persist
		IProject testProject = getProject("TestProject");
		EPackage a = TestHelper.createEcoreModel("modelA");
		createEClassInEPackage(a, "A");
		save(testProject, a);

		// Choose the EClass
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, "modelA.ecore", rs);
		assertEquals(_a.getName(), "modelA");
		EClass _A = (EClass) _a.getEClassifier("A");

		// Drop the EClass in the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(_A);

		// Drop the JavaClass in the selection view
		SelectionView.getOpenedView().dropToSelection(javaClass);

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(_A, javaClass));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(_A, javaClass));
	}

	@Test
	public void testLinkCreationCElementToEClass() throws OperationCanceledException, CoreException, IOException {
		// Create a project
		ICProject cFile = createCDTProject("TestProject");
		assertTrue(projectExists("TestProject"));

		// Create a model and persist
		IProject testProject = getProject("TestProject");
		EPackage a = TestHelper.createEcoreModel("modelA");
		createEClassInEPackage(a, "A");
		save(testProject, a);

		// Choose the EClass
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, "modelA.ecore", rs);
		assertEquals(_a.getName(), "modelA");
		EClass _A = (EClass) _a.getEClassifier("A");

		// Drop the EClass in the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(_A);

		// Drop the c File in the selection view
		SelectionView.getOpenedView().dropToSelection(cFile);

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(_A, cFile));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(_A, cFile));
	}

}
