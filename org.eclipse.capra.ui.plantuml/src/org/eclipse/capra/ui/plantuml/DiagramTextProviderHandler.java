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
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.helpers.TraceCreationHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import net.sourceforge.plantuml.eclipse.utils.DiagramTextProvider;

/**
 * Provides PlantUML with a string representation of elements connected by trace
 * links.
 *
 * @author Anthony Anjorin, Salome Maro
 */
public class DiagramTextProviderHandler implements DiagramTextProvider {
	private EObject artifactModel = null;

	@Override
	public String getDiagramText(IEditorPart editor, ISelection input) {
		List<Object> selectedModels = TraceCreationHelper
				.extractSelectedElements(editor.getSite().getSelectionProvider().getSelection());
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
			IArtifactHandler<Object> handler = (IArtifactHandler<Object>) artifactHelper.getHandler(selectedModels.get(0)).orElse(null);
			
			if (handler != null) {
				selectedObject = handler.createWrapper(selectedModels.get(0), artifactModel);
				if (selectedObject != null) {
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
						return VisualizationHelper.createNeighboursView(traces, EMFHelper.linearize(handler.createWrapper(selectedModels.get(0), artifactModel)));
					} else if (selectedModels.size() == 2) {
						IArtifactHandler<Object> handlerSecondElement = (IArtifactHandler<Object>) artifactHelper.getHandler(selectedModels.get(1)).orElse(null);
						if (ToggleTransitivityHandler.isTraceViewTransitive()) {
							firstModelElements = EMFHelper
									.linearize(handler.createWrapper(selectedModels.get(0), artifactModel));
							secondModelElements = EMFHelper.linearize(
									handlerSecondElement.createWrapper(selectedModels.get(1), artifactModel));
						} else {
							List<EObject> firstObject = new ArrayList<>();
							firstObject.add(handler.createWrapper(selectedModels.get(0), artifactModel));
							List<EObject> secondObject = new ArrayList<>();
							secondObject.add(handlerSecondElement.createWrapper(selectedModels.get(1), artifactModel));
							firstModelElements = firstObject;
							secondModelElements = secondObject;
						}
							
					} else if (selectedModels.size() > 2) {
						if (ToggleTransitivityHandler.isTraceViewTransitive()) {
							firstModelElements = selectedModels.stream().flatMap(r -> {
								IArtifactHandler<Object> individualhandler = (IArtifactHandler<Object>) artifactHelper.getHandler(r).orElse(null);
								return EMFHelper.linearize(individualhandler.createWrapper(r, artifactModel)).stream();
							}).collect(Collectors.toList());
							secondModelElements = firstModelElements;
						} else {
							List<EObject> wrappers = new ArrayList<>();
							selectedModels.stream().forEach(o -> {
								IArtifactHandler<Object> individualhandler = (IArtifactHandler<Object>) artifactHelper.getHandler(o)
											.orElse(null);
								wrappers.add(individualhandler.createWrapper(o, artifactModel));
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
								return VisualizationHelper.createNeighboursView(relevantTraces, wrappers);}
						}
						
						
					}
				}
			}
		}
		if (DisplayInternalLinksHandler.areInternalLinksShown()) {
		return VisualizationHelper.createMatrix(traceModel, firstModelElements, secondModelElements, true);}
		else {return VisualizationHelper.createMatrix(traceModel, firstModelElements, secondModelElements, false);}
	
	}

	@Override
	public boolean supportsEditor(IEditorPart editor) {
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