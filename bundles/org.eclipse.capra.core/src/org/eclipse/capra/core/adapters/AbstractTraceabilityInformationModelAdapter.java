/*******************************************************************************
 * Copyright (c) 2016-2022 Chalmers | University of Gothenburg, rt-labs and others.
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
package org.eclipse.capra.core.adapters;

import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Implements standard functionality for the methods defined in the
 * {@link ITraceabilityInformationModelAdapter}, in particular to delegate
 * identifying internal links to the respective handler.
 */
public abstract class AbstractTraceabilityInformationModelAdapter implements ITraceabilityInformationModelAdapter {

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		IPersistenceAdapter persistenceAdapter = ExtensionPointHelper.getPersistenceAdapter().orElseThrow();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		IArtifactHandler<?> handlerFirstElement = artifactHelper.getHandler(first).orElseThrow();
		IArtifactHandler<?> handlerSecondElement = artifactHelper.getHandler(second).orElseThrow();

		return handlerFirstElement.isThereAnInternalTraceBetween(first, second)
				|| handlerSecondElement.isThereAnInternalTraceBetween(first, second);
	}
}
