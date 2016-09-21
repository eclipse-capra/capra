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
package org.eclipse.capra.ui.handlers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.handlers.ArtifactHandler;
import org.eclipse.capra.core.handlers.PriorityHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.helpers.EMFHelper;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class TraceCreationHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		createTrace(window, (traceTypes, selection) -> getTraceTypeToCreate(window, traceTypes, selection));
		return null;
	}

	public void createTrace(IWorkbenchWindow window,
			BiFunction<Collection<EClass>, List<EObject>, Optional<EClass>> chooseTraceType) {
		List<Object> selection = SelectionView.getOpenedView().getSelection();

		TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();

		ResourceSet resourceSet = new ResourceSetImpl();
		// add trace model to resource set
		EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);
		// add artifact model to resource set
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);

		Collection<ArtifactHandler> artifactHandlers = ExtensionPointHelper.getArtifactHandlers();

		List<EObject> selectionAsEObjects = mapSelectionToEObjects(window, artifactModel, artifactHandlers, selection);

		Collection<EClass> traceTypes = traceAdapter.getAvailableTraceTypes(selectionAsEObjects);
		Optional<EClass> chosenType = chooseTraceType.apply(traceTypes, selectionAsEObjects);

		if (chosenType.isPresent()) {
			EObject root = traceAdapter.createTrace(chosenType.get(), traceModel, selectionAsEObjects);
			persistenceAdapter.saveTracesAndArtifacts(root, artifactModel);
		}
	}

	private List<EObject> mapSelectionToEObjects(IWorkbenchWindow window, EObject artifactModel,
			Collection<ArtifactHandler> artifactHandlers, List<Object> selection) {
		return selection.stream().map(sel -> convertToEObject(window, sel, artifactHandlers, artifactModel))
				.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
	}

	private Optional<EObject> convertToEObject(IWorkbenchWindow window, Object sel,
			Collection<ArtifactHandler> artifactHandlers, EObject artifactModel) {
		List<ArtifactHandler> availableHandlers = artifactHandlers.stream()
				.filter(handler -> handler.canHandleSelection(sel)).collect(Collectors.toList());
		Optional<PriorityHandler> priorityHandler = ExtensionPointHelper.getPriorityHandler();

		if (availableHandlers.size() == 1) {
			return Optional.of(availableHandlers.get(0).getEObjectForSelection(sel, artifactModel));
		} else if (availableHandlers.size() > 1 && priorityHandler.isPresent()) {
			ArtifactHandler selectedHandler = priorityHandler.get().getSelectedHandler(availableHandlers, sel);
			return Optional.of(selectedHandler.getEObjectForSelection(sel, artifactModel));
		} else {
			MessageDialog.openWarning(window.getShell(), "No handler for selected item",
					"There is no handler for " + sel + " so it will be ignored.");
			return Optional.empty();
		}

	}

	private Optional<EClass> getTraceTypeToCreate(IWorkbenchWindow window, Collection<EClass> traceTypes,
			List<EObject> selectionAsEObjects) {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(window.getShell(), new LabelProvider() {
			@Override
			public String getText(Object element) {
				EClass eclass = (EClass) element;
				return eclass.getName();
			}
		});
		dialog.setTitle("Select the trace type you want to create");
		dialog.setElements(traceTypes.toArray());
		dialog.setMessage("Selection: "
				+ selectionAsEObjects.stream().map(EMFHelper::getIdentifier).collect(Collectors.toList()));

		if (dialog.open() == Window.OK) {
			return Optional.of((EClass) dialog.getFirstResult());
		}

		return Optional.empty();
	}
}
