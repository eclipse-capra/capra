package org.eclipse.capra.testsuite;

import static org.eclipse.capra.testsupport.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsupport.TestHelper.createEClassInEPackage;
import static org.eclipse.capra.testsupport.TestHelper.createSimpleProject;
import static org.eclipse.capra.testsupport.TestHelper.getProject;
import static org.eclipse.capra.testsupport.TestHelper.load;
import static org.eclipse.capra.testsupport.TestHelper.projectExists;
import static org.eclipse.capra.testsupport.TestHelper.purgeModels;
import static org.eclipse.capra.testsupport.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsupport.TestHelper.save;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.generic.tracemodel.RelatedTo;
import org.eclipse.capra.generic.tracemodel.TracemodelFactory;
import org.eclipse.capra.testsupport.TestHelper;
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
 * Unit tests to cover traceability link used in to retain independence of a
 * concrete trace.
 * 
 * @author Mihaela Grubii
 *
 */
public class TestUnitCoreAdaptersConnection {
	private static final String CLASS_A_NAME = "A";
	private static final String CLASS_B_NAME = "B";
	private static final String CLASS_C_NAME = "C";
	private static final String CLASS_D_NAME = "D";

	private static final String MODEL_A_FILENAME = "modelA.ecore";
	private static final String MODEL_A_NAME = "modelA";

	private static final String TEST_PROJECT_NAME = "TestProject";
	private static final String T_LINK_NAME_A = "tlinkA";
	private static final String TRACE_MODEL_NAME = "traceModel";
	private static final String TRACE_MODEL_FILENAME = "traceModel.ecore";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
		purgeModels();
	}

	@Test
	public void testConnectionEqualsForContractEquivalentValidations() throws CoreException, IOException {

		// Create a project
		createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// Create an Ecore model with five three classes
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		createEClassInEPackage(a, CLASS_B_NAME);
		createEClassInEPackage(a, CLASS_C_NAME);

		// create a mock trace model with one trace type
		EPackage tm = TestHelper.createEcoreModel(TRACE_MODEL_NAME);
		createEClassInEPackage(tm, T_LINK_NAME_A);

		save(testProject, a);
		save(testProject, tm);

		// get existing trace type from trace model
		RelatedTo tlinkA = TracemodelFactory.eINSTANCE.createRelatedTo();

		// Load the models and select the classes
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, MODEL_A_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);

		EPackage _tm = load(testProject, TRACE_MODEL_FILENAME, rs);
		assertEquals(_tm.getName(), TRACE_MODEL_NAME);

		EClass classA = (EClass) _a.getEClassifier(CLASS_A_NAME);

		EClass classB = (EClass) _a.getEClassifier(CLASS_B_NAME);

		EClass classC = (EClass) _a.getEClassifier(CLASS_C_NAME);

		EClass tlinkB = (EClass) _tm.getEClassifier(T_LINK_NAME_A);

		List<EObject> targets = new ArrayList<EObject>();
		targets.add(classB);
		targets.add(classC);

		List<EObject> targetsAll = new ArrayList<EObject>();
		targetsAll.add(classA);
		targetsAll.add(classB);
		targetsAll.add(classC);

		// create a connection
		Connection con1 = new Connection(Arrays.asList(classA), targets, tlinkB);
		// create a connection
		Connection con2 = new Connection(Arrays.asList(classA), targets, tlinkB);
		// create a connection
		Connection con3 = new Connection(Arrays.asList(classA), targets, tlinkB);

		// create a connection
		Connection con4 = new Connection(Arrays.asList(classA), targets, tlinkA);

		// create a connection
		Connection con5 = new Connection(Arrays.asList(classB), targets, tlinkB);
		// create a connection
		Connection con8 = new Connection(Arrays.asList(classC), targets, tlinkB);

		// create a connection
		Connection con6 = new Connection(Arrays.asList(classA), targetsAll, tlinkB);

		// check if the connections equals not null
		assertNotEquals(con1, null);

		// check if the connections not null
		assertNotEquals(con1, classA);

		// check if the con is reflexive
		assertEquals(con1, con1);

		// check if con is symmetric
		assertEquals(con1, con2);
		assertEquals(con1.equals(con2), con2.equals(con1));

		// check if con is transitive
		assertEquals(con1, con2);
		assertEquals(con2, con3);
		assertEquals(con3, con1);

		assertNotEquals(con1, con8);
		assertNotEquals(con8, con1);

		assertNotEquals(con1, con5);
		assertNotEquals(con5, con1);

		// check if con is consistent
		int i = 0;
		while (i <= 3) {
			assertEquals(con1, con2);
			assertNotEquals(con1, con4);
			assertNotEquals(con1, con5);
			assertEquals(con1, con6);
			i++;
		}

		targets.remove(0);
		// create a connection
		Connection con7 = new Connection(Arrays.asList(classA), targets, tlinkB);
		// check if con is consistent when target is being modified
		assertEquals(con1, con7);
		targetsAll.removeAll(targetsAll.subList(0, 2));
		assertEquals(con1, con6);

	}

	@Test
	public void testConnectionHashCodeForContractEquivalentValidations() throws CoreException, IOException {

		// Create a project
		createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// Create an Ecore model with five three classes
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		createEClassInEPackage(a, CLASS_B_NAME);
		createEClassInEPackage(a, CLASS_C_NAME);

		// create a mock trace model with one trace type
		EPackage tm = TestHelper.createEcoreModel(TRACE_MODEL_NAME);
		createEClassInEPackage(tm, T_LINK_NAME_A);

		save(testProject, a);
		save(testProject, tm);

		// get existing trace type from trace model
		RelatedTo tlinkB = TracemodelFactory.eINSTANCE.createRelatedTo();

		// Load the models and select the classes
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, MODEL_A_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);

		EPackage _tm = load(testProject, TRACE_MODEL_FILENAME, rs);
		assertEquals(_tm.getName(), TRACE_MODEL_NAME);

		EClass classA = (EClass) _a.getEClassifier(CLASS_A_NAME);

		EClass classB = (EClass) _a.getEClassifier(CLASS_B_NAME);

		EClass classC = (EClass) _a.getEClassifier(CLASS_C_NAME);

		EClass tlinkA = (EClass) _tm.getEClassifier(T_LINK_NAME_A);

		List<EObject> targets = new ArrayList<EObject>();
		targets.add(classB);
		targets.add(classA);

		List<EObject> targetsC = new ArrayList<EObject>();
		targets.add(classC);

		// create a connection
		Connection con1 = new Connection(Arrays.asList(classA), targets, tlinkA);

		// create a connection
		Connection con2 = new Connection(Arrays.asList(classA), targets, tlinkA);

		// create a connection
		Connection con3 = new Connection(Arrays.asList(classA), targetsC, tlinkA);

		// create an empty origin connection
		Connection emptyOrigin1 = new Connection(null, targets, tlinkA);
		// create an empty origin connection
		Connection emptyOrigin2 = new Connection(null, targets, tlinkA);
		// create an empty origin connection
		Connection emptyOrigin3 = new Connection(null, targetsC, tlinkA);

		// create an empty target connection
		Connection emptyTargetCon1 = new Connection(Arrays.asList(classA), null, tlinkA);
		// create an empty target connection
		Connection emptyTargetCon2 = new Connection(Arrays.asList(classA), null, tlinkA);
		// create an empty target connection
		Connection emptyTargetCon3 = new Connection(Arrays.asList(classB), null, tlinkA);

		// create an empty link connection
		Connection emptyTLinkCon1 = new Connection(Arrays.asList(classA), targets, null);
		// create an empty link connection
		Connection emptyTLinkCon2 = new Connection(Arrays.asList(classA), targets, null);
		// create an empty link connection
		Connection emptyTLinkCon3 = new Connection(Arrays.asList(classB), targets, null);

		// check if the hashcode returns the same when invoked on the same object
		assertEquals(con1.hashCode(), con1.hashCode());

		// check if index changes when the object is changed
		int i = con1.hashCode();
		assertEquals(con1.hashCode(), i);
		targets.remove(0);
		assertNotEquals(con1.hashCode(), i);

		// if two distinct objects are equal the integer result of hashCode is equal
		assertEquals(con1, con2);
		assertEquals(con1.equals(con2), con1.hashCode() == con2.hashCode());

		// if two distinct objects are unequal the integer result of hashCode is not
		// equal
		assertNotEquals(con1, con3);
		assertEquals(con1.equals(con3), con1.hashCode() == con3.hashCode());

		// check if the hashCode() for invalid connections are equals
		assertEquals(emptyOrigin1.hashCode(), emptyOrigin2.hashCode());

		// check if the hashCode() for invalid connections are equals
		assertNotEquals(emptyOrigin2.hashCode(), emptyOrigin3.hashCode());

		// check if the hashCode() for invalid connections are equals
		assertEquals(emptyTargetCon1.hashCode(), emptyTargetCon2.hashCode());

		// check if the hashCode() for invalid connections are equals
		assertNotEquals(emptyTargetCon2.hashCode(), emptyTargetCon3.hashCode());

		// check if the hashCode() for invalid connections are equals
		assertEquals(emptyTLinkCon1.hashCode(), emptyTLinkCon2.hashCode());

		// check if the hashCode() for invalid connections are equals
		assertNotEquals(emptyTLinkCon2.hashCode(), emptyTLinkCon3.hashCode());
	}
}
