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

import org.eclipse.capra.core.handlers.ArtifactHandler;
import org.eclipse.capra.core.handlers.PriorityHandler;
import org.eclipse.capra.handler.hudson.HudsonHandler;
import org.eclipse.mylyn.builds.internal.core.BuildElement;
import org.eclipse.mylyn.builds.internal.core.TestElement;

/**
 * Provides a simple default policy for selecting an {@link ArtifactHandler} in
 * cases where tests or builds from Hudson are selected by returning the first
 * available {@link HudsonHandler}.
 */
public class DefaultPriorityHandler implements PriorityHandler {

	@Override
	public ArtifactHandler getSelectedHandler(Collection<ArtifactHandler> handlers, Object selectedElement) {
		if (selectedElement instanceof TestElement || selectedElement instanceof BuildElement) {
			return handlers.stream().filter(h -> h instanceof HudsonHandler).findAny().get();

		}
		return null;
	}

}
