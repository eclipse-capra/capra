package org.eclipse.capra.core.adapters;

import org.eclipse.capra.core.handlers.ArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.emf.ecore.EObject;

/**
 * Base class for the definition of a custom {@link ArtifactMetaModelAdapter}.
 * Implements a simple strategy to retrieve artifact handlers through the
 * registered extensions.
 */
public abstract class AbstractArtifactMetaModelAdapter implements ArtifactMetaModelAdapter {

	@Override
	public ArtifactHandler getArtifactHandlerInstance(EObject artifact) {
		String handler = getArtifactHandler(artifact);
		return ExtensionPointHelper.getArtifactHandler(handler).orElse(null);
	}

}
