/*******************************************************************************
 * Copyright (c) 2023 Jan-Philipp Steghöfer
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *  
 * SPDX-License-Identifier: EPL-2.0
 *  
 * Contributors:
 *      Jan-Philipp Steghöfer - initial API and implementation
 *******************************************************************************/
package de.jcup.asciidoctoreditor.outline;

/**
 * Provides access to the {@link AsciiDoctorEditorTreeContentProvider}.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class ContentProviderAccess {

	/**
	 * Get an {@link AsciiDoctorEditorTreeContentProvider} instance.
	 * 
	 * @return the instance
	 */
	public static AsciiDoctorEditorTreeContentProvider getContentProvider() {
		return new AsciiDoctorEditorTreeContentProvider();
	}

}
