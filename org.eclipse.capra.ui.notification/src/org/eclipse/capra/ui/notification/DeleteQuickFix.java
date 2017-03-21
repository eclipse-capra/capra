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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapper;
import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapperContainer;
import org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelFactory;
import org.eclipse.capra.GenericTraceMetaModel.GenericTraceModel;
import org.eclipse.capra.GenericTraceMetaModel.RelatedTo;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.IMarkerResolution;

/**
 * A quick fix to delete a trace link if one of the linked objects is no longer
 * available.
 * 
 * @author Michael Warne
 */
public class DeleteQuickFix implements IMarkerResolution {

	private String label;

	DeleteQuickFix(String label) {
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
		ArtifactWrapperContainer awc = (ArtifactWrapperContainer) tracePersistenceAdapter
				.getArtifactWrappers(resourceSet);
		GenericTraceModel traceModel = (GenericTraceModel) tracePersistenceAdapter.getTraceModel(resourceSet);

		List<RelatedTo> traces = traceModel.getTraces();
		TraceMetaModelAdapter traceMetamodelAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
		List<ArtifactWrapper> artifacts = ((ArtifactWrapperContainer) awc).getArtifacts();
		List<RelatedTo> toDelete = new ArrayList<>();

		String artifactContainerFileName = awc.eResource().getURI().lastSegment();
		String markerContainerFileName = new File(marker.getResource().toString()).getName();
		String markerUri = marker.getAttribute(CapraNotificationHelper.OLD_URI, null);

		if (markerContainerFileName.equals(artifactContainerFileName)) {
			// The element that the marker points to is a Capra artifact.
			int index = 0;
			for (ArtifactWrapper aw : artifacts) {
				if (aw.getUri().equals(markerUri)) {
					List<Connection> connections = traceMetamodelAdapter.getConnectedElements(aw, traceModel);
					connections.forEach(c -> {
						for (RelatedTo t : traces)
							if (c.getTlink().equals(t))
								toDelete.add(t);
					});
					break;
				}
				index++;
			}

			// Delete selected artifacts.
			ArtifactWrapper toRemove = awc.getArtifacts().get(index);
			EcoreUtil.delete(toRemove);
			URI artifactModelURI = EcoreUtil.getURI(awc);
			Resource resourceForArtifacts = resourceSet.createResource(artifactModelURI);
			resourceForArtifacts.getContents().add(awc);

			try {
				resourceForArtifacts.save(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// The element that the marker points to is an EObject and is not
			// contained in the Capra artifact model.
			URI deletedEObjectUri = URI.createURI(markerUri);
			for (RelatedTo trace : traceModel.getTraces()) {
				for (EObject item : trace.getItem()) {
					URI itemUri = CapraNotificationHelper.getFileUri(item);
					if (deletedEObjectUri.equals(itemUri)) {
						toDelete.add(trace);
						break;
					}
				}
			}
		}

		// Delete selected traces.
		traces.removeAll(toDelete);
		GenericTraceModel newTraceModel = GenericTraceMetaModelFactory.eINSTANCE.createGenericTraceModel();
		newTraceModel.getTraces().addAll(traces);
		URI traceModelURI = EcoreUtil.getURI(traceModel);
		Resource resourceForTraces = resourceSet.createResource(traceModelURI);
		resourceForTraces.getContents().add(newTraceModel);

		try {
			resourceForTraces.save(null);
			marker.delete();
		} catch (IOException | CoreException e) {
			e.printStackTrace();
		}
	}
}
