/*******************************************************************************
 * Copyright (c) 2017 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.handler.marker;

import java.util.ArrayList;
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
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();

		IResource resource = artifact.getResource();

		EObject wrapper = adapter.createArtifact(artifactModel, this.getClass().getName(),
				resource.getLocationURI().toString(), Long.toString(artifact.getId()), this.getDisplayName(artifact),
				resource.getFullPath().toString());

		return wrapper;
	}

	@Override
	public IMarker resolveWrapper(EObject wrapper) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		IResource target = org.eclipse.core.resources.ResourcesPlugin.getWorkspace().getRoot()
				.findMember(adapter.getArtifactPath(wrapper));
		Long id = new Long(adapter.getArtifactIdentifier(wrapper));
		IMarker marker = target.getMarker(id);
		return marker;
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
	public void addInternalLinks(EObject investigatedElement, List<Connection> allElements,
			ArrayList<Integer> duplicationCheck, List<String> selectedRelationshipTypes) {
		// Intentionally do nothing

	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		return false;
	}
}
