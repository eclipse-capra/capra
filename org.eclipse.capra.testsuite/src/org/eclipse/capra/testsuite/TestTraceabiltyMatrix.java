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

public class TestTraceabiltyMatrix {

	private final static String EXPECTED_TEXT_FOR_SELECTED_PACKAGES = "@startuml\n" + "salt\n" + "{#\n"
			+ ".|B : EClass|BB : EClass|modelB : EPackage\n" + "A : EClass |X|.|.\n" + "AA : EClass |.|X|.\n"
			+ "modelA : EPackage |.|.|.\n" + "}\n" + "\n" + "@enduml\n";

	private final static String EXPECTED_TEXT_FOR_SELECTED_CLASSES = "@startuml\n" + "salt\n" + "{#\n"
			+ ".|A : EClass|B : EClass|AA : EClass|BB : EClass\n" + "A : EClass |.|X|.|.\n" + "B : EClass |X|.|.|.\n"
			+ "AA : EClass |.|.|.|X\n" + "BB : EClass |.|.|X|.\n" + "}\n" + "\n" + "@enduml\n";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
	}

	@Test
	public void testMatrix() throws CoreException, IOException, InterruptedException {

		// Create a project
		createSimpleProject("TestProject");
		assertTrue(projectExists("TestProject"));

		// Create two models each with two classes and persist them
		IProject testProject = getProject("TestProject");
		EPackage a = TestHelper.createEcoreModel("modelA");
		createEClassInEPackage(a, "A");
		createEClassInEPackage(a, "AA");
		save(testProject, a);

		EPackage b = createEcoreModel("modelB");
		createEClassInEPackage(b, "B");
		createEClassInEPackage(b, "BB");
		save(testProject, b);

		// Load them and choose the four classes
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, "modelA.ecore", rs);
		assertEquals(_a.getName(), "modelA");
		EClass _A = (EClass) _a.getEClassifier("A");
		EClass _AA = (EClass) _a.getEClassifier("AA");

		EPackage _b = load(testProject, "modelB.ecore", rs);
		assertEquals(_b.getName(), "modelB");
		EClass _B = (EClass) _b.getEClassifier("B");
		EClass _BB = (EClass) _b.getEClassifier("BB");

		// Add A and B to the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(_B);
		assertFalse(SelectionView.getOpenedView().getSelection().isEmpty());

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(_A, _B));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
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
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());

		// Remove trace model from resource set to make sure the trace model is
		// re-loaded to capture the second trace link
		removeTraceModel(rs);
		assertTrue(thereIsATraceBetween(_AA, _BB));

		// create a selection with Package A and B
		List<Object> selectedPackages = new ArrayList<>();
		selectedPackages.add(_a);
		selectedPackages.add(_b);

		DiagramTextProviderHandler provider = new DiagramTextProviderHandler();
		String plantUMLTextForSelectedPackages = provider.getDiagramText(selectedPackages);

		assertTrue(plantUMLTextForSelectedPackages.equals(EXPECTED_TEXT_FOR_SELECTED_PACKAGES));

		List<Object> selectedClasses = new ArrayList<>();
		selectedClasses.add(_A);
		selectedClasses.add(_B);
		selectedClasses.add(_AA);
		selectedClasses.add(_BB);

		String plantUMLTextForSelectedClasses = provider.getDiagramText(selectedClasses);
		assertTrue(plantUMLTextForSelectedClasses.equals(EXPECTED_TEXT_FOR_SELECTED_CLASSES));
	}

	private void removeTraceModel(ResourceSet rs) {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		EObject tm = persistenceAdapter.getTraceModel(rs);
		rs.getResources().remove(tm.eResource());
	}

}
