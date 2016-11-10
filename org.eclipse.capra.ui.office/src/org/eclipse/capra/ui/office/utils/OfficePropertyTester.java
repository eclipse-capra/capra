/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.capra.ui.office.utils;

import org.eclipse.capra.ui.office.objects.CapraExcelRow;
import org.eclipse.capra.ui.office.views.OfficeView;
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

	private static final String IS_VIEW_EMPTY = "isViewEmpty";
	private static final String IS_EXCEL_OBJECT = "isExcelObject";

	@Override
	public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {

		// Triggers when a test is called to check whether the Office view is
		// empty or not.
		if (property.equals(IS_VIEW_EMPTY))
			if (OfficeView.getOpenedView().getSelection().isEmpty())
				return false;
			else
				return true;

		// Triggers when a test is called to check if the objects in the Office
		// view represent Excel rows.
		if (property.equals(IS_EXCEL_OBJECT))
			if (!OfficeView.getOpenedView().getSelection().isEmpty()
					&& OfficeView.getOpenedView().getSelection().get(0) instanceof CapraExcelRow)
				return true;
			else
				return false;

		return false;
	}
}
