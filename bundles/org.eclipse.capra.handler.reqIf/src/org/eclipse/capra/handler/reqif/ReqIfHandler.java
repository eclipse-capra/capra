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
package org.eclipse.capra.handler.reqif;

import java.util.Collections;
import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
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

	@Override
	public List<Connection> addInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes) {
		// Method currently left empty to wait for user requirements of relevant
		// internal links for REQIF requirements.
		return Collections.emptyList();
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		return false;
	}
}
