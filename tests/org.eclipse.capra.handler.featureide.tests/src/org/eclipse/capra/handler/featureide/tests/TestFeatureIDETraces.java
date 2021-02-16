/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *   Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.handler.featureide.tests;

import static org.eclipse.capra.testsupport.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsupport.TestHelper.resetSelectionView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystems;

import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.testsupport.TestHelper;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Before;
import org.junit.Test;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.impl.DefaultFeatureModelFactory;
import de.ovgu.featureide.fm.core.base.impl.FMFactoryManager;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelFormat;

public class TestFeatureIDETraces {

	private static final String FEATURE_A_NAME = "FeatureA";

	private static final String TEST_PROJECT_NAME = "TestProject";

	private static final String MODEL_A_FILE_NAME = "modelA.xml";
	private static final String DUMMY_FILE_NAME = "dummy.txt";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
	}

	@Test
	public void TestTraceCreation() throws CoreException, IOException, NoSuchExtensionException {
		FMFactoryManager fmFactoryManager = FMFactoryManager.getInstance();

		// Create a project
		IProject testProject = TestHelper.createSimpleProject(TEST_PROJECT_NAME);
		assert (TestHelper.projectExists(TEST_PROJECT_NAME));

		// Create a feature model with one feature
		IFeatureModel modelA = fmFactoryManager.getFactory(DefaultFeatureModelFactory.ID).create();
		modelA.createDefaultValues(TEST_PROJECT_NAME);

		IFeature feature = fmFactoryManager.getFactory(modelA).createFeature(modelA, FEATURE_A_NAME);
		modelA.addFeature(feature);
		modelA.getStructure().getRoot().addChild(feature.getStructure());

		java.nio.file.Path path = FileSystems.getDefault().getPath(testProject.getLocation().toOSString(),
				MODEL_A_FILE_NAME);
		modelA.setSourceFile(path);
		FeatureModelManager.save(modelA, path, new XmlFeatureModelFormat());
		assert (testProject.getFile(MODEL_A_FILE_NAME).exists());

		// Create an empty file
		IFile dummyFile = TestHelper.createEmptyFileInProject(DUMMY_FILE_NAME, TEST_PROJECT_NAME);
		assert (testProject.getFile(DUMMY_FILE_NAME).exists());

		// Add both files to selection
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(feature);
		SelectionView.getOpenedView().dropToSelection(dummyFile);
		assertFalse(SelectionView.getOpenedView().getSelection().isEmpty());

		// Create a trace via the selection view
		TestHelper.createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		assertTrue(TestHelper.thereIsATraceBetween(feature, dummyFile));

		// Clear selection view
		SelectionView.getOpenedView().clearSelection();

		// Test if the resolved wrapper equals the feature
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		ResourceSet resource = new ResourceSetImpl();
		ArtifactHelper artifactHelper = new ArtifactHelper(persistenceAdapter.getArtifactWrappers(resource));
		EObject featureWrapper = artifactHelper.createWrapper(feature);
		IArtifactHandler<?> featureIdeHandler = artifactHelper.getHandler(feature).orElse(null);
		assertNotNull(featureIdeHandler);
		Object resolvedFeature = featureIdeHandler.resolveWrapper(featureWrapper);
		assertEquals(feature, resolvedFeature);

	}
}
