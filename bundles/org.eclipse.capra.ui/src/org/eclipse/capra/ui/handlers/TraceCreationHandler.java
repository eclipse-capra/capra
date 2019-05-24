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
package org.eclipse.capra.ui.handlers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.core.helpers.TraceHelper;
import org.eclipse.capra.ui.preferences.CapraPreferences;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class TraceCreationHandler extends AbstractHandler {

	private static final String CAPRA_INFORMATION = "Capra Information";
	private static final String TRACE_LINK_EXISTS = "The trace link you want to create already exists and will therefore not be created";
	private static final String TRACE_LINK_SUCCESSFULLY_CREATED = "Trace link has been successfully created";
	private static final String DO_NOT_SHOW_DIALOG_AGAIN = "Do not show this dialog again";
	private static final String SELECT_TRACE_LINK_TYPE = "Select the trace type you want to create";
	private static final String SELECTION = "Selection";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		createTrace(window, (traceTypes, selection) -> getTraceTypeToCreate(window, traceTypes, selection));
		return null;
	}

	public void createTrace(IWorkbenchWindow window,
			BiFunction<Collection<EClass>, List<EObject>, Optional<EClass>> chooseTraceType) {
		List<?> artifacts = SelectionView.getOpenedView().getSelection();

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
			// check if the connection already exists
			if (traceHelper.traceExists(wrappers, chosenType.get(), traceModel)) {
				MessageDialog.openInformation(window.getShell(), CAPRA_INFORMATION, TRACE_LINK_EXISTS);
			} else {
				traceHelper.createTrace(wrappers, chosenType.get());
				persistenceAdapter.saveTracesAndArtifacts(traceModel, artifactModel);
				traceHelper.annotateTrace(wrappers);

				// check from preferences if user wants to see the "trace
				// successfully created dialog"
				IPreferenceStore store = CapraPreferences.getPreferences();
				if (store.getBoolean(CapraPreferences.SHOW_TRACE_CREATED_CONFIRMATION_DIALOG)) {
					MessageDialogWithToggle.open(MessageDialog.INFORMATION, window.getShell(), CAPRA_INFORMATION,
							TRACE_LINK_SUCCESSFULLY_CREATED, DO_NOT_SHOW_DIALOG_AGAIN, false, store,
							CapraPreferences.SHOW_TRACE_CREATED_CONFIRMATION_DIALOG, SWT.NONE);
				}
			}
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
		dialog.setTitle(SELECT_TRACE_LINK_TYPE);
		dialog.setElements(traceTypes.toArray());

		dialog.setMessage(
				SELECTION + " : " + wrappers.stream().map(this::getSelectionDisplayName).collect(Collectors.toList()));

		if (dialog.open() == Window.OK) {
			return Optional.of((EClass) dialog.getFirstResult());
		}

		return Optional.empty();
	}

	private String getSelectionDisplayName(EObject element) {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(new ResourceSetImpl());
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		IArtifactHandler<?> handler = artifactHelper.getHandler(artifactHelper.unwrapWrapper(element)).get();

		return handler.withCastedHandler(artifactHelper.unwrapWrapper(element), (h, o) -> h.getDisplayName(o))
				.orElse(EMFHelper.getIdentifier(element));

	}

}
