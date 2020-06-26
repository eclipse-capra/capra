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
package org.eclipse.capra.ui.plantuml.views;

import org.eclipse.capra.ui.plantuml.DisplayInternalLinksHandler;
import org.eclipse.capra.ui.plantuml.ToggleDisplayGraphHandler;
import org.eclipse.capra.ui.plantuml.ToggleTransitivityHandler;
import org.eclipse.capra.ui.plantuml.TransitivityDepthHandler;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Composite;

import net.sourceforge.plantuml.eclipse.views.PlantUmlView;

/**
 * A simple extension of {@link PlantUmlView} to serve as the entry point for
 * menu contributions and later extensions.
 */
public class CapraPlantUmlView extends PlantUmlView {

	private IPreferenceChangeListener preferenceChangeListener;

	@Override
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);
		registerPreferenceChangeListener();
	}

	private void registerPreferenceChangeListener() {
		preferenceChangeListener = new IPreferenceChangeListener() {
			@Override
			public void preferenceChange(PreferenceChangeEvent event) {
				updateDiagramTextFromCurrentPart();
			}
		};
		InstanceScope.INSTANCE.getNode(DisplayInternalLinksHandler.TOGGLE_INTERNAL_LINKS_PREFERENCE)
				.addPreferenceChangeListener(preferenceChangeListener);
		InstanceScope.INSTANCE.getNode(ToggleTransitivityHandler.TOGGLE_TRANSITIVITY_PREFERENCE)
				.addPreferenceChangeListener(preferenceChangeListener);
		InstanceScope.INSTANCE.getNode(ToggleDisplayGraphHandler.DISPLAY_GRAPH_PREFERENCE)
				.addPreferenceChangeListener(preferenceChangeListener);
		InstanceScope.INSTANCE.getNode(TransitivityDepthHandler.TRANSITIVITY_DEPTH_PREFERENCE)
				.addPreferenceChangeListener(preferenceChangeListener);
	}

}
