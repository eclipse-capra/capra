/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.handler.cdt.preferences;

import org.eclipse.capra.handler.cdt.Activator;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class CDTPreferences {

    public static final IScopeContext SCOPE_CONTEXT = InstanceScope.INSTANCE;
    public static final String PREFERENCE_NODE = Activator.PLUGIN_ID;

	// Should annotate C source code?
	public static final String ANNOTATE_CDT = "ANNOTATE_CDT";
	public static final boolean ANNOTATE_CDT_DEFAULT = false;

	// Doxygen tag to use for annotation
	public static final String ANNOTATE_CDT_TAG = "ANNOTATE_CDT_TAG";
	public static final String ANNOTATE_CDT_TAG_DEFAULT = "parent";

	// Doxygen tag prefix to use for annotation
	public static final String ANNOTATE_CDT_TAG_PREFIX = "ANNOTATE_CDT_TAG_PREFIX";
	public static final String ANNOTATE_CDT_TAG_PREFIX_DEFAULT = "@";

    public static IEclipsePreferences getPreferences() {
        return SCOPE_CONTEXT.getNode(PREFERENCE_NODE);
    }

}