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
package org.eclipse.capra.handler.featureide;

import java.util.Collections;
import java.util.List;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import de.ovgu.featureide.fm.core.base.impl.Feature;

public class FeatureIdeHandler extends AbstractArtifactHandler<Feature> {

	@Override
	public EObject createWrapper(Feature spec, EObject artifactModel) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();

		final IPath path = Path.fromOSString(spec.getFeatureModel().getSourceFile().toString());
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IFile file = root.getFileForLocation(path);
		String uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true).toPlatformString(false);

		EObject wrapper = adapter.createArtifact(artifactModel, this.getClass().getName(), uri,
				Long.toString(spec.getInternalId()), spec.getName(), spec.getFeatureModel().getSourceFile().toString());
		return wrapper;
	}

	@Override
	public Feature resolveWrapper(EObject wrapper) {
		return null;
	}

	@Override
	public String getDisplayName(Feature spec) {
		return spec.getName();
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		return null;
	}

	@Override
	public List<Connection> addInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes) {
		// Method currently left empty to wait for user requirements of relevant
		// internal links for FeatureIDE models
		return Collections.emptyList();
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		return false;
	}
}
