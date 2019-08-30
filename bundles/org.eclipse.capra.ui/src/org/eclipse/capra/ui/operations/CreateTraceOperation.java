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
package org.eclipse.capra.ui.operations;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.core.helpers.TraceHelper;
import org.eclipse.capra.ui.preferences.CapraPreferences;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * Operation to create trace links, undo and redo.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class CreateTraceOperation extends AbstractOperation {

	private static final String CAPRA_INFORMATION = "Capra Information";
	private static final String TRACE_LINK_EXISTS = "The trace link you want to create already exists and will therefore not be created";
	private static final String TRACE_LINK_SUCCESSFULLY_CREATED = "Trace link has been successfully created";
	private static final String DO_NOT_SHOW_DIALOG_AGAIN = "Do not show this dialog again";
	private static final String SELECT_TRACE_LINK_TYPE = "Select the trace type you want to create";
	private static final String SELECTION = "Selection";

	private Optional<EClass> chosenType;
	private List<EObject> wrappers;
	private EObject traceModel;

	private List<?> artifacts = null;

	private BiFunction<Collection<EClass>, List<EObject>, Optional<EClass>> chooseTraceType = null;

	private CreateTraceOperation(String label) {
		super(label);
	}

	/**
	 * Creates a new operation to create links.
	 * 
	 * @param label     the label used for the operation. Should never be
	 *                  <code>null</code>.
	 * @param artifacts the artifacts on which this operation is performed. Should
	 *                  never be <code>null</code>.
	 */
	public CreateTraceOperation(String label, List<?> artifacts) {
		super(label);
		Assert.isNotNull(artifacts);
		this.artifacts = artifacts;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (artifacts == null || artifacts.isEmpty()) {
			return Status.CANCEL_STATUS;
		}
		Shell shell = info.getAdapter(Shell.class);
		if (chooseTraceType == null) {
			chooseTraceType = (traceTypes, selection) -> getTraceTypeToCreate(shell, traceTypes, selection);
		}
		createTrace(shell, chooseTraceType);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		TraceHelper traceHelper = new TraceHelper(traceModel);
		if (this.wrappers != null && this.chosenType != null && this.traceModel != null) {
			List<Connection> connections = traceHelper.getTraces(this.wrappers, this.chosenType.get());
			if (!connections.isEmpty()) {
				traceHelper.deleteTraces(connections);
				return Status.OK_STATUS;
			}
		}
		return Status.CANCEL_STATUS;
	}

	/**
	 * Create a trace link after eliciting the trace type via the provided
	 * bi-function.
	 * 
	 * @param shell           a shell instance to allow opening message windows
	 * @param chooseTraceType a bi-function used to select a suitable trace link
	 *                        type
	 */
	public void createTrace(Shell shell,
			BiFunction<Collection<EClass>, List<EObject>, Optional<EClass>> chooseTraceType) {
		TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();

		ResourceSet resourceSet = new ResourceSetImpl();
		// add trace model to resource set
		this.traceModel = persistenceAdapter.getTraceModel(resourceSet);
		// add artifact model to resource set
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);

		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		TraceHelper traceHelper = new TraceHelper(this.traceModel);

		// Create the artifact wrappers
		this.wrappers = artifactHelper.createWrappers(this.artifacts);

		// Get the type of trace to be created
		Collection<EClass> traceTypes = traceAdapter.getAvailableTraceTypes(this.wrappers);
		this.chosenType = chooseTraceType.apply(traceTypes, this.wrappers);

		// Create trace
		if (this.chosenType.isPresent()) {
			// check if the connection already exists
			if (traceHelper.traceExists(this.wrappers, this.chosenType.get())) {
				MessageDialog.openInformation(shell, CAPRA_INFORMATION, TRACE_LINK_EXISTS);
			} else {
				traceHelper.createTrace(this.wrappers, this.chosenType.get());
				persistenceAdapter.saveTracesAndArtifacts(traceModel, artifactModel);
				traceHelper.annotateTrace(this.wrappers);

				// check from preferences if user wants to see the "trace
				// successfully created dialog"
				IPreferenceStore store = CapraPreferences.getPreferences();
				if (store.getBoolean(CapraPreferences.SHOW_TRACE_CREATED_CONFIRMATION_DIALOG)) {
					MessageDialogWithToggle.open(MessageDialog.INFORMATION, shell, CAPRA_INFORMATION,
							TRACE_LINK_SUCCESSFULLY_CREATED, DO_NOT_SHOW_DIALOG_AGAIN, false, store,
							CapraPreferences.SHOW_TRACE_CREATED_CONFIRMATION_DIALOG, SWT.NONE);
				}
			}
		}
	}

	/**
	 * Sets the bi-function to select a suitable trace link type.
	 * 
	 * @param chooseTraceType the bi-function to use to select a suitable trace link
	 *                        type
	 */
	public void setChooseTraceType(BiFunction<Collection<EClass>, List<EObject>, Optional<EClass>> chooseTraceType) {
		this.chooseTraceType = chooseTraceType;
	}

	private Optional<EClass> getTraceTypeToCreate(Shell shell, Collection<EClass> traceTypes, List<EObject> wrappers) {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new LabelProvider() {
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
