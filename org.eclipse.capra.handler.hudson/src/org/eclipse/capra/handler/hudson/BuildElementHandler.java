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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.resources.IResourceDelta;
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
				build.getLabel(), build.getUrl());
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

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addInternalLinks(EObject investigatedElement, List<Connection> allElements,
			ArrayList<Integer> duplicationCheck, List<String> selectedRelationshipTypes) {
		// Method currently left empty to wait for user requirements of relevant internal links for Build Elements
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		return false;
	}

}
