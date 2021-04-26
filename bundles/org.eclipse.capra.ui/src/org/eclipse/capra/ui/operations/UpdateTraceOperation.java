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

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.core.helpers.TraceHelper;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swt.widgets.Shell;

/**
 * Operation to update trace links. Also allows to undo and redo the operation.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class UpdateTraceOperation extends AbstractOperation {

	private static final String ERROR_DIALOG_TITLE = "Error updating trace link";
	private static final String EXCEPTION_MESSAGE_RUNTIME_EXCEPTION = "An exception occured during the update of the trace link.";
	private static final String EXCEPTION_MESSAGE_INTERRUPTED_EXCEPTION = "The transaction to update the trace link was interrupted.";

	private Connection connection;
	private String featureName;
	private Object postUpdateValue;
	private Object preUpdateValue;

	/**
	 * Creates a new operation to update trace links.
	 * 
	 * @param label      the label used for the operation. Should never be
	 *                   <code>null</code>.
	 * @param connection the connection representing the trace link that should be
	 *                   updated.
	 * @param feature    the feature that should be updated. This value will be cast
	 *                   to <code>String</code> and an
	 *                   {@link IllegalArgumentException} will be thrown if it is
	 *                   not a string.
	 * @param value      the value to which the feature should be updated.
	 */
	public UpdateTraceOperation(String label, Connection connection, Object feature, Object value) {
		super(label);
		this.connection = connection;
		if (!(feature instanceof String)) {
			throw new IllegalArgumentException("Feature must be a string!");
		}
		this.featureName = (String) feature;
		this.postUpdateValue = value;
		this.preUpdateValue = getValue(connection, featureName);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return executeTraceUpdate(info, postUpdateValue);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return executeTraceUpdate(info, preUpdateValue);
	}

	/**
	 * Executes the update of the trace link.
	 * 
	 * @param info  the {@code IAdaptable} which called this operation (should
	 *              provide a {@code Shell} adapter
	 * @param value the value to set the feature to
	 * @return the status of the update: {@link Status.OK_STATUS} if everything
	 *         worked as expected, {@link Status.ERROR} otherwise
	 */
	private IStatus executeTraceUpdate(IAdaptable info, Object value) {
		Shell shell = info.getAdapter(Shell.class);
		IStatus executionStatus = null;
		try {
			updateTrace(connection, featureName, value);
			executionStatus = Status.OK_STATUS;
		} catch (IllegalStateException e) {
			executionStatus = new Status(Status.ERROR, OperationsHelper.PLUGIN_ID, e.getMessage(), e);
			OperationsHelper.createErrorMessage(shell, executionStatus, ERROR_DIALOG_TITLE, e.getMessage());
		} catch (InterruptedException e) {
			executionStatus = new Status(Status.ERROR, OperationsHelper.PLUGIN_ID, e.getMessage(), e);
			OperationsHelper.createErrorMessage(shell, executionStatus, ERROR_DIALOG_TITLE,
					EXCEPTION_MESSAGE_INTERRUPTED_EXCEPTION);
		} catch (RuntimeException e) {
			executionStatus = new Status(Status.ERROR, OperationsHelper.PLUGIN_ID, e.getMessage(), e);
			OperationsHelper.createErrorMessage(shell, executionStatus, ERROR_DIALOG_TITLE,
					EXCEPTION_MESSAGE_RUNTIME_EXCEPTION);
		}
		return executionStatus;
	}

	/**
	 * Retrieves the value of a structural features of the trace link encapsulated
	 * in the given {@link Connection}.
	 * 
	 * @param connection  the {@code Connection} that contains the trace link whose
	 *                    structural feature should be retrieved.
	 * @param featureName the name of the structural feature to retrieve
	 * @return the value of the structural feature
	 */
	private Object getValue(Connection connection, String featureName) {
		EStructuralFeature structuralFeature = connection.getTlink().eClass().getEStructuralFeature(featureName);
		return connection.getTlink().eGet(structuralFeature);
	}

	/**
	 * Updates the trace link in the provided {@link Connection} by setting the
	 * given feature to the given value.
	 * 
	 * @param connection  the connection that wraps the trace link that should be
	 *                    updated
	 * @param featureName the name of the structural feature to update
	 * @param value       the value to set the structural feature to
	 * @throws IllegalStateException if the transaction in which the trace is
	 *                               updated is rolled back
	 * @throws InterruptedException  if the transaction in which the trace is
	 *                               updated is interrupted
	 */
	private void updateTrace(Connection connection, String featureName, Object value)
			throws IllegalStateException, InterruptedException {
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().orElseThrow();

		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);
		TraceHelper traceHelper = new TraceHelper(traceModel);
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);

		traceHelper.updateTrace(connection, featureName, value);
		persistenceAdapter.saveTracesAndArtifacts(traceModel, artifactModel);
	}

}
