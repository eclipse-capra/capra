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
 * An exception that is to be thrown when an office object can't be tracked back
 * to its native environment.
 * 
 * @author Dusan Kalanj
 *
 */
public class CapraOfficeObjectNotFound extends Exception {

	private static final long serialVersionUID = -3973348630832482778L;
	private static final String EXCEPTION_MESSAGE = "Could not find the object with ID %s in its document. Maybe the file has been edited or moved.";

	/**
	 * A default constructor. Please use {@link CapraOfficeObjectNotFound(String, Throwable)}
	 * whenever possible to propagate the original cause.
	 * 
	 * @param id
	 *            the id of the object that couldn't be found
	 */
	public CapraOfficeObjectNotFound(String id) {
		super(formatExceptionMessage(id));
	}
	
	/**
	 * Creates a new exception indicating that an office object could not be found.
	 * This is the preferred constructor since it does not hide the original cause.
	 * 
	 * @param id
	 * @param cause
	 */
	public CapraOfficeObjectNotFound(String id, Throwable cause) {
		super(formatExceptionMessage(id), cause);
	}

	private static String formatExceptionMessage(String id) {
		return String.format(EXCEPTION_MESSAGE, id);
	}
}
