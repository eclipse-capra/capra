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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.handlers.PriorityHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.handler.hudson.BuildElementHandler;
import org.eclipse.capra.handler.hudson.TestElementHandler;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.ITestCase;
import org.eclipse.mylyn.builds.internal.core.BuildFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestDefaultPriorityHandler {

	private IArtifactHandler<?> emfHandler;
	private IArtifactHandler<?> buildElementHandler;
	private IArtifactHandler<?> testElementHandler;

	private PriorityHandler priorityHandler;

	@SuppressWarnings("restriction")
	private IBuildElement buildElement = BuildFactory.eINSTANCE.createBuildPlan();

	@SuppressWarnings("restriction")
	private ITestCase testCase = BuildFactory.eINSTANCE.createTestCase();

	@Before
	public void setup() {
		priorityHandler = ExtensionPointHelper.getPriorityHandler().get();
		emfHandler = ExtensionPointHelper.getArtifactHandler("org.eclipse.capra.handler.emf.EMFHandler").get();
		testElementHandler = ExtensionPointHelper
				.getArtifactHandler("org.eclipse.capra.handler.hudson.TestElementHandler").get();
		buildElementHandler = ExtensionPointHelper
				.getArtifactHandler("org.eclipse.capra.handler.hudson.BuildElementHandler").get();
	}

	@Test
	public void testCorrectSelection() {
		assertEquals(BuildElementHandler.class, priorityHandler
				.getSelectedHandler(ExtensionPointHelper.getArtifactHandlers(), buildElement).getClass());
		assertEquals(TestElementHandler.class,
				priorityHandler.getSelectedHandler(ExtensionPointHelper.getArtifactHandlers(), testCase).getClass());
	}

	@Test
	public void testPrioritiesOrder() {
		Collection<IArtifactHandler<?>> handlers = new ArrayList<>();
		handlers.add(emfHandler);
		handlers.add(buildElementHandler);

		// First, without the testCaseHandler, test case is picked up by EMF (fallback)
		Assert.assertEquals(buildElementHandler, priorityHandler.getSelectedHandler(handlers, buildElement));
		Assert.assertEquals(emfHandler, priorityHandler.getSelectedHandler(handlers, testCase));

		// Now, we add the testCaseHandler and we should see a change in priority
		handlers.add(testElementHandler);
		Assert.assertEquals(testElementHandler, priorityHandler.getSelectedHandler(handlers, testCase));

		// Turn order around
		handlers.clear();
		handlers.add(testElementHandler);
		handlers.add(buildElementHandler);
		handlers.add(emfHandler);
		Assert.assertEquals(buildElementHandler, priorityHandler.getSelectedHandler(handlers, buildElement));
		Assert.assertEquals(testElementHandler, priorityHandler.getSelectedHandler(handlers, testCase));
	}

}
