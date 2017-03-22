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

package org.eclipse.capra.ui.office.views;

import java.util.HashMap;

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
		HashMap<String, Boolean> isSheetEmptyMap = OfficeView.getOpenedView().getIsSheetEmptyMap();

		if (isSheetEmptyMap == null)
			return;

		// Add sheetNames to the dynamic context menu and make them
		// un-selectable if they are empty
		for (String sheetName : isSheetEmptyMap.keySet()) {
			MenuItem menuItem = new MenuItem(menu, SWT.CHECK, index);

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

			if (OfficeView.getOpenedView().getSelectedSheetName().contentEquals(sheetName))
				menuItem.setSelection(true);

			if (!isSheetEmptyMap.get(sheetName))
				menuItem.setText(sheetName);
			else {
				menuItem.setText(sheetName + " (Empty)");
				menuItem.setEnabled(false);
			}
		}
	}
}