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
package org.eclipse.capra.generic.priority;

import java.util.Collection;
import java.util.Comparator;

import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.handlers.PriorityHandler;

/**
 * Provides a simple default policy for selecting an {@link IArtifactHandler} by
 * selecting the one which handles the more specific type. This is determined by
 * comparing the assignability of the classes handled by the
 * <code>ArtifactHandlers</code>.
 */
public class DefaultPriorityHandler implements PriorityHandler {

	@Override
	public <T> IArtifactHandler<? extends T> getSelectedHandler(
			Collection<? extends IArtifactHandler<? extends T>> handlers, Object artifact) {
		return handlers.stream().filter(handler -> handler.canHandleArtifact(artifact))
				.max(new ArtifactHandlerPriorityComparator()).orElseThrow();
	}

	/**
	 * A comparator that compares two classes by whether the handled classes are
	 * assignable to each other. If instance A is of a type that is a superclass or
	 * superinterface of instance B, it will return A>B. A=B if the assignment works
	 * in both directions or if the assignment does not work at all.
	 */
	private class ArtifactHandlerPriorityComparator implements Comparator<IArtifactHandler<?>> {

		@Override
		public int compare(IArtifactHandler<?> o1, IArtifactHandler<?> o2) {
			if (!o1.getHandledClass().isAssignableFrom(o2.getHandledClass())
					&& o2.getHandledClass().isAssignableFrom(o1.getHandledClass())) {
				return 1;
			} else if (o1.getHandledClass().isAssignableFrom(o2.getHandledClass())
					&& !o2.getHandledClass().isAssignableFrom(o1.getHandledClass())) {
				return -1;
			} else {
				return 0;
			}
		}
	}

}
