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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IStructuredSelection;

public class ReqIfHandler implements ArtifactHandler {

	@Override
	public boolean canHandleSelection(Object selection) {
		return selection instanceof IStructuredSelection;
	}

	@Override
	public EObject getEObjectForSelection(Object selection, EObject artifactModel) {
		IStructuredSelection sel = (IStructuredSelection) selection;
		EObject obj = EObject.class.cast(sel.getFirstElement());
		return obj;
	}

	@Override
	public Object resolveArtifact(EObject artifact) {
		return artifact;
	}

}
