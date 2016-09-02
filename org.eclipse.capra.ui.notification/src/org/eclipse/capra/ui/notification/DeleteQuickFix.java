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
import org.eclipse.emf.common.util.EList;
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

	private URI artifactModelURI;
	private URI traceModelURI;
	private TracePersistenceAdapter tracePersistenceAdapter;
	private ResourceSet resourceSet;
	private EObject awc;
	private String label;
	private Resource resourceForArtifacts;
	private Resource resourceForTraces;
	private ArtifactWrapperContainer container;
	private EObject traceModel;
	private TraceMetaModelAdapter traceMetamodelAdapter;
	private List<RelatedTo> toDelete = new ArrayList<>();

	DeleteQuickFix(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {

		resourceSet = new ResourceSetImpl();
		tracePersistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		traceMetamodelAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
		traceModel = tracePersistenceAdapter.getTraceModel(resourceSet);
		traceModelURI = EcoreUtil.getURI(traceModel);
		GenericTraceModel newTraceModel = GenericTraceMetaModelFactory.eINSTANCE.createGenericTraceModel();
		resourceForTraces = resourceSet.createResource(traceModelURI);

		GenericTraceModel simpleTM = (GenericTraceModel) traceModel;
		List<RelatedTo> traces = simpleTM.getTraces();

		awc = tracePersistenceAdapter.getArtifactWrappers(resourceSet);
		artifactModelURI = EcoreUtil.getURI(awc);

		resourceForArtifacts = resourceSet.createResource(artifactModelURI);
		EList<ArtifactWrapper> list = ((ArtifactWrapperContainer) awc).getArtifacts();
		container = (ArtifactWrapperContainer) awc;
		int counter = -1;
		for (ArtifactWrapper aw : list) {
			counter++;
			try {
				if (aw.getName().equals(marker.getAttribute("fileName"))) {
					List<Connection> connections = traceMetamodelAdapter.getConnectedElements(aw, traceModel);
					connections.forEach(c -> {
						for (RelatedTo t : traces) {
							if (c.getTlink().equals(t)) {
								toDelete.add(t);
							}
						}
					});
					traces.removeAll(toDelete);
					newTraceModel.getTraces().addAll(traces);
					resourceForTraces.getContents().add(newTraceModel);

					ArtifactWrapper toRemove = container.getArtifacts().get(counter);
					EcoreUtil.delete(toRemove);
					resourceForArtifacts.getContents().add(container);
					try {
						resourceForTraces.save(null);
						resourceForArtifacts.save(null);
					} catch (IOException e) {

						e.printStackTrace();
					}

					break;
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			marker.delete();
		} catch (CoreException e) {

			e.printStackTrace();
		}
	}
}
