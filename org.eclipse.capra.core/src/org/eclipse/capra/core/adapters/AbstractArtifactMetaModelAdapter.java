/*******************************************************************************
 * Copyright (c) 2017 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.core.adapters;

import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.emf.ecore.EObject;

/**
 * Base class for the definition of a custom {@link ArtifactMetaModelAdapter}.
 * Implements a simple strategy to retrieve artifact handlers through the
 * registered extensions.
 */
public abstract class AbstractArtifactMetaModelAdapter implements ArtifactMetaModelAdapter {

	@Override
	public IArtifactHandler<Object> getArtifactHandlerInstance(EObject artifact) {
		String handler = getArtifactHandler(artifact);
		return ExtensionPointHelper.getArtifactHandler(handler).orElse(null);
	}

}
