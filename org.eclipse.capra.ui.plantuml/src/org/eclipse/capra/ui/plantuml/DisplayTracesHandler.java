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
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Toggles between showing transitive and direct links
 * 
 * @author Anthony Anjorin, Salome Maro
 */
public class DisplayTracesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (isTraceViewTransitive())
			setTraceViewTransitive(false);
		else
			setTraceViewTransitive(true);

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

		return transitivity.get("option", "direct").equals("transitive");
	}

	private static Preferences getPreference() {
		Preferences preferences = InstanceScope.INSTANCE.getNode("org.eclipse.capra.ui.plantuml.toggleTransitivity");
		Preferences transitivity = preferences.node("transitivity");
		return transitivity;
	}

	/**
	 * Sets whether the trace view is set to show transitive traces.
	 * 
	 * @param value
	 *            indicates whether transitive traces should be shown
	 */
	public static void setTraceViewTransitive(boolean value) {
		Preferences transitivity = getPreference();

		transitivity.put("option", value ? "transitive" : "direct");

		try {
			// forces the application to save the preferences
			transitivity.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}