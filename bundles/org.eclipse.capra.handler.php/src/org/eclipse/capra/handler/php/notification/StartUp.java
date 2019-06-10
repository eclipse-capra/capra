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
package org.eclipse.capra.handler.php.notification;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.ui.IStartup;

/**
 * Registers the startup extension to add the PHP Element change listener.
 */
public class StartUp implements IStartup {

	@Override
	public void earlyStartup() {
		DLTKCore.addElementChangedListener(new PHPElementChangeListener());
	}
}
