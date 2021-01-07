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
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Toggles between showing transitive and direct links
 * 
 * @author Anthony Anjorin, Salome Maro
 */
public class TransitivityDepthHandler extends AbstractHandler {

	private static final Logger LOG = LoggerFactory.getLogger(TransitivityDepthHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		String initialValue = getTransitivityDepth();
		InputDialog depthInput = new InputDialog(window.getShell(), "Transitivity depth",
				"Input the desired depth limit for transitivity. Enter 0 if no limit is desired.", initialValue, null);
		if (depthInput.open() == Window.OK) {
			String depth = depthInput.getValue();
			setTransitivityDepth(depth);
		}

		return null;
	}

	/**
	 * Gets the depth that was set by the user for transitivity returns 0 in case no
	 * depth was set or no depth limit is wanted
	 * 
	 * @return
	 */
	public static String getTransitivityDepth() {
		Preferences transitivity = getPreference();
		return transitivity.get("option", "0");
	}

	private static Preferences getPreference() {
		Preferences preferences = InstanceScope.INSTANCE.getNode("org.eclipse.capra.ui.plantuml.transitivityDepth");
		return preferences.node("transitivityDepth");
	}

	/**
	 * Sets whether the trace view is set to show transitive traces.
	 * 
	 * @param value indicates whether transitive traces should be shown
	 */
	public static void setTransitivityDepth(String depth) {
		Preferences transitivity = getPreference();

		transitivity.put("option", depth);

		try {
			// forces the application to save the preferences
			transitivity.flush();
		} catch (BackingStoreException e) {
			LOG.warn("Could not save transitivity depth preferences!", e);
		}
	}
}
