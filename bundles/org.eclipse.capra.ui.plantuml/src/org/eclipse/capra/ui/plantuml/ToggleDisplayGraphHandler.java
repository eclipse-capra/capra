/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.ui.plantuml;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Toggles between showing displaying a graph and a matrix if several model
 * elements are selected.
 * 
 * @author Jan-Philipp Steghöfer
 */
public class ToggleDisplayGraphHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Command command = event.getCommand();
		boolean oldValue = HandlerUtil.toggleCommandState(command);
		setDisplayGraph(!oldValue);
		return null;
	}

	/**
	 * Checks whether the trace view is set to show a graph.
	 * 
	 * @return {@code true} if the graph view is enabled, {@code false}
	 *         otherwise
	 */
	public static boolean isDisplayGraph() {
		Preferences graphDisplay = getPreference();

		return graphDisplay.get("option", "graph").equals("matrix");
	}

	private static Preferences getPreference() {
		Preferences preferences = InstanceScope.INSTANCE.getNode("org.eclipse.capra.ui.plantuml.displayGraph");
		Preferences transitivity = preferences.node("displayGraph");
		return transitivity;
	}

	/**
	 * Sets whether the trace view is set to show a graph or a matrix.
	 * 
	 * @param value
	 *            {@code true} if the graph view is enabled, {@code false}
	 *            otherwise
	 * 
	 */
	public static void setDisplayGraph(boolean value) {
		Preferences transitivity = getPreference();

		transitivity.put("option", value ? "graph" : "matrix");

		try {
			// forces the application to save the preferences
			transitivity.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}