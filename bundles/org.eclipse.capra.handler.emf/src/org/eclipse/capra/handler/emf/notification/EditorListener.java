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
package org.eclipse.capra.handler.emf.notification;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.PropertySheet;

/**
 * Listens to changes in the IWorkbenchPage. If a model editor is activated in
 * the page, the listener registers a ModelChangeListener to it and unregisters
 * it from the previous editor.
 * 
 * @author Dusan Kalanj
 *
 */
public class EditorListener implements IPartListener {

	private ModelChangeListener modelChangeListener = new ModelChangeListener();
	private ResourceSet previousResourceSet;

	/**
	 * The default constructor that registers the ModelChangeListener to the
	 * currently active part, if the part is an EMF editor.
	 */
	public EditorListener() {
		IWorkbenchPage wp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditingDomainProvider domainProvider = wp.getActivePart().getAdapter(IEditingDomainProvider.class);

		// If the editor that is active when the EditorListener is created is an
		// EMF editor, directly register the ModelChangeListener.
		if (domainProvider != null) {
			ResourceSet resourceSet = domainProvider.getEditingDomain().getResourceSet();
			if (resourceSet != null) {
				EList<Adapter> adapters = resourceSet.eAdapters();
				if (!adapters.contains(modelChangeListener))
					adapters.add(modelChangeListener);
				previousResourceSet = resourceSet;
			}
		}
	}

	@Override
	public void partActivated(IWorkbenchPart part) {

		if (part instanceof PropertySheet)
			return;

		IEditingDomainProvider domainProvider = part.getAdapter(IEditingDomainProvider.class);
		ResourceSet currentResourceSet = null;
		if (domainProvider != null)
			currentResourceSet = domainProvider.getEditingDomain().getResourceSet();

		if (previousResourceSet == currentResourceSet)
			return;

		if (currentResourceSet != null) {
			if (previousResourceSet != null)
				// Remove ModelChangeListener from the previous ResourceSet.
				previousResourceSet.eAdapters().remove(modelChangeListener);
			previousResourceSet = currentResourceSet;
			EList<Adapter> adapters = currentResourceSet.eAdapters();
			if (!adapters.contains(modelChangeListener))
				// Add ModelChangeListener to the current ResourceSet.
				Display.getDefault().syncExec(() -> adapters.add(modelChangeListener));
		}
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		// We are not interested in this event.
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		// We are not interested in this event.
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		// We are not interested in this event.
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		// We are not interested in this event.
	}
}
