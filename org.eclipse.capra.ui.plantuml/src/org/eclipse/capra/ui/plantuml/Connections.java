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
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.generic.artifactmodel.ArtifactWrapper;
import org.eclipse.emf.ecore.EObject;

/**
 * Helper class for generating PlantUML diagrams from a collection of
 * {@link Connection}
 *
 * @author Anthony Anjorin, Salome Maro
 */
public class Connections {

	private static final String CHARACTERS_TO_BE_REMOVED = "[\", \']";
	private List<Connection> connections;
	private EObject origin;

	private Set<EObject> allObjects;
	private Map<EObject, String> object2Id;
	private Map<String, String> id2Label;

	Connections(List<Connection> connections, List<EObject> selectedObjects) {
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
			id2Label.put(id, getArtifactLabel(o));
		});
	}

	public String originLabel() {
		return id2Label.get(object2Id.get(origin));
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
		return id2Label.get(id);
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

	/**
	 * The method gets the label of the element to be used for display in the
	 * plant UML graph view and matrix view
	 *
	 * @param object
	 *            The object for which the label is needed. This can be an EMF
	 *            original representation or an artifact wrapper if the original
	 *            object was not an EMF element
	 * @return The label to be displayed
	 */
	public static String getArtifactLabel(EObject object) {
		String artifactLabel = null;
		if (object instanceof ArtifactWrapper) {
			ArtifactWrapper wrapper = (ArtifactWrapper) object;
			Collection<IArtifactHandler<?>> artifactHandlers = ExtensionPointHelper.getArtifactHandlers();

			for (IArtifactHandler<?> handler : artifactHandlers) {
				String handlerName = handler.toString().substring(0, handler.toString().indexOf('@'));
				if (handlerName.equals(wrapper.getArtifactHandler())) {
					Object originalObject = handler.resolveWrapper(object);
					if (originalObject != null) {
						artifactLabel = handler.withCastedHandler(originalObject, (h, o) -> h.getDisplayName(o))
								.orElseThrow(IllegalArgumentException::new);
					} else { // original object cannot be resolved
								// therefore use the wrapper name
						String label = EMFHelper.getIdentifier(object);
						artifactLabel = label.substring(0, label.indexOf(':'));
					}
				}
			}
		} else {
			artifactLabel = EMFHelper.getIdentifier(object);
		}
		// remove unwanted characters like ", '
		if (artifactLabel != null) {
			return artifactLabel.replaceAll(CHARACTERS_TO_BE_REMOVED, " ");
		} else {
			// This can happen if the trace model contains elements for which
			// the artifact handler is not available.
			// While this should not happen in a user installation, it is not
			// uncommon during testing.
			return "Unknown (no fitting artifact handler found)";
		}
	}
}
