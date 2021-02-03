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
import java.util.stream.Collectors;

import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.emf.ecore.EObject;

/**
 * An minimal abstraction of a traceability link used in
 * {@link TraceMetaModelAdapter}, to retain independence of a concrete trace
 * metamodel.
 * 
 * @author Anthony Anjorin, Salome Maro
 */
public class Connection {
	private List<EObject> origins;
	private List<EObject> targets;
	private EObject tlink;

	/**
	 * Constructs a new {@code Connection} instance based on the provided origins,
	 * targets, and underlying trace link.
	 * 
	 * @param origins the origins, i.e., the artifacts from which the link
	 *                originates
	 * @param targets the targets, i.e., the artifacts to which the link points
	 * @param tlink   the underlying trace link from the trace model
	 */
	public Connection(List<EObject> origins, List<EObject> targets, EObject tlink) {
		this.origins = origins;
		this.targets = targets;
		this.tlink = tlink;
	}

	/**
	 * Get all origins, i.e., artifacts from which the trace link originates.
	 * 
	 * @return the origins
	 */
	public List<EObject> getOrigins() {
		return origins;
	}

	/**
	 * Get all targets, i.e., artifacts to which the trace link points.
	 * 
	 * @return the targets
	 */
	public List<EObject> getTargets() {
		return targets;
	}

	/**
	 * Gets the underlying trace link from the trace model.
	 * 
	 * @return the trace link which this {@code Connection} instance represents
	 */
	public EObject getTlink() {
		return tlink;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (!(object instanceof Connection)) {
			return false;
		}
		if (object == this) {
			return true;
		}
		Connection connection = (Connection) object;

		List<EObject> allFirstElements = new ArrayList<>(this.getTargets());
		allFirstElements.addAll(this.getOrigins());

		List<EObject> allSecondElements = new ArrayList<>(connection.getTargets());
		allSecondElements.addAll(connection.getOrigins());

		String firstTraceType = EMFHelper.getIdentifier(this.getTlink().eClass());
		String secondTracetype = EMFHelper.getIdentifier(connection.getTlink().eClass());
		if (!(firstTraceType.equals(secondTracetype))) {
			return false;
		}

		List<String> firstElementsIds = allFirstElements.stream().map(EMFHelper::getIdentifier)
				.collect(Collectors.toList());
		List<String> secondElementsIds = allSecondElements.stream().map(EMFHelper::getIdentifier)
				.collect(Collectors.toList());
		return (firstElementsIds.containsAll(secondElementsIds) && secondElementsIds.containsAll(firstElementsIds));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((origins == null) ? 0 : origins.stream().mapToInt(e -> EMFHelper.getIdentifier(e).hashCode()).sum());
		result = prime * result
				+ ((targets == null) ? 0 : targets.stream().mapToInt(e -> EMFHelper.getIdentifier(e).hashCode()).sum());
		result = prime * result + ((tlink == null) ? 0 : tlink.hashCode());
		return result;
	}

}
