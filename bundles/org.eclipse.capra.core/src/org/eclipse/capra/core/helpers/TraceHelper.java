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
package org.eclipse.capra.core.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.handlers.AnnotationException;
import org.eclipse.capra.core.handlers.IAnnotateArtifact;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Helper class for creating traces
 */
public class TraceHelper {

	private EObject traceModel;
	private TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().orElseThrow();
	private ArtifactMetaModelAdapter artifactAdapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter()
			.orElseThrow();

	/**
	 * @param traceModel
	 */
	public TraceHelper(EObject traceModel) {
		this.traceModel = traceModel;
	}

	/**
	 * Create a trace of the given traceType
	 *
	 * @param origins
	 * @param targets
	 * @param traceType
	 * @return the trace link that has been created
	 */
	public EObject createTrace(List<EObject> origins, List<EObject> targets, EClass traceType) {
		return traceAdapter.createTrace(traceType, traceModel, origins, targets);
	}

	/**
	 * Deletes the given traces from the trace model
	 * 
	 * @param toDelete the traces to delete from the trace model
	 */
	public void deleteTraces(List<Connection> toDelete) {
		traceAdapter.deleteTrace(toDelete, traceModel);
	}

	/**
	 * Annotate artifacts represented by wrappers
	 *
	 * @param wrappers
	 */
	public void annotateTrace(List<EObject> wrappers) {
		// Annotate if possible
		for (EObject wrapper : wrappers) {
			IArtifactHandler<?> handler = artifactAdapter.getArtifactHandlerInstance(wrapper);

			if (handler instanceof IAnnotateArtifact) {
				IAnnotateArtifact h = (IAnnotateArtifact) handler;
				try {
					// Get unique connected artifacts, not including this
					// element
					// TODO: maybe add an adapter method for this?
					Set<EObject> connectedElements = new HashSet<>();
					final StringBuilder annotation = new StringBuilder();
					List<Connection> connections = traceAdapter.getConnectedElements(wrapper, traceModel);
					connections.forEach(c -> c.getTargets().forEach(t -> {
						if (t != wrapper) {
							connectedElements.add(t);
						}
					}));

					// Build annotation string
					connectedElements.forEach(e -> {
						if (annotation.length() > 0) {
							annotation.append(", ");
						}
						String name = artifactAdapter.getArtifactName(e);
						annotation.append(name);
					});

					h.annotateArtifact(wrapper, annotation.toString());
				} catch (AnnotationException e) {
					// Ignore
				}
			}
		}
	}

	/**
	 * Gets connected artifacts from a given {@link Connection}.
	 * 
	 * @param connection
	 * @return a list of unique connected artifacts, either the actual artifacts in
	 *         case of EMF model elements or artifact wrappers for other artifact
	 *         types.
	 */
	public List<EObject> getTracedElements(Connection connection) {
		List<EObject> tracedElements = new ArrayList<>();
		for (EObject origin : connection.getOrigins()) {
			if (!tracedElements.contains(origin)) {
				tracedElements.add(origin);
			}
		}
		for (EObject target : connection.getTargets()) {
			if (!tracedElements.contains(target)) {
				tracedElements.addAll(connection.getTargets());
			}
		}
		return tracedElements;
	}

	/**
	 * Checks if a trace link of a certain type containing a certain selection
	 * already exists in the trace model for the instance of this class.
	 * 
	 * @param selection the selected elements
	 * @param traceType the type of trace link
	 * @return true if the link exists
	 * @deprecated As of 0.8.2. Please use {@link #traceExists(List, List, EClass)}
	 *             instead
	 */
	@Deprecated
	public boolean traceExists(List<EObject> selection, EClass traceType) {
		return !getTraces(selection, traceType).isEmpty();
	}

	/**
	 * Checks if a trace link of a certain type with the provided origins and
	 * targets already exists in the trace model for the instance of this class.
	 * 
	 * @param origins   the origins of the trace link
	 * @param targets   the targets of the trace link
	 * @param traceType the type of trace link
	 * @return true if the link exists
	 * 
	 */
	public boolean traceExists(List<EObject> origins, List<EObject> targets, EClass traceType) {
		return !getTraces(origins, targets, traceType).isEmpty();
	}

	/**
	 * Returns all trace links of the given type containing the given selection that
	 * exist in the trace model set in the instance of this class.
	 * 
	 * This version of the method implicitly treats the first element in the
	 * provided list as the source and the rest as the targets of the link.
	 * 
	 * @param selection the selected elements
	 * @param traceType the type of trace link
	 * @return a list of trace links that fit the criteria
	 * @deprecated As of 0.8.2. Please use {@link #getTraces(List, List, EClass)}
	 *             instead.
	 */
	@Deprecated
	public List<Connection> getTraces(List<EObject> selection, EClass traceType) {

		List<EObject> targets = new ArrayList<>(selection);
		EObject origin = targets.get(0);
		targets.remove(0);
		return getTraces(Arrays.asList(origin), targets, traceType);
	}

	/**
	 * Returns all trace links of the given type containing the given origins and
	 * targets that exist in the trace model set in the instance of this class.
	 * 
	 * @param origins   the origins of the trace links that should be retrieved
	 * @param targets   the targets of the trace links that should be retrieved
	 * @param traceType the type of trace link
	 * @return a list of trace links that fit the criteria
	 */
	public List<Connection> getTraces(List<EObject> origins, List<EObject> targets, EClass traceType) {
		// create temporary trace model with a temporary trace link
		EObject tempTraceModel = ExtensionPointHelper.getTracePersistenceAdapter().orElseThrow()
				.getTraceModel(new ResourceSetImpl());
		EObject tempTlink = traceAdapter.createTrace(traceType, tempTraceModel, origins, targets);

		// create a connection
		Connection connection = new Connection(origins, targets, tempTlink);

		return traceAdapter.getAllTraceLinks(this.traceModel).stream().filter(c -> c.equals(connection))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Checks if a given EMF element or artifactWrapper is referenced in a trace in
	 * the trace model.
	 * 
	 * @param artifact the artifact to look for
	 * @return true if a trace that references the artifact exists, false otherwise
	 */
	public boolean isArtifactInTraceModel(EObject artifact) {
		List<Connection> connections = traceAdapter.getAllTraceLinks(traceModel);
		for (Connection c : connections) {
			for (EObject a : getTracedElements(c)) {
				if (EcoreUtil.equals(artifact, a)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns a list of connections containing the artifacts passed. Trace links
	 * that contain a subset (at least 2) of the artifacts passed are also returned.
	 * 
	 * @param artifacts a list of EMF artifacts or artifact wrappers
	 * @return connections containing at least two of the elements in the artifacts
	 *         passed.
	 */

	public List<Connection> getTraces(List<EObject> artifacts) {
		List<Connection> connections = traceAdapter.getAllTraceLinks(traceModel);
		List<Connection> possibleConnections = new ArrayList<>();
		List<Connection> relevantConnections = new ArrayList<>();
		// check if the list of artifact is not null or empty before doing anything
		if (artifacts != null && !artifacts.isEmpty()) {
			for (Connection c : connections) {
				List<EObject> artifactsFromConnection = getTracedElements(c);
				for (EObject a : artifactsFromConnection) {
					if (artifacts.contains(a) && !possibleConnections.contains(c)) {
						possibleConnections.add(c);
					} else if (artifacts.contains(a) && possibleConnections.contains(c)) {
						relevantConnections.add(c);
					}
				}
			}

		}
		return relevantConnections;
	}

	/**
	 * Retrieves all unique artifacts connected by the provided collection of
	 * {@code traces}.
	 * <p>
	 * The method will always return a valid set which can be empty.
	 * 
	 * @param traces the collection of traces whose artifacts should be retrieved
	 * @return a set of unique artifacts connected by the provided list of
	 *         {@code traces}
	 */
	public static Set<EObject> getTracedElements(Collection<Connection> traces) {
		Set<EObject> inserted = new HashSet<>();
		for (Connection trace : traces) {
			inserted.addAll(trace.getOrigins());
			inserted.addAll(trace.getTargets());
		}
		return inserted;
	}

}
