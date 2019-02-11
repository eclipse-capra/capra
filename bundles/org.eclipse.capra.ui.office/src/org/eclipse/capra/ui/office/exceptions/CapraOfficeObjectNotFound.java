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
	private static String EXCEPTION_MESSAGE = "Could not find the object with ID %s in its document. Maybe the file has been edited or moved.";

	/**
	 * A default constructor.
	 * 
	 * @param id
	 *            the id of the object that couldn't be found
	 */
	public CapraOfficeObjectNotFound(String id) {
		super(String.format(EXCEPTION_MESSAGE, id));
	}

}
