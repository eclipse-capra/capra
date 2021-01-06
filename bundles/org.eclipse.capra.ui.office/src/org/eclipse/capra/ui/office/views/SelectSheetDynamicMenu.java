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

package org.eclipse.capra.ui.office.views;

import java.util.Map;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * A class tasked with dynamically filling the sheet-select context menu with
 * names of all the sheets contained in the currently opened Excel workbook.
 * 
 * @author Dusan Kalanj
 *
 */
public class SelectSheetDynamicMenu extends ContributionItem {

	@Override
	public void fill(Menu menu, int index) {

		// A HashMap that holds information about the emptiness of sheets. Key -
		// sheetName, value - true if map is empty, false otherwise
		Map<String, Boolean> isSheetEmptyMap = OfficeView.getOpenedView().getIsSheetEmptyMap();

		if (isSheetEmptyMap == null)
			return;

		// Add sheetNames to the dynamic context menu and make them
		// un-selectable if they are empty
		for (Map.Entry<String, Boolean> sheet : isSheetEmptyMap.entrySet()) {
			MenuItem menuItem = new MenuItem(menu, SWT.RADIO, index);

			menuItem.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					OfficeView.getOpenedView().displaySheet(menuItem.getText());
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					OfficeView.getOpenedView().displaySheet(menuItem.getText());
				}
			});

			if (OfficeView.getOpenedView().getSelectedSheetName().contentEquals(sheet.getKey())) {
				menuItem.setSelection(true);
			}
			if (Boolean.FALSE.equals(sheet.getValue())) {
				menuItem.setText(sheet.getKey());
			} else {
				menuItem.setText(sheet.getKey() + " (Empty)");
				menuItem.setEnabled(false);
			}
		}
	}
}