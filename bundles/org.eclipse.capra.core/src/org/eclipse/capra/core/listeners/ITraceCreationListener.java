/*******************************************************************************
 * Copyright (c) 2016, 2021 Chalmers | University of Gothenburg, rt-labs and others.
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
package org.eclipse.capra.core.listeners;

import org.eclipse.capra.core.adapters.Connection;

/**
 * Classes that implement this interface provide methods that deal with the
 * events that are generated when a new trace link is created.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public interface ITraceCreationListener {

	/**
	 * Called whenever a new trace link is created.
	 * 
	 * @param newTrace the newly created trace link
	 */
	void onTraceCreation(Connection newTrace);

}
