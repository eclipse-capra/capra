/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.handler.gef;

import org.eclipse.capra.core.handlers.ArtifactHandler;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.papyrus.infra.emf.utils.EMFHelper;

/**
 * Handler to allow tracing to and from visual model representations handled by
 * editors built with GEF.
 */
public class GEFHandler implements ArtifactHandler {

	@Override
	public boolean canHandleSelection(Object selection) {
		// TODO Auto-generated method stub
		return selection instanceof EditPart;

	}

	@Override
	public EObject getEObjectForSelection(Object selection, EObject artifactModel) {
		// TODO Auto-generated method stub
		EditPart sel = (EditPart) selection;

		return EMFHelper.getEObject(sel);

	}

	@Override
	public Object resolveArtifact(EObject artifact) {
		// TODO Auto-generated method stub
		return null;
	}

}
