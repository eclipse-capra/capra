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
package org.eclipse.capra.core.handlers;

import java.util.Collection;

/**
 * A priority handler is used to define which {@link ArtifactHandler} should
 * preferably be used when creating a new trace link. Depending on the choice,
 * different trace types can then be selected.
 */
public interface PriorityHandler {

	/**
	 * This method gets a list of available handers for a selection and returns
	 * the best (prioritized) handler that can handle the selection
	 * 
	 * @param handlers
	 *            List of available handlers for the selection
	 * 
	 * @param selectedElement
	 *            The selected Object s
	 * @return one handler
	 * 
	 */
	ArtifactHandler getSelectedHandler(Collection<ArtifactHandler> handlers, Object selectedElement);

}
