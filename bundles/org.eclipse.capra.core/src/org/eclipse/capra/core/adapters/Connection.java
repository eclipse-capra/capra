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
	private EObject origin;
	private List<EObject> targets;
	private EObject tlink;

	public Connection(EObject origin, List<EObject> targets, EObject tlink) {
		this.origin = origin;
		this.targets = targets;
		this.tlink = tlink;
	}

	public EObject getOrigin() {
		return origin;
	}

	public List<EObject> getTargets() {
		return targets;
	}

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
		allFirstElements.add(this.getOrigin());

		List<EObject> allSecondElements = new ArrayList<>(connection.getTargets());
		allSecondElements.add(connection.getOrigin());

		String firstTraceType = EMFHelper.getIdentifier(this.getTlink().eClass());
		String secondTracetype = EMFHelper.getIdentifier(connection.getTlink().eClass());
		if (!(firstTraceType.equals(secondTracetype))) {
			return false;
		}

		List<String> firstElementsIds = allFirstElements.stream().map(e -> EMFHelper.getIdentifier(e))
				.collect(Collectors.toList());
		List<String> secondElementsIds = allSecondElements.stream().map(e -> EMFHelper.getIdentifier(e))
				.collect(Collectors.toList());
		return firstElementsIds.containsAll(secondElementsIds);
	}

}
