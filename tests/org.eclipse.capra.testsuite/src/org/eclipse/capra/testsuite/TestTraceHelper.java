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

import static org.eclipse.capra.testsupport.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsupport.TestHelper.createEmptyFileInProject;
import static org.eclipse.capra.testsupport.TestHelper.createSimpleProject;
import static org.eclipse.capra.testsupport.TestHelper.createTraceForCurrentSelectionOfType;
import static org.eclipse.capra.testsupport.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsupport.TestHelper.thereIsATraceBetween;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.core.helpers.TraceHelper;
import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.Before;
import org.junit.Test;

/**
 * A class to test the helper methods in the {@link TraceHelper} class.
 * 
 * @author Salome Maro
 *
 */

public class TestTraceHelper {

	private static final String PROJECT_NAME = "TestProject";
	private static final String FILE_A = "FILE_A";
	private static final String FILE_B = "FILE_B";
	private static final String FILE_C = "FILE_C";
	private static final String FILE_D = "FILE_D";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
	}

	/**
	 * Tests for the getTraces(List<EObject> artifacts) method.
	 * 
	 * @throws CoreException
	 * @throws BuildException
	 */
	@Test
	public void testgetTracesMethod() throws CoreException, BuildException {

		// create a simple project
		createSimpleProject(PROJECT_NAME);
		// create some files in the project
		IFile fileA = createEmptyFileInProject(FILE_A, PROJECT_NAME);
		IFile fileB = createEmptyFileInProject(FILE_B, PROJECT_NAME);
		IFile fileC = createEmptyFileInProject(FILE_C, PROJECT_NAME);
		IFile fileD = createEmptyFileInProject(FILE_D, PROJECT_NAME);

		// create a trace link between File A and B
		SelectionView.getOpenedView().dropToSelection(fileA);
		SelectionView.getOpenedView().dropToSelection(fileB);
		assertFalse(thereIsATraceBetween(fileA, fileB));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(fileA, fileB));

		// clear selection
		SelectionView.getOpenedView().clearSelection();

		// create a trace link between File B and C
		SelectionView.getOpenedView().dropToSelection(fileB);
		SelectionView.getOpenedView().dropToSelection(fileC);
		assertFalse(thereIsATraceBetween(fileB, fileC));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(fileB, fileC));

		// clear selection
		SelectionView.getOpenedView().clearSelection();

		// create a list with wrappers of File A and B
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		List<Object> artifactsA_B = new ArrayList<Object>(Arrays.asList(fileA, fileB));

		List<EObject> A_B_wrappers = artifactHelper.createWrappers(artifactsA_B);

		// get trace link related to fileA and file B
		EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);
		TraceHelper traceHelper = new TraceHelper(traceModel);
		List<Connection> traceLinks = traceHelper.getTraces(A_B_wrappers);

		// check that only one link is returned
		assertTrue(traceLinks.size() == 1);
		// check that the trace link returned is between file A and file B
		assertEquals(A_B_wrappers.get(0), traceLinks.get(0).getOrigins().get(0));
		assertEquals(A_B_wrappers.get(1), traceLinks.get(0).getTargets().get(0));

		// create a list with wrappers of file A, B and C
		List<Object> artifactsA_B_C = new ArrayList<Object>(Arrays.asList(fileA, fileB, fileC));

		// create wrappers
		List<EObject> A_B_Cwrappers = artifactHelper.createWrappers(artifactsA_B_C);
		// get trace links related to file A, B and C
		List<Connection> traceLinksA_B_C = traceHelper.getTraces(A_B_Cwrappers);

		// check that only two links are returned
		assertTrue(traceLinksA_B_C.size() == 2);
		// check that the first link is between file A and B
		assertEquals(A_B_wrappers.get(0), traceLinksA_B_C.get(0).getOrigins().get(0));
		assertEquals(A_B_wrappers.get(1), traceLinksA_B_C.get(0).getTargets().get(0));
		// check that the second link is between file B and C
		assertEquals(A_B_Cwrappers.get(1), traceLinksA_B_C.get(1).getOrigins().get(0));
		assertEquals(A_B_Cwrappers.get(2), traceLinksA_B_C.get(1).getTargets().get(0));

		// Testing edge cases

		// Null sent to the method
		List<Connection> null_connections = traceHelper.getTraces(null);
		// Check that an empty list is returned.
		assertTrue(null_connections.size() == 0);

		// An element that is not in the trace model
		List<EObject> artifact_D = new ArrayList<EObject>(Arrays.asList(artifactHelper.createWrapper(fileD)));
		List<Connection> traceLinksD = traceHelper.getTraces(artifact_D);
		// Check that an empty list is returned
		assertTrue(traceLinksD.size() == 0);

		// One element in the traceModel and one not in the trace model
		List<Object> artifactsC_D = new ArrayList<Object>(Arrays.asList(fileC, fileD));
		List<EObject> C_D_wrappers = artifactHelper.createWrappers(artifactsC_D);
		// get trace links related to file C and D
		List<Connection> traceLinksC_D = traceHelper.getTraces(C_D_wrappers);
		// check that the list of links is null
		assertTrue(traceLinksC_D.size() == 0);
	}

}
