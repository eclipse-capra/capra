/*******************************************************************************
 * Copyright (c) 2016, 2020 Chalmers | University of Gothenburg, rt-labs and others.
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
package org.eclipse.capra.handler.capella;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.polarsys.capella.common.data.modellingcore.AbstractTrace;
import org.polarsys.capella.common.data.modellingcore.ModelElement;
import org.polarsys.capella.common.data.modellingcore.TraceableElement;

/**
 * Handler to allow tracing to and from Capella model elements.
 * 
 * @author Jan-Philipp Steghöfer
 */
public class CapellaHandler extends AbstractArtifactHandler<ModelElement> {

	@Override
	public EObject createWrapper(ModelElement artifact, EObject artifactModel) {
		return artifact;
	}

	@Override
	public ModelElement resolveWrapper(EObject wrapper) {
		return (ModelElement) wrapper;
	}

	@Override
	public String getDisplayName(ModelElement artifact) {
		return EMFHelper.getIdentifier(artifact);
	}

	@Override
	public List<Connection> addInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes) {
		List<Connection> internalLinks = new ArrayList<Connection>();
		if (investigatedElement instanceof TraceableElement) {
			EList<AbstractTrace> traces = ((TraceableElement) investigatedElement).getOutgoingTraces();
			for (AbstractTrace trace : traces) {
				Connection newConnection = new Connection(trace.getSourceElement(),
						Lists.mutable.of(trace.getTargetElement()), trace);
				internalLinks.add(newConnection);
			}
			return internalLinks;
		} else {
			return Lists.mutable.empty();
		}
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		if (first instanceof TraceableElement) {
			// TODO: This doesn't work. We probably need to use
			// TraceableElementHelper.doSwitch(), but that
			// requires an editingdomain and all sorts of things that are difficult to have
			// in place in a test.
			EList<AbstractTrace> traces = ((TraceableElement) first).getOutgoingTraces();
			for (AbstractTrace trace : traces) {
				if (EMFHelper.hasSameIdentifier(trace.getTargetElement(), second)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		return null;
	}
}
