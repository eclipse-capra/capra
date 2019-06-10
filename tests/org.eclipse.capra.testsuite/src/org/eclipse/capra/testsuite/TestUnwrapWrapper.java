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

import static org.eclipse.capra.testsuite.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsuite.TestHelper.createCDTProject;
import static org.eclipse.capra.testsuite.TestHelper.createCSourceFileInProject;
import static org.eclipse.capra.testsuite.TestHelper.createJavaProjectWithASingleJavaClass;
import static org.eclipse.capra.testsuite.TestHelper.createSimpleProject;
import static org.eclipse.capra.testsuite.TestHelper.createTraceForCurrentSelectionOfType;
import static org.eclipse.capra.testsuite.TestHelper.projectExists;
import static org.eclipse.capra.testsuite.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsuite.TestHelper.thereIsATraceBetween;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.junit.Before;
import org.junit.Test;

public class TestUnwrapWrapper {
	private static final String TEST_PROJECT_NAME_JAVA = "TestProject_java";
	private static final String TEST_PROJECT_NAME_C = "TestProject_C";
	private static final String C_FILE_NAME = "CClass.c";
	private static final String SIMPLE_PROJECT_NAME = "Simple_Test_Project";
	private static final String I_FILE_NAME_A = "testFile_A.txt";
	private static final String I_FILE_NAME_B = "testFile_B.txt";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
	}

	@Test
	public void testUnwrapWrapper_Java_and_C() throws CoreException, BuildException {
		// Create a java project
		IType javaClass = createJavaProjectWithASingleJavaClass(TEST_PROJECT_NAME_JAVA);
		assertTrue(projectExists(TEST_PROJECT_NAME_JAVA));

		// Create a C project
		ICProject cProject = createCDTProject(TEST_PROJECT_NAME_C);
		assertTrue(projectExists(TEST_PROJECT_NAME_C));

		// create a C class in the project
		ITranslationUnit cClass = createCSourceFileInProject(C_FILE_NAME, cProject);

		// Drop the JavaClass in the selection view
		SelectionView.getOpenedView().dropToSelection(javaClass);
		// Drop the c Class in the selection view
		SelectionView.getOpenedView().dropToSelection(cClass);

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(javaClass, cClass));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(javaClass, cClass));

		// check that the artifact wrappers have been created
		TracePersistenceAdapter persistentAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		ResourceSet resourceSet = new ResourceSetImpl();
		EObject artifactModel = persistentAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		assertTrue(artifactModel.eContents().size() == 2);

		// get the first artifact wrapper and unwrap
		EObject javaWrapper = artifactModel.eContents().get(0);
		assertTrue(artifactHelper.unwrapWrapper(javaWrapper) instanceof IJavaElement);
		IJavaElement javaElement = (IJavaElement) artifactHelper.unwrapWrapper(javaWrapper);
		// test that the unwrappedObject is equal to the original java class
		assertTrue(javaClass.equals(javaElement));

		// get the second artifact wrapper and unwrap
		EObject cWrapper = artifactModel.eContents().get(1);
		assertTrue(artifactHelper.unwrapWrapper(cWrapper) instanceof ICElement);
		ICElement cElement = (ICElement) artifactHelper.unwrapWrapper(cWrapper);
		// test that the unwrappedObject is equal to the original C class
		assertTrue(cClass.equals(cElement));

	}

	@Test
	public void testUnwrapWrapper_Files() throws CoreException {
		createSimpleProject(SIMPLE_PROJECT_NAME);
		IFile fileA = TestHelper.createEmptyFileInProject(I_FILE_NAME_A, SIMPLE_PROJECT_NAME);
		IFile fileB = TestHelper.createEmptyFileInProject(I_FILE_NAME_B, SIMPLE_PROJECT_NAME);

		// Drop the files in the selection view
		SelectionView.getOpenedView().dropToSelection(fileA);
		SelectionView.getOpenedView().dropToSelection(fileB);

		// Create a trace via the selection view
		assertFalse(thereIsATraceBetween(fileA, fileB));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(fileA, fileB));

		// check that the artifact wrappers have been created
		TracePersistenceAdapter persistentAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		ResourceSet resourceSet = new ResourceSetImpl();
		EObject artifactModel = persistentAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		assertTrue(artifactModel.eContents().size() == 2);

		// get the first artifact wrapper and unwrap
		EObject fileAWrapper = artifactModel.eContents().get(0);
		assertTrue(artifactHelper.unwrapWrapper(fileAWrapper) instanceof IFile);
		IFile unwrapedFileA = (IFile) artifactHelper.unwrapWrapper(fileAWrapper);
		// test that the unwrappedObject is equal to the original file
		assertTrue(fileA.equals(unwrapedFileA));

		// get the second artifact wrapper and unwrap
		EObject fileBWrapper = artifactModel.eContents().get(1);
		assertTrue(artifactHelper.unwrapWrapper(fileBWrapper) instanceof IFile);
		IFile unwrapedFileB = (IFile) artifactHelper.unwrapWrapper(fileBWrapper);
		// test that the unwrappedObject is equal to the original file
		assertTrue(fileB.equals(unwrapedFileB));

	}

}
