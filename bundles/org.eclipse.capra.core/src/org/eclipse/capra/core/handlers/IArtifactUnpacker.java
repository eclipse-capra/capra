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
package org.eclipse.capra.core.handlers;

/**
 * Defines functions to unpack the artifact from a surrounding container. This
 * can be helpful if the artifact is wrapped by another class and Eclipse calls
 * the methods of an {@link IArtifactHandler} with the wrapper instead of the
 * actual object the wrapper is able to handle.
 * 
 * @param <T>
 *            the type of the container
 * @param <K>
 *            the type of the artifact
 */
public interface IArtifactUnpacker<T, K> {

	/**
	 * Unpacks an artifact from a container.
	 * 
	 * @param container
	 *            the container containing the artifact
	 * @return an artifact unpacked from the container
	 */
	K unpack(T container);

}
