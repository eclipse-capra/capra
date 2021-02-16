/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *   Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.handler.uml.tests;

import static org.eclipse.capra.testsupport.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsupport.TestHelper.resetSelectionView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.testsupport.TestHelper;
import org.eclipse.capra.ui.plantuml.DiagramTextProviderHandler;
import org.eclipse.capra.ui.plantuml.DisplayInternalLinksHandler;
import org.eclipse.capra.ui.plantuml.ToggleTransitivityHandler;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Model;
import org.junit.Before;
import org.junit.Test;

public class TestUMLInternalLinks {

	private static final String CLASS_A_NAME = "A";
	private static final String CLASS_B_NAME = "B";
	private static final String CLASS_C_NAME = "C";

	private static final String MODEL_A_UML_FILENAME = "modelA.uml";

	private static final String MODEL_A_NAME = "modelA";

	private static final String TEST_PROJECT_NAME = "TestProject";

	private static final String EXPECTED_TEXT_FOR_INTERNAL_LINKS = "@startuml\n" + "left to right direction\n"
			+ "object \"A : Class\" as o0 #pink\n" + "object \"B : Class\" as o1\n" + "object \"C : Class\" as o2\n"
			+ "o0--o1: A : Class B : Class : RelatedTo\n" + "o1--o2: true : Generalization\n" + "@enduml\n";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
	}

	@Test
	public void TestInternalLinks() throws CoreException, IOException {
		// Create a project
		TestHelper.createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(TestHelper.projectExists(TEST_PROJECT_NAME));
		IProject testProject = TestHelper.getProject(TEST_PROJECT_NAME);

		// Create a UML model and three classes
		Model umlModel = TestUtil.createUMLModel(MODEL_A_NAME);
		TestUtil.createClassInUMLModel(umlModel, CLASS_A_NAME);
		TestUtil.createClassInUMLModel(umlModel, CLASS_B_NAME);
		TestUtil.createClassInUMLModel(umlModel, CLASS_C_NAME);

		// Save model
		TestUtil.save(testProject, umlModel);

		// Load them and choose the three classes
		ResourceSet rs = new ResourceSetImpl();

		Model _a = TestUtil.loadUMLModel(testProject, MODEL_A_UML_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);
		Class _A = (Class) _a.getOwnedMember(CLASS_A_NAME);
		Class _B = (Class) _a.getOwnedMember(CLASS_B_NAME);
		Class _C = (Class) _a.getOwnedMember(CLASS_C_NAME);

		// create generalization between class B and C
		TestUtil.createGeneralizationBetweenClasses(_B, _C);

		// save model
		TestUtil.save(testProject, umlModel);

		// Add A and B to the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(_B);
		assertFalse(SelectionView.getOpenedView().getSelection().isEmpty());

		// Create a trace via the selection view
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
		EObject traceModel = persistenceAdapter.getTraceModel(EditingDomainHelper.getResourceSet());
		assertFalse(traceAdapter.isThereATraceBetween(_A, _B, traceModel));

		TestHelper.createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		EObject upDatedTraceModel = persistenceAdapter.getTraceModel(EditingDomainHelper.getResourceSet());
		assertTrue(traceAdapter.isThereATraceBetween(_A, _B, upDatedTraceModel));

		// Clear selection view
		SelectionView.getOpenedView().clearSelection();

		// create a selection with class A
		List<Object> selection = new ArrayList<>();
		selection.add(_A);

		// test that internal links show for direct elements
		ToggleTransitivityHandler.setTraceViewTransitive(false);
		DisplayInternalLinksHandler.showInternalLinks(true);
		DiagramTextProviderHandler provider = new DiagramTextProviderHandler();
		String directlyConnectedElements = provider.getDiagramText(selection, Optional.<IWorkbenchPart>empty());
		assertEquals(EXPECTED_TEXT_FOR_INTERNAL_LINKS, directlyConnectedElements);

	}
}
