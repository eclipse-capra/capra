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
import static org.eclipse.capra.testsupport.TestHelper.purgeModels;
import static org.eclipse.capra.testsupport.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsupport.TestHelper.thereIsATraceBetween;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.capra.core.adapters.IPersistenceAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.ui.preferences.CapraPreferences;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * A class to test the traces project renaming functionality through capra
 * preferences.
 * 
 * @author Mihaela Grubii
 *
 */

public class TestTraceProjectRename {

	private static final String PROJECT_NAME = "TestProject";
	private static final String FILE_A = "FILE_A";
	private static final String FILE_B = "FILE_B";
	private static final String FILE_C = "FILE_C";
	private static final String PREFERENCE_NAME = org.eclipse.capra.core.preferences.CapraPreferences.CAPRA_PERSISTENCE_PROJECT_NAME;
	private static final String OLD_VALID_PROJECT_NAME = "_TraceProjectTest";
	private static final String NEW_VALID_PROJECT_NAME = "ProjectWithNewName1";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
		purgeModels();
	}

	@After
	public void destroy() {
		CapraPreferences.getPreferences().setValue(PREFERENCE_NAME, "");
	}

	/**
	 * Test valid input for Persistence Project Name preference.
	 * 
	 * @throws CoreException
	 * @throws BuildException
	 */
	@Test
	public void testRenameTraceProject() throws CoreException, BuildException {

		// set a value for the project name capra preferences
		CapraPreferences pref = new CapraPreferences();
		IWorkbench workbench = PlatformUI.getWorkbench();
		pref.init(workbench);
		// input new value for the project name preference
		CapraPreferences.getPreferences().setValue(PREFERENCE_NAME, OLD_VALID_PROJECT_NAME);

		// verify if project and traces should be saved successfully
		assertTrue("Saving traces and artifacts successfully ", pref.performOk());

		// create a simple project
		createSimpleProject(PROJECT_NAME);
		// create some files in the project
		IFile fileA = createEmptyFileInProject(FILE_A, PROJECT_NAME);
		IFile fileB = createEmptyFileInProject(FILE_B, PROJECT_NAME);
		IFile fileC = createEmptyFileInProject(FILE_C, PROJECT_NAME);

		// drop files File A and B in the selection view
		SelectionView.getOpenedView().dropToSelection(fileA);
		SelectionView.getOpenedView().dropToSelection(fileB);

		// create a trace link
		assertFalse(thereIsATraceBetween(fileA, fileB));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(fileA, fileB));

		// get the project with the inputed value
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(OLD_VALID_PROJECT_NAME);

		// verify that project with expected name is created
		assertTrue("Newly renamed project exists", project.exists());

		// verify that resourceSet traceModel and artifactModel are not empty
		IPersistenceAdapter persistenceAdapter = ExtensionPointHelper.getPersistenceAdapter().orElseThrow();
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		// get trace model of the resource set
		EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);
		// get artifact model of the resource set
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		List<EObject> traceModelContentsList = traceModel.eContents();
		List<EObject> traceModelArtifactList = artifactModel.eContents();

		// verifying contents of the traceModel and artifactMoodel
		assertFalse("Trace model contents is not an empty list", traceModelContentsList.isEmpty());
		assertFalse("Artifact model contents is not an empty list", traceModelArtifactList.isEmpty());

		// verify expected files exist
		assertTrue(project.getFile("traceModel.xmi").exists());
		assertTrue(project.getFile("artifactWrappers.xmi").exists());

		// clear selection
		SelectionView.getOpenedView().clearSelection();

		// input new value for the project name preference
		CapraPreferences.getPreferences().setValue(PREFERENCE_NAME, NEW_VALID_PROJECT_NAME);

		// the project and traces should be saved successfully
		assertTrue("Saving traces and artifacts successfully", pref.performOk());

		workbench = PlatformUI.getWorkbench();
		pref.init(workbench);

		// get the project with the inputed value
		IProject projectNew = ResourcesPlugin.getWorkspace().getRoot().getProject(NEW_VALID_PROJECT_NAME);

		// the project and traces should be saved successfully
		assertTrue("Saving traces and artifacts successfully", pref.performOk());

		// verify that project with expected name is created
		assertTrue("Newly renamed project exists", projectNew.exists());

		List<EObject> traceModelContentsList_New1 = traceModel.eContents();
		List<EObject> traceModelArtifactList_New1 = artifactModel.eContents();

		// verifying contents of the traceModel and artifactMoodel are not empty
		assertFalse("Trace model contents is not an empty list", traceModelContentsList_New1.isEmpty());
		assertFalse("Artifact model contents is not an empty list", traceModelArtifactList_New1.isEmpty());

		// create a trace link between File B and C
		SelectionView.getOpenedView().dropToSelection(fileB);
		SelectionView.getOpenedView().dropToSelection(fileC);
		assertFalse(thereIsATraceBetween(fileB, fileC));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(fileB, fileC));

		workbench = PlatformUI.getWorkbench();
		pref.init(workbench);

		// the project and traces should be saved successfully
		assertTrue("Saving traces and artifacts successfully", pref.performOk());

		// get the project with the NEW inputed value
		projectNew = ResourcesPlugin.getWorkspace().getRoot().getProject(NEW_VALID_PROJECT_NAME);

		// verify that project with expected name is created and exists
		assertTrue("Newly renamed project exists", projectNew.exists());

		List<EObject> traceModelContentsList_New2 = traceModel.eContents();
		List<EObject> traceModelArtifactList_New2 = artifactModel.eContents();

		// verify that new artifact and trace model contents size increased and the list
		// contains the old contents
		assertTrue(traceModelContentsList_New2.containsAll(traceModelContentsList));
		assertTrue(traceModelArtifactList_New2.containsAll(traceModelArtifactList));

		// verify expected files exist
		assertTrue(projectNew.getFile("traceModel.xmi").exists());
		assertTrue(projectNew.getFile("artifactWrappers.xmi").exists());
	}

}
