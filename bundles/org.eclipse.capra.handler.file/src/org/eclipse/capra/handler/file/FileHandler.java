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
package org.eclipse.capra.handler.file;

import java.util.Collections;
import java.util.List;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * Handler to allow tracing to and from arbitrary files in the file system.
 */
public class FileHandler extends AbstractArtifactHandler<IFile> {

	@Override
	public EObject createWrapper(IFile file, EObject artifactModel) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();
		String uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true).toPlatformString(false);
		return adapter.createArtifact(artifactModel, this.getClass().getName(), uri, file.getName(),
				file.getFullPath().toString());
	}

	@Override

	public IFile resolveWrapper(EObject wrapper) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();
		String uri = adapter.getArtifactUri(wrapper);
		return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(uri));
	}

	@Override
	public String getDisplayName(IFile file) {
		return file.getName();
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		return delta.getResource().getName()
				+ " has been changed. Please check if associated trace links are still valid.";
	}

	@Override
	public List<Connection> getInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes) {
		// Method currently left empty to wait for user requirements of relevant
		// internal links for Files
		return Collections.emptyList();
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		return false;
	}

}
