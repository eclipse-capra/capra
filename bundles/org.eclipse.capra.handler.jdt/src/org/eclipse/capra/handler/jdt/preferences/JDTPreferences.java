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
package org.eclipse.capra.handler.jdt.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class JDTPreferences {

	private JDTPreferences() {
		// Private constructor to hide implicit public one.
	}

	public static final IScopeContext SCOPE_CONTEXT = InstanceScope.INSTANCE;
	public static final String PREFERENCE_NODE = "org.eclipse.capra.ui.jdt";

	// Should annotate Java source code?
	public static final String ANNOTATE_JDT = "ANNOTATE_JDT";
	public static final boolean ANNOTATE_JDT_DEFAULT = false;

	// Doxygen tag to use for annotation
	public static final String ANNOTATE_JDT_TAG = "ANNOTATE_JDT_TAG";
	public static final String ANNOTATE_JDT_TAG_DEFAULT = "parent";

	public static IEclipsePreferences getPreferences() {
		return SCOPE_CONTEXT.getNode(PREFERENCE_NODE);
	}

}
