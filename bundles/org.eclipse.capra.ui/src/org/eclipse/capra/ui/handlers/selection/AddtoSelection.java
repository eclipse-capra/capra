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
package org.eclipse.capra.ui.handlers.selection;

import java.util.List;

import org.eclipse.capra.ui.helpers.TraceCreationHelper;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Action to add an object to the selection view.
 * 
 * @author Anthony Anjorin, Salome Maro
 */
public class AddtoSelection extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<Object> selection = TraceCreationHelper.extractSelectedElements(event);
		SelectionView.getOpenedView().dropToSelection(selection);
		return null;
	}

}
