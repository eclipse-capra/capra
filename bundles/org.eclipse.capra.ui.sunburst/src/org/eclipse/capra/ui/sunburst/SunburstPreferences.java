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
package org.eclipse.capra.ui.sunburst;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class SunburstPreferences {

	/**
	 * Private constructor to hide implicit public one.
	 */
	private SunburstPreferences() {
		// Deliberately do nothing.
	}

	public static final IScopeContext SCOPE_CONTEXT = InstanceScope.INSTANCE;
	public static final String PREFERENCE_NODE = "org.eclipse.capra.ui.sunburst";

	// Maximum recursion depth for drawing sunburst
	public static final String MAX_RECURSION_LEVEL = "MAX_RECURSION_LEVEL";
	public static final int MAX_RECURSION_LEVEL_DEFAULT = 5;

	public static IEclipsePreferences getPreferences() {
		return SCOPE_CONTEXT.getNode(PREFERENCE_NODE);
	}

}
