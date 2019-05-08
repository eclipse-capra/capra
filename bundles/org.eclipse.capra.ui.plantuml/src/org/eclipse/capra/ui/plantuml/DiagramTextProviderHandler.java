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
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.handlers.IArtifactUnpacker;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.helpers.TraceCreationHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import net.sourceforge.plantuml.eclipse.utils.DiagramTextProvider;
import net.sourceforge.plantuml.eclipse.views.PlantUmlView;

/**
 * Provides PlantUML with a string representation of elements connected by trace
 * links.
 *
 * @author Anthony Anjorin, Salome Maro, Jan-Philipp Steghöfer
 */
public class DiagramTextProviderHandler implements DiagramTextProvider {
	private EObject artifactModel = null;

	@Override
	public String getDiagramText(IEditorPart editor, ISelection input) {
		return (getDiagramText((IWorkbenchPart) editor, input));
	}

	@Override
	public String getDiagramText(IViewPart view, ISelection input) {
		return (getDiagramText((IWorkbenchPart) view, input));
	}

	public String getDiagramText(IWorkbenchPart part, ISelection input) {
		List<Object> selectedModels = new ArrayList<>();
		if (part.getSite().getSelectionProvider() != null) {
			selectedModels.addAll(
					TraceCreationHelper.extractSelectedElements(part.getSite().getSelectionProvider().getSelection()));
		}
		return getDiagramText(selectedModels);
	}

	@SuppressWarnings("unchecked")
	public String getDiagramText(List<Object> selectedModels) {
		List<EObject> firstModelElements = null;
		List<EObject> secondModelElements = null;
		EObject selectedObject = null;
		ResourceSet resourceSet = new ResourceSetImpl();
		EObject traceModel = null;
		List<Connection> traces = new ArrayList<>();

		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		TraceMetaModelAdapter metamodelAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();

		artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);

		if (selectedModels.size() > 0) {
			ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
			// check if there is a hander for the selected and get its Wrapper
			IArtifactHandler<Object> handler = (IArtifactHandler<Object>) artifactHelper
					.getHandler(selectedModels.get(0)).orElse(null);

			if (handler != null) {
				// unpack element in case it is in a container
				Object firstElement_unpacked = null;
				if (handler instanceof IArtifactUnpacker) {
					firstElement_unpacked = IArtifactUnpacker.class.cast(handler).unpack(selectedModels.get(0));
				} else
					firstElement_unpacked = selectedModels.get(0);
				selectedObject = handler.createWrapper(firstElement_unpacked, artifactModel);
				if (selectedObject != null && selectedObject.eResource() != null) {
					resourceSet = selectedObject.eResource().getResourceSet();
					traceModel = persistenceAdapter.getTraceModel(resourceSet);
					List<String> selectedRelationshipTypes = SelectRelationshipsHandler.getSelectedRelationshipTypes();
					if (selectedModels.size() == 1) {
						if (ToggleTransitivityHandler.isTraceViewTransitive()) {
							int transitivityDepth = Integer.parseInt(TransitivityDepthHandler.getTransitivityDepth());
							traces = metamodelAdapter.getTransitivelyConnectedElements(selectedObject, traceModel,
									selectedRelationshipTypes, transitivityDepth);
						} else {
							traces = metamodelAdapter.getConnectedElements(selectedObject, traceModel,
									selectedRelationshipTypes);
						}
						if (DisplayInternalLinksHandler.areInternalLinksShown()
								&& ToggleTransitivityHandler.isTraceViewTransitive()) {
							EObject previousElement = SelectRelationshipsHandler.getPreviousElement();
							int transitivityDepth = Integer.parseInt(TransitivityDepthHandler.getTransitivityDepth());
							if (previousElement != null) {
								String previousElementName = EMFHelper.getNameAttribute(previousElement);
								String currentElementName = EMFHelper.getNameAttribute(selectedObject);
								if (!previousElementName.equals(currentElementName)) {
									SelectRelationshipsHandler.clearPossibleRelationsForSelection();
									SelectRelationshipsHandler.emptySelectedRelationshipTypes();
									SelectRelationshipsHandler.setPreviousElement(selectedObject);
								}
							} else {
								SelectRelationshipsHandler.setPreviousElement(selectedObject);
							}
							traces.addAll(metamodelAdapter.getInternalElementsTransitive(selectedObject, traceModel,
									selectedRelationshipTypes, transitivityDepth, traces));
						} else if (DisplayInternalLinksHandler.areInternalLinksShown()) {
							EObject previousElement = SelectRelationshipsHandler.getPreviousElement();
							if (previousElement != null) {
								String previousElementName = EMFHelper.getNameAttribute(previousElement);
								String currentElementName = EMFHelper.getNameAttribute(selectedObject);
								if (!previousElementName.equals(currentElementName)) {
									SelectRelationshipsHandler.clearPossibleRelationsForSelection();
									SelectRelationshipsHandler.emptySelectedRelationshipTypes();
									SelectRelationshipsHandler.setPreviousElement(selectedObject);
								}
							} else {
								SelectRelationshipsHandler.setPreviousElement(selectedObject);
							}
							traces.addAll(metamodelAdapter.getInternalElements(selectedObject, traceModel,
									selectedRelationshipTypes, false, 0, traces));
						}
						List<EObject> links = extractLinksFromTraces(traces);
						SelectRelationshipsHandler.addToPossibleRelationsForSelection(links);
						return VisualizationHelper.createNeighboursView(traces,
								EMFHelper.linearize(handler.createWrapper(firstElement_unpacked, artifactModel)));
					} else if (selectedModels.size() == 2) {
						// unpack second element in case it is in a container
						Object secondElement_unpacked = null;
						if (handler instanceof IArtifactUnpacker) {
							secondElement_unpacked = IArtifactUnpacker.class.cast(handler)
									.unpack(selectedModels.get(1));
						} else
							secondElement_unpacked = selectedModels.get(1);

						IArtifactHandler<Object> handlerSecondElement = (IArtifactHandler<Object>) artifactHelper
								.getHandler(selectedModels.get(1)).orElse(null);
						if (ToggleTransitivityHandler.isTraceViewTransitive()) {
							firstModelElements = EMFHelper
									.linearize(handler.createWrapper(firstElement_unpacked, artifactModel));
							secondModelElements = EMFHelper.linearize(
									handlerSecondElement.createWrapper(secondElement_unpacked, artifactModel));
						} else {
							List<EObject> firstObject = new ArrayList<>();
							firstObject.add(handler.createWrapper(firstElement_unpacked, artifactModel));
							List<EObject> secondObject = new ArrayList<>();
							secondObject.add(handlerSecondElement.createWrapper(secondElement_unpacked, artifactModel));
							firstModelElements = firstObject;
							secondModelElements = secondObject;
						}

					} else if (selectedModels.size() > 2) {
						if (ToggleTransitivityHandler.isTraceViewTransitive()) {
							firstModelElements = selectedModels.stream().flatMap(r -> {
								IArtifactHandler<Object> individualhandler = (IArtifactHandler<Object>) artifactHelper
										.getHandler(r).orElse(null);
								Object object = null;
								if (individualhandler instanceof IArtifactUnpacker) {
									object = IArtifactUnpacker.class.cast(individualhandler).unpack(r);
								} else {
									object = r;
								}
								return EMFHelper.linearize(individualhandler.createWrapper(object, artifactModel))
										.stream();
							}).collect(Collectors.toList());
							secondModelElements = firstModelElements;
						} else {
							List<EObject> wrappers = new ArrayList<>();
							selectedModels.stream().forEach(o -> {
								IArtifactHandler<Object> individualhandler = (IArtifactHandler<Object>) artifactHelper
										.getHandler(o).orElse(null);
								Object object = null;
								if (individualhandler instanceof IArtifactUnpacker) {
									object = IArtifactUnpacker.class.cast(individualhandler).unpack(o);
								} else {
									object = o;
								}
								wrappers.add(individualhandler.createWrapper(object, artifactModel));
							});

							firstModelElements = wrappers;
							secondModelElements = firstModelElements;

							// User selected to display a graph with only the
							// selected elements
							if (ToggleDisplayGraphHandler.isDisplayGraph()) {
								for (EObject object : wrappers) {
									traces.addAll(metamodelAdapter.getConnectedElements(object, traceModel));
								}

								List<Connection> relevantTraces = new ArrayList<>();
								for (Connection connection : traces) {
									if (selectedModels.contains(connection.getOrigin())
											&& selectedModels.stream().anyMatch(connection.getTargets()::contains)) {
										Connection newConnection = new Connection(
												connection.getOrigin(), connection.getTargets().stream()
														.filter(selectedModels::contains).collect(Collectors.toList()),
												connection.getTlink());
										relevantTraces.add(newConnection);
									}
								}
								return VisualizationHelper.createNeighboursView(relevantTraces, wrappers);
							}
						}

					}
				}
			}
		}
		if (DisplayInternalLinksHandler.areInternalLinksShown()) {
			return VisualizationHelper.createMatrix(traceModel, firstModelElements, secondModelElements, true);
		} else {
			return VisualizationHelper.createMatrix(traceModel, firstModelElements, secondModelElements, false);
		}

	}

	@Override
	public boolean supportsEditor(IEditorPart editor) {
		return true;
	}

	@Override
	public boolean supportsView(IViewPart part) {
		if (part instanceof PlantUmlView) {
			return false;
		}
		return true;
	}

	@Override
	public boolean supportsSelection(ISelection selection) {
		return true;
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