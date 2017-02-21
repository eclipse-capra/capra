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
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.core.helpers.TraceHelper;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class TraceCreationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		createTrace(window, (traceTypes, selection) -> getTraceTypeToCreate(window, traceTypes, selection));
		return null;
	}

	public void createTrace(IWorkbenchWindow window,
			BiFunction<Collection<EClass>, List<EObject>, Optional<EClass>> chooseTraceType) {
		List<Object> artifacts = SelectionView.getOpenedView().getSelection();

		TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();

		ResourceSet resourceSet = new ResourceSetImpl();
		// add trace model to resource set
		EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);
		// add artifact model to resource set
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);

		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		TraceHelper traceHelper = new TraceHelper(traceModel);

		// Create the artifact wrappers
		List<EObject> wrappers = artifactHelper.createWrappers(artifacts);

		// Get the type of trace to be created
		Collection<EClass> traceTypes = traceAdapter.getAvailableTraceTypes(wrappers);
		Optional<EClass> chosenType = chooseTraceType.apply(traceTypes, wrappers);

		// Create trace
		if (chosenType.isPresent()) {
			traceHelper.createTrace(wrappers, chosenType.get());
			persistenceAdapter.saveTracesAndArtifacts(traceModel, artifactModel);
			traceHelper.annotateTrace(wrappers);
		}
	}

	private Optional<EClass> getTraceTypeToCreate(IWorkbenchWindow window, Collection<EClass> traceTypes,
			List<EObject> wrappers) {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(window.getShell(), new LabelProvider() {
			@Override
			public String getText(Object element) {
				EClass eclass = (EClass) element;
				return eclass.getName();
			}
		});
		dialog.setTitle("Select the trace type you want to create");
		dialog.setElements(traceTypes.toArray());
		dialog.setMessage("Selection: " + wrappers.stream().map(EMFHelper::getIdentifier).collect(Collectors.toList()));

		if (dialog.open() == Window.OK) {
			return Optional.of((EClass) dialog.getFirstResult());
		}

		return Optional.empty();
	}
}
