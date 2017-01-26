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
		EObject traceModel = tracePersistenceAdapter.getTraceModel(resourceSet);
		GenericTraceModel simpleTM = (GenericTraceModel) traceModel;
		EObject awc = tracePersistenceAdapter.getArtifactWrappers(resourceSet);

		List<RelatedTo> traces = simpleTM.getTraces();
		TraceMetaModelAdapter traceMetamodelAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
		List<ArtifactWrapper> fileArtifacts = ((ArtifactWrapperContainer) awc).getArtifacts();
		List<RelatedTo> toDelete = new ArrayList<>();

		int index = 0;
		for (ArtifactWrapper aw : fileArtifacts) {
			if (aw.getUri().equals(marker.getAttribute(CapraNotificationHelper.OLD_URI, null))) {
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

		traces.removeAll(toDelete);
		GenericTraceModel newTraceModel = GenericTraceMetaModelFactory.eINSTANCE.createGenericTraceModel();
		newTraceModel.getTraces().addAll(traces);
		URI traceModelURI = EcoreUtil.getURI(traceModel);
		Resource resourceForTraces = resourceSet.createResource(traceModelURI);
		resourceForTraces.getContents().add(newTraceModel);
		ArtifactWrapperContainer container = (ArtifactWrapperContainer) awc;
		ArtifactWrapper toRemove = container.getArtifacts().get(index);
		EcoreUtil.delete(toRemove);
		URI artifactModelURI = EcoreUtil.getURI(awc);
		Resource resourceForArtifacts = resourceSet.createResource(artifactModelURI);
		resourceForArtifacts.getContents().add(container);

		try {
			resourceForTraces.save(null);
			resourceForArtifacts.save(null);
			marker.delete();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
