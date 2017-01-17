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

		String[] sheetNames = getSheetNames();

		for (String sheetName : sheetNames) {
			MenuItem menuItem = new MenuItem(menu, SWT.CHECK, index);
			menuItem.setText(sheetName);
			menuItem.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					OfficeView.getOpenedView().selectSheet(menuItem.getText());
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					OfficeView.getOpenedView().selectSheet(menuItem.getText());
				}
			});

			if (OfficeView.getOpenedView().getSelectedSheetName().contentEquals(sheetName))
				menuItem.setSelection(true);
		}
	}

	/**
	 * Gets all the sheet names contained in the currently opened workbook.
	 * 
	 * @return Array of all sheet names from the current workbook.
	 */
	private String[] getSheetNames() {
		return OfficeView.getOpenedView().getSheetNames();
	}
}