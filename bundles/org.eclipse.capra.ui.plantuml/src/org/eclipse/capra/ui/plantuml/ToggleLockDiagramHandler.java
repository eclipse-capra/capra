/*******************************************************************************
 * Copyright (c) 2016, 2020 Chalmers | University of Gothenburg, rt-labs and others.
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
 * Toggles between locking the view and updating it when new elements are
 * selected.
 * 
 * @author Jan-Philipp Steghöfer
 */
public class ToggleLockDiagramHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Command command = event.getCommand();
		boolean oldValue = HandlerUtil.toggleCommandState(command);
		setlockDiagram(!oldValue);
		return null;
	}

	/**
	 * Checks whether the view should be locked and thus not refreshed.
	 * 
	 * @return {@code true} if the lock is enabled, {@code false} otherwise
	 */
	public static boolean isLockDiagram() {
		Preferences lockDiagram = getPreference();
		return lockDiagram.getBoolean("option", false);
	}

	private static Preferences getPreference() {
		Preferences preferences = InstanceScope.INSTANCE.getNode("org.eclipse.capra.ui.plantuml.lockDiagram");
		return preferences.node("lockDiagram");
	}

	/**
	 * Sets whether the trace view is locked and should thus not be refreshed when
	 * new elements are selected.
	 * 
	 * @param value {@code true} if the view is locked, {@code false} otherwise
	 * 
	 */
	public static void setlockDiagram(boolean value) {
		Preferences transitivity = getPreference();
		transitivity.putBoolean("option", value);

		try {
			// forces the application to save the preferences
			transitivity.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}