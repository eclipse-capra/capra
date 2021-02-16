package org.eclipse.capra.testsuite;

import static org.eclipse.capra.testsupport.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsupport.TestHelper.createEClassInEPackage;
import static org.eclipse.capra.testsupport.TestHelper.createEcoreModel;
import static org.eclipse.capra.testsupport.TestHelper.createSimpleProject;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.capra.core.adapters.AbstractMetaModelAdapter;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.generic.tracemodel.GenericMetaModelAdapter;
import org.eclipse.capra.generic.tracemodel.GenericTraceModel;
import org.eclipse.capra.generic.tracemodel.RelatedTo;
import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.testsupport.TestHelper;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests to cover transitive elements link used in to retain independence of a
 * concrete trace.
 * 
 * @author Mihaela Grubii
 *
 */
public class TestTransitiveElementsInternalAndConnected {
	private static final String CLASS_A_NAME = "A";
	private static final String CLASS_B_NAME = "B";
	private static final String CLASS_C_NAME = "C";
	private static final String CLASS_D_NAME = "D";
	private static final String CLASS_E_NAME = "E";
	private static final String CLASS_F_NAME = "F";

	private static final String MODEL_A_FILENAME = "modelA.ecore";
	private static final String MODEL_B_FILENAME = "modelB.ecore";
	private static final String MODEL_C_FILENAME = "modelC.ecore";
	private static final String MODEL_A_NAME = "modelA";
	private static final String MODEL_B_NAME = "modelB";
	private static final String MODEL_C_NAME = "modelC";

	private static final String TEST_PROJECT_NAME = "TestProject";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
		purgeModels();
	}

	@Test
	public void testGenericMetaModelForConnectedElements() throws CoreException, IOException {

		// Create a project
		createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// Create 3 models and persist them
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		createEClassInEPackage(a, CLASS_F_NAME);
		save(testProject, a);

		EPackage b = createEcoreModel(MODEL_B_NAME);
		createEClassInEPackage(b, CLASS_B_NAME);
		createEClassInEPackage(b, CLASS_C_NAME);
		save(testProject, b);

		EPackage c = createEcoreModel(MODEL_C_NAME);
		createEClassInEPackage(c, CLASS_D_NAME);
		createEClassInEPackage(c, CLASS_E_NAME);
		save(testProject, c);

		// Load the models and select the classes
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, MODEL_A_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);
		EClass _A = (EClass) _a.getEClassifier(CLASS_A_NAME);

		EPackage _b = load(testProject, MODEL_B_FILENAME, rs);
		assertEquals(_b.getName(), MODEL_B_NAME);
		EClass _B = (EClass) _b.getEClassifier(CLASS_B_NAME);

		EPackage _c = load(testProject, MODEL_C_FILENAME, rs);
		assertEquals(_c.getName(), MODEL_C_NAME);
		EClass _D = (EClass) _c.getEClassifier(CLASS_D_NAME);
		EClass _E = (EClass) _c.getEClassifier(CLASS_E_NAME);

		// create direct trace from A to B
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(_B);
		TestHelper.createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		SelectionView.getOpenedView().clearSelection();

		AbstractMetaModelAdapter genMod = new GenericMetaModelAdapter();
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		GenericTraceModel traceModel = (GenericTraceModel) persistenceAdapter.getTraceModel(resourceSet);

		// testing transitively connected elements when there is no transitive
		// connection
		List<Connection> con1 = genMod.getTransitivelyConnectedElements(_A, traceModel, 0);
		List<Connection> con2 = genMod.getConnectedElements(_A, traceModel);
		assertEquals(con1, con2);

		// create direct trace B to E, D to E
		SelectionView.getOpenedView().dropToSelection(_B);
		SelectionView.getOpenedView().dropToSelection(_E);
		TestHelper.createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		SelectionView.getOpenedView().clearSelection();

		SelectionView.getOpenedView().dropToSelection(_D);
		SelectionView.getOpenedView().dropToSelection(_E);
		TestHelper.createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		SelectionView.getOpenedView().clearSelection();

		// testing transitively connected elements when there are transitive
		// connection
		List<Connection> con3 = genMod.getTransitivelyConnectedElements(_A, traceModel, 0);
		assertNotEquals(con1, con3);
		assertNotEquals(con2, con3);
		assertTrue(con3.size() + " is greater than " + con2.size(), con3.size() > con2.size());

		// testing transitively connected elements when current depth not 0
		List<Connection> con4 = genMod.getTransitivelyConnectedElements(_A, traceModel, 1);
		assertNotEquals(con3, con4);

		// testing transitive and non transitive connected elements for trace parameter
		List<RelatedTo> traces = traceModel.getTraces();
		con1 = genMod.getTransitivelyConnectedElements(traces.get(0), traceModel, 0);
		con2 = genMod.getConnectedElements(traces.get(0), traceModel);
		assertFalse(con1.toString() + " is not empty", con1.isEmpty());
		assertTrue(con1.size() + " is greater than " + con2.size(), con1.size() > con2.size());

		// Verify connected getConnectedElements for trace parameter
		List<String> traceLinks = Arrays.asList(TracemodelPackage.eINSTANCE.getRelatedTo().getName());
		con1 = genMod.getConnectedElements(traces.get(0), traceModel, new ArrayList<String>());
		con2 = genMod.getConnectedElements(traces.get(0), traceModel);
		assertEquals(con1, con2);
		con1 = genMod.getConnectedElements(traces.get(0), traceModel, traceLinks);
		assertEquals(con1, con2);
	}

	@Test
	public void testAbstractMetaModelForTransitiveElements() throws CoreException, IOException {

		// Create a project
		createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// Create 3 models and persist them
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		createEClassInEPackage(a, CLASS_F_NAME);
		save(testProject, a);

		EPackage b = createEcoreModel(MODEL_B_NAME);
		createEClassInEPackage(b, CLASS_B_NAME);
		createEClassInEPackage(b, CLASS_C_NAME);
		save(testProject, b);

		EPackage c = createEcoreModel(MODEL_C_NAME);
		createEClassInEPackage(c, CLASS_D_NAME);
		createEClassInEPackage(c, CLASS_E_NAME);
		save(testProject, c);

		// Load the models and select the classes
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, MODEL_A_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);
		EClass _A = (EClass) _a.getEClassifier(CLASS_A_NAME);

		EPackage _b = load(testProject, MODEL_B_FILENAME, rs);
		assertEquals(_b.getName(), MODEL_B_NAME);
		EClass _B = (EClass) _b.getEClassifier(CLASS_B_NAME);
		EClass _C = (EClass) _b.getEClassifier(CLASS_C_NAME);

		EPackage _c = load(testProject, MODEL_C_FILENAME, rs);
		assertEquals(_c.getName(), MODEL_C_NAME);
		EClass _D = (EClass) _c.getEClassifier(CLASS_D_NAME);
		EClass _E = (EClass) _c.getEClassifier(CLASS_E_NAME);

		// Verify connected getConnectedElements for trace parameter
		List<String> traceLinks = Arrays.asList(TracemodelPackage.eINSTANCE.getRelatedTo().getName());

		// create direct trace from A to B, B to E, D to E
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(_B);
		TestHelper.createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		SelectionView.getOpenedView().clearSelection();

		SelectionView.getOpenedView().dropToSelection(_B);
		SelectionView.getOpenedView().dropToSelection(_E);
		TestHelper.createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		SelectionView.getOpenedView().clearSelection();

		SelectionView.getOpenedView().dropToSelection(_D);
		SelectionView.getOpenedView().dropToSelection(_E);
		TestHelper.createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		SelectionView.getOpenedView().clearSelection();

		AbstractMetaModelAdapter genMod = new GenericMetaModelAdapter();
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		GenericTraceModel traceModel = (GenericTraceModel) persistenceAdapter.getTraceModel(resourceSet);

		// testing method for getting internal links connected
		List<Connection> con2 = genMod.getInternalElementsTransitive(_D, traceModel, traceLinks, 0);
		List<Connection> con3 = genMod.getInternalElementsTransitive(_C, traceModel, traceLinks, 0);

		// Checking that classes from the same package have different connection
		assertNotEquals(con2.size(), con3.size());

		// adding direct trace from A to C
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(_C);
		TestHelper.createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		SelectionView.getOpenedView().clearSelection();

		// adding direct trace from C to E
		SelectionView.getOpenedView().dropToSelection(_C);
		SelectionView.getOpenedView().dropToSelection(_E);
		TestHelper.createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		SelectionView.getOpenedView().clearSelection();

		con3 = genMod.getInternalElementsTransitive(_C, traceModel, traceLinks, 0);
		con2 = genMod.getInternalElementsTransitive(_B, traceModel, traceLinks, 0);

		// Checking that after additional trace link classes have the same internal
		// links connected
		assertEquals(con2.size(), con3.size());

	}

}
