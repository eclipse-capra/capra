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
 * Toggles between showing transitive and direct links
 *
 * @author Anthony Anjorin, Salome Maro
 */
public class ToggleTransitivityHandler extends AbstractHandler {

	public static final String TOGGLE_TRANSITIVITY_PREFERENCE = "org.eclipse.capra.ui.plantuml.toggleTransitivity";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Command command = event.getCommand();
		boolean oldValue = HandlerUtil.toggleCommandState(command);
		setTraceViewTransitive(!oldValue);
		return null;
	}

	/**
	 * Checks whether the trace view is set to show transitive traces.
	 *
	 * @return {@code true} if transitive traces are enabled, {@code false}
	 *         otherwise
	 */
	public static boolean isTraceViewTransitive() {
		Preferences transitivity = getPreference();

		return transitivity.get("option", "direct").equals("direct");
	}

	private static Preferences getPreference() {
		return InstanceScope.INSTANCE.getNode(TOGGLE_TRANSITIVITY_PREFERENCE);
	}

	/**
	 * Sets whether the trace view is set to show transitive traces.
	 *
	 * @param value indicates whether transitive traces should be shown
	 */
	public static void setTraceViewTransitive(boolean value) {
		Preferences transitivity = getPreference();
		transitivity.put("option", value ? "direct" : "transitive");

		try {
			// forces the application to save the preferences
			transitivity.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}