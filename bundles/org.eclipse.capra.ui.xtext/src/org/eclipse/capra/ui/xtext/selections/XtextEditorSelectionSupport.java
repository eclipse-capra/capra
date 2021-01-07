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

import java.util.Collections;
import java.util.List;

import org.eclipse.capra.ui.selections.ISelectionSupport;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocumentUtil;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

/**
 * Allows extracting EMF model elements from an {@link ISelection} of Xtext
 * editors.
 * 
 * @author Dominique Blouin
 *
 */
public class XtextEditorSelectionSupport implements ISelectionSupport {

	private final EObjectAtOffsetHelper eObjectOffsetHelper;

	public XtextEditorSelectionSupport() {
		eObjectOffsetHelper = new EObjectAtOffsetHelper();
	}

	@Override
	public boolean supportsWorkbenchPart(final IWorkbenchPart workbenchPart) {
		return workbenchPart instanceof XtextEditor;
	}

	@Override
	public List<Object> extractSelectedElements(final ISelection selection, final IWorkbenchPart workbenchPart) {
		if (selection instanceof ITextSelection) {
			final XtextResource resource = getResource(workbenchPart);

			final ITextSelection textselection = (ITextSelection) selection;
			final EObject selectedElement = eObjectOffsetHelper.resolveElementAt(resource, textselection.getOffset());

			if (selectedElement == null) {
				return Collections.emptyList();
			}

			return Collections.singletonList(selectedElement);
		}
		return Collections.emptyList();
	}

	protected XtextResource getResource(final IWorkbenchPart workbenchPart) {
		final IXtextDocument document = XtextDocumentUtil.get(workbenchPart);

		if (document == null) {
			return null;
		}

		return document.readOnly(new IUnitOfWork<XtextResource, XtextResource>() {

			@Override
			public XtextResource exec(XtextResource state) throws Exception {
				return state;
			}
		});
	}

	@Override
	public ResourceSet getResourceSet(final IWorkbenchPart workbenchPart) {
		final Resource resource = getResource(workbenchPart);

		return resource == null ? null : resource.getResourceSet();
	}
}
