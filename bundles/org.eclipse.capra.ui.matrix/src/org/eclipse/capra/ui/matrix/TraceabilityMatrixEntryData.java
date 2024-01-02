/*******************************************************************************
 * Copyright (c) 2016-2023 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *   
 * SPDX-License-Identifier: EPL-2.0
 *   
 * Contributors:
 *     Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *     Chalmers | University of Gothenburg - additional features, updated API
 *     Fredrik Johansson and Themistoklis Ntoukolis - initial implementation of the Matrix View
 *     Jan-Philipp Steghöfer - additional features
 *******************************************************************************/
package org.eclipse.capra.ui.matrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.core.resources.IMarker;
import org.eclipse.emf.ecore.EObject;

/**
 * 
 * @author Jan-Philipp Steghöfer
 */
public class TraceabilityMatrixEntryData {
	private EObject artifact;
	private List<Connection> connections = new ArrayList<>();
	private Set<IMarker> markers = new HashSet<>();
	private String label;

	public TraceabilityMatrixEntryData(EObject artifact, String label) {
		this.artifact = artifact;
		this.label = label;
	}

	public EObject getArtifact() {
		return artifact;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public Set<IMarker> getMarkers() {
		return markers;
	}

	public void setArtifact(EObject artifact) {
		this.artifact = artifact;
	}

	public void setConnections(List<Connection> connections) {
		this.connections = connections;
	}

	public void setMarkers(Set<IMarker> markers) {
		this.markers = markers;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}

}