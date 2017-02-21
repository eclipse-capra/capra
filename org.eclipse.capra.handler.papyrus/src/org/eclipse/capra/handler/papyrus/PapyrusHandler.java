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
package org.eclipse.capra.handler.papyrus;

import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrus.emf.facet.custom.metamodel.v0_2_0.internal.treeproxy.EObjectTreeElement;
import org.eclipse.papyrus.infra.emf.utils.EMFHelper;

/**
 * A handler to create trace links from and to model elements created in
 * Papyrus.
 */
public class PapyrusHandler extends AbstractArtifactHandler<EObjectTreeElement> {


	@Override
	public EObject createWrapper(EObjectTreeElement artifact, EObject artifactModel) {
		// Returns the EObject corresponding to the input object if the input is
		// an EObject, or if it is Adaptable to an EObject
		return EMFHelper.getEObject(artifact);
	}

	@Override
	public EObjectTreeElement resolveWrapper(EObject wrapper) {
		return (EObjectTreeElement) wrapper; // TODO
	}

	@Override
	public String getDisplayName(EObjectTreeElement artifact) {
		EObject sel = EMFHelper.getEObject(artifact);
		return org.eclipse.capra.core.helpers.EMFHelper.getIdentifier(sel); // TODO
	}

}
