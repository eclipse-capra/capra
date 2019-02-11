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
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class CDTPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(CDTPreferences.ANNOTATE_CDT, CDTPreferences.ANNOTATE_CDT_DEFAULT);
        store.setDefault(CDTPreferences.ANNOTATE_CDT_TAG, CDTPreferences.ANNOTATE_CDT_TAG_DEFAULT);
        store.setDefault(CDTPreferences.ANNOTATE_CDT_TAG_PREFIX, CDTPreferences.ANNOTATE_CDT_TAG_PREFIX_DEFAULT);
	}

}
