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

package org.eclipse.capra.ui.office.utils;

import org.eclipse.capra.ui.office.model.CapraExcelRow;
import org.eclipse.core.expressions.PropertyTester;

/**
 * A PropertyTester class that corresponds to the propertyTester definition in
 * the plugin.xml and checks if the menu options in the context/toolbar menu
 * should be displayed.
 *
 * @author Dusan Kalanj
 *
 */
public class OfficePropertyTester extends PropertyTester {

	private static final String IS_VIEW_POPULATED = "isViewPopulated";
	private static final String IS_EXCEL_OBJECT = "isExcelObject";

	@Override
	public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {

		if (property.equals(IS_VIEW_POPULATED)) {
			return receiver != null;
		}
		if (property.equals(IS_EXCEL_OBJECT)) {
			return receiver instanceof CapraExcelRow;
		}
		return false;
	}
}
