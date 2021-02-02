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
import java.util.ArrayList;
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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.IMarkerResolution;

/**
 * A quick fix to delete a trace link if one of the linked objects is no longer
 * available.
 *
 * @author Michael Warne
 */
public class DeleteQuickFix implements IMarkerResolution {
	ArtifactMetaModelAdapter artifactAdapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();

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

		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		List<Connection> toDelete = new ArrayList<>();
		List<Connection> toRecreate = new ArrayList<>();
		EObject artifactToDelete = null;
		TracePersistenceAdapter tracePersistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter()
				.orElseThrow();
		EObject traceModel = tracePersistenceAdapter.getTraceModel(resourceSet);
		TraceHelper traceHelper = new TraceHelper(traceModel);
		TraceMetaModelAdapter traceMetamodelAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().orElseThrow();
		EObject artifactModel = tracePersistenceAdapter.getArtifactWrappers(resourceSet);

		// get all artifacts
		List<EObject> artifacts = artifactAdapter.getAllArtifacts(artifactModel);

		String artifactContainerFileName = artifactModel.eResource().getURI().lastSegment();
		String markerContainerFileName = new File(marker.getResource().toString()).getName();
		String markerUri = marker.getAttribute(CapraNotificationHelper.OLD_URI, null);

		if (markerContainerFileName.equals(artifactContainerFileName)) {
			// The element that the marker points to is a an artifactWrapper.
			for (EObject aw : artifacts) {
				if (artifactAdapter.getArtifactUri(aw).equals(markerUri)) {
					// get all associated trace links
					List<Connection> traceLinks = traceMetamodelAdapter.getConnectedElements(aw, traceModel);
					for (Connection con : traceLinks) {
						// check if the link has only two elements and add it to
						// the list of links to delete
						if (traceHelper.getTracedElements(con).size() == 2) {
							toDelete.add(con);
						} else {
							toRecreate.add(con);
						}
					}
					artifactToDelete = aw;
					break;
				}
			}
			// delete trace links associated with deleted artifact
			traceMetamodelAdapter.deleteTrace(toDelete, traceModel);

			// Delete selected artifactWrapper.
			EcoreUtil.delete(artifactToDelete);

			// recreate links that need to remain
			recreateTrace(toRecreate, traceModel);

			// use the persistence adapter to save the trace model and artifact
			// model
			tracePersistenceAdapter.saveTracesAndArtifacts(traceModel, artifactModel);

			// delete Marker
			try {
				marker.delete();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} else {
			// The element that the marker points to is an EObject
			for (Connection c : traceMetamodelAdapter.getAllTraceLinks(traceModel)) {
				// check for all trace links with less than two items
				if (traceHelper.getTracedElements(c).size() < 2) {
					toDelete.add(c);
				}
			}

			// Delete the trace links
			traceMetamodelAdapter.deleteTrace(toDelete, traceModel);
			try {
				marker.delete();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	private void recreateTrace(List<Connection> toRecreate, EObject traceModel) {
		TraceMetaModelAdapter traceMetaModelAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().orElseThrow();
		// create a new trace link with the remaining items
		for (Connection c : toRecreate) {
			traceMetaModelAdapter.createTrace(c.getTlink().eClass(), traceModel, c.getOrigins(), c.getTargets());
		}

		// delete the existing trace link
		traceMetaModelAdapter.deleteTrace(toRecreate, traceModel);
	}
}
