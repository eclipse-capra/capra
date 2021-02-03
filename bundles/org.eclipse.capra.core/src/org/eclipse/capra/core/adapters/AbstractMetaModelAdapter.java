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
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Implements standard functionality for the methods defined in the
 * {@link TraceMetaModelAdapter}, in particular to delegate retrieving internal
 * links to the respective handler.
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

	@Override
	public List<Connection> getInternalElementsTransitive(EObject element, EObject traceModel,
			List<String> traceLinkTypes, int maximumDepth) {
		List<Object> accumulator = new ArrayList<>();
		List<Connection> existingTraces = new ArrayList<>();
		return getInternalElementsTransitive(element, traceModel, accumulator, traceLinkTypes, 0, maximumDepth,
				existingTraces);
	}

	@Override
	public List<Connection> getInternalElements(EObject element, EObject traceModel, List<String> traceLinkTypes) {
		return getInternalElements(element, traceModel, traceLinkTypes, false, 0, new ArrayList<>());
	}

	@SuppressWarnings("unchecked")
	private List<Connection> getInternalElements(EObject element, EObject traceModel, List<String> traceLinkTypes,
			boolean traceLinksTransitive, int transitivityDepth, List<Connection> existingTraces) {
		List<Connection> allElements = new ArrayList<>();
		List<Connection> directElements;
		if (traceLinksTransitive) {
			directElements = getTransitivelyConnectedElements(element, traceModel, traceLinkTypes, transitivityDepth);
		} else {
			directElements = getConnectedElements(element, traceModel, traceLinkTypes);
		}
		List<Integer> hashCodes = new ArrayList<>();

		for (Connection conn : existingTraces) {
			hashCodes.add(conn.hashCode());
		}

		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().orElseThrow();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		for (Connection conn : directElements) {
			if (!hashCodes.contains(conn.hashCode())) {
				allElements.add(conn);
			}

			// get internal links from source
			for (EObject o : conn.getOrigins()) {
				Object origin = artifactHelper.unwrapWrapper(o);
				IArtifactHandler<?> originHandler = artifactHelper.getHandler(origin).get();
				if (originHandler != null) {
					allElements.addAll(originHandler.getInternalLinks(o, traceLinkTypes));
				}
			}
			// get internal links from targets
			for (EObject o : conn.getTargets()) {
				Object originalObject = artifactHelper.unwrapWrapper(o);
				IArtifactHandler<?> handler = artifactHelper.getHandler(originalObject).orElseThrow();
				if (handler != null) {
					allElements.addAll(handler.getInternalLinks(o, traceLinkTypes));
				}
			}
		}
		// show internal links even when no Capra links are present
		if (directElements.isEmpty()) {
			Object originalObject = artifactHelper.unwrapWrapper(element);
			IArtifactHandler<?> handler = artifactHelper.getHandler(originalObject).orElseThrow();
			if (handler != null) {
				allElements.addAll(handler.getInternalLinks(element, traceLinkTypes));
			}

		}

		if (element.getClass().getPackage().toString().contains("org.eclipse.eatop")) {
			IArtifactHandler<Object> handler = (IArtifactHandler<Object>) artifactHelper.getHandler(element)
					.orElseThrow();
			allElements.addAll(handler.getInternalLinks(element, traceLinkTypes));
		}
		return allElements;
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().orElseThrow();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		IArtifactHandler<?> handlerFirstElement = artifactHelper.getHandler(first).orElseThrow();
		IArtifactHandler<?> handlerSecondElement = artifactHelper.getHandler(second).orElseThrow();

		return handlerFirstElement.isThereAnInternalTraceBetween(first, second)
				|| handlerSecondElement.isThereAnInternalTraceBetween(first, second);
	}
}
