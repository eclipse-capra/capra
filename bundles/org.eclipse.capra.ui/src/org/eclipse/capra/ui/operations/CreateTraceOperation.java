/*******************************************************************************
 * Copyright (c) 2016-2021 Chalmers | University of Gothenburg, rt-labs and others.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
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
import org.eclipse.jface.dialogs.ErrorDialog;
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

	private static final String ERROR_DIALOG_TITLE = "Error creating new trace link";
	private static final String EXCEPTION_MESSAGE_RUNTIME_EXCEPTION = "An exception occured during trace link creation.";
	private static final String EXCEPTION_MESSAGE_CLASS_CAST_EXCEPTION = "The selected trace link type does not support these types of artifacts.";
	private static final String EXCEPTION_MESSAGE_CLASS_CAST_EXCEPTION_REASON = "Selected origin of class %s and targets of class %s are not compatible with the selected trace link type.";

	private static final String CAPRA_INFORMATION = "Capra Information";
	private static final String TRACE_LINK_EXISTS = "The trace link you want to create already exists and will therefore not be created";
	private static final String TRACE_LINK_SUCCESSFULLY_CREATED = "Trace link has been successfully created";
	private static final String DO_NOT_SHOW_DIALOG_AGAIN = "Do not show this dialog again";
	private static final String SELECT_TRACE_LINK_TYPE = "Select the trace type you want to create";
	private static final String SOURCE = "Source:";
	private static final String TARGET = "Target:";
	private static final String PLUGIN_ID = "org.eclipse.capra.ui";

	private Optional<EClass> chosenType;
	private List<EObject> originWrappers;
	private List<EObject> targetWrappers;
	private EObject traceModel;

	private List<?> origins = null;
	private List<?> targets = null;

	private BiFunction<Collection<EClass>, TraceableObjects, Optional<EClass>> chooseTraceType = null;

	/**
	 * Creates a new operation to create links.
	 * 
	 * @param label     the label used for the operation. Should never be
	 *                  <code>null</code>.
	 * @param artifacts the artifacts on which this operation is performed. Should
	 *                  never be <code>null</code>.
	 */
	public CreateTraceOperation(String label, List<?> origins, List<?> targets) {
		super(label);
		Assert.isNotNull(origins);
		Assert.isNotNull(targets);
		this.origins = origins;
		this.targets = targets;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		Shell shell = info.getAdapter(Shell.class);
		IStatus executionStatus = null;
		if (origins == null || targets == null || origins.isEmpty() || targets.isEmpty()) {
			MessageDialog.openWarning(shell, "No origins or targets selected",
					"At least one source artifact (marked by a checkbox) and one target artifact need to be in the list.");
			return Status.CANCEL_STATUS;
		}
		if (chooseTraceType == null) {
			chooseTraceType = (traceTypes, selection) -> getTraceTypeToCreate(shell, traceTypes, selection);
		}
		try {
			createTrace(shell, chooseTraceType);
			executionStatus = Status.OK_STATUS;
		} catch (IllegalStateException e) {
			executionStatus = new Status(Status.ERROR, PLUGIN_ID, e.getMessage(), e);
			createErrorMessage(shell, executionStatus, e.getMessage());
		} catch (ClassCastException e) {
			executionStatus = new Status(Status.ERROR, PLUGIN_ID,
					String.format(EXCEPTION_MESSAGE_CLASS_CAST_EXCEPTION_REASON, returnElementClasses(this.origins),
							returnElementClasses(this.targets)),
					e);
			createErrorMessage(shell, executionStatus, EXCEPTION_MESSAGE_CLASS_CAST_EXCEPTION);
		} catch (RuntimeException e) {
			executionStatus = new Status(Status.ERROR, PLUGIN_ID, e.getMessage(), e);
			createErrorMessage(shell, executionStatus, EXCEPTION_MESSAGE_RUNTIME_EXCEPTION);
		}
		return executionStatus;

	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		TraceHelper traceHelper = new TraceHelper(traceModel);
		if (this.originWrappers != null && this.targetWrappers != null && this.chosenType != null
				&& this.traceModel != null) {
			List<Connection> connections = traceHelper.getTraces(this.originWrappers, this.targetWrappers,
					this.chosenType.get());
			if (!connections.isEmpty()) {
				traceHelper.deleteTraces(connections);
				return Status.OK_STATUS;
			}
		}
		return Status.CANCEL_STATUS;
	}

	public void createTrace(Shell shell) {
		Assert.isNotNull(this.chooseTraceType);
		this.createTrace(shell, this.chooseTraceType);
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
			BiFunction<Collection<EClass>, TraceableObjects, Optional<EClass>> chooseTraceType) {
		TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().orElseThrow();
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().orElseThrow();

		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		// add trace model to resource set
		this.traceModel = persistenceAdapter.getTraceModel(resourceSet);
		// add artifact model to resource set
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);

		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		TraceHelper traceHelper = new TraceHelper(this.traceModel);

		// Create the artifact wrappers
		this.originWrappers = artifactHelper.createWrappers(this.origins);
		this.targetWrappers = artifactHelper.createWrappers(this.targets);
		List<EObject> allWrappers = new ArrayList<EObject>(this.originWrappers);
		allWrappers.addAll(this.targetWrappers);

		// Get the type of trace to be created
		Collection<EClass> traceTypes = traceAdapter.getAvailableTraceTypes(allWrappers);
		this.chosenType = chooseTraceType.apply(traceTypes,
				new TraceableObjects(this.originWrappers, this.targetWrappers));

		// Create trace
		if (this.chosenType.isPresent()) {
			// check if the connection already exists
			if (traceHelper.traceExists(this.originWrappers, this.targetWrappers, this.chosenType.get())) {
				MessageDialog.openInformation(shell, CAPRA_INFORMATION, TRACE_LINK_EXISTS);
			} else {
				traceHelper.createTrace(originWrappers, targetWrappers, this.chosenType.get());
				persistenceAdapter.saveTracesAndArtifacts(traceModel, artifactModel);
				traceHelper.annotateTrace(allWrappers);

				// check from preferences if user wants to see the "trace
				// successfully created dialog
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
	public void setChooseTraceType(BiFunction<Collection<EClass>, TraceableObjects, Optional<EClass>> chooseTraceType) {
		this.chooseTraceType = chooseTraceType;
	}

	private Optional<EClass> getTraceTypeToCreate(Shell shell, Collection<EClass> traceTypes,
			TraceableObjects traceableObjects) {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new LabelProvider() {
			@Override
			public String getText(Object element) {
				EClass eclass = (EClass) element;
				return eclass.getName();
			}
		});
		dialog.setTitle(SELECT_TRACE_LINK_TYPE);
		dialog.setElements(traceTypes.toArray());

		dialog.setMessage(SOURCE + ": "
				+ traceableObjects.getOrigins().stream().map(this::getSelectionDisplayName).collect(Collectors.toList())
				+ "\n" + TARGET + ": " + traceableObjects.getTargets().stream().map(this::getSelectionDisplayName)
						.collect(Collectors.toList()));

		if (dialog.open() == Window.OK) {
			return Optional.of((EClass) dialog.getFirstResult());
		}

		return Optional.empty();
	}

	private String getSelectionDisplayName(EObject element) {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().orElseThrow();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(EditingDomainHelper.getResourceSet());
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		IArtifactHandler<?> handler = artifactHelper.getHandler(artifactHelper.unwrapWrapper(element)).orElseThrow();

		return handler.withCastedHandler(artifactHelper.unwrapWrapper(element), (h, o) -> h.getDisplayName(o))
				.orElse(EMFHelper.getIdentifier(element));

	}

	private static void createErrorMessage(Shell shell, IStatus status, String message) {
		ErrorDialog.openError(shell, ERROR_DIALOG_TITLE, message, status);
	}

	private static String returnElementClasses(List<?> elements) {
		Set<String> unique = new HashSet<String>();
		elements.forEach(el -> unique.add(el.getClass().getCanonicalName()));
		return unique.toString().replace("[", "").replace("]", "");
	}

	/**
	 * A simple helper class that stores the origin and target artifacts for access
	 * in the bifunctions used in the trace creation.
	 */
	public class TraceableObjects {

		/**
		 * Construct a new instance of {@code TraceableObjects} with the given origins
		 * and targets.
		 * 
		 * @param origins a list of artifacts that should be the origin of the new trace
		 *                link
		 * @param targets a list of artifacts that should be the target of the new trace
		 *                link
		 */
		public TraceableObjects(List<EObject> origins, List<EObject> targets) {
			this.origins = origins;
			this.targets = targets;
		}

		private List<EObject> origins;
		private List<EObject> targets;

		/**
		 * Gets the origins of the new trace link.
		 * 
		 * @return a list of artifacts that should be the origin of the new trace link
		 */
		public List<EObject> getOrigins() {
			return origins;
		}

		/**
		 * Gets the targets of the new trace link.
		 * 
		 * @return a list of artifacts that should be the target of the new trace link
		 */
		public List<EObject> getTargets() {
			return targets;
		}

	}

}
