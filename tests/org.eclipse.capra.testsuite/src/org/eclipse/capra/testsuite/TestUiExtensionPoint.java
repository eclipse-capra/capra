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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.testsupport.extension.UiTransferExtensionDummy;
import org.eclipse.swt.dnd.Transfer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Contains an integration test to check if contributions to extension points
 * are set correctly.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class TestUiExtensionPoint {

	private static final String TRANSFER_EXTENSION_POINT_ID = "org.eclipse.capra.ui.transfers";

	@Test
	public void testExtensionPoint() {

		List<Transfer> transfers = new ArrayList<Transfer>();

		// Get all additionally configured transfers from the extension point.
		transfers.addAll(ExtensionPointHelper.getExtensions(TRANSFER_EXTENSION_POINT_ID, "class").stream()
				.map(Transfer.class::cast).collect(Collectors.toList()));

		Assert.assertTrue(transfers.size() > 0);
		Assert.assertTrue(transfers.get(0) instanceof UiTransferExtensionDummy);
	}

}
