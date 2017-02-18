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
package org.eclipse.capra.core.helpers;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.handlers.IAnnotateArtifact;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.handlers.PriorityHandler;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * Helper class for creating traces
 */
public class TraceCreationHelper {

	private EObject traceModel;
	private EObject artifactModel;
	private TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
	private ArtifactMetaModelAdapter artifactAdapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
	private Optional<PriorityHandler> priorityHandler = ExtensionPointHelper.getPriorityHandler();
	private Collection<IArtifactHandler<Object>> handlers = ExtensionPointHelper.getArtifactHandlers();

	/**
	 * @param traceModel
	 * @param artifactModel
	 */
	public TraceCreationHelper(EObject traceModel, EObject artifactModel) {
		this.traceModel = traceModel;
		this.artifactModel = artifactModel;
	}

	/**
	 * Get a handler for the artifact
	 * @param artifact
	 * @return handler or null if no handler exists
	 */
	public IArtifactHandler<Object> getHandler(Object artifact) {
		List<IArtifactHandler<Object>> availableHandlers = handlers
				.stream()
				.filter(h -> h.canHandleArtifact(artifact))
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
	 * Creates wrappers for artifacts
	 *
	 * @param artifacts
	 * @return List of wrappers
	 */
	public List<EObject> createWrappers(List<Object> artifacts) {
		List<EObject> wrappers = artifacts
				.stream()
				.map(a -> createWrapper(a))
				.collect(Collectors.toList());
		return wrappers;
	}

	/**
	 * Create a trace of the given traceType
	 *
	 * @param wrappers
	 * @param traceType
	 */
	public void createTrace(List<EObject> wrappers, EClass traceType) {
		traceAdapter.createTrace(traceType, traceModel, wrappers);
	}

	/**
	 * Annotate artifacts represented by wrappers
	 *
	 * @param wrappers
	 */
	public void annotateTrace(List<EObject> wrappers) {
		// Annotate if possible
		for (EObject wrapper : wrappers) {
			IArtifactHandler<Object> handler = artifactAdapter.getArtifactHandlerInstance(wrapper);
			if (handler instanceof IAnnotateArtifact) {
				IAnnotateArtifact h = (IAnnotateArtifact) handler;
				try {
					// Get unique connected artifacts, not including this element
					// TODO: maybe add an adapter method for this?
					Set<EObject> connectedElements = new HashSet<EObject>();
					final StringBuilder annotation = new StringBuilder();
					List<Connection> connections = traceAdapter.getConnectedElements(wrapper, traceModel);
					connections.forEach(c -> {
						c.getTargets().forEach(t -> {
							if (t != wrapper) {
								connectedElements.add(t);
							}
						});
					});

					// Build annotation string
					connectedElements.forEach(e -> {
						if (annotation.length() > 0) {
							annotation.append(", ");
						}
						String name = artifactAdapter.getArtifactName(e);
						annotation.append(name);
					});

					h.annotateArtifact(wrapper, annotation.toString());
				} catch (Exception e) {
					// Ignore
				}
			}
		}
	}

}
