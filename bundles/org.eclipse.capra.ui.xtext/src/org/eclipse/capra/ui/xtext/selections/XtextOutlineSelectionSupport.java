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
package org.eclipse.capra.ui.xtext.selections;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.capra.ui.selections.ISelectionSupport;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;

/**
 * Allows extracting EMF model elements from an {@link ISelection} of the Xtext
 * outline view.
 * 
 * @author Dominique Blouin
 *
 */
public class XtextOutlineSelectionSupport implements ISelectionSupport {

	@Override
	public boolean supportsWorkbenchPart(final IWorkbenchPart workbenchPart) {
		if (workbenchPart instanceof ContentOutline) {
			final ISelection selection = ((ContentOutline) workbenchPart).getSelection();

			return !extractSelectedEObjectNodes(selection).isEmpty();
		}

		return false;
	}

	private List<EObjectNode> extractSelectedEObjectNodes(final ISelection selection) {
		final List<EObjectNode> selectedElements = new ArrayList<>();

		if (selection instanceof IStructuredSelection) {
			for (final Object selectedEleme : ((IStructuredSelection) selection).toList()) {
				if (selectedEleme instanceof EObjectNode) {
					selectedElements.add((EObjectNode) selectedEleme);
				}
			}
		}

		return selectedElements;
	}

	@Override
	public List<Object> extractSelectedElements(final ISelection selection, final IWorkbenchPart workbenchPart) {
		final List<Object> selectedElements = new ArrayList<>();
		final List<EObjectNode> selectedEObjectNodes = extractSelectedEObjectNodes(selection);

		final ResourceSet resSet = getResourceSet(workbenchPart);

		for (final EObjectNode selectedEleme : selectedEObjectNodes) {
			final URI objectUri = selectedEleme.getEObjectURI();
			selectedElements.add(resSet.getEObject(objectUri, true));
		}

		return selectedElements;
	}

	@Override
	public ResourceSet getResourceSet(IWorkbenchPart workbenchPart) {
		return new ResourceSetImpl();
	}
}
