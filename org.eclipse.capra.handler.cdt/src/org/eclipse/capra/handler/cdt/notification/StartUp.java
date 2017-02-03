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

package org.eclipse.capra.handler.cdt.notification;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.ui.IStartup;

/**
 * Registers the startup extension to add the C Element change listener.
 */
public class StartUp implements IStartup {

	@Override
	public void earlyStartup() {
		CoreModel.getDefault().addElementChangedListener(new CElementChangeListener());
	}
}
