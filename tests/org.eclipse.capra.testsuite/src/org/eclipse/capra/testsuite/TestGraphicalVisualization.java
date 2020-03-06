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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.ui.plantuml.DiagramTextProviderHandler;
import org.eclipse.capra.ui.plantuml.ToggleTransitivityHandler;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IWorkbenchPart;
import org.junit.Before;
import org.junit.Test;

public class TestGraphicalVisualization {

	private static final String CLASS_A_NAME = "A";
	private static final String CLASS_B_NAME = "B";
	private static final String CLASS_C_NAME = "C";

	private static final String MODEL_A_FILENAME = "modelA.ecore";
	private static final String MODEL_B_FILENAME = "modelB.ecore";

	private static final String MODEL_A_NAME = "modelA";
	private static final String MODEL_B_NAME = "modelB";

	private static final String TEST_PROJECT_NAME = "TestProject";
	private static final String TEST_PROJECT_NAME_JAVA = "TestProject_java";
	private static final String TEST_PROJECT_NAME_C = "TestProject_C";
	private static final String TEST_C_CLASS = "CClass.c";

	private static final String LINE_SEPARATOR = System.lineSeparator();

	private static final String EXPECTED_TEXT_FOR_DIRECT_CONNECTIONS = "@startuml" + LINE_SEPARATOR
			+ "object \"A : EClass\" as o0 #pink" + LINE_SEPARATOR + "object \"B : EClass\" as o1" + LINE_SEPARATOR
			+ "o0--o1: A : EClass B : EClass : RelatedTo" + LINE_SEPARATOR + "@enduml" + LINE_SEPARATOR;

	private static final String EXPECTED_TEXT_FOR_TRANSITIVE_CONNECTIONS = "@startuml" + LINE_SEPARATOR
			+ "object \"A : EClass\" as o0 #pink" + LINE_SEPARATOR + "object \"B : EClass\" as o1" + LINE_SEPARATOR
			+ "object \"C : EClass\" as o2" + LINE_SEPARATOR + "o0--o1: A : EClass B : EClass : RelatedTo"
			+ LINE_SEPARATOR + "o1--o2: B : EClass C : EClass : RelatedTo" + LINE_SEPARATOR + "@enduml"
			+ LINE_SEPARATOR;
	private static final String EXPECTED_TEXT_FOR_GOTO_LINKS = "@startuml" + LINE_SEPARATOR
			+ "object \"TestClass [[platform:/resource/TestProject_java/src/org/eclipse/capra/test/TestClass.java#org.eclipse.capra.test.TestClass (Go to)]]\" as o0 #pink"
			+ LINE_SEPARATOR
			+ "object \"CClass.c [[platform:/resource/TestProject_C/CClass.c#CClass.c (Go to)]]\" as o1"
			+ LINE_SEPARATOR + "o0--o1: TestClass CClass.c : RelatedTo" + LINE_SEPARATOR + "@enduml" + LINE_SEPARATOR;

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
	}

	@Test
	public void testPlantUMLGraphView() throws CoreException, IOException, InterruptedException {

		// Create a project
		createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// Create two models each with two classes and persist them
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		save(testProject, a);

		EPackage b = createEcoreModel(MODEL_B_NAME);
		createEClassInEPackage(b, CLASS_B_NAME);
		createEClassInEPackage(b, CLASS_C_NAME);
		save(testProject, b);

		// Load them and choose the four classes
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, MODEL_A_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);
		EClass _A = (EClass) _a.getEClassifier(CLASS_A_NAME);

		EPackage _b = load(testProject, MODEL_B_FILENAME, rs);
		assertEquals(_b.getName(), MODEL_B_NAME);
		EClass _B = (EClass) _b.getEClassifier(CLASS_B_NAME);
		EClass _C = (EClass) _b.getEClassifier(CLASS_C_NAME);

		// Add A and B to the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(_B);
		assertFalse(SelectionView.getOpenedView().getSelection().isEmpty());

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(_A, _B));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(_A, _B));

		// Clear selection view
		SelectionView.getOpenedView().clearSelection();

		// Add B and C to selection view
		SelectionView.getOpenedView().dropToSelection(_B);
		SelectionView.getOpenedView().dropToSelection(_C);

		// Create a traceLink between B and C
		assertFalse(thereIsATraceBetween(_B, _C));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		// Remove trace model from resource set to make sure the trace model is
		// re-loaded to capture the second trace link
		removeTraceModel(rs);
		assertTrue(thereIsATraceBetween(_B, _C));

		// create a selection with class A
		List<Object> selection = new ArrayList<>();
		selection.add(_A);

		// Test directly connected Elements
		ToggleTransitivityHandler.setTraceViewTransitive(false);
		DiagramTextProviderHandler provider = new DiagramTextProviderHandler();
		String DirectlyConnectedElements = provider.getDiagramText(selection, Optional.<IWorkbenchPart>empty());
		assertTrue(DirectlyConnectedElements.equals(EXPECTED_TEXT_FOR_DIRECT_CONNECTIONS));

		// Test transitively connected Elements
		ToggleTransitivityHandler.setTraceViewTransitive(true);
		String transitivelysConnectedElements = provider.getDiagramText(selection, Optional.<IWorkbenchPart>empty());
		assertTrue(transitivelysConnectedElements.equals(EXPECTED_TEXT_FOR_TRANSITIVE_CONNECTIONS));

	}

	@Test
	public void testGoToLink() throws CoreException, BuildException {
		// Create a java project
		IType javaClass = createJavaProjectWithASingleJavaClass(TEST_PROJECT_NAME_JAVA);
		assertTrue(projectExists(TEST_PROJECT_NAME_JAVA));

		// Create a C project
		ICProject cProject = createCDTProject(TEST_PROJECT_NAME_C);
		assertTrue(projectExists(TEST_PROJECT_NAME_C));

		// create a C class in the CProject
		ITranslationUnit cFile = TestHelper.createCSourceFileInProject(TEST_C_CLASS, cProject);

		// Drop the JavaClass in the selection view
		SelectionView.getOpenedView().dropToSelection(javaClass);
		// Drop the c File in the selection view
		SelectionView.getOpenedView().dropToSelection(cFile);

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(javaClass, cFile));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(javaClass, cFile));

		// create a selection with the java class
		List<Object> selection = new ArrayList<>();
		selection.add(javaClass);

		// Test directly connected Elements
		ToggleTransitivityHandler.setTraceViewTransitive(false);
		DiagramTextProviderHandler provider = new DiagramTextProviderHandler();
		String directlyConnectedElements = provider.getDiagramText(selection, Optional.<IWorkbenchPart>empty());
		assertTrue(directlyConnectedElements.equals(EXPECTED_TEXT_FOR_GOTO_LINKS));

	}

	private void removeTraceModel(ResourceSet rs) {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		EObject tm = persistenceAdapter.getTraceModel(rs);
		rs.getResources().remove(tm.eResource());
	}

}
