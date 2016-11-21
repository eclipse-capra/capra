/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.capra.ui.office.preferences;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * An activator class that controls the plugin life-cycle.
 * 
 * @author Dusan Kalanj
 *
 */
public class PreferenceActivator extends AbstractUIPlugin {

	private static PreferenceActivator preferenceActivator = new PreferenceActivator();

	/**
	 * Empty constructor
	 */
	public PreferenceActivator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		preferenceActivator = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		preferenceActivator = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance of the PreferenceActivator class.
	 *
	 * @return instance of PreferenceActivator class
	 */
	public static PreferenceActivator getDefault() {
		return preferenceActivator;
	}
}