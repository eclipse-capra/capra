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

/**
 * Helper class for creating traces
 */
public class TraceHelper {

	private EObject traceModel;
	private TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
	private ArtifactMetaModelAdapter artifactAdapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();

	/**
	 * @param traceModel
	 */
	public TraceHelper(EObject traceModel) {
		this.traceModel = traceModel;
	}

	/**
	 * Create a trace of the given traceType
	 *
	 * @param wrappers
	 * @param traceType
	 * @return the trace link that has been created
	 */
	public EObject createTrace(List<EObject> wrappers, EClass traceType) {
		return traceAdapter.createTrace(traceType, traceModel, wrappers);
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
		if (!tracedElements.contains(connection.getOrigin())) {
			tracedElements.add(connection.getOrigin());
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
	 */
	public boolean traceExists(List<EObject> selection, EClass traceType) {
		return !getTraces(selection, traceType).isEmpty();
	}

	/**
	 * Returns all trace links of the given type containing the given selection that
	 * exist in the trace model set in the instance of this class.
	 * 
	 * @param selection the selected elements
	 * @param traceType the type of trace link
	 * @return a list of trace links that fit the criteria
	 */
	public List<Connection> getTraces(List<EObject> selection, EClass traceType) {
		TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
		// create a connection
		List<EObject> allElements = new ArrayList<>(selection);
		EObject source = allElements.get(0);
		allElements.remove(0);
		List<EObject> targets = allElements;

		// create temporary trace model with a temporary trace link
		EObject tempTraceModel = ExtensionPointHelper.getTracePersistenceAdapter().get()
				.getTraceModel(new ResourceSetImpl());
		EObject tempTlink = traceAdapter.createTrace(traceType, tempTraceModel, selection);

		Connection connection = new Connection(source, targets, tempTlink);

		return traceAdapter.getAllTraceLinks(this.traceModel).stream().filter(c -> c.equals(connection))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Checks if a given EMF element or artifactWrapper is contained in the trace
	 * model.
	 * 
	 * @param artifact the artifact to look for
	 * @return true if the artifact exists, false otherwise
	 */

	public boolean isArtifactInTraceModel(EObject artifact) {
		List<EObject> artifacts = new ArrayList<EObject>();
		List<Connection> connections = traceAdapter.getAllTraceLinks(traceModel);
		for (Connection c : connections) {
			artifacts.addAll(getTracedElements(c));
		}
		if (artifacts.contains(artifact)) {
			return true;
		} else
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
		List<Connection> possibleConnections = new ArrayList<Connection>();
		List<Connection> relevantConnections = new ArrayList<Connection>();
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
			inserted.add(trace.getOrigin());
			List<EObject> targets = trace.getTargets();
			for (EObject target : targets) {
				inserted.add(target);
			}
		}
		return inserted;
	}

}
