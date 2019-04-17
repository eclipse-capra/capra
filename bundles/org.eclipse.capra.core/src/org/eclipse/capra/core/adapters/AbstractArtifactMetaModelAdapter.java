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
	public IArtifactHandler<?> getArtifactHandlerInstance(EObject artifact) {
		String handler = getArtifactHandler(artifact);
		return ExtensionPointHelper.getArtifactHandler(handler).orElse(null);
	}

}
