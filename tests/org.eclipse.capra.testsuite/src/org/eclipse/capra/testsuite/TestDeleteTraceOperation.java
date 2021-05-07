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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.IPersistenceAdapter;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.core.helpers.TraceHelper;
import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.testsupport.TestHelper;
import org.eclipse.capra.ui.operations.CreateTraceOperation;
import org.eclipse.capra.ui.operations.DeleteTraceOperation;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Test;

public class TestDeleteTraceOperation {

	private static final String TEST_NAME_UPDATED = "test-name-updated";
	private static final String CLASS_A_NAME = "A";
	private static final String CLASS_B_NAME = "B";
	private static final String CLASS_C_NAME = "C";

	private static final String MODEL_A_FILENAME = "modelA.ecore";
	private static final String MODEL_B_FILENAME = "modelB.ecore";
	private static final String MODEL_A_NAME = "modelA";
	private static final String MODEL_B_NAME = "modelB";

	private static final String TEST_PROJECT_NAME = "TestProject";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
		purgeModels();
	}

	/**
	 * Tests the creation of a trace link and its subsequent deletion using the
	 * operations defined in {@code org.eclipse.capra.ui.operations}.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 */
	@Test
	public void testDeleteTrace() throws CoreException, IOException {
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
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(_B);

		EClass traceType = TracemodelPackage.eINSTANCE.getRelatedTo();

		IAdaptable adapter = SelectionView.getOpenedView().getSite();

		IWorkbench workbench = PlatformUI.getWorkbench();
		IOperationHistory operationHistory = workbench.getOperationSupport().getOperationHistory();

		CreateTraceOperation createTraceOperation = TestHelper
				.prepareCreateTraceOperationForCurrentSelectionOfType(traceType);
		try {
			assertEquals(operationHistory.execute(createTraceOperation, null, adapter), Status.OK_STATUS);
		} catch (ExecutionException e) {
			fail("Could not create trace: ExecutionException in operation");
		}

		IPersistenceAdapter persistenceAdapter = ExtensionPointHelper.getPersistenceAdapter().get();
		TraceHelper traceHelper = new TraceHelper(
				persistenceAdapter.getTraceModel(EditingDomainHelper.getResourceSet()));
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(_A.eResource().getResourceSet());
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		List<EObject> selection = artifactHelper.createWrappers(SelectionView.getOpenedView().getSelection());

		// Check that the trace between A and B exists
		traceHelper = new TraceHelper(persistenceAdapter.getTraceModel(EditingDomainHelper.getResourceSet()));
		selection = artifactHelper.createWrappers(SelectionView.getOpenedView().getSelection());
		assertTrue(traceHelper.traceExists(selection, traceType));

		List<Connection> traces = traceHelper.getTraces(Arrays.asList(new EClass[] { _A, _B }));
		assertFalse(traces.isEmpty());

		DeleteTraceOperation deleteOp = new DeleteTraceOperation("Delete trace", traces.get(0));
		try {
			assertEquals(operationHistory.execute(deleteOp, null, adapter), Status.OK_STATUS);
		} catch (ExecutionException e) {
			fail("Could not delete trace: ExecutionException in operation");
		}

		List<Connection> tracesAfterDelete = traceHelper.getTraces(Arrays.asList(new EClass[] { _A, _B }));
		assertTrue(tracesAfterDelete.isEmpty());

		try {
			assertEquals(operationHistory.undoOperation(deleteOp, null, adapter), Status.OK_STATUS);
		} catch (ExecutionException e) {
			fail("Could not undo trace deletion: ExecutionException in operation");
		}

		List<Connection> tracesAfterUndo = traceHelper.getTraces(Arrays.asList(new EClass[] { _A, _B }));
		assertFalse(tracesAfterUndo.isEmpty());

	}

}
