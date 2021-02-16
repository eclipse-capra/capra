package org.eclipse.capra.testsuite;

import static org.eclipse.capra.testsupport.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsupport.TestHelper.createEClassInEPackage;
import static org.eclipse.capra.testsupport.TestHelper.createEmptyFileInProject;
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.generic.tracemodel.RelatedTo;
import org.eclipse.capra.generic.tracemodel.TracemodelFactory;
import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.testsupport.TestHelper;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests to cover the helper methods in EMFHelper, ArtifactHelper
 * 
 * @author Mihaela Grubii
 *
 */
public class TestEMFArtifactHelper {
	private static final String MODEL_A_NAME = "modelA";
	private static final String MODEL_C_NAME = "modelC";
	private static final String FILE_A = "FILE_A";
	private static final String FILE_B = "FILE_B";
	private static final String FILE_C = "FILE_C";
	private static final String TEST_PROJECT_NAME = "TestProject";
	private static final String T_LINK_NAME_A = "tlinkA";
	private static final String TRACE_MODEL_NAME = "traceModel";
	private static final String CLASS_A_NAME = "A";
	private static final String CLASS_B_NAME = "B";
	private static final String CLASS_C_NAME = "C";
	private static final String linkName = "linkA";
	private static final String MODEL_A_FILENAME = "modelA.ecore";
	private static final String MODEL_C_FILENAME = "modelC.ecore";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
		purgeModels();
	}

	@Test
	public void testGetIdentifierAndGetAnyNameAtributForHelperMethods() throws CoreException, IOException {

		// create a project
		createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// create an Ecore model with three classes
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		// create a mock trace model with one trace type
		EPackage tm = TestHelper.createEcoreModel(TRACE_MODEL_NAME);
		createEClassInEPackage(tm, T_LINK_NAME_A);

		// sace project
		save(testProject, a);
		save(testProject, tm);

		// get existing trace type from trace model
		RelatedTo tlinkA = TracemodelFactory.eINSTANCE.createRelatedTo();
		tlinkA.setName(linkName);

		// create the artifactModel
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().orElseThrow();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(EditingDomainHelper.getResourceSet());

		// test getIdentifier for a RelatedTo EObject for returning the type of
		// the EObject with appended identifier
		String response1 = EMFHelper.getIdentifier(tlinkA);
		assertTrue(response1 + " contains :", response1.contains(":"));

		assertTrue(response1 + " contains RelatedTo", response1.contains(tlinkA.eClass().getName()));

		assertTrue(response1 + " contains name", response1.contains(linkName));

		// create similar object tlinkC and verify that the identifiers are not equal
		RelatedTo tlinkC = TracemodelFactory.eINSTANCE.createRelatedTo();
		assertNotEquals(EMFHelper.getIdentifier(tlinkA), EMFHelper.getIdentifier(tlinkC));

		// test getIdentifier for an ArtifactWrapperContainer EObject that has all
		// attributes as null
		String response2 = EMFHelper.getIdentifier(artifactModel);
		assertEquals(response2, "ArtifactWrapperContainer");

		// create a null object
		RelatedTo tlinkB = null;

		// test getIdentifier for for null value
		response1 = EMFHelper.getIdentifier(tlinkB);
		assertEquals(response1, "<null>");

		// test getIdentifier for an object with a valid attribute origin, invalid name
		RelatedTo tlinkD = TracemodelFactory.eINSTANCE.createRelatedTo();
		tlinkD.setOrigin(artifactModel);
		response1 = EMFHelper.getIdentifier(tlinkD);
		assertTrue(response1 + " contains : RelatedTo", response1.contains(": RelatedTo"));

		// test tryGetAnyAttribute for a RelateTo object with no name attribute
		assertEquals(EMFHelper.tryGetNameAttribute(tlinkD, new StringBuilder()), false);

		// adding name attribute to the object
		tlinkD.setName(linkName);
		StringBuilder strb = new StringBuilder();

		// verify if the created object of type string builder is empty
		assertTrue(strb.toString().isEmpty());

		// after adding name to the object tryGetNameAttribute for tlinkD should be true
		assertEquals(EMFHelper.tryGetNameAttribute(tlinkD, strb), true);

		// verify that string builder has been altered and equals to name
		assertEquals(strb.toString(), linkName);

		// create a non empty string builder
		StringBuilder response3 = new StringBuilder().append("uniqueId");
		assertFalse(response3.toString().isEmpty());

		// after adding name tryGetNameAttribute for tlinkA should be a true assumption
		assertEquals(EMFHelper.tryGetNameAttribute(tlinkD, response3), true);

		// verify that string builder response3 has been altered
		assertNotEquals(response3, new StringBuilder().append("uniqueId"));
		assertTrue(response3.toString().contains("uniqueId"));
		assertTrue(response3.toString().contains(linkName));

		// create an empty string builder
		StringBuilder stra = new StringBuilder();
		RelatedTo tlinkE = TracemodelFactory.eINSTANCE.createRelatedTo();

		// test tryGetAnyAttribute for a RelateTo object with a valid attribute list and
		// empty string builder
		assertEquals(EMFHelper.tryGetAnyAttribute(tlinkE, tlinkE.eClass().getEAllAttributes(), stra), true);

		// test that string builder has changed
		assertFalse(stra.toString().isEmpty());

		// adding name attribute to the tlinkE object
		tlinkE.setName(linkName);
		stra = new StringBuilder();

		// after adding name tryGetAnyAttribute for tlinkA should be a true assumption
		assertEquals(EMFHelper.tryGetAnyAttribute(tlinkE, tlinkE.eClass().getEAllAttributes(), stra), true);

		// validating that the string builder is not empty anymore
		assertFalse(stra.toString().isEmpty());

		// create a new string builder
		stra = new StringBuilder().append(linkName);
		// test tryGetAnyAttribute for valid list of attributes when string builder is
		// not empty
		assertEquals(EMFHelper.tryGetAnyAttribute(tlinkE, tlinkE.eClass().getEAllAttributes(), stra), true);

		// test that string builder has been modified but it still contains name
		assertTrue(stra.toString().contains(linkName));
		assertNotEquals(stra.toString(), new StringBuilder().append(linkName).toString());

		// test tryGetAnyAttribute for tlinkA with an empty list of attributes
		assertEquals(EMFHelper.tryGetAnyAttribute(tlinkE, Collections.emptyList(), new StringBuilder()), false);

		// test tryGetAnyAttribute for tlinkA with only one string attribute
		assertEquals(EMFHelper.tryGetAnyAttribute(tlinkE, tlinkA.eClass().getEAllAttributes().subList(0, 1),
				new StringBuilder()), true);

		// test tryGetAnyAttribute for tlinkA with null stringName and NonString Name
		// for an eObject with no attributes
		assertEquals(
				EMFHelper.tryGetAnyAttribute(tlinkE, artifactModel.eClass().getEAllAttributes(), new StringBuilder()),
				false);

		RelatedTo tlinkF = TracemodelFactory.eINSTANCE.createRelatedTo();
		// test getNameAttribute for an object with no name attribute
		assertTrue(tlinkF + " is empty", EMFHelper.getNameAttribute(tlinkF).isEmpty());

		tlinkF.setName(linkName);
		// test getNameAttribute for an object with name attribute as linkA
		assertEquals(EMFHelper.getNameAttribute(tlinkF), linkName);

		// has same identifier comparison
		assertTrue(EMFHelper.hasSameIdentifier(tlinkE, tlinkE));

		tlinkE.setName("LinkB");
		// has different identifiers
		assertFalse(EMFHelper.hasSameIdentifier(tlinkE, tlinkF));
	}

	@Test
	public void testWrappingAndUnwrappingForObjectsOfTypeEObjectForClassArtifactHelper()
			throws CoreException, IOException {
		// create a project
		createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// create 2 models and persist them
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		createEClassInEPackage(a, CLASS_B_NAME);
		save(testProject, a);

		EPackage c = TestHelper.createEcoreModel(MODEL_C_NAME);
		createEClassInEPackage(c, CLASS_A_NAME);
		save(testProject, c);

		// load the models and select the classes
		ResourceSet rs = new ResourceSetImpl();
		EPackage _a = load(testProject, MODEL_A_FILENAME, rs);
		EPackage _c = load(testProject, MODEL_C_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);
		EClass _A = (EClass) _a.getEClassifier(CLASS_A_NAME);
		EClass _B = (EClass) _a.getEClassifier(CLASS_B_NAME);
		EClass _C = (EClass) _c.getEClassifier(CLASS_C_NAME);

		// create direct trace from class A to B
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(_B);
		TestHelper.createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// check if a trace has been created successfully
		assertTrue(thereIsATraceBetween(_A, _B));

		// create artifact model ad helper
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);

		// test create wrappers and verify that wrappers are created for given artifacts
		List<Object> artifactsA_B = new ArrayList<Object>(Arrays.asList(_A, _B));
		int size = artifactsA_B.size();
		assertFalse(String.format("there are %d artifacts instead of 0", size), artifactsA_B.isEmpty());
		List<EObject> wrappers = artifactHelper.createWrappers(artifactsA_B);

		// returned wrappers should be of the same size
		assertEquals(wrappers.size(), size);

		// wrapper should contain all the wrapped artifacts
		artifactsA_B.forEach(e -> assertTrue(wrappers + " contains " + e, wrappers.contains(e)));

		// verify create wrappers for an empty list
		List<EObject> emptyWrappers = artifactHelper.createWrappers(Collections.emptyList());
		assertTrue(emptyWrappers.isEmpty());

		// verify create wrappers from a non empty list with elements with no handlers
		List<Object> noWrappersList = new ArrayList<>();
		noWrappersList.add(new StringBuilder().append("elem1"));
		noWrappersList.add(new StringBuilder().append("elem2"));

		// for the list with no handlers the response should be empty
		List<EObject> wrappers2 = artifactHelper.createWrappers(noWrappersList);
		assertTrue(wrappers2.isEmpty());

		// verify if a createWrapper and createWrappers have consistent output
		EObject wrapper1 = artifactHelper.createWrapper(_A);

		// if wrappers list contains an artifact, then a single create wrapper for the
		// same artifact will be included in wrappers list
		assertEquals(wrappers.contains(_A), true);
		assertEquals(wrapper1, _A);
		assertTrue(wrappers + " contains " + wrapper1, wrappers.contains(wrapper1));

		// verify create wrapper for no handler artifact
		EObject wrapper2 = artifactHelper.createWrapper(noWrappersList.get(0));
		assertEquals(wrapper2, null);

		// wrap and unwrap the artifact to verify if a createWrapper and createWrappers
		// have consistent output with unwrapWrapper
		EObject wrapper3 = artifactHelper.createWrapper(_B);
		Object result3 = artifactHelper.unwrapWrapper(wrapper3);

		// verify unwrapWrapper for a valid input parameter and verify that the object
		// is changed into original artifact
		assertTrue(result3 instanceof EClass);
		assertEquals(_B, result3);

		// verify unwrapWrapper for null value
		result3 = artifactHelper.unwrapWrapper(null);
		assertEquals(result3, null);

		// verify unwrapWrapper for an unwrapped object
		result3 = artifactHelper.unwrapWrapper(_C);
		assertEquals(result3, _C);

	}

	@Test
	public void testWrappingAndUnwrappingForObjectsOfTypeFileForArtifactHelper() throws CoreException, IOException {
		// create a project
		createSimpleProject(TEST_PROJECT_NAME);
		// create some files in the project
		IFile fileA = createEmptyFileInProject(FILE_A, TEST_PROJECT_NAME);
		IFile fileB = createEmptyFileInProject(FILE_B, TEST_PROJECT_NAME);
		IFile fileC = createEmptyFileInProject(FILE_C, TEST_PROJECT_NAME);

		// create a trace link between File A and B
		SelectionView.getOpenedView().dropToSelection(fileA);
		SelectionView.getOpenedView().dropToSelection(fileB);
		assertFalse(thereIsATraceBetween(fileA, fileB));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// check if a trace has been created successfully
		assertTrue(thereIsATraceBetween(fileA, fileB));

		// check that the artifact wrappers have been created
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);

		// test create wrappers and verify that wrappers are created for a file
		List<Object> artifactsA_B = new ArrayList<Object>(Arrays.asList(fileA, fileB));
		int size = artifactsA_B.size();

		// the size should be greater than 0
		assertFalse(String.format("there are %d artifacts instead of 0", size), artifactsA_B.isEmpty());
		List<EObject> wrappers = artifactHelper.createWrappers(artifactsA_B);

		// returned wrappers should be of the same size
		assertEquals(wrappers.size(), size);

		// wrapper should contain all the wrapped artifacts
		artifactsA_B.forEach(
				e -> assertTrue(wrappers + " contains " + e, wrappers.contains(artifactHelper.createWrapper(e))));

		// verify if a createWrapper and createWrappers have consistent output
		EObject wrapper1 = artifactHelper.createWrapper(fileA);

		// if wrappers list contains an artifact, then a single create wrapper for the
		// same artifact will be included in wrappers list
		assertTrue(wrappers + " contains " + wrapper1, wrappers.contains(wrapper1));

		// wrap and unwrap the artifact to verify if a createWrapper and createWrappers
		// have consistent output
		EObject wrapper3 = artifactHelper.createWrapper(fileB);
		Object result3 = artifactHelper.unwrapWrapper(wrapper3);

		// verify unwrapWrapper for a valid input parameter and verify that the object
		// is changed to original artifact
		assertTrue(result3 instanceof IFile);
		assertEquals(fileB, result3);

		// verify unwrapWrapper for an unwrapped object of type file
		result3 = artifactHelper.unwrapWrapper(fileC);
		assertEquals(result3, fileC);

	}

	@Test
	public void testGetArtifactLabelArtifactHelper() throws CoreException, IOException {

		// create a project
		createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// create an Ecore model with five three classes
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		// create a mock trace model with one trace type
		EPackage tm = TestHelper.createEcoreModel(TRACE_MODEL_NAME);
		createEClassInEPackage(tm, T_LINK_NAME_A);

		save(testProject, a);
		save(testProject, tm);

		// get existing trace type from trace model
		RelatedTo tlinkA = TracemodelFactory.eINSTANCE.createRelatedTo();
		tlinkA.setName(linkName);

		// create the artifactModel
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		EClass _A = (EClass) a.getEClassifier(CLASS_A_NAME);

		// test getArtifactLabel for an object without handler available
		assertEquals(artifactHelper.getArtifactLabel(_A), "Unknown (no fitting artifact handler found)");

		// test getArtifactLabel for a wrapper object
		EObject wrapper1 = artifactHelper.createWrapper(tlinkA);
		String label1 = artifactHelper.getArtifactLabel(wrapper1);
		assertEquals(label1, EMFHelper.getIdentifier(tlinkA));

		// test getArtifactLabel for RelatedTo object
		String label2 = artifactHelper.getArtifactLabel(tlinkA);
		assertEquals(label2, EMFHelper.getIdentifier(tlinkA));

		// test getArtifactLabel for artifact helper with no artifacts
		String label3 = artifactHelper.getArtifactLabel(artifactModel);
		assertEquals(label3, EMFHelper.getIdentifier(artifactModel));
	}

	@Test
	public void testGetArtifactLocationForArtifactHelper() throws CoreException, IOException {
		// create a project
		createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// create 2 models and persist them
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		createEClassInEPackage(a, CLASS_B_NAME);
		save(testProject, a);

		EPackage c = TestHelper.createEcoreModel(MODEL_C_NAME);
		createEClassInEPackage(c, CLASS_A_NAME);
		save(testProject, c);

		// load the models and select the 2 classes
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, MODEL_A_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);
		EClass _A = (EClass) _a.getEClassifier(CLASS_A_NAME);
		EClass _B = (EClass) _a.getEClassifier(CLASS_B_NAME);

		// create direct trace from A to B
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(_B);
		TestHelper.createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// create the artifactModel
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);

		// test getArtifactLocation for a wrapper EObject
		EObject wrapper1 = artifactHelper.createWrapper(artifactModel);
		String uri1 = artifactHelper.getArtifactLocation(wrapper1);
		assertTrue(uri1 + " contains platform:/resource", uri1.contains("platform:/resource"));

		// test getArtifactLocation for another EObject
		String uri2 = artifactHelper.getArtifactLocation(artifactModel);
		assertTrue(uri1 + " contains platform:/resource", uri2.contains("platform:/resource"));

		// test getArtifactLocation for a different object type
		String uri3 = artifactHelper.getArtifactLocation(_A);
		assertEquals(uri3, null);
	}

}
