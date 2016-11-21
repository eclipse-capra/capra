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

package org.eclipse.capra.ui.office.preferences;

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

	public PreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = PreferenceActivator.getDefault().getPreferenceStore();
		store.setDefault(OfficePreferences.CHAR_COUNT, OfficePreferences.CHAR_COUNT_DEFAULT);
		store.setDefault(OfficePreferences.EXCEL_COLUMN_RADIO_CHOICE,
				OfficePreferences.EXCEL_COLUMN_RADIO_ID_IS_LINE_NUMBER);
		store.setDefault(OfficePreferences.EXCEL_CUSTOM_COLUMN, OfficePreferences.EXCEL_CUSTOM_COLUMN_DEFAULT);
		store.setDefault(OfficePreferences.EXCEL_COLUMN_VALUE, OfficePreferences.EXCEL_COLUMN_VALUE_DEFAULT);
	}
}