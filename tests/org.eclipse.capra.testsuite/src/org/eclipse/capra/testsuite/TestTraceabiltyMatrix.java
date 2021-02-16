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
import static org.eclipse.capra.testsupport.TestHelper.thereIsATraceBetween;
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
import org.eclipse.capra.testsupport.TestHelper;
import org.eclipse.capra.ui.plantuml.DiagramTextProviderHandler;
import org.eclipse.capra.ui.plantuml.ToggleDisplayGraphHandler;
import org.eclipse.capra.ui.plantuml.ToggleTransitivityHandler;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.ui.IWorkbenchPart;
import org.junit.Before;
import org.junit.Test;

public class TestTraceabiltyMatrix {

	private static final String CLASS_A_NAME = "A";
	private static final String CLASS_AA_NAME = "AA";
	private static final String CLASS_B_NAME = "B";
	private static final String CLASS_BB_NAME = "BB";

	private static final String MODEL_A_FILENAME = "modelA.ecore";
	private static final String MODEL_B_FILENAME = "modelB.ecore";

	private static final String MODEL_A_NAME = "modelA";
	private static final String MODEL_B_NAME = "modelB";

	private static final String TEST_PROJECT_NAME = "TestProject";

	private static final String LINE_SEPARATOR = System.lineSeparator();

	private static final String EXPECTED_TEXT_FOR_SELECTED_PACKAGES_DIRECT = "@startuml" + LINE_SEPARATOR + "salt"
			+ LINE_SEPARATOR + "{#" + LINE_SEPARATOR + ".|modelA : EPackage|modelB : EPackage" + LINE_SEPARATOR
			+ "modelA : EPackage |. |X" + LINE_SEPARATOR + "modelB : EPackage |. |." + LINE_SEPARATOR + "}"
			+ LINE_SEPARATOR + LINE_SEPARATOR + "@enduml" + LINE_SEPARATOR;

	private static final String EXPECTED_TEXT_FOR_SELECTED_PACKAGES_TRANSITIVE = "@startuml" + LINE_SEPARATOR + "salt"
			+ LINE_SEPARATOR + "{#" + LINE_SEPARATOR
			+ ".|modelA : EPackage|A : EClass|AA : EClass|modelB : EPackage|B : EClass|BB : EClass" + LINE_SEPARATOR
			+ "modelA : EPackage |. |. |. |X |. |." + LINE_SEPARATOR + "A : EClass |. |. |. |. |X |." + LINE_SEPARATOR
			+ "AA : EClass |. |. |. |. |. |X" + LINE_SEPARATOR + "modelB : EPackage |. |. |. |. |. |." + LINE_SEPARATOR
			+ "B : EClass |. |. |. |. |. |." + LINE_SEPARATOR + "BB : EClass |. |. |. |. |. |." + LINE_SEPARATOR + "}"
			+ LINE_SEPARATOR + LINE_SEPARATOR + "@enduml" + LINE_SEPARATOR;

	private static final String EXPECTED_TEXT_FOR_SELECTED_CLASSES = "@startuml" + LINE_SEPARATOR + "salt"
			+ LINE_SEPARATOR + "{#" + LINE_SEPARATOR + ".|A : EClass|B : EClass|AA : EClass|BB : EClass"
			+ LINE_SEPARATOR + "A : EClass |. |X |. |." + LINE_SEPARATOR + "B : EClass |. |. |. |." + LINE_SEPARATOR
			+ "AA : EClass |. |. |. |X" + LINE_SEPARATOR + "BB : EClass |. |. |. |." + LINE_SEPARATOR + "}"
			+ LINE_SEPARATOR + LINE_SEPARATOR + "@enduml" + LINE_SEPARATOR;

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
		purgeModels();
	}

	@Test
	public void testMatrix() throws CoreException, IOException, InterruptedException {

		// Create a project
		createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// Create two models each with two classes and persist them
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		createEClassInEPackage(a, CLASS_AA_NAME);
		save(testProject, a);

		EPackage b = createEcoreModel(MODEL_B_NAME);
		createEClassInEPackage(b, CLASS_B_NAME);
		createEClassInEPackage(b, CLASS_BB_NAME);
		save(testProject, b);

		// Load them and choose the four classes
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, MODEL_A_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);
		EClass _A = (EClass) _a.getEClassifier(CLASS_A_NAME);
		EClass _AA = (EClass) _a.getEClassifier(CLASS_AA_NAME);

		EPackage _b = load(testProject, MODEL_B_FILENAME, rs);
		assertEquals(_b.getName(), MODEL_B_NAME);
		EClass _B = (EClass) _b.getEClassifier(CLASS_B_NAME);
		EClass _BB = (EClass) _b.getEClassifier(CLASS_BB_NAME);

		// Add A and B to the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(_B);
		assertFalse(SelectionView.getOpenedView().getSelection().isEmpty());

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(_A, _B));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(_A, _B));

		// Clear the selection view
		SelectionView.getOpenedView().clearSelection();
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());

		// Add AA and BB to selection view
		SelectionView.getOpenedView().dropToSelection(_AA);
		SelectionView.getOpenedView().dropToSelection(_BB);
		assertFalse(SelectionView.getOpenedView().getSelection().isEmpty());

		// Create a trace between AA and BB
		assertFalse(thereIsATraceBetween(_AA, _BB));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Remove trace model from resource set to make sure the trace model is
		// re-loaded to capture the second trace link
		removeTraceModel(rs);
		assertTrue(thereIsATraceBetween(_AA, _BB));

		// create trace link between package A and B
		// clear selection
		SelectionView.getOpenedView().clearSelection();
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());

		// Add Package A and B to selection view
		SelectionView.getOpenedView().dropToSelection(_a);
		SelectionView.getOpenedView().dropToSelection(_b);
		assertFalse(SelectionView.getOpenedView().getSelection().isEmpty());

		// Create a trace between Package A and B
		assertFalse(thereIsATraceBetween(_a, _b));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Remove trace model from resource set to make sure the trace model is
		// re-loaded to capture the third trace link
		removeTraceModel(rs);
		assertTrue(thereIsATraceBetween(_a, _b));

		// create a selection with Package A and B
		List<Object> selectedPackages = new ArrayList<>();
		selectedPackages.add(_a);
		selectedPackages.add(_b);

		// Test directly connected Elements
		ToggleTransitivityHandler.setTraceViewTransitive(false);
		ToggleDisplayGraphHandler.setDisplayGraph(false);
		DiagramTextProviderHandler provider = new DiagramTextProviderHandler();
		String plantUMLTextForSelectedPackages_Direct = provider.getDiagramText(selectedPackages,
				Optional.<IWorkbenchPart>empty());
		assertEquals(EXPECTED_TEXT_FOR_SELECTED_PACKAGES_DIRECT, plantUMLTextForSelectedPackages_Direct);

		// Test transitively connected Elements
		ToggleTransitivityHandler.setTraceViewTransitive(true);
		String plantUMLTextForSelectedPackages_Transitive = provider.getDiagramText(selectedPackages,
				Optional.<IWorkbenchPart>empty());
		assertEquals(EXPECTED_TEXT_FOR_SELECTED_PACKAGES_TRANSITIVE, plantUMLTextForSelectedPackages_Transitive);

		// test multiple classes selected
		List<Object> selectedClasses = new ArrayList<>();
		selectedClasses.add(_A);
		selectedClasses.add(_B);
		selectedClasses.add(_AA);
		selectedClasses.add(_BB);

		String plantUMLTextForSelectedClasses = provider.getDiagramText(selectedClasses,
				Optional.<IWorkbenchPart>empty());
		assertEquals(EXPECTED_TEXT_FOR_SELECTED_CLASSES, plantUMLTextForSelectedClasses);
	}

	private void removeTraceModel(ResourceSet rs) {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		EObject tm = persistenceAdapter.getTraceModel(rs);
		rs.getResources().remove(tm.eResource());
	}
}
