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
package org.eclipse.capra.generic.priority;

import java.util.Collection;

import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.handlers.PriorityHandler;
import org.eclipse.capra.handler.hudson.BuildElementHandler;
import org.eclipse.capra.handler.hudson.TestElementHandler;
import org.eclipse.mylyn.builds.internal.core.BuildElement;
import org.eclipse.mylyn.builds.internal.core.TestElement;

/**
 * Provides a simple default policy for selecting an {@link ArtifactHandler} in
 * cases where tests or builds from Hudson are selected by returning the first
 * available {@link TestElementHandler} or {@link BuildElementHandler}.
 */
public class DefaultPriorityHandler implements PriorityHandler {

	@Override
	public IArtifactHandler<Object> getSelectedHandler(Collection<IArtifactHandler<Object>> handlers, Object selectedElement) {
		// TODO: is this needed if HudsonHandler is split into Build/TestElementHandler?
		if (selectedElement instanceof TestElement) {
			return handlers.stream().filter(h -> h.getClass().isAssignableFrom(TestElementHandler.class)).findAny().get();

		} else if (selectedElement instanceof BuildElement) {
			return handlers.stream().filter(h -> h.getClass().isAssignableFrom(BuildElementHandler.class)).findAny().get();
		}
		return null;
	}

}
