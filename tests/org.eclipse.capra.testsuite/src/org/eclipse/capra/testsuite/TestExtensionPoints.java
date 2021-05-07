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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.junit.Test;

/**
 * Tests if all important extension points provide a valid extension.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class TestExtensionPoints {

	@Test
	public void testExtensionPoints() {
		assertTrue(ExtensionPointHelper.getTraceabilityInformationModelAdapter().isPresent());
		assertTrue(ExtensionPointHelper.getArtifactMetaModelAdapter().isPresent());
		assertTrue(ExtensionPointHelper.getPriorityHandler().isPresent());
		assertTrue(ExtensionPointHelper.getTraceMetadataAdapter().isPresent());
		assertTrue(ExtensionPointHelper.getPersistenceAdapter().isPresent());
		assertFalse(ExtensionPointHelper.getArtifactHandlers().isEmpty());
	}

}
