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

import java.util.List;
import java.util.Optional;

import org.eclipse.capra.ui.operations.CreateTraceOperation;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IAdaptable;

public class TraceCreationHandler extends AbstractHandler {

	private IUndoContext undoContext = IOperationHistory.GLOBAL_UNDO_CONTEXT;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IAdaptable adapter = SelectionView.getOpenedView().getSite();

		IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();

		List<Object> sources = SelectionView.getOpenedView().getSources();
		List<Object> targets = SelectionView.getOpenedView().getTargets();

		CreateTraceOperation createTraceOperation = new CreateTraceOperation("Create trace link", sources, targets);
		createTraceOperation.addContext(undoContext);
		if (SelectionView.getOpenedView().getSelectedTraceType() != null) {
			createTraceOperation
					.setChooseTraceType((a, b) -> Optional.of(SelectionView.getOpenedView().getSelectedTraceType()));
		}
		operationHistory.execute(createTraceOperation, null, adapter);
		return null;
	}

	public void setUndoContext(IUndoContext undoContext) {
		this.undoContext = undoContext;
	}

}
