/*******************************************************************************
 * Copyright (c) 2016, 2022 Chalmers | University of Gothenburg, rt-labs and others.
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
package org.eclipse.capra.ui.plantuml.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Toggles between showing incoming and outgoing traceability links
 * 
 * @author Jan-Philipp Steghöfer
 */
public class ReverseLinkDirectionHandler extends AbstractHandler {

	private static final String REVERSE_DIRECTION_PREFERENCE_NODE = "reverseDirection";
	private static final String REVERSE_DIRECTION_PREFERENCE = "org.eclipse.capra.ui.plantuml.reverseLinkDirection";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Command command = event.getCommand();
		boolean oldValue = HandlerUtil.toggleCommandState(command);
		setReverseLinkDirection(!oldValue);
		return null;
	}

	/**
	 * Checks whether instead of outcoming links, ingoing links should be shown.
	 * 
	 * @return {@code true} if incoming links should be shown, {@code false}
	 *         otherwise
	 */
	public static boolean isReverseLinkDirection() {
		Preferences reverseDirection = getPreference();
		return reverseDirection.getBoolean("option", false);
	}

	private static Preferences getPreference() {
		Preferences preferences = InstanceScope.INSTANCE.getNode(REVERSE_DIRECTION_PREFERENCE);
		return preferences.node(REVERSE_DIRECTION_PREFERENCE_NODE);
	}

	/**
	 * Sets whether the instead of outcoming links, ingoing links should be shown.
	 * 
	 * @param value {@code true} if incoming links should be shown, {@code false}
	 *              otherwise
	 * 
	 */
	public static void setReverseLinkDirection(boolean value) {
		Preferences reverseLinkDirection = getPreference();
		reverseLinkDirection.putBoolean("option", value);

		try {
			// forces the application to save the preferences
			reverseLinkDirection.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}