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

import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.papyrus.infra.emf.utils.EMFHelper;

/**
 * Handler to allow tracing to and from visual model representations handled by
 * editors built with GEF.
 */
public class GEFHandler extends AbstractArtifactHandler<EditPart> {

	@Override
	public EObject createWrapper(EditPart artifact, EObject artifactModel) {
		return EMFHelper.getEObject(artifact);
	}

	@Override
	public EditPart resolveWrapper(EObject wrapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayName(EditPart artifact) {
		return org.eclipse.capra.core.helpers.EMFHelper.getIdentifier(EMFHelper.getEObject(artifact));
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		return null;
	}
}
