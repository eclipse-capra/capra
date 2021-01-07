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
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Toggles between showing (DSL) internal links or not
 * 
 * @author Dominik Einkemmer
 */
public class DisplayInternalLinksHandler extends AbstractHandler {

	private static final Logger LOG = LoggerFactory.getLogger(DisplayInternalLinksHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		showInternalLinks(!areInternalLinksShown());
		return null;
	}

	/**
	 * Checks whether the trace view is set to show transitive traces.
	 * 
	 * @return {@code true} if transitive traces are enabled, {@code false}
	 *         otherwise
	 */
	public static boolean areInternalLinksShown() {
		Preferences internalLinks = getPreference();

		return internalLinks.get("option", "turnedOff").equals("shown");
	}

	private static Preferences getPreference() {
		Preferences preferences = InstanceScope.INSTANCE.getNode("org.eclipse.capra.ui.plantuml.toggleInternalLinks");
		return preferences.node("internalLinks");
	}

	/**
	 * Sets whether the trace view is set to show transitive traces.
	 * 
	 * @param value indicates whether transitive traces should be shown
	 */
	public static void showInternalLinks(boolean value) {
		Preferences internalLinks = getPreference();

		internalLinks.put("option", value ? "shown" : "turnedOff");

		try {
			// forces the application to save the preferences
			internalLinks.flush();
		} catch (BackingStoreException e) {
			LOG.warn("Could not save internal links preferences!", e);
		}
	}
}
