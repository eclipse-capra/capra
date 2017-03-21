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
package org.eclipse.capra.ui.notification;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapper;
import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapperContainer;
import org.eclipse.capra.GenericTraceMetaModel.GenericTraceModel;
import org.eclipse.capra.GenericTraceMetaModel.RelatedTo;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.IMarkerResolution;

/**
 * Renames and updates the properties in the associated artifact wrapper to
 * reflect changes in the original object represented by the wrapper.
 * 
 * @author Michael Warne
 */
public class RenameOrMoveQuickFix implements IMarkerResolution {

	private String label;

	RenameOrMoveQuickFix(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {
		ResourceSet resourceSet = new ResourceSetImpl();
		TracePersistenceAdapter tracePersistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		EObject model = tracePersistenceAdapter.getArtifactWrappers(resourceSet);

		String artifactContainerFileName = model.eResource().getURI().lastSegment();
		String markerFileName = new File(marker.getResource().toString()).getName();

		if (markerFileName.equals(artifactContainerFileName)) {
			// The element that the marker points to is a Capra artifact.
			List<ArtifactWrapper> artifacts = ((ArtifactWrapperContainer) model).getArtifacts();
			String oldArtifactUri = marker.getAttribute(CapraNotificationHelper.OLD_URI, null);
			for (ArtifactWrapper aw : artifacts) {
				if (aw.getUri().equals(oldArtifactUri)) {
					String newArtifactUri = marker.getAttribute(CapraNotificationHelper.NEW_URI, null);
					aw.setUri(newArtifactUri);
					aw.setPath(newArtifactUri);
					aw.setName(marker.getAttribute(CapraNotificationHelper.NEW_NAME, null));
					break;
				}
			}

		} else {
			// The element that the marker points to is an EObject and is not
			// contained in the Capra artifact model.
			model = tracePersistenceAdapter.getTraceModel(resourceSet);
			List<RelatedTo> traces = ((GenericTraceModel) model).getTraces();
			String oldArtifactUri = marker.getAttribute(CapraNotificationHelper.OLD_URI, null);
			URI markerUri = URI.createURI(oldArtifactUri);
			for (RelatedTo trace : traces) {
				for (EObject item : trace.getItem()) {
					URI itemUri = CapraNotificationHelper.getFileUri(item);
					if (markerUri.equals(itemUri)) {
						URI newUri = URI.createURI(marker.getAttribute(CapraNotificationHelper.NEW_URI, null));
						((InternalEObject) item).eSetProxyURI(newUri);
					}
				}
			}
		}

		// Update references inside the model (can be artifact or trace model).
		Resource resource = resourceSet.createResource(EcoreUtil.getURI(model));
		resource.getContents().add(model);

		try {
			resource.save(null);
			marker.delete();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}