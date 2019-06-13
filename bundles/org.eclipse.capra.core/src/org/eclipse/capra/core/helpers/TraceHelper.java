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

	public List<EObject> getTracedElements(Connection connection) {
		List<EObject> tracedElements = new ArrayList<>();
		tracedElements.add(connection.getOrigin());
		tracedElements.addAll(connection.getTargets());
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

}
