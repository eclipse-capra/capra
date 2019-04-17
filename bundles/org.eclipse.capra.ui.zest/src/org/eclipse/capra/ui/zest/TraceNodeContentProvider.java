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
package org.eclipse.capra.ui.zest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef4.zest.fx.jface.IGraphContentProvider;

/**
 * 
 * This class provides the content to be displayed on the Zest view in form of a
 * graph. It gets the traceability links and forms a graph with the connected
 * elements as nodes and the traceability links as edges.
 *
 */
public class TraceNodeContentProvider implements IGraphContentProvider {

	private TraceMetaModelAdapter metaModelAdapter;
	private EObject traceModel = null;
	private List<Connection> connections = new ArrayList<>();
	private List<Object> currentSelection = null;

	/**
	 * This constructor gets the trace model, traceMetamodel adapter and the
	 * list of selected objects by the user and sets them to the private
	 * variables of the class
	 * 
	 * @param traceModel
	 *            the current trace model
	 * @param metaModelAdapter
	 *            the metamodel adapter
	 * @param element
	 *            the current selection
	 */
	public TraceNodeContentProvider(EObject traceModel, TraceMetaModelAdapter metaModelAdapter, List<Object> element) {
		this.traceModel = traceModel;
		this.currentSelection = element;
		this.metaModelAdapter = metaModelAdapter;

	}

	@Override
	public Object[] getAdjacentNodes(Object node) {
		List<EObject> nodes = new ArrayList<>();

		if (node instanceof EObject) {
			EObject object = (EObject) node;
			connections = metaModelAdapter.getConnectedElements(object, traceModel);
			for (Connection c : connections) {
				nodes.addAll(c.getTargets());
			}
		}
		return nodes.toArray();
	}

	@Override
	public Object[] getNestedGraphNodes(Object node) {
		return null;
	}

	@Override
	public Object[] getNodes() {
		List<EObject> nodes = new ArrayList<>();
		if (currentSelection.get(0) instanceof EObject) {
			EObject object = (EObject) currentSelection.get(0);
			int transitivityDepth = 0;
			connections = metaModelAdapter.getTransitivelyConnectedElements(object, traceModel, transitivityDepth);
			for (Connection c : connections) {
				nodes.add(c.getOrigin());
				nodes.addAll(c.getTargets());
			}
		}
		// return only distinct values as nodes of the graph
		return nodes.stream().distinct().collect(Collectors.toList()).toArray();

	}

	@Override
	public boolean hasNestedGraph(Object node) {
		return false;
	}

}