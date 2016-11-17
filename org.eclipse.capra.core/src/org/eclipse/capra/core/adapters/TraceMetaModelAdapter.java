/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
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
	 * @return root of trace model that now contains the newly created trace
	 */
	EObject createTrace(EClass traceType, EObject traceModel, List<EObject> selection);

	/**
	 * Used to delete a trace
	 * 
	 * @param traceModel
	 *            Trace model to delete from
	 * @param first
	 *            First object
	 * @param second
	 *            Second object
	 */
	void deleteTrace(EObject first, EObject second, EObject traceModel);

	/**
	 * Decide if two objects are connected according to the given trace model
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
	 * @return A Map with the following structure: [Trace object t -> {list of
	 *         all objects connected to element via t}]
	 */
	List<Connection> getTransitivelyConnectedElements(EObject element, EObject traceModel);

}
