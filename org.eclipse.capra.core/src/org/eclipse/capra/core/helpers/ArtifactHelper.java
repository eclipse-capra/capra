package org.eclipse.capra.core.helpers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.handlers.PriorityHandler;
import org.eclipse.emf.ecore.EObject;

public class ArtifactHelper {
	private EObject artifactModel;
	private Optional<PriorityHandler> priorityHandler = ExtensionPointHelper.getPriorityHandler();
	private Collection<IArtifactHandler<Object>> handlers = ExtensionPointHelper.getArtifactHandlers();

	/**
	 * @param artifactModel
	 */
	public ArtifactHelper(EObject artifactModel) {
		this.artifactModel = artifactModel;
	}

	/**
	 * Creates wrappers for artifacts
	 *
	 * @param artifacts
	 * @return List of wrappers
	 */
	public List<EObject> createWrappers(List<Object> artifacts) {
		List<EObject> wrappers = artifacts.stream().map(a -> createWrapper(a)).collect(Collectors.toList());
		return wrappers;
	}

	/**
	 * Creates wrapper for artifact
	 *
	 * @param artifact
	 * @return wrapper of null if no handler exists
	 */
	public EObject createWrapper(Object artifact) {
		IArtifactHandler<Object> handler = getHandler(artifact);
		if (handler == null) {
			return null;
		}
		return handler.createWrapper(artifact, artifactModel);
	}

	/**
	 * Get a handler for the artifact
	 * 
	 * @param artifact
	 * @return handler or null if no handler exists
	 */
	public IArtifactHandler<Object> getHandler(Object artifact) {
		List<IArtifactHandler<Object>> availableHandlers = handlers.stream().filter(h -> h.canHandleArtifact(artifact))
				.collect(Collectors.toList());

		// TODO: Could this be folded into the pipeline above?
		if (availableHandlers.size() == 1) {
			return availableHandlers.get(0);
		} else if (availableHandlers.size() > 1 && priorityHandler.isPresent()) {
			return priorityHandler.get().getSelectedHandler(availableHandlers, artifact);
		} else {
			return null;
		}
	}

}
