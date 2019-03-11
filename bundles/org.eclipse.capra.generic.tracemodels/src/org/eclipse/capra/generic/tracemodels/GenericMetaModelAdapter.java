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
package org.eclipse.capra.generic.tracemodels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.eclipse.capra.core.adapters.AbstractMetaModelAdapter;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.generic.tracemodel.GenericTraceModel;
import org.eclipse.capra.generic.tracemodel.RelatedTo;
import org.eclipse.capra.generic.tracemodel.TracemodelFactory;
import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides generic functionality to deal with traceability meta models.
 */
public class GenericMetaModelAdapter extends AbstractMetaModelAdapter implements TraceMetaModelAdapter {
	
	private static final Logger LOG = LoggerFactory.getLogger(GenericMetaModelAdapter.class);

	private static final int DEFAULT_INITIAL_TRANSITIVITY_DEPTH =-2;

	public GenericMetaModelAdapter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public EObject createModel() {
		return TracemodelFactory.eINSTANCE.createGenericTraceModel();
	}

	@Override
	public Collection<EClass> getAvailableTraceTypes(List<EObject> selection) {
		Collection<EClass> traceTypes = new ArrayList<>();
		if (selection.size() > 1) {
			traceTypes.add(TracemodelPackage.eINSTANCE.getRelatedTo());
		}
		return traceTypes;
	}

	@Override
	public EObject createTrace(EClass traceType, EObject traceModel, List<EObject> selection) {
		GenericTraceModel tm = (GenericTraceModel) traceModel;
		EObject trace = TracemodelFactory.eINSTANCE.create(traceType);
		RelatedTo relatedToTrace = (RelatedTo) trace;
		relatedToTrace.getItem().addAll(selection);

		// String builder to build the name of the trace link so by adding the
		// elements it connects so as to make it easy for a user to visually
		// differentiate trace links
		StringBuilder name = new StringBuilder();
		for (Object obj : selection) {
				name.append(" ").append(ExtensionPointHelper.getArtifactHandlers().stream()
						.map(handler -> handler.withCastedHandler(obj, (h, e) -> h.getDisplayName(e)))
						.filter(Optional::isPresent)
						.map(Optional::get)
						.findFirst()
						.orElseGet(obj::toString));
		}
		relatedToTrace.setName(name.toString());
		tm.getTraces().add(relatedToTrace);
		return tm;
	}

	@Override
	public boolean isThereATraceBetween(EObject firstElement, EObject secondElement, EObject traceModel) {
		GenericTraceModel root = (GenericTraceModel) traceModel;
		List<RelatedTo> relevantLinks = new ArrayList<RelatedTo>();
		List<RelatedTo> allTraces = root.getTraces();

		for (RelatedTo trace : allTraces) {
			if (!firstElement.equals(secondElement)) {
				if (trace.getItem().contains(firstElement) && trace.getItem().contains(secondElement)) {
					relevantLinks.add(trace);
				}
			}
		}
		return relevantLinks.size() > 0;
	}

	@Override
	public List<Connection> getConnectedElements(EObject element, EObject tracemodel) {
		GenericTraceModel root = (GenericTraceModel) tracemodel;
		List<Connection> connections = new ArrayList<>();
		List<RelatedTo> traces = root.getTraces();

		if (element instanceof RelatedTo) {
			RelatedTo trace = (RelatedTo) element;
			connections.add(new Connection(element, trace.getItem(), trace));
		} else {
			for (RelatedTo trace : traces) {
				if (trace.getItem().contains(element)) {
					connections.add(new Connection(element, trace.getItem(), trace));
				}
			}
		}
		return connections;
	}

	@Override
	public List<Connection> getConnectedElements(EObject element, EObject tracemodel,
			List<String> selectedRelationshipTypes) {
		GenericTraceModel root = (GenericTraceModel) tracemodel;
		List<Connection> connections = new ArrayList<>();
		List<RelatedTo> traces = root.getTraces();

		if (selectedRelationshipTypes.size() == 0 || selectedRelationshipTypes
				.contains(TracemodelPackage.eINSTANCE.getRelatedTo().getName())) {
			if (element instanceof RelatedTo) {
				RelatedTo trace = (RelatedTo) element;
				connections.add(new Connection(element, trace.getItem(), trace));
			} else {
				for (RelatedTo trace : traces) {
					if (trace.getItem().contains(element)) {
						connections.add(new Connection(element, trace.getItem(), trace));
					}
				}
			}
		}
		return connections;
	}

	private List<Connection> getTransitivelyConnectedElements(EObject element, EObject traceModel,
			List<Object> accumulator, int currentDepth, int maximumDepth) {
		List<Connection> directElements = getConnectedElements(element, traceModel);
		List<Connection> allElements = new ArrayList<>();

		int currDepth = currentDepth + 1;
		for (Connection connection : directElements) {
			if (!accumulator.contains(connection.getTlink())) {
				allElements.add(connection);
				accumulator.add(connection.getTlink());
				for (EObject e : connection.getTargets()) {
					if (maximumDepth == 0 || currDepth <= maximumDepth) {
						allElements.addAll(
								getTransitivelyConnectedElements(e, traceModel, accumulator, currDepth, maximumDepth));
					}
				}
			}
		}
		return allElements;
	}

	@Override
	public List<Connection> getTransitivelyConnectedElements(EObject element, EObject traceModel, int maximumDepth) {
		List<Object> accumulator = new ArrayList<>();
		return getTransitivelyConnectedElements(element, traceModel, accumulator, DEFAULT_INITIAL_TRANSITIVITY_DEPTH, maximumDepth);
	}

	@Override
	public List<Connection> getAllTraceLinks(EObject traceModel) {
		GenericTraceModel model = (GenericTraceModel) traceModel;
		List<Connection> allLinks = new ArrayList<>();

		for (RelatedTo trace : model.getTraces()) {
			EObject origin = trace.getItem().get(0);
			trace.getItem().remove(0);
			allLinks.add(new Connection(origin, trace.getItem(), trace));
		}
		return allLinks;
	}

	@Override
	public void deleteTrace(List<Connection> toDelete, EObject traceModel) {
		if (traceModel instanceof GenericTraceModel) {
			GenericTraceModel tModel = (GenericTraceModel) traceModel;
			EList<RelatedTo> links = tModel.getTraces();
			ResourceSet resourceSet = new ResourceSetImpl();
			for (Connection c : toDelete) {
				links.remove(c.getTlink());
			}
			GenericTraceModel newTraceModel = TracemodelFactory.eINSTANCE.createGenericTraceModel();
			newTraceModel.getTraces().addAll(links);
			URI traceModelURI = EcoreUtil.getURI(traceModel);
			Resource resourceForTraces = resourceSet.createResource(traceModelURI);
			resourceForTraces.getContents().add(newTraceModel);
	
			try {
				resourceForTraces.save(null);
                          	// TODO: Think of a way to let the developer handle such sitations (e.g., via an Exception)
			} catch (IOException e) {
				LOG.error("Trace model could not be saved.", e);
			}
		}
	}

	@Override
	public List<Connection> getTransitivelyConnectedElements(EObject element, EObject traceModel,
			List<String> selectedRelationshipTypes, int maximumDepth) {
		List<Object> accumulator = new ArrayList<>();
		return getTransitivelyConnectedElements(element, traceModel, accumulator, selectedRelationshipTypes, DEFAULT_INITIAL_TRANSITIVITY_DEPTH,
				maximumDepth);
	}

	private List<Connection> getTransitivelyConnectedElements(EObject element, EObject traceModel,
			List<Object> accumulator, List<String> selectedRelationshipTypes, int currentDepth, int maximumDepth) {
		List<Connection> directElements = getConnectedElements(element, traceModel, selectedRelationshipTypes);
		List<Connection> allElements = new ArrayList<>();
		int currDepth = currentDepth + 1;
		for (Connection connection : directElements) {
			if (!accumulator.contains(connection.getTlink())) {
				allElements.add(connection);
				accumulator.add(connection.getTlink());
				for (EObject e : connection.getTargets()) {
					if (maximumDepth == 0 || currDepth <= maximumDepth) {
						allElements.addAll(getTransitivelyConnectedElements(e, traceModel, accumulator,
								selectedRelationshipTypes, currDepth, maximumDepth));
					}
				}
			}
		}

		return allElements;
	}
}
