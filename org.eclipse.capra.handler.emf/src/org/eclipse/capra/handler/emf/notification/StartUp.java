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
package org.eclipse.capra.handler.emf.notification;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

/**
 * A startup extension that registers the EditorListener (that is tasked with
 * registering a ModelChangeListener) when the plugin starts.
 */
public class StartUp implements IStartup {

	@Override
	public void earlyStartup() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				// Add the EditorListener that registers the ModelChangeListener
				// to newly opened EMF editors.
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.addPartListener(new EditorListener());
			}
		});
	}
}
