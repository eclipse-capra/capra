/*******************************************************************************
 * Copyright (c) 2016, 2021 Chalmers | University of Gothenburg, rt-labs and others.
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

import org.eclipse.capra.core.adapters.IPersistenceAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.editors.StringIdentifierEditor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * The main preferences page for Eclipse Capra.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class CapraPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String SHOW_TRACE_CREATED_CONFIRMATION_DIALOG = "org.eclipse.capra.preferences.showTraceCreatedConfirmationDialog";
	public static final String SHOW_TRACE_CREATED_CONFIRMATION_DIALOG_LABEL = "Show confirmation after a trace link has been created";
	private static final String PREFERENCE_PAGE_DESCRIPTION = "Eclipse Capra UI Preferences";
	public static final String CAPRA_PERSISTENCE_PROJECT_NAME_LABEL = "Input new project name for stored traces: ";
	public static final String PROJECT_IDENTIFIER_PATTERN = "^([a-zA-Z_$][a-zA-Z\\d_$]*)$";

	private IPersistenceAdapter persistenceAdapter;
	private EObject traceModel;
	private EObject artifactModel;
	private EObject metadataModel;

	@Override
	public void init(IWorkbench workbench) {
		setDescription(PREFERENCE_PAGE_DESCRIPTION);
		setPreferenceStore(getPreferenceStore());

		this.persistenceAdapter = ExtensionPointHelper.getPersistenceAdapter().orElseThrow();

		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		// add trace model to resource set
		this.traceModel = this.persistenceAdapter.getTraceModel(resourceSet);
		// add artifact model to resource set
		this.artifactModel = this.persistenceAdapter.getArtifactWrappers(resourceSet);
		// add metadata model to resource set
		this.metadataModel = this.persistenceAdapter.getMetadataContainer(resourceSet);
	}

	@Override
	protected void createFieldEditors() {
		BooleanFieldEditor booleanEditor = new BooleanFieldEditor(SHOW_TRACE_CREATED_CONFIRMATION_DIALOG,
				SHOW_TRACE_CREATED_CONFIRMATION_DIALOG_LABEL, getFieldEditorParent());
		StringIdentifierEditor stringEditor = new StringIdentifierEditor(
				org.eclipse.capra.core.preferences.CapraPreferences.CAPRA_PERSISTENCE_PROJECT_NAME,
				CAPRA_PERSISTENCE_PROJECT_NAME_LABEL, 20, getFieldEditorParent(), PROJECT_IDENTIFIER_PATTERN, true);
		addField(booleanEditor);
		addField(stringEditor);
	}

	@Override
	public boolean performOk() {
		boolean isOk = super.performOk();
		this.persistenceAdapter.saveModels(traceModel, artifactModel, metadataModel);
		return isOk;
	}

	/**
	 * Provides access to the preferences store. All clients should use
	 * {@link org.eclipse.capra.core.CapraPreferences} instead.
	 * 
	 * @return the preference store used by Eclipse Capra
	 */
	public static IPreferenceStore getPreferences() {
		return new ScopedPreferenceStore(InstanceScope.INSTANCE,
				org.eclipse.capra.core.preferences.CapraPreferences.CAPRA_PREFERENCE_PAGE_ID);
	}

}
