/*******************************************************************************
 *  Copyright (c) 2016 Chalmers | Gothenburg University, rt-labs and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *      Chalmers|Gothenburg University and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.handler.reqif;

import org.eclipse.capra.core.handlers.ArtifactHandler;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rmf.reqif10.SpecObject;
import org.eclipse.rmf.reqif10.impl.SpecHierarchyImpl;

public class ReqIfHandler implements ArtifactHandler {

	@Override
	public boolean canHandleSelection(Object selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (!(structuredSelection.getFirstElement() == null)) {
				Object element = structuredSelection.getFirstElement();
				if (element instanceof SpecHierarchyImpl) {
					return true;
				}
			}
		}
		return false;

	}

	@Override
	public EObject getEObjectForSelection(Object selection, EObject artifactModel) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		Object element = structuredSelection.getFirstElement();
		SpecHierarchyImpl specification = (SpecHierarchyImpl) element;
		return specification;

	}

	@Override
	public Object resolveArtifact(EObject artifact) {
		return artifact;
	}

	@Override
	public String getDisplayName(Object selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		Object element = structuredSelection.getFirstElement();
		SpecHierarchyImpl specification = (SpecHierarchyImpl) element;
		SpecObject specObject = specification.getObject();

		return specObject.getIdentifier();
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		return null;
	}
}
