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

package org.eclipse.capra.ui.office.preferences;

import org.eclipse.capra.ui.office.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * This class is tasked with initializing preference values when the plugin is
 * started for the first time.
 * 
 * @author Dusan Kalanj
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(OfficePreferences.CHAR_COUNT, OfficePreferences.CHAR_COUNT_DEFAULT);
		store.setDefault(OfficePreferences.EXCEL_COLUMN_RADIO_CHOICE,
				OfficePreferences.EXCEL_COLUMN_RADIO_ID_IS_LINE_NUMBER);
		store.setDefault(OfficePreferences.EXCEL_CUSTOM_COLUMN, OfficePreferences.EXCEL_CUSTOM_COLUMN_DEFAULT);
		store.setDefault(OfficePreferences.EXCEL_COLUMN_VALUE, OfficePreferences.EXCEL_COLUMN_VALUE_DEFAULT);
		store.setDefault(OfficePreferences.WORD_FIELD_NAME, OfficePreferences.WORD_FIELD_NAME_DEFAULT);
	}
}