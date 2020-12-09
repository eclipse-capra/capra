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
package org.eclipse.capra.ui.plantuml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Strings;

/**
 * Helper class for generating PlantUML diagrams from a collection of
 * {@link Connection}
 *
 * @author Anthony Anjorin, Salome Maro, Jan-Philipp Steghöfer
 */
public class Connections {

	private static final String QUOTE_CHARACTERS = "[\"\']";
	private static final String NEWLINE_CHARACTERS = "[\r\n]+";
	private List<Connection> connections;
	private EObject origin;

	private Set<EObject> allObjects;
	private Map<EObject, String> object2Id;
	private Map<String, String> id2Label;
	private Map<String, String> id2Location;

	private ArtifactHelper artifactHelper;

	private String sanitize(String input) {
		return input.replaceAll(QUOTE_CHARACTERS, " ").replaceAll(NEWLINE_CHARACTERS, " ");
	}

	Connections(List<Connection> connections, List<EObject> selectedObjects, EObject artifactModel) {
		this.artifactHelper = new ArtifactHelper(artifactModel);
		this.connections = connections;
		origin = selectedObjects.get(0);

		allObjects = new LinkedHashSet<>();
		allObjects.addAll(selectedObjects);
		connections.forEach(c -> allObjects.addAll(c.getTargets()));

		object2Id = new LinkedHashMap<>();
		int i = 0;
		for (EObject o : allObjects) {
			object2Id.put(o, "o" + i++);
		}

		id2Label = new LinkedHashMap<>();
		allObjects.forEach(o -> {
			String id = object2Id.get(o);
			id2Label.put(id, artifactHelper.getArtifactLabel(o));
		});

		id2Location = new LinkedHashMap<>();
		allObjects.forEach(o -> {
			String id = object2Id.get(o);
			id2Location.put(id, artifactHelper.getArtifactLocation(o));
		});
	}

	public String originLabel() {
		return sanitize(id2Label.get(object2Id.get(origin)));
	}

	public String originLocation() {
		return id2Location.get(object2Id.get(origin));
	}

	public boolean originHasLocation() {
		return !Strings.isNullOrEmpty(id2Location.get(object2Id.get(origin)));
	}

	public String originId() {
		return object2Id.get(origin);
	}

	public Collection<String> objectIdsWithoutOrigin() {
		Collection<String> all = new ArrayList<>();
		all.addAll(object2Id.values());
		all.remove(originId());
		return all;
	}

	public String label(String id) {
		return sanitize(id2Label.get(id));
	}

	public String location(String id) {
		return id2Location.get(id);
	}

	public boolean hasLocation(String id) {
		return !Strings.isNullOrEmpty(id2Location.get(id));
	}

	public List<String> arrows() {
		Set<String> arrows = new HashSet<>();

		connections.forEach(c -> {
			c.getTargets().forEach(trg -> {
				if (!trg.equals(c.getOrigin())) {
					arrows.add(object2Id.get(c.getOrigin()) + "--" + object2Id.get(trg) + ":"
							+ EMFHelper.getIdentifier(c.getTlink()));
				}
			});
		});

		return arrows.stream().collect(Collectors.toList());
	}

}
