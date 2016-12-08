/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.ui.cdt.preferences;

import org.eclipse.capra.handler.cdt.preferences.CDTPreferences;
import org.eclipse.capra.ui.cdt.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class CDTPreferencePage extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public CDTPreferencePage() {
		super(GRID);
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(
				CDTPreferences.ANNOTATE_CDT,
				"Annotate C source code",
				getFieldEditorParent()));

		addField(new StringFieldEditor(
				CDTPreferences.ANNOTATE_CDT_TAG,
				"Annotation tag",
				getFieldEditorParent()));

		addField(new StringFieldEditor(
				CDTPreferences.ANNOTATE_CDT_TAG_PREFIX,
				"Doxygen tag prefix",
				getFieldEditorParent()));
}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(null); // TODO?
	}

}
