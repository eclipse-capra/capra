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
package org.eclipse.capra.core.handlers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

public class AnnotationException extends CoreException {

	private static final long serialVersionUID = 1L;

	public AnnotationException(IStatus status) {
		super(status);
	}

}
