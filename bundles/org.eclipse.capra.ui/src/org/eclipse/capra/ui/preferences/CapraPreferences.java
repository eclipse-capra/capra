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
package org.eclipse.capra.ui.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class CapraPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String CAPRA_PREFERENCE_PAGE_ID = "org.eclipse.capra.ui";
	public static final String SHOW_TRACE_CREATED_CONFIRMATION_DIALOG = "Show confirmation after a trace link has been created";
	public static final String SHOW_TRACE_CREATED_CONFIRMATION_DIALOG_LABEL = "Show confirmation after a trace link has been created";
	private static final String PREFERENCE_PAGE_DESCRIPTION = "Eclipse Capra UI Preferences";

	@Override
	public void init(IWorkbench workbench) {
		setDescription(PREFERENCE_PAGE_DESCRIPTION);
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, CAPRA_PREFERENCE_PAGE_ID));
	}

	@Override
	protected void createFieldEditors() {
		BooleanFieldEditor booleanEditor = new BooleanFieldEditor(SHOW_TRACE_CREATED_CONFIRMATION_DIALOG,
				SHOW_TRACE_CREATED_CONFIRMATION_DIALOG_LABEL, getFieldEditorParent());
		addField(booleanEditor);
	}

	public static IPreferenceStore getPreferences() {
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, CAPRA_PREFERENCE_PAGE_ID);
	}
}
