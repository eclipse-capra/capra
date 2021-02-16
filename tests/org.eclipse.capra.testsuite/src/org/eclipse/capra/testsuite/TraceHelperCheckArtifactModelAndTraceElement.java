package org.eclipse.capra.testsuite;

import static org.eclipse.capra.testsupport.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsupport.TestHelper.createEClassInEPackage;
import static org.eclipse.capra.testsupport.TestHelper.createEmptyFileInProject;
import static org.eclipse.capra.testsupport.TestHelper.createSimpleProject;
import static org.eclipse.capra.testsupport.TestHelper.createTraceForCurrentSelectionOfType;
import static org.eclipse.capra.testsupport.TestHelper.purgeModels;
import static org.eclipse.capra.testsupport.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsupport.TestHelper.thereIsATraceBetween;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.core.helpers.TraceHelper;
import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.testsupport.TestHelper;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.Before;
import org.junit.Test;

/**
 * Test to check for fetched artifact wrapper and verifying if it is contained
 * in he trace model, as well as retrieving unique artifacts connected by trace
 * model.
 * 
 * @author Mihaela Grubii
 *
 */
public class TraceHelperCheckArtifactModelAndTraceElement {
	private static final String MODEL_A_NAME = "modelA";
	private static final String FILE_A = "FILE_A";
	private static final String FILE_B = "FILE_B";
	private static final String FILE_C = "FILE_C";
	private static final String FILE_D = "FILE_D";
	private static final String FILE_E = "FILE_E";
	private static final String TEST_PROJECT_NAME = "TestProject";
	private static final String CLASS_A_NAME = "A";
	private static final String CLASS_B_NAME = "B";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
		purgeModels();
	}

	@Test
	public void testIfArtifactIsInTraceModelAndIfConnectedTotheCollectionOfTraces() throws CoreException, IOException {

		// create a simple project
		createSimpleProject(TEST_PROJECT_NAME);

		// create some files in the project
		IFile fileA = createEmptyFileInProject(FILE_A, TEST_PROJECT_NAME);
		IFile fileB = createEmptyFileInProject(FILE_B, TEST_PROJECT_NAME);
		IFile fileC = createEmptyFileInProject(FILE_C, TEST_PROJECT_NAME);
		IFile fileD = createEmptyFileInProject(FILE_D, TEST_PROJECT_NAME);
		IFile fileE = createEmptyFileInProject(FILE_E, TEST_PROJECT_NAME);

		// create class in active package
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		createEClassInEPackage(a, CLASS_B_NAME);

		// load the models and select the classes
		EClass classA = (EClass) a.getEClassifier(CLASS_A_NAME);
		EClass classB = (EClass) a.getEClassifier(CLASS_B_NAME);

		// create a trace link between File A and B
		SelectionView.getOpenedView().dropToSelection(fileA);
		SelectionView.getOpenedView().dropToSelection(fileB);
		EClass traceType = TracemodelPackage.eINSTANCE.getRelatedTo();
		createTraceForCurrentSelectionOfType(traceType);
		assertTrue(thereIsATraceBetween(fileA, fileB));

		// clear selection
		SelectionView.getOpenedView().clearSelection();

		// create a trace link between File B and C
		SelectionView.getOpenedView().dropToSelection(fileB);
		SelectionView.getOpenedView().dropToSelection(fileC);
		createTraceForCurrentSelectionOfType(traceType);
		assertTrue(thereIsATraceBetween(fileB, fileC));

		// clear selection
		SelectionView.getOpenedView().clearSelection();

		// create a trace link between File E and C
		SelectionView.getOpenedView().dropToSelection(fileC);
		SelectionView.getOpenedView().dropToSelection(fileE);
		createTraceForCurrentSelectionOfType(traceType);
		assertTrue(thereIsATraceBetween(fileC, fileE));

		// clear selection
		SelectionView.getOpenedView().clearSelection();

		// create a list with wrappers of File A and B
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		List<Object> artifacts_A_B = Arrays.asList(fileA, fileB);
		List<EObject> A_Bwrappers = artifactHelper.createWrappers(artifacts_A_B);

		// get trace link related to fileA and fileB
		EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);
		TraceHelper traceHelper = new TraceHelper(traceModel);
		List<Connection> traceLinks_A_B = traceHelper.getTraces(A_Bwrappers);

		// get origins and targets for all trace links for A and B
		List<EObject> origins_A_B = new ArrayList<>();
		traceLinks_A_B.forEach(v -> origins_A_B.addAll(v.getOrigins()));
		List<EObject> targets_A_B = new ArrayList<>();
		traceLinks_A_B.forEach(v -> targets_A_B.addAll(v.getTargets()));

		// check if traceExists() returns true if the origins and targets exist
		assertTrue(traceHelper.traceExists(origins_A_B, targets_A_B, traceType) + " expected to be true",
				traceHelper.traceExists(origins_A_B, targets_A_B, traceType));

		// set origins and targets as unrelated trace model classes
		List<EObject> origins_A_C = Arrays.asList(classA);
		List<EObject> targets_A_C = Arrays.asList(classB);

		// return false if the origins and targets do not share the same trace type
		assertFalse(traceHelper.traceExists(origins_A_C, targets_A_C, traceType) + " expected to be false",
				traceHelper.traceExists(origins_A_C, targets_A_C, traceType));

		// check if wrapper is in trace model
		A_Bwrappers.forEach(wrapper -> assertTrue(traceHelper.isArtifactInTraceModel(wrapper) + " expected to be true",
				traceHelper.isArtifactInTraceModel(wrapper)));

		// create wrapper for file that is not a part of the trace model
		List<Object> artifacts_D = Arrays.asList(fileD);
		List<EObject> D_wrappers = artifactHelper.createWrappers(artifacts_D);

		// check if wrapper is not in trace model
		assertFalse(traceHelper.isArtifactInTraceModel(D_wrappers.get(0)) + " expected to be false",
				traceHelper.isArtifactInTraceModel(D_wrappers.get(0)));

		// check if getTraces will return an empty string for unrelated artifacts
		assertTrue(traceHelper.getTraces(D_wrappers).isEmpty() + " expected to be true",
				traceHelper.getTraces(D_wrappers).isEmpty());

		// create an empty list of wrappers
		List<Object> artifacts_empty = new ArrayList<Object>();
		List<EObject> empty_wrappers = artifactHelper.createWrappers(artifacts_empty);

		// check if empty wrapper is in trace model
		assertTrue(traceHelper.getTraces(empty_wrappers).isEmpty() + " expected to be true",
				traceHelper.getTraces(empty_wrappers).isEmpty());

		// create a list with wrappers of File A, B, C and E
		List<Object> artifacts_A_B_C_E = Arrays.asList(fileA, fileB, fileC, fileE);
		List<EObject> A_B_C_Ewrappers = artifactHelper.createWrappers(artifacts_A_B_C_E);

		// if artifacts list size is greater than 2
		assertTrue(traceHelper.getTraces(A_B_C_Ewrappers).size() + " expected to be 3",
				traceHelper.getTraces(A_B_C_Ewrappers).size() == 3);

		// check that the output traces are a subset of the input artifacts based on
		// size
		assertTrue(traceHelper.getTraces(A_B_C_Ewrappers).size() < A_B_C_Ewrappers.size());

		// get trace link related to fileA, fileC, fileB and fileE
		List<Connection> traceLinks_A_B_C_E = traceHelper.getTraces(A_B_C_Ewrappers);

		// check if expected amount of trace links found
		assertTrue(
				"for fileA, filB, fileC, fileE there were found " + traceLinks_A_B_C_E.size() + " out of 3 trace links",
				traceLinks_A_B_C_E.size() == 3);

		// since method outputs a set the values are distinct
		Set<EObject> uniqueArtifacts = TraceHelper.getTracedElements(traceLinks_A_B_C_E);

		// check response for a valid input
		assertFalse("unique set " + uniqueArtifacts + " is not empty", uniqueArtifacts.isEmpty());

		// check if unique artifacts are an expected amount
		assertTrue("for fileA, filB, fileC, fileE there were found " + uniqueArtifacts.size() + " out of 4 artifacts",
				uniqueArtifacts.size() == 4);

		// check response is not null for an empty list
		assertIsNull(TraceHelper.getTracedElements(Collections.emptyList()));
	}

	private void assertIsNull(Set<EObject> tracedElements) {
		// TODO Auto-generated method stub

	}

}
