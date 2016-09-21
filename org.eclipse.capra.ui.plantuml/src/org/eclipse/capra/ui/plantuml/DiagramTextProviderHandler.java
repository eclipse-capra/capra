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
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.helpers.EMFHelper;
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
	private TraceMetaModelAdapter metamodelAdapter;
	private List<Connection> traces = new ArrayList<>();

	@Override
	public String getDiagramText(IEditorPart editor, ISelection input) {
		List<Object> selectedModels = TraceCreationHelper
				.extractSelectedElements(editor.getSite().getSelectionProvider().getSelection());

		return getDiagramText(selectedModels);
	}

	/**
	 * Creates the {@code String} representation PlantUML uses to render the
	 * graphics. Based on the selected models, the relevant trace links are
	 * extracted and a decision is made whether a matrix of a tree view has to
	 * be displayed.
	 * 
	 * @param selectedModels
	 *            the models whose trace links should be displayed
	 * @return a string representation of the visualisation that can be rendered
	 *         by PlantUML
	 */
	public String getDiagramText(List<Object> selectedModels) {
		List<EObject> firstModelElements = null;
		List<EObject> secondModelElements = null;

		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		metamodelAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();

		ResourceSet resourceSet = new ResourceSetImpl();
		if (selectedModels.size() > 0 && selectedModels.get(0) instanceof EObject)
			resourceSet = ((EObject) selectedModels.get(0)).eResource().getResourceSet();

		EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);

		if (selectedModels.size() == 1 && selectedModels.get(0) instanceof EObject) {
			EObject selectedEObject = (EObject) selectedModels.get(0);
			if (DisplayTracesHandler.isTraceViewTransitive()) {
				traces = metamodelAdapter.getTransitivelyConnectedElements(selectedEObject, traceModel);
			} else {
				traces = metamodelAdapter.getConnectedElements(selectedEObject, traceModel);
			}

			return VisualizationHelper.createNeighboursView(traces, selectedEObject);
		} else if (selectedModels.size() == 2) {
			firstModelElements = EMFHelper.linearize(selectedModels.get(0));
			secondModelElements = EMFHelper.linearize(selectedModels.get(1));
		} else {
			firstModelElements = selectedModels.stream().flatMap(r -> EMFHelper.linearize(r).stream())
					.collect(Collectors.toList());

			secondModelElements = firstModelElements;
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
