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
package org.eclipse.capra.ui.reqif.preferences;

import org.eclipse.capra.handler.reqif.preferences.ReqifPreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class ReqifPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String REQIF_PREFERENCE_PAGE_ID = "org.eclipse.capra.ui.reqif";
	public static final String ID_ATTRIBUTE = "Attribute containing requirement ID";

	public ReqifPreferencePage() {
		super(GRID);
	}

	@Override
	protected void createFieldEditors() {
		StringFieldEditor stringEditor = new StringFieldEditor(ReqifPreferences.REQIF_ID_ATTRIBUTE, ID_ATTRIBUTE,
				getFieldEditorParent());
		addField(stringEditor);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, REQIF_PREFERENCE_PAGE_ID));
		setDescription(null);
	}
}
