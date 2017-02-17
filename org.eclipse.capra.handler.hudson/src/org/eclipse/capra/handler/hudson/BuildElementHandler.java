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
package org.eclipse.capra.handler.hudson;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.mylyn.builds.internal.core.BuildElement;

/**
 * A handler to allow tracing to and from build elements handled by the continuous
 * integration server Hudson via the integrated Mylyn facilities.
 */
public class BuildElementHandler extends AbstractArtifactHandler<BuildElement> {

	@Override
	public EObject createWrapper(BuildElement build, EObject artifactModel) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		EObject wrapper = adapter.createArtifact(artifactModel, this.getClass().getName(), build.getUrl(),
				build.getLabel());
		return wrapper;
	}

	@Override
	public BuildElement resolveWrapper(EObject wrapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayName(BuildElement build) {
		return build.getName();
	}

}
