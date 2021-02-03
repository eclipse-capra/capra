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
 * &quot;trace&quot; is, as long as these methods can be implemented.
 * 
 * Important concepts:
 * <ul>
 * <li>Trace type: A trace link always has a specific type that is designated by
 * a name (e.g., "Related to" or "Implemented by"), the type of artifacts it
 * connects, and the number of artifacts that can be connected (e.g., one origin
 * artifact can be connected to several target artifacts).</li>
 * <li>Origin and Target: In general, a trace link is directed, i.e., it
 * connects one or more origin artifacts to one or more source artifacts.</li>
 * <li>Transitivity: Consider to trace links A->B and B->C. A and B as well as B
 * and C are directly connected. A and C, however, are transitively connected,
 * meaning that the connection between these elements goes via another
 * element.</li>
 * <li>Internal links: Many DSLs already contain relationships as part of the
 * constructs they support. UML, e.g., supports relationships such as
 * associations, generalisation, messages, etc. Eclipse Capra can make use of
 * such &quot;internal links&quot;. Which relationships are exposed to Eclipse
 * Capra in this way is determined by the respective handler.
 * </ul>
 * 
 * @author Anthony Anjorin, Salome Maro
 *
 */
public interface TraceMetaModelAdapter {

	/**
	 * Create a new model for trace links.
	 * 
	 * @return the new trace model
	 */

	EObject createModel();

	/**
	 * Used to retrieve a set of trace types that can be created for the given
	 * objects. This method does not differentiate between origin and target.
	 * Implementing classes need to ensure that this distinction is either ignored
	 * or ensure that the correct origin are determined from the provided list
	 * (e.g., by using the first list entry as the source).
	 * 
	 * @param selection the objects for which the available trace types should be
	 *                  retrieved
	 * @return A collection of possible types of traces that can be created for the
	 *         given objects
	 */
	Collection<EClass> getAvailableTraceTypes(List<EObject> selection);

	/**
	 * Used to create a trace of the given type.
	 * 
	 * @param traceType  the type of the trace to be created
	 * @param traceModel the root of the trace model that should contain the trace
	 *                   type. If {@code null}, a new root is created and returned.
	 * @param origins    the origin artifacts for this trace link
	 * @param targets    the target artifacts for this trace link
	 * @return the trace link that has just been created
	 */
	EObject createTrace(EClass traceType, EObject traceModel, List<EObject> origins, List<EObject> targets);

	/**
	 * Decide if two objects are connected according to the given trace model. This
	 * means that all traces in the trace model are checked and {@code true} is
	 * returned if and only if the trace model contains a trace link that connects
	 * the {@code origin} to the {@code target}.
	 * 
	 * @param origin     First object
	 * @param target     Second object
	 * @param traceModel trace model to base decision on
	 * @return <code>true</code> if the artifacts are connected, <code>false</code>
	 *         otherwise
	 */
	boolean isThereATraceBetween(EObject origin, EObject target, EObject traceModel);

	/**
	 * Determine a list of all objects connected to {@code element} according to the
	 * given trace model.
	 * 
	 * There are two ways {@code element} can be used:
	 * <ol>
	 * <li>If {@code element} is an arbitrary {@link EObject}, then the method
	 * returns all trace links in which {@code element} is one of the origins.</li>
	 * <li>If {@code element} is a trace link, then the method returns a
	 * representation of the trace link itself.</li>
	 * </ol>
	 * 
	 * @param element    the element used to determine the list of connected
	 *                   objects.
	 * @param traceModel the trace model to base calculation on
	 * @return a list of {@link Connection}s from the provided {@code traceModel}
	 *         that contain {@code element} as one of their origins or a
	 *         representation of {@code element} if {@code element} is a trace link
	 */
	List<Connection> getConnectedElements(EObject element, EObject traceModel);

	/**
	 * Determine a list of all objects connected to {@code element} according to the
	 * given trace model based on the provided list of trace link types. If
	 * {@code selectedRelationshipTypes} is empty or {@code null}, this method
	 * behaves exactly like {@link #getConnectedElements(EObject, EObject)}.
	 * 
	 * @param element        the element used to determine the list of connected
	 *                       objects.
	 * @param traceModel     the trace model to base calculation on
	 * @param traceLinkTypes a list of permissible trace link types (may be
	 *                       {@code null} or empty)
	 * @return a list of {@link Connection}s from the provided {@code traceModel}
	 *         that contain {@code element} as one of their origins or a
	 *         representation of {@code element} if {@code element} is a trace link
	 */
	List<Connection> getConnectedElements(EObject element, EObject traceModel, List<String> traceLinkTypes);

	/**
	 * Determine a list of all objects transitively connected to {@code element}
	 * according to the given trace model.
	 * <p>
	 * This means that if a trace model contains a link A->B and a link B->C, this
	 * method will return the two connections representing this link if
	 * {@code transitivityDepth} is either 0 or greater than 1. If the trace model
	 * does not contain any transitive links of this sort or the
	 * {@code transitivityDepth} is set to 1, this method should behave exactly like
	 * {@link #getConnectedElements(EObject, EObject, List)}.
	 * 
	 * @param element           The element used to determine the list of connected
	 *                          objects. Note that this element could be a trace in
	 *                          the trace model.
	 * @param traceModel        Trace model to base calculation on
	 * @param transitivityDepth The maximum depth of the transitive closure.
	 *                          {@code 0} means that no limit is applied.
	 * @return a list of {@link Connection}s from the provided {@code traceModel}
	 *         that contain {@code element} as one of their origins or a
	 *         representation of {@code element} if {@code element} is a trace link
	 */
	List<Connection> getTransitivelyConnectedElements(EObject element, EObject traceModel, int transitivityDepth);

	/**
	 * Determine a list of all objects transitively connected to {@code element}
	 * according to the given trace model based on the provided list of trace link
	 * types. If {@code selectedRelationshipTypes} is empty or {@code null}, this
	 * method behaves exactly like
	 * {@link #getTransitivelyConnectedElements(EObject, EObject, int)}.
	 * 
	 * @param element           the element used to determine the list of connected
	 *                          objects.
	 * @param traceModel        trace model to base calculation on
	 * @param traceLinkTypes    a list of permissible trace link types (may be
	 *                          {@code null} or empty)
	 * @param transitivityDepth The maximum depth of the transitive closure.
	 *                          {@code 0} means that no limit is applied.
	 * @return a list of {@link Connection}s from the provided {@code traceModel}
	 *         that contain {@code element} as one of their origins or a
	 *         representation of {@code element} if {@code element} is a trace link
	 */
	List<Connection> getTransitivelyConnectedElements(EObject element, EObject traceModel, List<String> traceLinkTypes,
			int transitivityDepth);

	/**
	 * Given a trace model, this method returns a list of all trace links in the
	 * model.
	 * 
	 * @param traceModel
	 * @return a list of all connections in the trace model
	 */
	List<Connection> getAllTraceLinks(EObject traceModel);

	/**
	 * Deletes specific trace links from a given trace model. This is useful for
	 * example when you want to delete links based on a quick fix from the
	 * notification feature.
	 * 
	 * @param toDelete   a list of links to be deleted
	 * @param traceModel the trace model to delete the links from
	 */
	void deleteTrace(List<Connection> toDelete, EObject traceModel);

	/**
	 * Determine a list of all connections in which {@code element} is the origin,
	 * including connections that result from internal relationships in a DSL.
	 * Effectively, this method thus returns the same connections as
	 * {@link #getConnectedElements(EObject, EObject, List)} plus the connections
	 * resulting from internal relationships.
	 * 
	 * This method delegates the determination which internal relationships should
	 * be exposed to Eclipse Capra this way to the handler of the DSL. It is,
	 * however, possible to limit the relationship types by providing a non-null,
	 * non-empty list of relationship type names in {@code relationshipTypes}.
	 * 
	 * @param element        The element used to determine the list of connected
	 *                       objects. Note that this element could be a trace in the
	 *                       trace model.
	 * @param traceModel     Trace model to base calculation on
	 * @param traceLinkTypes Relationship types to include. All relationship types
	 *                       are included if this list is {@code null} or empty.
	 * @return a list of {@link Connection}s that represent trace links as well as
	 *         relationships within the model in the DSL in which {@code element} is
	 *         formulated.
	 */
	List<Connection> getInternalElements(EObject element, EObject traceModel, List<String> traceLinkTypes);

	/**
	 * Determine a list of all connections in which {@code element} is the origin,
	 * including connections that result from internal relationships in a DSL and
	 * transitive connections up to the specified depth. Effectively, this method
	 * thus returns the same connections as
	 * {@link #getTransitivelyConnectedElements(EObject, EObject, List, int)} plus
	 * the connections resulting from internal relationships.
	 * 
	 * This method delegates the determination which internal relationships should
	 * be exposed to Eclipse Capra this way to the handler of the DSL. It is,
	 * however, possible to limit the relationship types by providing a non-null,
	 * non-empty list of relationship type names in {@code relationshipTypes}.
	 * 
	 * @param element           The element used to determine the list of connected
	 *                          objects. Note that this element could be a trace in
	 *                          the trace model.
	 * @param traceModel        Trace model to base calculation on
	 * @param traceLinkTypes    Relationship types to include. All relationship
	 *                          types are included if this list is {@code null} or
	 *                          empty.
	 * @param transitivityDepth The maximum depth the user wants to go down
	 *                          transitively. 0 indicates no limit.
	 * @return a list of {@link Connection}s that represent relationships within the
	 *         model in the DSL in which {@code element} is formulated.
	 */
	List<Connection> getInternalElementsTransitive(EObject element, EObject traceModel, List<String> traceLinkTypes,
			int transitivityDepth);

	/**
	 * Decide if two objects are connected internally. This method is implemented by
	 * passing the selected objects down to the artifact handlers. It returns
	 * {@code true} if and only if the artifact handler determines that there is a
	 * relevant internal relationship between the two provided objects.
	 * 
	 * @param first  First object
	 * @param second Second object
	 * @return <code>true</code> if the objects are internally connected,
	 *         <code>false</code> otherwise
	 */
	boolean isThereAnInternalTraceBetween(EObject first, EObject second);
}
