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

import java.util.Arrays;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * Operation to update metadata. Also allows to undo and redo the operation.
 * <p>
 * The operation uses the {@link IPropertySource} for a {@code Connection} or an
 * artifact to retrieve the current value and to set the new value. For this
 * purpose, it looks for the property with the given name. If that property is
 * not found, an error message is shown and the operation abort with an error
 * status.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class UpdateMetadataPropertyOperation extends AbstractOperation {

	private static final String ERROR_DIALOG_TITLE = "Error updating metadata";
	private static final String EXCEPTION_MESSAGE_RUNTIME_EXCEPTION = "An exception occured during the update of the metadata.";

	private IPropertySource metadataPropertySource;
	private Object featureId;
	private Object postUpdateValue;
	private Object preUpdateValue;

	/**
	 * Creates a new operation to update the metadata.
	 * 
	 * @param carrier the object that carries the metadata to update. This should be
	 *                either an instance of {@code Connection} or an EObject
	 *                representing an artifact.
	 * @param label   the label used for the operation. Should never be
	 *                <code>null</code>.
	 * @param feature the name of the metadata feature that should be updated,
	 *                corresponding to the {@code displayName} of the corresponding
	 *                {@link IPropertyDescriptor}
	 * @param value   the value to which the feature should be updated.
	 */
	public UpdateMetadataPropertyOperation(Object carrier, String label, String feature, Object value) {
		super(label);
		this.metadataPropertySource = Platform.getAdapterManager().getAdapter(carrier, IPropertySource.class);
		IPropertyDescriptor descriptor = getPropertyDescriptor(feature);
		if (descriptor != null) {
			this.featureId = descriptor.getId();
		}
		this.postUpdateValue = value;
		this.preUpdateValue = getCurrentFeatureValue();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return executeMetadataUpdate(info, postUpdateValue);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return executeMetadataUpdate(info, preUpdateValue);
	}

	/**
	 * Retrieves the value of the feature of the metadata.
	 * 
	 * @return the value of the feature
	 */
	private Object getCurrentFeatureValue() {
		if (featureId != null) {
			return this.metadataPropertySource.getPropertyValue(featureId);
		} else {
			return null;
		}
	}

	/**
	 * @return
	 */
	private IPropertyDescriptor getPropertyDescriptor(String displayName) {
		IPropertyDescriptor[] descriptors = this.metadataPropertySource.getPropertyDescriptors();
		return Arrays.stream(descriptors).filter(d -> d.getDisplayName().equals(displayName)).findFirst().orElse(null);
	}

	/**
	 * Executes the update of the metadata.
	 * 
	 * @param info  the {@code IAdaptable} which called this operation (should
	 *              provide a {@code Shell} adapter
	 * @param value the value to set the feature to
	 * @return the status of the update: {@link Status.OK_STATUS} if everything
	 *         worked as expected, {@link Status.ERROR} otherwise
	 */
	private IStatus executeMetadataUpdate(IAdaptable info, Object value) {
		IStatus executionStatus = Status.CANCEL_STATUS;
		Shell shell = info.getAdapter(Shell.class);
		if (this.featureId == null) {
			executionStatus = new Status(Status.ERROR, OperationsHelper.PLUGIN_ID,
					"Metadata field " + this.featureId + "not found");
			OperationsHelper.createErrorMessage(shell, executionStatus, ERROR_DIALOG_TITLE,
					"Metadata field " + this.featureId + "not found");
		} else {
			try {
				this.metadataPropertySource.setPropertyValue(this.featureId, value);
				executionStatus = Status.OK_STATUS;
			} catch (IllegalStateException e) {
				executionStatus = new Status(Status.ERROR, OperationsHelper.PLUGIN_ID, e.getMessage(), e);
				OperationsHelper.createErrorMessage(shell, executionStatus, ERROR_DIALOG_TITLE, e.getMessage());
			} catch (RuntimeException e) {
				executionStatus = new Status(Status.ERROR, OperationsHelper.PLUGIN_ID, e.getMessage(), e);
				OperationsHelper.createErrorMessage(shell, executionStatus, ERROR_DIALOG_TITLE,
						EXCEPTION_MESSAGE_RUNTIME_EXCEPTION);
			}
		}
		return executionStatus;
	}

}
