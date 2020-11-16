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
package org.eclipse.capra.ui.notification;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.core.helpers.TraceHelper;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.IMarkerResolution;

/**
 * Renames and updates the properties in the associated artifact wrapper to
 * reflect changes in the original object represented by the wrapper.
 *
 * @author Michael Warne
 */
public class RenameOrMoveQuickFix implements IMarkerResolution {

	ArtifactMetaModelAdapter artifactAdapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();

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
		TracePersistenceAdapter tracePersistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter()
				.orElseThrow();
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		EObject traceModel = tracePersistenceAdapter.getTraceModel(resourceSet);
		EObject artifactModel = tracePersistenceAdapter.getArtifactWrappers(resourceSet);
		TraceMetaModelAdapter traceMetaModelAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().orElseThrow();
		TraceHelper traceHelper = new TraceHelper(traceModel);

		String artifactContainerFileName = artifactModel.eResource().getURI().lastSegment();
		String markerFileName = new File(marker.getResource().toString()).getName();

		if (markerFileName.equals(artifactContainerFileName)) {
			// The element that the marker points to is a Capra artifact.
			List<EObject> artifacts = artifactAdapter.getAllArtifacts(artifactModel);
			String oldArtifactUri = marker.getAttribute(CapraNotificationHelper.OLD_URI, null);
			for (EObject aw : artifacts) {
				if (artifactAdapter.getArtifactUri(aw).equals(oldArtifactUri)) {
					String newArtifactUri = marker.getAttribute(CapraNotificationHelper.NEW_URI, null);
					artifactAdapter.createArtifact(artifactModel, artifactAdapter.getArtifactHandler(aw),
							newArtifactUri, marker.getAttribute(CapraNotificationHelper.NEW_NAME, null),
							newArtifactUri);
					break;
				}
			}

		} else {
			// The element that the marker points to is an EObject and is not
			// contained in the Capra artifact model.
			List<Connection> traces = traceMetaModelAdapter.getAllTraceLinks(traceModel);
			String oldArtifactUri = marker.getAttribute(CapraNotificationHelper.OLD_URI, null);
			URI markerUri = URI.createURI(oldArtifactUri);
			for (Connection c : traces) {
				for (EObject item : traceHelper.getTracedElements(c)) {
					URI itemUri = CapraNotificationHelper.getFileUri(item);
					if (markerUri.equals(itemUri)) {
						URI newUri = URI.createURI(marker.getAttribute(CapraNotificationHelper.NEW_URI, null));
						((InternalEObject) item).eSetProxyURI(newUri);
					}
				}
			}
		}

		// Update references inside the model (can be artifact or trace model).
		Resource resource = resourceSet.createResource(EcoreUtil.getURI(artifactModel));
		resource.getContents().add(artifactModel);

		try {
			resource.save(null);
			marker.delete();
		} catch (IOException | CoreException e) {
			e.printStackTrace();
		}
	}
}