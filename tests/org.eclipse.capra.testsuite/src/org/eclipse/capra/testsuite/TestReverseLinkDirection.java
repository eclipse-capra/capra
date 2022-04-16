package org.eclipse.capra.testsuite;

import static org.eclipse.capra.testsupport.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsupport.TestHelper.createJavaClassInFolder;
import static org.eclipse.capra.testsupport.TestHelper.createJavaProject;
import static org.eclipse.capra.testsupport.TestHelper.createJavaProjectWithASingleJavaClass;
import static org.eclipse.capra.testsupport.TestHelper.createSimpleProject;
import static org.eclipse.capra.testsupport.TestHelper.createSourceFolder;
import static org.eclipse.capra.testsupport.TestHelper.createTraceForCurrentSelectionOfType;
import static org.eclipse.capra.testsupport.TestHelper.getConnectionsFrom;
import static org.eclipse.capra.testsupport.TestHelper.projectExists;
import static org.eclipse.capra.testsupport.TestHelper.purgeModels;
import static org.eclipse.capra.testsupport.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsupport.TestHelper.thereIsATraceBetween;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.generic.artifactmodel.ArtifactWrapper;
import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.junit.Before;
import org.junit.Test;

public class TestReverseLinkDirection {

	private static final String TEST_PROJECT_NAME_JAVA_A = "TestProject_A_java";
	private static final String TEST_PROJECT_NAME_JAVA_B = "TestProject_B_java";
	private static final String TEST_CLASS_NAME_A = "TestClassA";
	private static final String TEST_CLASS_NAME_B = "TestClassB";
	private static final String TEST_CLASS_NAME_C = "TestClassC";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
		purgeModels();
	}

	@Test
	public void testReverseIsThereATraceBetween() throws CoreException, IOException {
		// Create a java project
		IType javaClassA = createJavaProjectWithASingleJavaClass(TEST_PROJECT_NAME_JAVA_A);
		assertTrue(projectExists(TEST_PROJECT_NAME_JAVA_A));

		// Create another Jave project
		IType javaClassB = createJavaProjectWithASingleJavaClass(TEST_PROJECT_NAME_JAVA_B);
		assertTrue(projectExists(TEST_PROJECT_NAME_JAVA_B));

		// Drop the JavaClass in the selection view
		SelectionView.getOpenedView().dropToSelection(javaClassA);
		// Drop the c File in the selection view
		SelectionView.getOpenedView().dropToSelection(javaClassB);

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(javaClassA, javaClassB));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(javaClassA, javaClassB));

		// Check that if we do not ignore direction, we don't see the link
		assertFalse(thereIsATraceBetween(javaClassB, javaClassA));

		// If link direction is reversed, the original trace should not be found
		assertFalse(thereIsATraceBetween(javaClassA, javaClassB, true));

		// Check that we see the link if we ignore directionality
		assertTrue(thereIsATraceBetween(javaClassB, javaClassA, true));
	}

	@Test
	public void testReverseGetConnectedElements() throws CoreException, IOException {
		// Create a java project
		IJavaProject projectA = createJavaProject(createSimpleProject(TEST_PROJECT_NAME_JAVA_A));
		assertTrue(projectExists(TEST_PROJECT_NAME_JAVA_A));

		IFolder sourceFolderA = createSourceFolder(projectA.getProject());
		IType javaClassA = createJavaClassInFolder(projectA, sourceFolderA, TEST_CLASS_NAME_A);

		// Create another Jave project
		IJavaProject projectB = createJavaProject(createSimpleProject(TEST_PROJECT_NAME_JAVA_B));
		assertTrue(projectExists(TEST_PROJECT_NAME_JAVA_B));

		IFolder sourceFolderB = createSourceFolder(projectB.getProject());
		IType javaClassB = createJavaClassInFolder(projectB, sourceFolderB, TEST_CLASS_NAME_B);
		IType javaClassC = createJavaClassInFolder(projectB, sourceFolderB, TEST_CLASS_NAME_C);

		SelectionView.getOpenedView().dropToSelection(javaClassA);
		SelectionView.getOpenedView().dropToSelection(javaClassB);

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(javaClassA, javaClassB));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(javaClassA, javaClassB));

		SelectionView.getOpenedView().clearSelection();
		SelectionView.getOpenedView().dropToSelection(javaClassA);
		SelectionView.getOpenedView().dropToSelection(javaClassC);

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(javaClassA, javaClassC));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(javaClassA, javaClassC));

		List<Connection> connectionsFromA = getConnectionsFrom(javaClassA, false);
		assertEquals(2, connectionsFromA.size());

		List<Connection> connectionsFromC = getConnectionsFrom(javaClassC, false);
		assertTrue(connectionsFromC.isEmpty());

		connectionsFromC = getConnectionsFrom(javaClassC, true);
		assertEquals(1, connectionsFromC.size());

		assertEquals(1, connectionsFromC.get(0).getOrigins().size());
		ArtifactWrapper javaClassCWrapper = (ArtifactWrapper) connectionsFromC.get(0).getOrigins().get(0);

		assertEquals(TEST_CLASS_NAME_A, javaClassCWrapper.getName());
	}

}
