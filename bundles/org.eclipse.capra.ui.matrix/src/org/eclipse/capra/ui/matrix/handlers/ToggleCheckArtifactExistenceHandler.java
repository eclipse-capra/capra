/*******************************************************************************
 * Copyright (c) 2023 Jan-Philipp Steghöfer
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *   
 * SPDX-License-Identifier: EPL-2.0
 *   
 * Contributors:
 *     Jan-Philipp Steghöfer - initial implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.ui.matrix.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToggleCheckArtifactExistenceHandler extends AbstractHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ToggleCheckArtifactExistenceHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Command command = event.getCommand();
		boolean oldValue = HandlerUtil.toggleCommandState(command);
		checkArtifaceExistence(!oldValue);
		return null;
	}

	/**
	 * Checks whether the matrix view should check if linked artifacts exist.
	 * 
	 * @return {@code true} if linked artifacts should be checked, {@code false}
	 *         otherwise
	 */
	public static boolean checkArtifaceExistence() {
		Preferences internalLinks = getPreference();
		return internalLinks.getBoolean("option", false);
	}

	private static Preferences getPreference() {
		Preferences preferences = InstanceScope.INSTANCE.getNode("org.eclipse.capra.ui.matrix");
		return preferences.node("checkArtifactExistence");
	}

	/**
	 * Sets whether the matrix view should check if linked artifacts exist.
	 * 
	 * @param value indicates whether linked artifacts should be checked
	 */
	public static void checkArtifaceExistence(boolean value) {
		Preferences preference = getPreference();
		preference.putBoolean("option", value);

		try {
			// forces the application to save the preferences
			preference.flush();
		} catch (BackingStoreException e) {
			LOG.warn("Could not save internal links preferences!", e);
		}
	}
}
