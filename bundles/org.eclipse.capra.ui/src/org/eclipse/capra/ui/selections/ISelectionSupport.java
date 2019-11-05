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
package org.eclipse.capra.ui.selections;

import java.util.List;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.WorkbenchPart;

/**
 * Interface for supporting selections in {@link WorkbenchPart} instances. This
 * allows customization of how semantic objects are extracted from an
 * {@link ISelection} and is useful when, e.g., the selection contains wrapper
 * objects that need to be resolved.
 * 
 * @author Dominique Blouin
 *
 */
public interface ISelectionSupport {

	/**
	 * Checks if the given {@link WorkbenchPart} instance is supported.
	 * 
	 * @param workbenchPart
	 *            the workbench part
	 * @return {@code true} if this handler supports the given workbench part,
	 *         {@code false} otherwise.
	 */
	boolean supportsWorkbenchPart(IWorkbenchPart workbenchPart);

	/**
	 * Extracts objects from an {@link ISelection}, resolving or unwrapping them
	 * when necessary to return types that can be handled by one of the Capra
	 * handlers.
	 * 
	 * @param selection
	 *            the selection from the workbench part
	 * @param workbenchPart
	 *            the workbench part
	 * @return a list of the selected elements
	 */
	List<Object> extractSelectedElements(ISelection selection, IWorkbenchPart workbenchPart);

	/**
	 * Attempts to retrieve the {@link ResourceSet} instance that is used by the
	 * {@link WorkbenchPart}. The {@code ResourceSet} is only available if the
	 * {@code WorkbenchPart} handles EMF models. If this is not the case, this
	 * method returns {@code null}.
	 * 
	 * @param workbenchPart
	 *            the {@code WorkbenchPart} whose {@code ResourceSet} should be
	 *            retrieved
	 * @return the {@code ResourceSet} used by {@code part} or {@code null} if
	 *         no {@code ResourceSet} can be found
	 */
	ResourceSet getResourceSet(IWorkbenchPart workbenchPart);
}
