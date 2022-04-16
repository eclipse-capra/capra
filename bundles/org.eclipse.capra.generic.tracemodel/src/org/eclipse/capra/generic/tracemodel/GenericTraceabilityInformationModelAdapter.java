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
package org.eclipse.capra.generic.tracemodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.capra.core.adapters.AbstractTraceabilityInformationModelAdapter;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.IMetadataAdapter;
import org.eclipse.capra.core.adapters.IPersistenceAdapter;
import org.eclipse.capra.core.adapters.ITraceabilityInformationModelAdapter;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.core.listeners.ITraceCreationListener;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * Provides functionality to access the generic trace meta-model. This trace
 * meta-model only contains one link type {@code RelatedTo} that can be used to
 * relate any artifacts to each other. It is directed and has a single origin
 * artifacts and one or more target artifacts.
 */
public class GenericTraceabilityInformationModelAdapter extends AbstractTraceabilityInformationModelAdapter
		implements ITraceabilityInformationModelAdapter {

	private static final int DEFAULT_INITIAL_TRANSITIVITY_DEPTH = 1;

	public GenericTraceabilityInformationModelAdapter() {
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
	public EObject createTrace(EClass traceType, EObject traceModel, List<EObject> origins, List<EObject> targets) {
		GenericTraceModel tm = (GenericTraceModel) traceModel;
		EObject trace = TracemodelFactory.eINSTANCE.create(traceType);
		RelatedTo relatedToTrace = (RelatedTo) trace;
		relatedToTrace.setOrigin(origins.get(0));
		relatedToTrace.getTargets().addAll(targets);
		IPersistenceAdapter persistenceAdapter = ExtensionPointHelper.getPersistenceAdapter().orElseThrow();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(EditingDomainHelper.getResourceSet());
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);

		// String builder to build the name of the trace link so by adding the
		// elements it connects so as to make it easy for a user to visually
		// differentiate trace links
		StringBuilder name = new StringBuilder();
		name.append(artifactHelper.getHandler(artifactHelper.unwrapWrapper(origins.get(0))).orElseThrow()
				.withCastedHandler(artifactHelper.unwrapWrapper(origins.get(0)), (h, e) -> h.getDisplayName(e))
				.orElseGet(origins.get(0)::toString));
		for (Object obj : targets) {
			name.append(" ")
					.append(artifactHelper.getHandler(artifactHelper.unwrapWrapper(obj)).orElseThrow()
							.withCastedHandler(artifactHelper.unwrapWrapper(obj), (h, e) -> h.getDisplayName(e))
							.orElseGet(obj::toString));
		}
		relatedToTrace.setName(name.toString());

		TransactionalEditingDomain editingDomain = EditingDomainHelper.getEditingDomain();
		// We're saving the trace model and the artifact model in the same transaction
		Command cmd = new RecordingCommand(editingDomain, "Add trace") {
			@Override
			protected void doExecute() {
				tm.getTraces().add(relatedToTrace);
			}
		};

		try {
			((TransactionalCommandStack) editingDomain.getCommandStack()).execute(cmd, null); // default options
		} catch (RollbackException e) {
			throw new IllegalStateException("Adding a trace link was rolled back.", e);
		} catch (InterruptedException e) {
			throw new IllegalStateException("Adding a trace link was interrupted.", e);
		}

		// Inform all registered listeners
		Connection conn = new Connection(origins, targets, relatedToTrace);
		Collection<ITraceCreationListener> listeners = ExtensionPointHelper.getTraceCreationListeners();
		for (ITraceCreationListener creationListener : listeners) {
			creationListener.onTraceCreation(conn);
		}

		return relatedToTrace;
	}

	@Override
	public boolean isThereATraceBetween(EObject firstElement, EObject secondElement, EObject traceModel) {
		return this.isThereATraceBetween(firstElement, secondElement, traceModel, false);
	}

	@Override
	public List<Connection> getConnectedElements(EObject element, EObject tracemodel) {
		return this.getConnectedElements(element, tracemodel, new ArrayList<String>());
	}

	@Override
	public List<Connection> getConnectedElements(EObject element, EObject traceModel, boolean reverseDirection) {
		return getConnectedElements(element, traceModel, new ArrayList<String>(), reverseDirection);
	}

	@Override
	public List<Connection> getConnectedElements(EObject element, EObject traceModel,
			List<String> selectedRelationshipTypes, boolean reverseDirection) {
		GenericTraceModel root = (GenericTraceModel) traceModel;
		List<Connection> connections = new ArrayList<>();
		List<RelatedTo> traces = root.getTraces();
		if (selectedRelationshipTypes.isEmpty()
				|| selectedRelationshipTypes.contains(TracemodelPackage.eINSTANCE.getRelatedTo().getName())) {
			if (element instanceof RelatedTo) {
				RelatedTo trace = (RelatedTo) element;
				connections.add(new Connection(Arrays.asList(element), trace.getTargets(), trace));
			} else {
				for (RelatedTo trace : traces) {
					if (!reverseDirection && EcoreUtil.equals(element, trace.getOrigin())) {
						connections.add(new Connection(Arrays.asList(element), trace.getTargets(), trace));
					} else if (reverseDirection && EMFHelper.isElementInList(trace.getTargets(), element)) {
						connections
								.add(new Connection(Arrays.asList(trace.getOrigin()), Arrays.asList(element), trace));
					}
				}
			}
		}
		return connections;
	}

	@Override
	public List<Connection> getConnectedElements(EObject element, EObject tracemodel,
			List<String> selectedRelationshipTypes) {
		return getConnectedElements(element, tracemodel, selectedRelationshipTypes, false);
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
		return getTransitivelyConnectedElements(element, traceModel, accumulator, DEFAULT_INITIAL_TRANSITIVITY_DEPTH,
				maximumDepth);
	}

	@Override
	public List<Connection> getAllTraceLinks(EObject traceModel) {
		GenericTraceModel model = (GenericTraceModel) traceModel;
		List<Connection> allLinks = new ArrayList<>();

		for (RelatedTo trace : model.getTraces()) {
			allLinks.add(new Connection(Arrays.asList(trace.getOrigin()), trace.getTargets(), trace));
		}
		return allLinks;
	}

	@Override
	public void deleteTrace(List<Connection> toDelete, EObject traceModel) {
		List<Object> toRemove = new ArrayList<>();
		IPersistenceAdapter persistenceAdapter = ExtensionPointHelper.getPersistenceAdapter().orElseThrow();
		if (traceModel instanceof GenericTraceModel) {
			GenericTraceModel tModel = (GenericTraceModel) traceModel;
			for (Connection c : toDelete) {
				for (RelatedTo trace : tModel.getTraces()) {
					if (EcoreUtil.equals(trace, c.getTlink())) {
						toRemove.add(trace);
					}
				}
			}

			// Remove the metadata information about the traces
			IMetadataAdapter metadataAdapter = ExtensionPointHelper.getTraceMetadataAdapter().orElseThrow();
			for (Object trace : toRemove) {
				if (trace instanceof EObject) {
					metadataAdapter.removeMetadata((EObject) trace,
							persistenceAdapter.getMetadataContainer(EditingDomainHelper.getResourceSet()));
				}
			}

			// Remove the traces from the trace model
			TransactionalEditingDomain editingDomain = EditingDomainHelper.getEditingDomain();
			Command cmd = new RecordingCommand(editingDomain, "Remove traces") {
				@Override
				protected void doExecute() {
					for (Object trace : toRemove) {
						tModel.getTraces().remove(trace);
					}
				}
			};

			try {
				((TransactionalCommandStack) editingDomain.getCommandStack()).execute(cmd, null); // default options
			} catch (RollbackException e) {
				throw new IllegalStateException("Removing trace links was rolled back.", e);
			} catch (InterruptedException e) {
				throw new IllegalStateException("Removing trace links was interrupted.", e);
			}

			persistenceAdapter.saveModels(tModel,
					persistenceAdapter.getArtifactWrappers(EditingDomainHelper.getResourceSet()),
					persistenceAdapter.getMetadataContainer(EditingDomainHelper.getResourceSet()));
		}
	}

	@Override
	public List<Connection> getTransitivelyConnectedElements(EObject element, EObject traceModel,
			List<String> traceLinkTypes, int maximumDepth) {
		List<Object> accumulator = new ArrayList<>();
		return getTransitivelyConnectedElements(element, traceModel, accumulator, traceLinkTypes,
				DEFAULT_INITIAL_TRANSITIVITY_DEPTH, maximumDepth);
	}

	private List<Connection> getTransitivelyConnectedElements(EObject element, EObject traceModel,
			List<Object> accumulator, List<String> traceLinkTypes, int currentDepth, int maximumDepth) {
		List<Connection> directElements = getConnectedElements(element, traceModel, traceLinkTypes);
		List<Connection> allElements = new ArrayList<>();
		int currDepth = currentDepth + 1;
		for (Connection connection : directElements) {
			if (!accumulator.contains(connection.getTlink())) {
				allElements.add(connection);
				accumulator.add(connection.getTlink());
				for (EObject e : connection.getTargets()) {
					if (maximumDepth == 0 || currDepth <= maximumDepth) {
						allElements.addAll(getTransitivelyConnectedElements(e, traceModel, accumulator, traceLinkTypes,
								currDepth, maximumDepth));
					}
				}
			}
		}

		return allElements;
	}

	@Override
	public boolean isThereATraceBetween(EObject origin, EObject target, EObject traceModel, boolean reverseDirection) {
		if (traceModel == null || origin == null || target == null) {
			return false;
		}
		EObject firstElement;
		EObject secondElement;

		if (!reverseDirection) {
			firstElement = origin;
			secondElement = target;
		} else {
			firstElement = target;
			secondElement = origin;
		}
		GenericTraceModel root = (GenericTraceModel) traceModel;
		List<RelatedTo> relevantLinks = new ArrayList<>();
		List<RelatedTo> allTraces = root.getTraces();

		for (RelatedTo trace : allTraces) {
			if (!firstElement.equals(secondElement) && EMFHelper.hasSameUriOrIdentifier(firstElement, trace.getOrigin())
					&& EMFHelper.isElementInList(trace.getTargets(), secondElement)) {
				relevantLinks.add(trace);
			}
		}
		return !relevantLinks.isEmpty();
	}

}
