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
package org.eclipse.capra.handler.app4mc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.app4mc.amalthea.model.INamed;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.emf.ecore.EObject;

/**
 * A handler for APP4MC models.
 * 
 * @author Salome Maro
 *
 */
public class APP4MCHandler extends AbstractArtifactHandler<INamed> {

	@Override
	public EObject createWrapper(INamed artifact, EObject artifactModel) {
		return artifact;
	}

	@Override
	public INamed resolveWrapper(EObject wrapper) {
		return (INamed) wrapper;
	}

	@Override
	public String getDisplayName(INamed artifact) {
		return artifact.getName();
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		// TODO
		return null;
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		if (first.equals(second)) {
			return false;
		} else if (first instanceof INamed && second instanceof INamed) {
			if (first.eCrossReferences() != null && second.eCrossReferences() != null) {
				if (first.eCrossReferences().contains(second) || second.eCrossReferences().contains(first)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<Connection> getInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes) {
		List<Connection> connections = new ArrayList<>();
		if (investigatedElement instanceof INamed) {
			INamed named = (INamed) investigatedElement;
			if (named.eCrossReferences() != null) {
				List<EObject> refs = named.eCrossReferences();
				for (EObject ref : refs) {
					if (selectedRelationshipTypes != null) {
						if (selectedRelationshipTypes.size() == 0
								|| selectedRelationshipTypes.contains(ref.eClass().getName())) {
							connections
									.add(new Connection(Arrays.asList(investigatedElement), Arrays.asList(ref), ref));
						}
					} else {// selectedRelationshipTypes is null and therefore return all cross references
						// as internal links
						connections.add(new Connection(Arrays.asList(investigatedElement), Arrays.asList(ref), ref));
					}
				}
			}
		}
		return connections;
	}

}
