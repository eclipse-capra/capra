/*******************************************************************************
 * Copyright (c) 2016, 2021 Chalmers | University of Gothenburg, rt-labs and others.
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
import static org.eclipse.capra.testsupport.TestHelper.createEClassInEPackage;
import static org.eclipse.capra.testsupport.TestHelper.createEcoreModel;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.IMetadataAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.generic.metadatamodel.MetadataContainer;
import org.eclipse.capra.generic.metadatamodel.TraceMetadata;
import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.testsupport.TestHelper;
import org.eclipse.capra.ui.adapters.PropertySourceExtensionPointHelper;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestTraceMetadata {

	private static final String CLASS_A_NAME = "A";
	private static final String CLASS_B_NAME = "B";

	private static final String MODEL_A_FILENAME = "modelA.ecore";
	private static final String MODEL_B_FILENAME = "modelB.ecore";

	private static final String MODEL_A_NAME = "modelA";
	private static final String MODEL_B_NAME = "modelB";

	private static final String TEST_PROJECT_NAME = "TestProject";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
		purgeModels();
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void TestRetrieveTraceMetadata() throws CoreException, IOException {
		// Create a project
		createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// Create two models and persist them
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		save(testProject, a);

		EPackage b = createEcoreModel(MODEL_B_NAME);
		createEClassInEPackage(b, CLASS_B_NAME);
		save(testProject, b);

		// Load them, choose two elements
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, MODEL_A_FILENAME, rs);
		EClass _A = (EClass) _a.getEClassifier(CLASS_A_NAME);

		EPackage _b = load(testProject, MODEL_B_FILENAME, rs);
		EClass _B = (EClass) _b.getEClassifier(CLASS_B_NAME);

		// Add them to the selection view
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(_B);

		// Create a trace via the selection view
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(_A, _B));

		Connection conn = TestHelper.getConnectionBetween(_A, _B);

		// We're getting the metadata for the link and see if the defaults are set.
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().orElseThrow();
		IMetadataAdapter metadataAdapter = ExtensionPointHelper.getTraceMetadataAdapter().orElseThrow();
		MetadataContainer metadataContainer = (MetadataContainer) persistenceAdapter
				.getMetadataContainer(EditingDomainHelper.getResourceSet());
		TraceMetadata metadata = (TraceMetadata) metadataAdapter.getMetadataForTrace(conn.getTlink(),
				metadataContainer);
		assertNotNull(metadata);
		assertTrue(metadata.getComment().isEmpty());

		// Now we're setting one of the values directly. That should not work since
		// we're not in a transaction. We thus check that the exception is thrown. Funny
		// enough, the value still changes and we assert on that, too.
		exception.expect(IllegalStateException.class);
		metadata.setComment("Test");

		assertEquals("Test", metadata.getComment());

		// And finally, we set the metadata again
		metadataAdapter.setMetadataForTrace(conn.getTlink(), metadata, metadataContainer);

		// Now we're checking the property source provider
		Optional<IPropertySourceProvider> metadataPropertySourceProviderOpt = PropertySourceExtensionPointHelper
				.getMetadataPropertySourceProvider();
		assertTrue(metadataPropertySourceProviderOpt.isPresent());
		IPropertySourceProvider metadataPropertySourceProvider = metadataPropertySourceProviderOpt.get();
		IPropertySource metadataPropertySource = metadataPropertySourceProvider.getPropertySource(conn);
		assertNotNull(metadataPropertySource);
		IPropertyDescriptor[] propertyDescriptors = metadataPropertySource.getPropertyDescriptors();
		assertNotNull(propertyDescriptors);
		assertEquals("Comment", propertyDescriptors[1].getDisplayName());
		assertEquals("Test", metadataPropertySource.getPropertyValue(propertyDescriptors[1].getId()));

		// Next, we're setting one of the properties via the property source and
		// checking if it's stored correctly.
		Date date = new Date();
		metadataPropertySource.setPropertyValue(propertyDescriptors[0].getId(), date);
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Object dateFromSourceObject = metadataPropertySource.getPropertyValue(propertyDescriptors[0].getId());
		assertTrue(dateFromSourceObject instanceof Date);
		assertEquals(formatter.format((Date) dateFromSourceObject), formatter.format(date));

	}

}
