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
package org.eclipse.capra.handler.marker;

import java.util.Collections;
import java.util.List;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.handlers.IArtifactUnpacker;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.eclipse.ui.views.markers.MarkerItem;

/**
 * Handler to allow tracing to and from IMarker instances as used by Eclipse to
 * associate notes with resources.
 */
public class MarkerHandler extends AbstractArtifactHandler<IMarker> implements IArtifactUnpacker<MarkerItem, IMarker> {

	@Override
	public EObject createWrapper(IMarker artifact, EObject artifactModel) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();

		IResource resource = artifact.getResource();

		return adapter.createArtifact(artifactModel, this.getClass().getName(), resource.getLocationURI().toString(),
				Long.toString(artifact.getId()), this.getDisplayName(artifact), resource.getFullPath().toString());
	}

	@Override
	public IMarker resolveWrapper(EObject wrapper) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();
		IResource target = org.eclipse.core.resources.ResourcesPlugin.getWorkspace().getRoot()
				.findMember(adapter.getArtifactPath(wrapper));
		Long id = Long.getLong(adapter.getArtifactIdentifier(wrapper));
		return target.getMarker(id);
	}

	@Override
	public String getDisplayName(IMarker artifact) {
		return MarkerUtilities.getMarkerType(artifact) + ": " + MarkerUtilities.getMessage(artifact);
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		return null;
	}

	@Override
	public IMarker unpack(MarkerItem container) {
		return container.getMarker();
	}

	@Override
	public List<Connection> getInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes) {
		// Method currently left empty to wait for user requirements of relevant
		// internal links for Markers
		return Collections.emptyList();
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		return false;
	}
}
