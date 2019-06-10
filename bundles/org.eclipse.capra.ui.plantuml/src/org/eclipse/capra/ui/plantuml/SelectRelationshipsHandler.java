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
package org.eclipse.capra.ui.plantuml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Toggles between showing (DSL) internal links or not
 * 
 * @author Dominik Einkemmer
 */
public class SelectRelationshipsHandler extends AbstractHandler {
	protected static List<String> selectedRelationshipTypes = new ArrayList<>();
	protected static List<String> possibleRelationshipTypes = new ArrayList<>();
	protected static EObject previousElement = null;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		ListSelectionDialog dialog = new ListSelectionDialog(window.getShell(), possibleRelationshipTypes,
				new ArrayContentProvider(), new LabelProvider() {
					@Override
					public String getText(Object element) {
						return (String) element;
					}
				}, "Selection:");
		dialog.setTitle("Select Relationships you want to include");
		dialog.setInitialElementSelections(selectedRelationshipTypes);

		if (dialog.open() == Window.OK) {
			Object[] results = dialog.getResult();
			List<String> selectedRelations = new ArrayList<>();
			for (Object res : results) {
				selectedRelations.add((String) res);
			}
			selectedRelationshipTypes = selectedRelations;
		}

		return null;
	}

	public static void addToPossibleRelationsForSelection(String className) {
		if (!possibleRelationshipTypes.contains(className)) {
			possibleRelationshipTypes.add(className);
		}
	}

	public static void addToPossibleRelationsForSelection(List<EObject> objects) {
		for (EObject obj : objects) {
			String className = obj.eClass().getName();
			if (!possibleRelationshipTypes.contains(className)) {
				possibleRelationshipTypes.add(className);
			}
		}
	}

	public static void clearPossibleRelationsForSelection() {
		possibleRelationshipTypes.clear();
	}

	public static List<String> getSelectedRelationshipTypes() {
		return selectedRelationshipTypes;
	}

	public static void emptySelectedRelationshipTypes() {
		selectedRelationshipTypes.clear();
	}

	public static EObject getPreviousElement() {
		return previousElement;
	}

	public static void setPreviousElement(EObject prevElement) {
		previousElement = prevElement;
	}
}
