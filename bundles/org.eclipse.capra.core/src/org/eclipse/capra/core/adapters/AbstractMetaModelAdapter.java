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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * Implements standard functionality for the methods defined in the
 * {@link TraceMetaModelAdapter}.
 */
public abstract class AbstractMetaModelAdapter implements TraceMetaModelAdapter {

	private List<Connection> getInternalElementsTransitive(EObject element, EObject traceModel,
			List<Object> accumulator, List<String> selectedRelationshipTypes, int currentDepth, int maximumDepth,
			List<Connection> existingTraces) {
		List<Connection> directElements = getInternalElements(element, traceModel, selectedRelationshipTypes, true,
				maximumDepth, existingTraces);
		List<Connection> allElements = new ArrayList<>();
		int currDepth = currentDepth + 1;
		for (Connection connection : directElements) {
			if (!accumulator.contains(connection.getTlink())) {
				allElements.add(connection);
				accumulator.add(connection.getTlink());
				for (EObject e : connection.getTargets()) {
					if (maximumDepth == 0 || currDepth < (maximumDepth + 2)) {
						allElements.addAll(getInternalElementsTransitive(e, traceModel, accumulator,
								selectedRelationshipTypes, currDepth, maximumDepth, existingTraces));
					}
				}
			}
		}

		return allElements;
	}

	/**
	 * Used to get internal links connected to a selected element.
	 * 
	 * @param element
	 *            the selected element
	 * @param traceModel
	 *            the current trace model
	 * @param selectedRelationshipTypes
	 *            the selected relationship types from the filter, if the user
	 *            has selected any
	 * @param maximumDepth
	 *            The maximum depth the transitivity should go. 0 means show all
	 *            the links
	 * @param existingTraces
	 *            The trace links that have been created manually by the user,
	 *            these are obtained from the trace model
	 */
	public List<Connection> getInternalElementsTransitive(EObject element, EObject traceModel,
			List<String> selectedRelationshipTypes, int maximumDepth, List<Connection> existingTraces) {
		List<Object> accumulator = new ArrayList<>();
		return getInternalElementsTransitive(element, traceModel, accumulator, selectedRelationshipTypes, 0,
				maximumDepth, existingTraces);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Connection> getInternalElements(EObject element, EObject traceModel,
			List<String> selectedRelationshipTypes, boolean traceLinksTransitive, int transitivityDepth,
			List<Connection> existingTraces) {
		List<Connection> allElements = new ArrayList<>();
		List<Connection> directElements;
		if (traceLinksTransitive) {
			directElements = getTransitivelyConnectedElements(element, traceModel, selectedRelationshipTypes,
					transitivityDepth);
		} else {
			directElements = getConnectedElements(element, traceModel, selectedRelationshipTypes);
		}
		List<Integer> hashCodes = new ArrayList<>();

		for (Connection conn : existingTraces) {
			int connectionHash = conn.getOrigin().hashCode() + conn.getTlink().hashCode();
			for (EObject targ : conn.getTargets()) {
				connectionHash += targ.hashCode();
			}
			hashCodes.add(connectionHash);
		}

		ResourceSet resourceSet = new ResourceSetImpl();
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		for (Connection conn : directElements) {
			int connectionHash = conn.getOrigin().hashCode() + conn.getTlink().hashCode();
			for (EObject targ : conn.getTargets()) {
				connectionHash += targ.hashCode();
			}
			if (!hashCodes.contains(connectionHash)) {
				allElements.add(conn);
			}
			// get internal links from source
			Object origin = artifactHelper.unwrapWrapper(conn.getOrigin());
			IArtifactHandler<?> originHandler = artifactHelper.getHandler(origin).get();
			if (originHandler != null) {
				allElements.addAll(originHandler.addInternalLinks(conn.getOrigin(), selectedRelationshipTypes));
			}
			// get internal links from targets
			for (EObject o : conn.getTargets()) {
				Object originalObject = artifactHelper.unwrapWrapper(o);
				IArtifactHandler<?> handler = artifactHelper.getHandler(originalObject).get();
				if (handler != null) {
					allElements.addAll(handler.addInternalLinks(o, selectedRelationshipTypes));
				}
			}
		}
		// show internal links even when no Capra links are present
		if (directElements.size() == 0) {
			Object originalObject = artifactHelper.unwrapWrapper(element);
			IArtifactHandler<?> handler = artifactHelper.getHandler(originalObject).get();
			if (handler != null) {
				allElements.addAll(handler.addInternalLinks(element, selectedRelationshipTypes));
			}

		}

		if (element.getClass().getPackage().toString().contains("org.eclipse.eatop")) {
			IArtifactHandler<Object> handler = (IArtifactHandler<Object>) artifactHelper.getHandler(element)
					.orElse(null);
			allElements.addAll(handler.addInternalLinks(element, selectedRelationshipTypes));
		}
		return allElements;
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {

		ResourceSet resourceSet = new ResourceSetImpl();
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		IArtifactHandler<?> handlerFirstElement = artifactHelper.getHandler(first).orElse(null);
		IArtifactHandler<?> handlerSecondElement = artifactHelper.getHandler(second).orElse(null);

		return handlerFirstElement.isThereAnInternalTraceBetween(first, second)
				|| handlerSecondElement.isThereAnInternalTraceBetween(first, second);
	}
}
