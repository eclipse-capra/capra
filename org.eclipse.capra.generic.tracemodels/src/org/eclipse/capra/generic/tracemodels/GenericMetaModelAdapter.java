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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelFactory;
import org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage;
import org.eclipse.capra.GenericTraceMetaModel.GenericTraceModel;
import org.eclipse.capra.GenericTraceMetaModel.RelatedTo;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.handlers.ArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * Provides generic functionality to deal with traceability meta models.
 */
public class GenericMetaModelAdapter implements TraceMetaModelAdapter {

	public GenericMetaModelAdapter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public EObject createModel() {
		return GenericTraceMetaModelFactory.eINSTANCE.createGenericTraceModel();
	}

	@Override
	public Collection<EClass> getAvailableTraceTypes(List<EObject> selection) {
		Collection<EClass> traceTypes = new ArrayList<>();
		if (selection.size() > 1) {

			traceTypes.add(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		}
		return traceTypes;
	}

	@Override
	public EObject createTrace(EClass traceType, EObject traceModel, List<EObject> selection,
			List<Object> originalSelection) {

		GenericTraceModel TM = (GenericTraceModel) traceModel;
		EObject trace = GenericTraceMetaModelFactory.eINSTANCE.create(traceType);
		RelatedTo RelatedToTrace = (RelatedTo) trace;
		RelatedToTrace.getItem().addAll(selection);

		// String builder to build the name of the trace link so by adding the
		// elements it connects so as to make it easy for a user to visually
		// differentiate trace links
		StringBuilder name = new StringBuilder();
		Collection<ArtifactHandler> artifactHandlers = ExtensionPointHelper.getArtifactHandlers();
		List<ArtifactHandler> availableHandlers = null;

		for (Object obj : originalSelection) {
			availableHandlers = artifactHandlers.stream().filter(handler -> handler.canHandleSelection(obj))
					.collect(Collectors.toList());
			if (availableHandlers.size() > 0) {
				name = name.append(" ").append(availableHandlers.get(0).getDisplayName(obj));
			} else
				name = name.append(" ").append(originalSelection.toString());
		}

		RelatedToTrace.setName(name.toString());
		TM.getTraces().add(RelatedToTrace);
		return TM;
	}

	@Override
	public void deleteTrace(EObject first, EObject second, EObject traceModel) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isThereATraceBetween(EObject firstElement, EObject secondElement, EObject traceModel) {
		GenericTraceModel root = (GenericTraceModel) traceModel;
		List<RelatedTo> traces = root.getTraces();
		for (RelatedTo trace : traces) {
			if (firstElement != secondElement) {
				return trace.getItem().contains(firstElement) && trace.getItem().contains(secondElement);
			}
		}
		return false;
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

	private List<Connection> getTransitivelyConnectedElements(EObject element, EObject traceModel,
			List<Object> accumulator) {
		List<Connection> directElements = getConnectedElements(element, traceModel);
		List<Connection> allElements = new ArrayList<>();

		directElements.forEach(connection -> {
			if (!accumulator.contains(connection.getTlink())) {
				allElements.add(connection);
				accumulator.add(connection.getTlink());
				connection.getTargets().forEach(e -> {
					allElements.addAll(getTransitivelyConnectedElements(e, traceModel, accumulator));
				});
			}
		});

		return allElements;
	}

	@Override
	public List<Connection> getTransitivelyConnectedElements(EObject element, EObject traceModel) {
		List<Object> accumulator = new ArrayList<>();
		return getTransitivelyConnectedElements(element, traceModel, accumulator);
	}

}
