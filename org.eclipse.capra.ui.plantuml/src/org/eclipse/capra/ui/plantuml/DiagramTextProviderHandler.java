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
			@SuppressWarnings("unchecked")
			IArtifactHandler<Object> handler = (IArtifactHandler<Object>) 
				artifactHelper.getHandler(selectedModels.get(0)).orElse(null);
			
			if (handler != null) {
				selectedObject = handler.createWrapper(selectedModels.get(0), artifactModel);

				if (selectedObject != null) {
					resourceSet = selectedObject.eResource().getResourceSet();
					traceModel = persistenceAdapter.getTraceModel(resourceSet);

					if (selectedModels.size() == 1) {
						if (DisplayTracesHandler.isTraceViewTransitive()) {
							traces = metamodelAdapter.getTransitivelyConnectedElements(selectedObject, traceModel);
						} else {
							traces = metamodelAdapter.getConnectedElements(selectedObject, traceModel);
						}

						return VisualizationHelper.createNeighboursView(traces, selectedObject);
					} else if (selectedModels.size() == 2) {
						if (DisplayTracesHandler.isTraceViewTransitive()) {
							firstModelElements = EMFHelper
									.linearize(handler.createWrapper(selectedModels.get(0), artifactModel));
							secondModelElements = EMFHelper
									.linearize(handler.createWrapper(selectedModels.get(1), artifactModel));
						} else {
							List<EObject> firstObject = new ArrayList<>();
							firstObject.add(handler.createWrapper(selectedModels.get(0), artifactModel));
							List<EObject> secondObject = new ArrayList<>();
							secondObject.add(handler.createWrapper(selectedModels.get(1), artifactModel));
							firstModelElements = firstObject;
							secondModelElements = secondObject;
						}
					} else if (selectedModels.size() > 2) {
						if (DisplayTracesHandler.isTraceViewTransitive()) {
							firstModelElements = selectedModels.stream()
									.flatMap(r -> EMFHelper.linearize(handler.createWrapper(r, artifactModel)).stream())
									.collect(Collectors.toList());
							secondModelElements = firstModelElements;
						} else {
							List<EObject> Objects = new ArrayList<>();
							selectedModels.stream().forEach(o -> {
								Objects.add(handler.createWrapper(o, artifactModel));

							});
							firstModelElements = Objects;
							secondModelElements = firstModelElements;
						}
					}
				}
			}
		}
		String umlString = VisualizationHelper.createMatrix(traceModel, firstModelElements, secondModelElements);

		return umlString;
	}

	@Override
	public boolean supportsEditor(IEditorPart editor) {
		return true;
	}

	@Override
	public boolean supportsSelection(ISelection selection) {
		return true;
	}
}
