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

import org.eclipse.capra.ui.plantuml.ToggleDisplayGraphHandler;
import org.eclipse.capra.ui.plantuml.ToggleLockDiagramHandler;
import org.eclipse.capra.ui.plantuml.ToggleTransitivityHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.commands.ICommandService;

import net.sourceforge.plantuml.eclipse.views.PlantUmlView;

/**
 * A simple extension of {@link PlantUmlView} to serve as the entry point for
 * menu contributions and later extensions.
 */
public class CapraPlantUmlView extends PlantUmlView {

	private static final String COMMANDS_TOGGLE_STATE = "org.eclipse.ui.commands.toggleState";

	@Override
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);
		ICommandService cmdService = getSite().getService(ICommandService.class);
		Command toggleTransitivity = cmdService.getCommand("org.eclipse.capra.ui.plantuml.toggleTransitivity");
		if (toggleTransitivity != null) {
			toggleTransitivity.getState(COMMANDS_TOGGLE_STATE)
					.setValue(ToggleTransitivityHandler.isTraceViewTransitive());
		}
		Command displayGraph = cmdService.getCommand("org.eclipse.capra.ui.plantuml.displayGraph");
		if (displayGraph != null) {
			displayGraph.getState(COMMANDS_TOGGLE_STATE)
					.setValue(ToggleDisplayGraphHandler.isDisplayGraph());
		}
		Command lockDiagram = cmdService.getCommand("org.eclipse.capra.ui.plantuml.lockDiagram");
		if (lockDiagram != null) {
			lockDiagram.getState(COMMANDS_TOGGLE_STATE)
					.setValue(ToggleLockDiagramHandler.isLockDiagram());
		}

	}
}
