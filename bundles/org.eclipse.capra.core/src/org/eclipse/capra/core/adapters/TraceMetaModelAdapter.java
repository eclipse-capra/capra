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

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * This interface defines all functionality that must be implemented to support
 * a specific trace metamodel. This enables swapping the concept of what a
 * "trace" is, as long as these methods can be implemented.
 * 
 * @author Anthony Anjorin, Salome Maro
 *
 */
public interface TraceMetaModelAdapter {
	
	/**
	 * Create a new model for the trace links.
	 * 
	 * @return the new trace model
	 */

	EObject createModel();

	/**
	 * Used to retrieve a set of types of traces that can be created for the
	 * given selection of objects in the Eclipse workspace
	 * 
	 * @param selection
	 *            The selection of objects the user has made and wants to create
	 *            a trace for in the Eclipse workspace
	 * @return A collection of possible types of traces that can be created for
	 *         the given selection
	 */
	Collection<EClass> getAvailableTraceTypes(List<EObject> selection);

	/**
	 * Used to create a trace of the given type
	 * 
	 * @param traceType
	 *            The type of the trace to be created
	 * @param traceModel
	 *            The root of the trace model that should contain the trace
	 *            type. If this is empty, then a new root is to be created and
	 *            returned.
	 * @param selection
	 *            Objects to create the trace for
	 * @return the trace link that has just been created
	 */
	EObject createTrace(EClass traceType, EObject traceModel, List<EObject> selection);

	/** Decide if two objects are connected according to the given trace model
	* 
	* @param first
	*            First object
	* @param second
	*            Second object
	* @param traceModel
	*            Trace model to base decision on
	* @return <code>true</code> if object are connected, <code>false</code>
	*         otherwise
	*/
	boolean isThereATraceBetween(EObject first, EObject second, EObject traceModel);

	/**
	 * Determine a list of all objects connected to element according to the
	 * given trace model
	 * 
	 * @param element
	 *            The element used to determine the list of connected objects.
	 *            Note that this element could be a trace in the trace model
	 * @param traceModel
	 *            Trace model to base calculation on
	 * @return A Map with the following structure: [Trace object t -> {list of
	 *         all objects connected to element via t}]
	 */
	List<Connection> getConnectedElements(EObject element, EObject traceModel);
	
	/**
	 * Determine a list of all objects connected to element according to the
	 * given trace model
	 * 
	 * @param element
	 *            The element used to determine the list of connected objects.
	 *            Note that this element could be a trace in the trace model
	 * @param traceModel
	 *            Trace model to base calculation on
	 * @param selectedRelationshipTypes
	 *            List of selected relationship types from the context menu of
	 *            plantuml
	 * @return A Map with the following structure: [Trace object t -> {list of
	 *         all objects connected to element via t}]
	 */
	List<Connection> getConnectedElements(EObject element, EObject traceModel, List<String> selectedRelationshipTypes);

	/**
	 * Determine a list of all objects connected to element according to the
	 * given trace model
	 * 
	 * @param element
	 *            The element used to determine the list of connected objects.
	 *            Note that this element could be a trace in the trace model
	 * @param traceModel
	 *            Trace model to base calculation on
	 * @param transitivityDepth
	 *            The maximum depth the user wants to go down transitively. 0
	 *            indicates no limit.
	 * @return A Map with the following structure: [Trace object t -> {list of
	 *         all objects connected to element via t}]
	 */
	List<Connection> getTransitivelyConnectedElements(EObject element, EObject traceModel, int transitivityDepth);

	/**
	 * Determine a list of all objects connected to element according to the
	 * given trace model
	 * 
	 * @param element
	 *            The element used to determine the list of connected objects.
	 *            Note that this element could be a trace in the trace model
	 * @param traceModel
	 *            Trace model to base calculation on
	 * @param selectedRelationshipTypes
	 *            List of selected relationship types from the context menu of
	 *            plantuml
	 * @param transitivityDepth
	 *            The maximum depth the user wants to go down transitively. 0
	 *            indicates no limit.
	 * @return A Map with the following structure: [Trace object t -> {list of
	 *         all objects connected to element via t}]
	 */
	List<Connection> getTransitivelyConnectedElements(EObject element, EObject traceModel,
			List<String> selectedRelationshipTypes, int transitivityDepth);
	
	/**
	 * Given a trace model, this method returns a list of all trace links in the
	 * model
	 * 
	 * @param traceModel
	 * @return A list of all connections in the trace model with the following
	 *         structure [Source, target(s) and the connecting trace link]
	 */
	List<Connection> getAllTraceLinks(EObject traceModel);

	/**
	 * Deletes specific trace links from a given trace model. This is useful for
	 * example when you want to delete links based on a quick fix from the
	 * notification feature
	 * 
	 * @param toDelete
	 *            a list of links to be deleted
	 * @param traceModel
	 *            the trace model to delete the links from
	 */
	void deleteTrace(List<Connection> toDelete, EObject traceModel);
	
	/**
	 * Determine a list of all objects internally connected to element (e.g.
	 * UML)
	 * 
	 * @param element
	 *            The element used to determine the list of connected objects.
	 *            Note that this element could be a trace in the trace model
	 * @param traceModel
	 *            Trace model to base calculation on
	 * @param selectedRelationshipTypes
	 *            List of selected relationship types from the context menu of
	 *            plantuml
	 * @param traceLinksTransitive
	 *            Used to determine if tracelink elements should be received
	 *            transitively
	 * @param transitivityDepth
	 *            Used to in case tracelinks are received transivitely in order
	 *            to set the depth
	 * @return A Map with the following structure: [Trace object t -> {list of
	 *         all objects connected to element via t}]
	 */
	List<Connection> getInternalElements(EObject element, EObject traceModel, List<String> selectedRelationshipTypes,
			boolean traceLinksTransitive, int transitivityDepth, List<Connection> existingTraces);

	/**
	 * Determine a list of elements internally connected to the selected one
	 * transitively
	 * 
	 * @param element
	 *            The element used to determine the list of connected objects.
	 *            Note that this element could be a trace in the trace model
	 * @param traceModel
	 *            Trace model to base calculation on
	 * @param transitivityDepth
	 *            The maximum depth the user wants to go down transitively. 0
	 *            indicates no limit.
	 * @return A Map with the following structure: [Trace object t -> {list of
	 *         all objects connected to element via t}]
	 */
	List<Connection> getInternalElementsTransitive(EObject element, EObject traceModel,
			List<String> selectedRelationshipTypes, int transitivityDepth, List<Connection> existingTraces);
	
	/**
	 * Decide if two objects are connected internally by passing the selected
	 * objects down to the artifact handlers and returns a String with the Type
	 * of connection for the trace matrix (empty String if no connection exists)
	 * This is implemented in the {@link AbstractMetaModelAdapter} and does not
	 * need to be overwritten but can be used like it is.
	 * 
	 * @param first
	 *            First object
	 * @param second
	 *            Second object
	 * @return <code>true</code> if object are connected, <code>false</code>
	 *         otherwise
	 */
	boolean isThereAnInternalTraceBetween(EObject first, EObject second);
}
