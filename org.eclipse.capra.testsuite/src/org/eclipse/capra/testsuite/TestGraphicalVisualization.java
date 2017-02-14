package org.eclipse.capra.testsuite;

import static org.eclipse.capra.testsuite.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsuite.TestHelper.createEClassInEPackage;
import static org.eclipse.capra.testsuite.TestHelper.createEcoreModel;
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

import org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.plantuml.DiagramTextProviderHandler;
import org.eclipse.capra.ui.plantuml.DisplayTracesHandler;
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

public class TestGraphicalVisualization {

	private final static String EXPECTED_TEXT_FOR_DIRECT_CONNECTIONS = "@startuml\n"
			+ "object \"A : EClass\" as o0 #pink\n" + "object \"B : EClass\" as o1\n" + "o0--o1:RelatedTo\n"
			+ "@enduml\n";

	private final static String EXPECTED_TEXT_FOR_TRANSITIVE_CONNECTIONS = "@startuml\n"
			+ "object \"A : EClass\" as o0 #pink\n" + "object \"B : EClass\" as o1\n" + "object \"C : EClass\" as o2\n"
			+ "o0--o1:RelatedTo\n" + "o1--o2:RelatedTo\n" + "@enduml\n";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
	}

	@Test
	public void testPlantUMLGraphView() throws CoreException, IOException, InterruptedException {

		// Create a project
		createSimpleProject("TestProject");
		assertTrue(projectExists("TestProject"));

		// Create two models each with two classes and persist them
		IProject testProject = getProject("TestProject");
		EPackage a = TestHelper.createEcoreModel("modelA");
		createEClassInEPackage(a, "A");
		save(testProject, a);

		EPackage b = createEcoreModel("modelB");
		createEClassInEPackage(b, "B");
		createEClassInEPackage(b, "C");
		save(testProject, b);

		// Load them and choose the four classes
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, "modelA.ecore", rs);
		assertEquals(_a.getName(), "modelA");
		EClass _A = (EClass) _a.getEClassifier("A");

		EPackage _b = load(testProject, "modelB.ecore", rs);
		assertEquals(_b.getName(), "modelB");
		EClass _B = (EClass) _b.getEClassifier("B");
		EClass _C = (EClass) _b.getEClassifier("C");

		// Add A and B to the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(_B);
		assertFalse(SelectionView.getOpenedView().getSelection().isEmpty());

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(_A, _B));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(_A, _B));

		// Clear selection view
		SelectionView.getOpenedView().clearSelection();

		// Add B and C to selection view
		SelectionView.getOpenedView().dropToSelection(_B);
		SelectionView.getOpenedView().dropToSelection(_C);

		// Create a traceLink between B and C
		assertFalse(thereIsATraceBetween(_B, _C));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		// Remove trace model from resource set to make sure the trace model is
		// re-loaded to capture the second trace link
		removeTraceModel(rs);
		assertTrue(thereIsATraceBetween(_B, _C));

		// create a selection with class A
		List<Object> selection = new ArrayList<>();
		selection.add(_A);

		// Test directly connected Elements
		DisplayTracesHandler.setTraceViewTransitive(false);
		DiagramTextProviderHandler provider = new DiagramTextProviderHandler();
		String DirectlyConnectedElements = provider.getDiagramText(selection);
		assertTrue(DirectlyConnectedElements.equals(EXPECTED_TEXT_FOR_DIRECT_CONNECTIONS));

		// Test transitively connected Elements
		DisplayTracesHandler.setTraceViewTransitive(true);
		String transitivelysConnectedElements = provider.getDiagramText(selection);
		assertTrue(transitivelysConnectedElements.equals(EXPECTED_TEXT_FOR_TRANSITIVE_CONNECTIONS));

	}

	private void removeTraceModel(ResourceSet rs) {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		EObject tm = persistenceAdapter.getTraceModel(rs);
		rs.getResources().remove(tm.eResource());
	}

}
