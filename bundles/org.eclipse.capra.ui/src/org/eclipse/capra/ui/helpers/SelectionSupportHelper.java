/*******************************************************************************
 * Copyright (c) 2016, 2019 Chalmers | University of Gothenburg, rt-labs, IRT SystemX, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *  
 * SPDX-License-Identifier: EPL-2.0
 *  
 * Contributors:
 *      IRT SystemX - initial API and implementation
 *******************************************************************************/
package org.eclipse.capra.ui.helpers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.selections.ISelectionSupport;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.views.properties.PropertySheet;

/**
 * A helper for extracting semantic elements from a selection of a workbench
 * part. Handles the workbench part handler extensions.
 * 
 * @author Dominique Blouin
 *
 */
public class SelectionSupportHelper {

	private static final List<ISelectionSupport> SELECTION_SUPPORTS = new ArrayList<>();

	static {
		for (Object extension : ExtensionPointHelper.getExtensions("org.eclipse.capra.ui.selectionSupport", "class")) {
			SELECTION_SUPPORTS.add((ISelectionSupport) extension);
		}
	}

	private SelectionSupportHelper() {
	}

	/**
	 * Extract selected elements from an {@link ExecutionEvent}.
	 * 
	 * @param event This is the click event to create a trace
	 * @return A list of all the selected elements
	 */
	public static List<Object> extractSelectedElements(final ExecutionEvent event) {
		final IWorkbenchPart workbenchPart = HandlerUtil.getActivePart(event);
		final ISelection currentSelection;

		// For some reason HandlerUtil.getCurrentSelection(event) returns the
		// previous selection in some cases so we look for
		// the selection from the selection provider first
		if (workbenchPart.getSite().getSelectionProvider() != null) {
			currentSelection = workbenchPart.getSite().getSelectionProvider().getSelection();
		} else {
			currentSelection = HandlerUtil.getCurrentSelection(event);
		}

		return extractSelectedElements(currentSelection, workbenchPart);
	}

	/**
	 * Extract selected elements from an {@link ISelection} by delegating the
	 * retrieval and unwrapping of the selection to the registered
	 * {@link ISelectionSupport} instances. If this fails, the selected elements of
	 * type {@link IAdaptable} are retrieved using
	 * {@link IAdaptable#getAdapter(Class)}. If this fails, too, the list is either
	 * empty or only contains those elements that are instances of type
	 * {@code IAdaptable}.
	 * 
	 * @param selection     the selection from the workbench part
	 * @param workbenchPart the workbench part from which the selection should be
	 *                      extracted
	 * @return a list of all selected elements retrieved using the first
	 *         {@code IWorkbenchSelectionSupport} instance registered for the
	 *         {@code WorkbenchPart} or all selected elements of type
	 *         {@code IAdaptable}
	 */
	public static List<Object> extractSelectedElements(final ISelection selection, final IWorkbenchPart workbenchPart) {
		List<Object> selectedElem = new ArrayList<>();
		if (selection.isEmpty()) {
			return selectedElem;
		}

		for (final ISelectionSupport handler : SELECTION_SUPPORTS) {
			if (handler.supportsWorkbenchPart(workbenchPart)) {
				List<Object> extractedElements = handler.extractSelectedElements(selection, workbenchPart);
				if (extractedElements != null && !extractedElements.isEmpty()) {
					selectedElem.addAll(extractedElements);
					break;
				}
			}
		}

		if (selectedElem.isEmpty()) {
			selectedElem = new ArrayList<>();

			if (selection instanceof IStructuredSelection) {
				for (final Object selElement : ((IStructuredSelection) selection).toList()) {
					final Object element = AdapterFactoryEditingDomain.unwrap(selElement);
					Object selectedElement = element;

					if (element instanceof IAdaptable) {
						final Object adaptedElement = ((IAdaptable) element).getAdapter(Object.class);

						if (adaptedElement != null) {
							selectedElement = adaptedElement;
						}
					}

					selectedElem.add(selectedElement);
				}
			}
		}

		return selectedElem;
	}

	/**
	 * Attempts to retrieve the {@link ResourceSet} instance that is used by the
	 * {@link WorkbenchPart}. The {@code ResourceSet} is only available if the
	 * {@code WorkbenchPart} handles EMF models. If this is not the case, this
	 * method returns {@code null}.
	 * 
	 * @param part the {@code WorkbenchPart} whose {@code ResourceSet} should be
	 *             retrieved
	 * @return the {@code ResourceSet} used by {@code part} or {@code null} if no
	 *         {@code ResourceSet} can be found
	 */
	public static ResourceSet getResourceSet(final IWorkbenchPart part) {
		ResourceSet resourceSet = null;
		if (!(part instanceof PropertySheet)) {
			// Iterate over the WorkbenchPartHandlers and find one that can take
			// care of the part
			for (final ISelectionSupport handler : SELECTION_SUPPORTS) {
				if (handler.supportsWorkbenchPart(part)) {
					final ResourceSet resSet = handler.getResourceSet(part);
					if (resSet != null) {
						resourceSet = resSet;
					}
				}

			}
			// If that fails, see if we can get the resource set from the
			// EditingDomain
			if (resourceSet == null) {
				final IEditingDomainProvider domainProvider = part.getAdapter(IEditingDomainProvider.class);

				if (domainProvider != null) {
					return domainProvider.getEditingDomain().getResourceSet();
				}
			}
		}
		return resourceSet;
	}
}