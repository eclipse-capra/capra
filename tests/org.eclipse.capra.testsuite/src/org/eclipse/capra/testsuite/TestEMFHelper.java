package org.eclipse.capra.testsuite;

import static org.eclipse.capra.testsuite.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsuite.TestHelper.createEClassInEPackage;
import static org.eclipse.capra.testsuite.TestHelper.createSimpleProject;
import static org.eclipse.capra.testsuite.TestHelper.getProject;
import static org.eclipse.capra.testsuite.TestHelper.projectExists;
import static org.eclipse.capra.testsuite.TestHelper.purgeModels;
import static org.eclipse.capra.testsuite.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsuite.TestHelper.save;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.generic.tracemodel.RelatedTo;
import org.eclipse.capra.generic.tracemodel.TracemodelFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests to cover the helper methods in EMFHelper
 * 
 * @author Mihaela Grubii
 *
 */
public class TestEMFHelper {
	private static final String MODEL_A_NAME = "modelA";

	private static final String TEST_PROJECT_NAME = "TestProject";
	private static final String T_LINK_NAME_A = "tlinkA";
	private static final String TRACE_MODEL_NAME = "traceModel";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
		purgeModels();
	}

	@Test
	public void testGetIdentifierAndGetAnyNameAtributHelperMethodse() throws CoreException, IOException {

		// Create a project
		createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// Create an Ecore model with five three classes
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		// create a mock trace model with one trace type
		EPackage tm = TestHelper.createEcoreModel(TRACE_MODEL_NAME);
		createEClassInEPackage(tm, T_LINK_NAME_A);

		save(testProject, a);
		save(testProject, tm);

		// get existing trace type from trace model
		RelatedTo tlinkA = TracemodelFactory.eINSTANCE.createRelatedTo();
		tlinkA.setName("LinkA");

		// Create the artifactModel
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().orElseThrow();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(EditingDomainHelper.getResourceSet());

		// test getIdentifier for a RelatedTo EObject for returning the type of
		// the EObject with appended identifier
		String response1 = EMFHelper.getIdentifier(tlinkA);
		assertTrue(response1 + " contains :", response1.contains(":"));

		assertTrue(response1 + " contains RelatedTo", response1.contains(tlinkA.eClass().getName()));

		assertTrue(response1 + " contains name", response1.contains("LinkA"));

		// create similar object tlinkC and verify that the identifiers are not equal
		RelatedTo tlinkC = TracemodelFactory.eINSTANCE.createRelatedTo();
		assertNotEquals(EMFHelper.getIdentifier(tlinkA), EMFHelper.getIdentifier(tlinkC));

		// test getIdentifier for and ArtifactWrapperContainer EObject that has all
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
		tlinkD.setName("linkD");
		StringBuilder strb = new StringBuilder();

		// the string builder verifying if empty
		assertTrue(strb.toString().isEmpty());

		// after adding name to the object tryGetNameAttribute for tlinkD should be true
		assertEquals(EMFHelper.tryGetNameAttribute(tlinkD, strb), true);

		// verify that string builder has been altered and equals to name
		assertEquals(strb.toString(), "linkD");

		// create a non empty string builder
		StringBuilder response3 = new StringBuilder().append("uniqueId");
		assertFalse(response3.toString().isEmpty());
		// save old value

		// after adding name tryGetNameAttribute for tlinkA should be a true assumption
		assertEquals(EMFHelper.tryGetNameAttribute(tlinkD, response3), true);

		// verify that string builder is not altered equals to the old value
		assertNotEquals(response3.toString(), new StringBuilder().append("uniqueId").toString());
		assertNotEquals(response3.toString(), "linkD");

		StringBuilder stra = new StringBuilder();
		RelatedTo tlinkE = TracemodelFactory.eINSTANCE.createRelatedTo();

		// string builder is empty
		assertTrue(stra.toString().isEmpty());

		// test tryGetAnyAttribute for a RelateTo object with a valid attribute list and
		// empty string builder
		assertEquals(EMFHelper.tryGetAnyAttribute(tlinkE, tlinkE.eClass().getEAllAttributes(), stra), true);

		// test that string builder has changed
		assertFalse(stra.toString().isEmpty());

		// adding name attribute to the tlinkE object
		tlinkE.setName("linkE");
		stra = new StringBuilder();

		// after adding name tryGetAnyAttribute for tlinkA should be a true assumption
		assertEquals(EMFHelper.tryGetAnyAttribute(tlinkE, tlinkE.eClass().getEAllAttributes(), stra), true);

		// validating that the string builder contains the name of the attribute and has
		// been modified and not empty
		assertFalse(stra.toString().isEmpty());

		stra = new StringBuilder().append("test");
		// test tryGetAnyAttribute for valid list of attributes when string builder is
		// not empty
		assertEquals(EMFHelper.tryGetAnyAttribute(tlinkE, tlinkE.eClass().getEAllAttributes(), stra), true);

		// test that string builder has been modified but it still contains test
		assertTrue(stra.toString().contains("test"));
		assertNotEquals(stra.toString(), new StringBuilder().append("test").toString());

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

		tlinkF.setName("LinkA");
		// test getNameAttribute for an object with name attribute as linkA
		assertEquals(EMFHelper.getNameAttribute(tlinkF), "LinkA");

		// has same identifier comparison
		assertTrue(EMFHelper.hasSameIdentifier(tlinkE, tlinkE));

		// has different identifiers
		assertFalse(EMFHelper.hasSameIdentifier(tlinkE, tlinkF));
	}

}
