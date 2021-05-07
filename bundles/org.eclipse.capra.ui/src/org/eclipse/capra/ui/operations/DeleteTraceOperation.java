package org.eclipse.capra.ui.operations;

import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.ITraceabilityInformationModelAdapter;
import org.eclipse.capra.core.adapters.IPersistenceAdapter;
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
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swt.widgets.Shell;

/**
 * Operation to delete trace links. Also allows to undo and redo the operation.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class DeleteTraceOperation extends AbstractOperation {

	private static final String ERROR_DIALOG_TITLE = "Error deleting trace link";
	private static final String EXCEPTION_MESSAGE_RUNTIME_EXCEPTION = "An exception occured during the deletion of the trace link.";

	private Connection connection;

	/**
	 * Creates a new operation to delete trace links.
	 * 
	 * @param label      the label used for the operation. Should never be
	 *                   <code>null</code>.
	 * @param connection the connection representing the trace link that should be
	 *                   deleted.
	 */
	public DeleteTraceOperation(String label, Connection connection) {
		super(label);
		this.connection = connection;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		Shell shell = info.getAdapter(Shell.class);
		IStatus executionStatus = null;

		try {
			deleteTrace();
			executionStatus = Status.OK_STATUS;
		} catch (IllegalStateException e) {
			executionStatus = new Status(Status.ERROR, OperationsHelper.PLUGIN_ID, e.getMessage(), e);
			OperationsHelper.createErrorMessage(shell, executionStatus, ERROR_DIALOG_TITLE, e.getMessage());
		} catch (RuntimeException e) {
			executionStatus = new Status(Status.ERROR, OperationsHelper.PLUGIN_ID, e.getMessage(), e);
			OperationsHelper.createErrorMessage(shell, executionStatus, ERROR_DIALOG_TITLE,
					EXCEPTION_MESSAGE_RUNTIME_EXCEPTION);
		}
		return executionStatus;

	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		IPersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().orElseThrow();

		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);
		TraceHelper traceHelper = new TraceHelper(traceModel);
		traceHelper.createTrace(connection.getOrigins(), connection.getTargets(), connection.getTlink().eClass());
		return Status.OK_STATUS;
	}

	/**
	 * Deletes the trace stored in the {@code connection} field.
	 */
	private void deleteTrace() {
		ITraceabilityInformationModelAdapter traceAdapter = ExtensionPointHelper.getTraceabilityInformationModelAdapter().get();
		IPersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		EObject metadataModel = persistenceAdapter.getMetadataContainer(resourceSet);
		traceAdapter.deleteTrace(List.of(connection), traceModel);
		persistenceAdapter.saveModels(traceModel, artifactModel, metadataModel);
	}

}
