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
package org.eclipse.capra.ui.matrix;

import org.eclipse.emf.ecore.EObject;

/**
 * Implementations of this interface provide a way to retrieve an
 * {@link EObject} for a given index.
 * 
 * @author Jan-Philipp Steghöfer
 */
public interface IEObjectForIndexProvider {

	/**
	 * Get an {@link EObject} for the given index.
	 * 
	 * @param index the index from which to get the {@code EObject}
	 * @return the {@code EObject} at that index
	 */
	public EObject getEObject(int index);

}
