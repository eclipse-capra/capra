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

import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.rmf.reqif10.SpecHierarchy;
import org.eclipse.rmf.reqif10.SpecObject;

public class ReqIfHandler extends AbstractArtifactHandler<SpecHierarchy> {

	// TODO: This used to expect IStructuredSelection input, why?

	@Override
	public EObject createWrapper(SpecHierarchy spec, EObject artifactModel) {
		return spec;
	}

	@Override
	public SpecHierarchy resolveWrapper(EObject wrapper) {
		return (SpecHierarchy) wrapper;
	}

	@Override
	public String getDisplayName(SpecHierarchy spec) {
		SpecObject specObject = spec.getObject();
		return specObject.getIdentifier();
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		return null;
	}
}
