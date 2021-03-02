/*******************************************************************************
 * Copyright (c) 2016, 2021 Chalmers | University of Gothenburg, rt-labs and others.
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
package org.eclipse.capra.handler.ode;

import java.util.Collections;
import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.emf.ecore.EObject;

import top.argumentation.ArgumentAsset;
import top.base.Element;
import top.base.ModelElement;

/**
 * An artifact handler to create traces to and from Open Dependability Exchange
 * (ODE) models. The ODE meta-model was specified by the DEIS research project
 * and can be used to, among other things, specify safety assurance cases.
 * <p>
 * The official version of the Eclipse implementation of the ODE meta-model can
 * be found at {@link https://github.com/DEIS-Project-EU/DDI-Scripting-Tools}.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class OdeHandler extends AbstractArtifactHandler<Element> {

	@Override
	public EObject createWrapper(Element artifact, EObject artifactModel) {
		return artifact;
	}

	@Override
	public Element resolveWrapper(EObject wrapper) {
		return (Element) wrapper;
	}

	@Override
	public String getDisplayName(Element artifact) {
		if (artifact instanceof ArgumentAsset) {
			try {
				return ((ArgumentAsset) artifact).getDescription().getContent().getValue().get(0).getContent();
			} catch (NullPointerException npe) {
				// Deliberately do nothing and fall back to the standard behaviour
			}
		}
		if (artifact instanceof ModelElement) {
			try {
				return ((ModelElement) artifact).getName().getContent();
			} catch (NullPointerException npe) {
				// Deliberately do nothing and fall back to the standard behaviour
			}
		}

		EObject sel = this.resolveWrapper(artifact);
		return org.eclipse.capra.core.helpers.EMFHelper.getIdentifier(sel);
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Connection> getInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes) {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

}
