/*******************************************************************************
 * Copyright (c) 2022 Jan-Philipp Steghöfer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *      Jan-Philipp Steghöfer - initial API and implementation
 *******************************************************************************/
package org.eclipse.capra.core.adapters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * A {@code ConnectionQuery} contains all relevant parameters to retrieve
 * {@link Connection} instances from the trace model. It is constructed by a
 * {@link Builder} and cannot be manipulated after it has been constructed.
 * 
 * Any {@code ConnectionQuery} instance is a single use object and should be
 * used for only one query, then discarded.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class ConnectionQuery {

	private static final int DEFAULT_INITIAL_TRANSITIVITY_DEPTH = 1;

	private EObject element = null;
	private EObject traceModel = null;
	private boolean reverseDirection = false;
	private List<String> selectedRelationshipTypes = new ArrayList<>();
	private int transitivityDepth = 1;
	private boolean traverseTransitiveLinks = false;
	private boolean includeInternalLinks = false;

	private ConnectionQuery(Builder builder) {
		this.element = builder.element;
		this.traceModel = builder.traceModel;
		this.reverseDirection = builder.reverseDirection;
		this.selectedRelationshipTypes = builder.selectedRelationshipTypes;
		this.transitivityDepth = builder.transitivityDepth;
		this.traverseTransitiveLinks = builder.traverseTransitiveLinks;
		this.includeInternalLinks = builder.includeInternalLinks;
	}

	/**
	 * Creates a new {@link ConnectionQuery} {@link Builder} using the provided
	 * trace model and origin or target element.
	 *
	 * @param traceModel the trace model from which to retrieve the connections
	 * @param element    the element which should be the start or end point of all
	 *                   relevant connections.
	 * @return a new {@link Builder} instance
	 */
	public static Builder of(EObject traceModel, EObject element) {
		return new Builder(traceModel, element);
	}

	/**
	 * The element used as a starting or end point to determine the list of
	 * connected objects. Note that this element could be a trace in the trace
	 * model.
	 * <p>
	 * There are two ways {@code element} can be used:
	 * <ol>
	 * <li>If {@code element} is an arbitrary {@link EObject}, then the method
	 * returns all trace links in which {@code element} is either one of the
	 * <b>origins</b> (if (@code reverseDirection} is {@code false}) or one of the
	 * <b>targets</b> (if (@code reverseDirection} is {@code true}).</li>
	 * <li>If {@code element} is a trace link, then the method returns a
	 * representation of the trace link itself. The {@code reverseDirection}
	 * parameter is then ignored.</li>
	 * </ol>
	 * 
	 * @return the element used as a starting or end point of the query.
	 */
	public EObject getElement() {
		return element;
	}

	/**
	 * The trace model which contains the traceability links that should be queried.
	 * 
	 * @return the trace model
	 */
	public EObject getTraceModel() {
		return traceModel;
	}

	/**
	 * If set to {@code true}, traverses links in the trace model in the opposite
	 * direction. That means that origin are interpreted as targets and vice versa.
	 * This allows identifying which trace links end in a certain element.
	 * <p>
	 * Eclipse Capra stores <b>directional</b> trace links that always have an
	 * origin and a target. A trace link X->Y connects artifacts X (as the origin)
	 * and (Y) as the source. However, in some situations, it is useful to be able
	 * to find trace links that are coming into Y and identify if X and Y are
	 * connected regardless of link direction. Calling this method with {@code Y} as
	 * the element and {@code reverseDirection} is set to {@code true} would
	 * therefore return all connections for which {@code Y} is the target.
	 * 
	 * @return if trace links should be traversed in reverse direction
	 */
	public boolean isReverseDirection() {
		return reverseDirection;
	}

	/**
	 * The relationship types that should be included in the query. If this is set
	 * to an empty list all possible relationship types are included.
	 * 
	 * @return the relationship types to include in the query
	 */
	public List<String> getSelectedRelationshipTypes() {
		return selectedRelationshipTypes;
	}

	/**
	 * The maximum depth of the transitive closure. A value of {@code 0} means that
	 * no limit is applied, a value of {@code 1} that transitive links are not
	 * included.
	 * 
	 * @return the maximum depth of the transitive closure
	 */
	public int getTransitivityDepth() {
		return transitivityDepth;
	}

	/**
	 * Determines if transitive trace links are included in the query. This means
	 * that if a trace model contains a link A->B and a link B->C and this method is
	 * called with {@code true} as the parameter value, the resulting query will
	 * return the two connections representing these links if the maximum depth
	 * (returned by {@link this#getTransitivityDepth()} is either 0 or greater than
	 * 1. If the trace model does not contain any transitive links of this sort or
	 * the {@code transitivityDepth} is set to 1, the query will behave exactly as
	 * if this property was set to {@code false}.
	 * 
	 * @return whether or not to include transitive links in the query
	 */
	public boolean isTraverseTransitiveLinks() {
		return traverseTransitiveLinks;
	}

	/**
	 * Determines if internal links of a DSL (such as associations in UML) should be
	 * included in the query.
	 * 
	 * @return whether or not to include internal links in the query
	 */
	public boolean isIncludeInternalLinks() {
		return includeInternalLinks;
	}

	/**
	 * Builder to construct new {@link ConnectionQuery} instances. This class
	 * follows the Builder pattern in which each setter returns the instance of the
	 * {@code Builder}.
	 * 
	 * @author Jan-Philipp Steghöfer
	 *
	 */
	public static final class Builder {

		private EObject element = null;
		private EObject traceModel = null;
		private boolean reverseDirection = false;
		private List<String> selectedRelationshipTypes = new ArrayList<>();
		private int transitivityDepth = ConnectionQuery.DEFAULT_INITIAL_TRANSITIVITY_DEPTH;
		private boolean traverseTransitiveLinks = false;
		private boolean includeInternalLinks = false;

		/**
		 * Constructs a new {@link ConnectionQuery} instance from the properties that
		 * have been set in the builder.
		 * 
		 * @return a new {@link ConnectionQuery} instance.
		 */
		public ConnectionQuery build() {
			if (element == null) {
				throw new IllegalArgumentException("The origin of the connection must be set!");
			}
			if (traceModel == null) {
				throw new IllegalArgumentException("The trace model must be set!");
			}
			return new ConnectionQuery(this);
		}

		/**
		 * Instantiate a new {@code Builder} using the provided trace model and origin
		 * or target element.
		 *
		 * @param traceModel the trace model from which to retrieve the connections
		 * @param element    the element which should be the start or end point of all
		 *                   relevant connections.
		 * 
		 */
		private Builder(EObject traceModel, EObject element) {
			this.element = element;
			this.traceModel = traceModel;
		}

		/**
		 * If set to {@code true}, traverses links in the trace model in the opposite
		 * direction. That means that origin are interpreted as targets and vice versa.
		 * This allows identifying which trace links end in a certain element.
		 * <p>
		 * Eclipse Capra stores <b>directional</b> trace links that always have an
		 * origin and a target. A trace link X->Y connects artifacts X (as the origin)
		 * and (Y) as the source. However, in some situations, it is useful to be able
		 * to find trace links that are coming into Y and identify if X and Y are
		 * connected regardless of link direction. Calling this method with {@code Y} as
		 * the element and {@code reverseDirection} is set to {@code true} would
		 * therefore return all connections for which {@code Y} is the target.
		 * 
		 * @param reverseDirection set to {@code true} if the trace model should be
		 *                         traversed from targets to origins
		 * @return the Builder instance
		 */
		public Builder setReverseDirection(boolean reverseDirection) {
			this.reverseDirection = reverseDirection;
			return this;
		}

		/**
		 * Sets the relationship types that should be included in the query. If this is
		 * set to an empty list or to {@code null}, all possible relationship types are
		 * included.
		 * 
		 * @param selectedRelationshipTypes a list of permissible trace link types (may
		 *                                  be {@code null} or empty)
		 * @return the Builder instance
		 */
		public Builder setSelectedRelationshipTypes(List<String> selectedRelationshipTypes) {
			if (selectedRelationshipTypes == null) {
				this.selectedRelationshipTypes = new ArrayList<>();
			} else {
				this.selectedRelationshipTypes = selectedRelationshipTypes;
			}
			return this;
		}

		/**
		 * Sets the maximum depth of the transitive closure. A value of {@code 0} means
		 * that no limit is applied, a value of {@code 1} that transitive links are not
		 * included.
		 * 
		 * @param transitivityDepth the maximum depth of the transitive closure
		 * @return the Builder instance
		 */
		public Builder setTransitivityDepth(int transitivityDepth) {
			if (transitivityDepth < 0) {
				throw new IllegalArgumentException("Transitivity depth cannot be negative!");
			}
			this.transitivityDepth = transitivityDepth;
			return this;
		}

		/**
		 * Determines if transitive trace links are included in the query. This means
		 * that if a trace model contains a link A->B and a link B->C and this method is
		 * called with {@code true} as the parameter value, the resulting query will
		 * return the two connections representing these links if the maximum transitive
		 * depth (set with {@link this#setTransitivityDepth(int)} is either 0 or greater
		 * than 1. If the trace model does not contain any transitive links of this sort
		 * or the {@code transitivityDepth} is set to 1, the query will behave exactly
		 * as if this property was set to {@code false}.
		 * 
		 * @param traverseTransitiveLinks
		 * @return the Builder instance
		 */
		public Builder setTraverseTransitiveLinks(boolean traverseTransitiveLinks) {
			this.traverseTransitiveLinks = traverseTransitiveLinks;
			return this;
		}

		/**
		 * Determines if internal links of a DSL (such as associations in UML) should be
		 * included in the query.
		 * 
		 * @param includeInternalLinks
		 * @return the Builder instance
		 */
		public Builder setIncludeInternalLinks(boolean includeInternalLinks) {
			this.includeInternalLinks = includeInternalLinks;
			return this;
		}
	}

}
