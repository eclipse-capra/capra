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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.ConnectionQuery;
import org.eclipse.capra.core.adapters.IPersistenceAdapter;
import org.eclipse.capra.core.adapters.ITraceabilityInformationModelAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.handlers.IArtifactUnpacker;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.helpers.SelectionSupportHelper;
import org.eclipse.capra.ui.plantuml.handlers.DisplayInternalLinksHandler;
import org.eclipse.capra.ui.plantuml.handlers.ReverseLinkDirectionHandler;
import org.eclipse.capra.ui.plantuml.handlers.SelectRelationshipTypesHandler;
import org.eclipse.capra.ui.plantuml.handlers.ToggleDisplayGraphHandler;
import org.eclipse.capra.ui.plantuml.handlers.ToggleTransitivityHandler;
import org.eclipse.capra.ui.plantuml.handlers.TransitivityDepthHandler;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.osgi.service.prefs.Preferences;

import net.sourceforge.plantuml.eclipse.utils.DiagramTextProvider;
import net.sourceforge.plantuml.eclipse.views.PlantUmlView;

/**
 * Provides PlantUML with a string representation of elements connected by trace
 * links.
 *
 * @author Anthony Anjorin, Salome Maro, Jan-Philipp Steghöfer
 */
public class CapraDiagramTextProvider implements DiagramTextProvider {
	private EObject artifactModel = null;

	private String currentDiagramText = null;

	private boolean isLockDiagram() {
		Preferences preferences = InstanceScope.INSTANCE.getNode("org.eclipse.capra.ui.plantuml.lockDiagram");
		return preferences.node("lockDiagram").getBoolean("option", false);
	}

	@Override
	public String getDiagramText(IEditorPart editor, ISelection input) {
		return (getDiagramText(editor));
	}

	@Override
	public String getDiagramText(IViewPart view, ISelection input) {
		return (getDiagramText(view));
	}

	public String getDiagramText(IWorkbenchPart part) {
		List<Object> selectedModels = new ArrayList<>();
		if (part.getSite().getSelectionProvider() != null) {
			selectedModels.addAll(SelectionSupportHelper
					.extractSelectedElements(part.getSite().getSelectionProvider().getSelection(), part));
		}
		return getDiagramText(selectedModels, Optional.of(part));
	}

	@SuppressWarnings("unchecked")
	public String getDiagramText(List<Object> selectedModels, Optional<IWorkbenchPart> part) {
		if (isLockDiagram()) {
			return currentDiagramText;
		}
		if (selectedModels == null || selectedModels.isEmpty() && currentDiagramText == null) {
			currentDiagramText = VisualizationHelper.createMatrix(null, artifactModel, null, null, true);
			return currentDiagramText;
		} else if (selectedModels == null || selectedModels.isEmpty()) {
			return currentDiagramText;
		}

		List<Connection> traces = new ArrayList<>();
		List<EObject> selectedEObjects = new ArrayList<>();
		EObject traceModel = null;

		IPersistenceAdapter persistenceAdapter = ExtensionPointHelper.getPersistenceAdapter().orElseThrow();
		ITraceabilityInformationModelAdapter metamodelAdapter = ExtensionPointHelper
				.getTraceabilityInformationModelAdapter().orElseThrow();

		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();

		artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		if (!selectedModels.isEmpty()) {

			ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
			// Get the artifact wrappers for all selected elements
			selectedModels.stream().forEach(obj -> {
				IArtifactHandler<Object> handler = (IArtifactHandler<Object>) artifactHelper.getHandler(obj)
						.orElse(null);
				if (handler != null) {
					// unpack element in case it is in a container
					Object unpackedElement = null;
					if (handler instanceof IArtifactUnpacker) {
						unpackedElement = IArtifactUnpacker.class.cast(handler).unpack(obj);
					} else {
						unpackedElement = obj;
					}
					if (ToggleTransitivityHandler.isTraceViewTransitive()) {
						// Make sure we have all relevant EObjects in the list.
						selectedEObjects
								.addAll(EMFHelper.linearize(handler.createWrapper(unpackedElement, artifactModel)));
					} else {
						selectedEObjects.add(handler.createWrapper(unpackedElement, artifactModel));
					}
				}
			});

			final EObject selectedObject = !selectedEObjects.isEmpty() ? selectedEObjects.get(0) : null;
			if (selectedObject != null && selectedObject.eResource() != null) {
				traceModel = persistenceAdapter.getTraceModel(resourceSet);

				List<String> selectedRelationshipTypes = SelectRelationshipTypesHandler.getSelectedRelationshipTypes();
				for (EObject obj : selectedEObjects) {
					traces.addAll(getViewableTraceLinks(obj, traceModel, metamodelAdapter, selectedRelationshipTypes));
				}

				if (selectedModels.size() == 1) {
					currentDiagramText = VisualizationHelper.createNeighboursView(traces,
							EMFHelper.linearize(selectedObject), artifactModel);
					return currentDiagramText;

				} else {
					// User selected to display a graph with only the selected elements
					if (ToggleDisplayGraphHandler.isDisplayGraph()) {
						currentDiagramText = renderMultiSelectionGraph(traces, selectedEObjects, artifactHelper);
						return currentDiagramText;
					}
				}

			}
		}

		// Standard case is to render a traceability matrix, potentially showing a
		// message instructing the user to select elements.
		currentDiagramText = VisualizationHelper.createMatrix(traceModel, artifactModel, selectedEObjects,
				selectedEObjects, DisplayInternalLinksHandler.areInternalLinksShown());
		return currentDiagramText;

	}

	/**
	 * Gets the code for a traceability graph that contains all selected objects.
	 * 
	 * @param traces
	 * @param selectedEObjects
	 * @param artifactHelper
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected String renderMultiSelectionGraph(List<Connection> traces, List<EObject> selectedEObjects,
			ArtifactHelper artifactHelper) {
		List<Connection> relevantTraces = new ArrayList<>();
		List<EObject> wrappers = new ArrayList<>();
		for (Connection connection : traces) {
			if (selectedEObjects.containsAll(connection.getOrigins())
					&& selectedEObjects.stream().anyMatch(connection.getTargets()::contains)) {
				Connection newConnection = new Connection(connection.getOrigins(), connection.getTargets().stream()
						.filter(selectedEObjects::contains).collect(Collectors.toList()), connection.getTlink());
				relevantTraces.add(newConnection);
				connection.getOrigins().stream().filter(selectedEObjects::contains).forEach(o -> {
					IArtifactHandler<Object> originHandler = (IArtifactHandler<Object>) artifactHelper.getHandler(o)
							.orElseThrow();
					wrappers.add(originHandler.createWrapper(o, artifactModel));
				});
				connection.getTargets().stream().filter(selectedEObjects::contains).forEach(t -> {
					IArtifactHandler<Object> targetHandler = (IArtifactHandler<Object>) artifactHelper.getHandler(t)
							.orElseThrow();
					wrappers.add(targetHandler.createWrapper(t, artifactModel));
				});
			}
		}
		return VisualizationHelper.createNeighboursView(relevantTraces, wrappers, artifactModel);
	}

	/**
	 * Get all trace links for the given object from the provided trace model that
	 * conform to the selected trace link types.
	 * 
	 * @param selectedObject
	 * @param traceModel
	 * @param metamodelAdapter
	 * @param selectedRelationshipTypes
	 * @return
	 */
	protected List<Connection> getViewableTraceLinks(EObject selectedObject, EObject traceModel,
			ITraceabilityInformationModelAdapter metamodelAdapter, List<String> selectedRelationshipTypes) {
		List<Connection> traces;

		ConnectionQuery query = new ConnectionQuery.Builder(traceModel, selectedObject)
				.setReverseDirection(ReverseLinkDirectionHandler.isReverseLinkDirection())
				.setSelectedRelationshipTypes(selectedRelationshipTypes)
				.setTraverseTransitiveLinks(ToggleTransitivityHandler.isTraceViewTransitive())
				.setTransitivityDepth(Integer.parseInt(TransitivityDepthHandler.getTransitivityDepth()))
				.setIncludeInternalLinks(DisplayInternalLinksHandler.areInternalLinksShown()).build();
		traces = metamodelAdapter.getConnections(query);

		updateSelectableRelationshipTypes(selectedObject, traces);
		return traces;
	}

	private void updateSelectableRelationshipTypes(EObject selectedObject, List<Connection> traces) {
		List<EObject> links = extractLinksFromTraces(traces);
		EObject previousElement = SelectRelationshipTypesHandler.getCurrentElement();
		if (previousElement != null) {
			String previousElementName = EMFHelper.getNameAttribute(previousElement);
			String currentElementName = EMFHelper.getNameAttribute(selectedObject);
			if (!previousElementName.equals(currentElementName)) {
				SelectRelationshipTypesHandler.clearPossibleRelationsForSelection();
				SelectRelationshipTypesHandler.emptySelectedRelationshipTypes();
			}
		}
		SelectRelationshipTypesHandler.setCurrentElement(selectedObject);
		SelectRelationshipTypesHandler.addToPossibleRelationsForSelection(links);
	}

	@Override
	public boolean supportsEditor(IEditorPart editor) {
		// This is a work around to disable update of the diagram if the view is locked.
		return !isLockDiagram();
	}

	@Override
	public boolean supportsView(IViewPart part) {
		// This is a work around to disable update of the diagram if the view is locked.
		if (isLockDiagram()) {
			return false;
		}
		return !(part instanceof PlantUmlView);
	}

	@Override
	public boolean supportsSelection(ISelection selection) {
		// This is a work around to disable update of the diagram if the view is locked.
		return !isLockDiagram();
	}

	private static List<EObject> extractLinksFromTraces(List<Connection> traces) {
		List<EObject> links = new ArrayList<>();
		for (Connection trace : traces) {
			if (!links.contains(trace.getTlink())) {
				links.add(trace.getTlink());
			}
		}
		return links;
	}
}