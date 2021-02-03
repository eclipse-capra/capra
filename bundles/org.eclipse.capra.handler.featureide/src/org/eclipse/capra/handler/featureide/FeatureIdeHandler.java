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
package org.eclipse.capra.handler.featureide;

import java.nio.file.FileSystems;
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

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.io.manager.FileHandler;

/**
 * A handler for feature models from FeatureIDE.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class FeatureIdeHandler extends AbstractArtifactHandler<IFeature> {

	@Override
	public EObject createWrapper(IFeature feature, EObject artifactModel) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();

		final IPath path = Path.fromOSString(feature.getFeatureModel().getSourceFile().toString());
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IFile file = root.getFileForLocation(path);
		String uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true).toPlatformString(false);

		return adapter.createArtifact(artifactModel, this.getClass().getName(), uri,
				Long.toString(feature.getInternalId()), feature.getName(),
				feature.getFeatureModel().getSourceFile().toString());
	}

	@Override
	public IFeature resolveWrapper(EObject wrapper) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();
		FileHandler<IFeatureModel> fileHandler = FeatureModelManager
				.getFileHandler(FileSystems.getDefault().getPath(adapter.getArtifactPath(wrapper).toOSString()));
		return fileHandler.getObject().getFeature(adapter.getArtifactName(wrapper));
	}

	@Override
	public String getDisplayName(IFeature feature) {
		return feature.getName();
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		return null;
	}

	@Override
	public List<Connection> getInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes) {
		// Method currently left empty to wait for user requirements of relevant
		// internal links for FeatureIDE models
		return Collections.emptyList();
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		return false;
	}
}
