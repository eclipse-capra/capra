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
package org.eclipse.capra.handler.emf;

import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.emf.ecore.EObject;

/**
 * Handler to allow tracing to and from arbitrary model elements handled by EMF.
 */
public class EMFHandler extends AbstractArtifactHandler<EObject> {

	@Override
	public EObject createWrapper(EObject artifact, EObject artifactModel) {
		return artifact;
	}

	@Override
	public EObject resolveWrapper(EObject wrapper) {
		return wrapper;
	}

	@Override
	public String getDisplayName(EObject artifact) {
		return EMFHelper.getIdentifier(artifact);
	}
}
