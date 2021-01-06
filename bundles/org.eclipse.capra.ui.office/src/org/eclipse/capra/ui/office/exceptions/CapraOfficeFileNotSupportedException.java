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

package org.eclipse.capra.ui.office.exceptions;

/**
 * An exception that is to be thrown when a non-supported file is dragged (or
 * selected in the file dialog) into the Office view.
 * 
 * @author Dusan Kalanj
 *
 */
public class CapraOfficeFileNotSupportedException extends Exception {

	private static final long serialVersionUID = -7730053652692861930L;
	private static final String EXCEPTION_MESSAGE = "File type %s is not supported.";

	/**
	 * A default constructor.
	 * 
	 * @param fileType the type of the non-supported file that was dragged/put into
	 *                 the Office view
	 */
	public CapraOfficeFileNotSupportedException(String fileType) {
		super(String.format(EXCEPTION_MESSAGE, fileType));
	}

}
