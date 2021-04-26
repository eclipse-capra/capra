/*******************************************************************************
 * Copyright (c) 2016-2021 Chalmers | University of Gothenburg, rt-labs and others.
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
package org.eclipse.capra.ui.operations;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Provides common elements for operations used by Eclipse Capra.
 * 
 * @author Jan-Philipp Steghöfer
 */
public class OperationsHelper {

	/**
	 * The plugin ID.
	 */
	public static final String PLUGIN_ID = "org.eclipse.capra.ui";

	/**
	 * Creates an error dialog that exposes an error to the user.
	 * 
	 * @param shell   the parent shell of the dialog
	 * @param status  the error to show to the user
	 * @param the     title to use for this dialog, or <code>null</code> to indicate
	 *                that the default title should be used
	 * @param message the message to show in this dialog, or <code>null</code> to
	 *                indicate that the error's message should be shown as the
	 *                primary message
	 */
	public static void createErrorMessage(Shell shell, IStatus status, String title, String message) {
		ErrorDialog.openError(shell, title, message, status);
	}

}
