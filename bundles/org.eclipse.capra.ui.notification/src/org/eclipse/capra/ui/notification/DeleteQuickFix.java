/*******************************************************************************
 * Copyright (c) 2016-2023 Chalmers | University of Gothenburg, rt-labs and others.
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
 *      Jan-Philipp Steghöfer - additional features, updated API
 *******************************************************************************/

package org.eclipse.capra.ui.notification;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.ConnectionQuery;
import org.eclipse.capra.core.adapters.IArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.IPersistenceAdapter;
import org.eclipse.capra.core.adapters.ITraceabilityInformationModelAdapter;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.core.helpers.TraceHelper;
import org.eclipse.capra.ui.operations.CreateTraceOperation;
import org.eclipse.capra.ui.operations.DeleteTraceOperation;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A quick fix to delete a trace link if one of the linked objects is no longer
 * available. Since trace links can have multiple origins and targets, the
 * following rules apply:
 * <ul>
 * <li>If a trace link only connects two elements, it is deleted.</li>
 * <li>If a trace link only has the element that no longer exists as its only
 * origin or target, it is deleted.</li>
 * <li>If a trace link has at least one more origin than the element that no
 * longer exits, a new trace link is created where the element that no longer
 * exists is not part of the origins any more. The original trace link is
 * deleted.</li>
 * <li>If a trace link has at least one more target than the element that no
 * longer exits, a new trace link is created where the element that no longer
 * exists is not part of the targets any more. The original trace link is
 * deleted.</li>
 * </ul>
 * 
 * @author Michael Warne
 * @author Jan-Philipp Steghöfer
 */
public class DeleteQuickFix implements IMarkerResolution {

	private static final Logger LOG = LoggerFactory.getLogger(DeleteQuickFix.class);

	private static final String TRACE_DELETION_TITLE = "Delete trace links";

	IArtifactMetaModelAdapter artifactAdapter = ExtensionPointHelper.getArtifactMetaModelAdapter().orElseThrow();

	private String label;

	public DeleteQuickFix(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {
		try {
			if (!CapraNotificationHelper.CAPRA_PROBLEM_MARKER_ID.equals(marker.getType())) {
				return;
			}
		} catch (CoreException e1) {
			LOG.warn("Exception when running DeleteQuickFix", e1);
			return;
		}

		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		List<Connection> toDelete = new ArrayList<>();
		List<Connection> toUpdate = new ArrayList<>();
		EObject artifactToDelete = null;
		IPersistenceAdapter tracePersistenceAdapter = ExtensionPointHelper.getPersistenceAdapter().orElseThrow();
		EObject traceModel = tracePersistenceAdapter.getTraceModel(resourceSet);
		TraceHelper traceHelper = new TraceHelper(traceModel);
		ITraceabilityInformationModelAdapter traceMetamodelAdapter = ExtensionPointHelper
				.getTraceabilityInformationModelAdapter().orElseThrow();
		EObject artifactModel = tracePersistenceAdapter.getArtifactWrappers(resourceSet);
		EObject metadataModel = tracePersistenceAdapter.getMetadataContainer(resourceSet);

		String artifactContainerFileName = artifactModel.eResource().getURI().lastSegment();
		String markerContainerFileName = new File(marker.getResource().toString()).getName();

		// get all artifacts
		List<EObject> artifacts = artifactAdapter.getAllArtifacts(artifactModel);

		String markerUri = marker.getAttribute(CapraNotificationHelper.OLD_URI, null);

		if (markerContainerFileName.equals(artifactContainerFileName)) {
			// The element that the marker points to is a an artifactWrapper.
			// This means that we are dealing with an artifact wrapper which we
			// need to retrieve from the artifact model.
			for (EObject aw : artifacts) {
				if (artifactAdapter.getArtifactUri(aw).equals(markerUri)) {
					artifactToDelete = aw;
				}
			}
			if (artifactToDelete == null) {
				LOG.warn("Could not find artifact with URI {}", markerUri);
				return;
			}
			// Find links which contain the artifact as origin...
			ConnectionQuery query = ConnectionQuery.of(traceModel, artifactToDelete).build();
			List<Connection> traceLinks = traceMetamodelAdapter.getConnections(query);
			// ... or as target.
			query = ConnectionQuery.of(traceModel, artifactToDelete).setReverseDirection(true).build();
			traceLinks.addAll(traceMetamodelAdapter.getConnections(query));
			for (Connection con : traceLinks) {
				// check if the link has only two elements or if origins/targets only consist of
				// the deleted artifact and add it to the list of links to delete
				if ((traceHelper.getTracedElements(con).size() <= 2)
						|| (con.getOrigins().size() == 1 && con.getOrigins().contains(artifactToDelete))
						|| (con.getTargets().size() == 1 && con.getTargets().contains(artifactToDelete))) {
					toDelete.add(con);
				} else {
					toDelete.add(con);
					toUpdate.add(con);
				}
			}
		} else {
			// An EObject has been deleted that is not represented by an artifact wrapper.
			// Since it's been deleted, it's gone and we need to find stray links.
			// This is potentially very costly...
			// Anyway, we're looking for all links in which the deleted EObject was the only
			// end of the link. We can do this by comparing the URIs.
			List<Connection> traceLinks = traceMetamodelAdapter.getAllTraceLinks(traceModel);
			for (Connection con : traceLinks) {
				if (con.getOrigins().size() == 1
						&& Objects.equals(EcoreUtil.getURI(con.getOrigins().get(0)).toString(), markerUri)) {
					toDelete.add(con);
				}
				if (con.getTargets().size() == 1
						&& Objects.equals(EcoreUtil.getURI(con.getTargets().get(0)).toString(), markerUri)) {
					toDelete.add(con);
				}
			}

		}

		// delete trace links associated with deleted artifact
		deleteTraces(toDelete);

		// recreate links that need to remain removing the deleted artifact
		recreateTraces(toUpdate, artifactToDelete);

		if (artifactToDelete != null) {
			ArtifactHelper helper = new ArtifactHelper(artifactModel);
			try {
				helper.deleteArtifact(artifactToDelete);
			} catch (IllegalStateException ex) {
				LOG.warn("Error deleting artifact.", ex);
				return;
			}
		}

		// use the persistence adapter to save the trace model and artifact
		// model
		tracePersistenceAdapter.saveModels(traceModel, artifactModel, metadataModel);

		// delete Marker
		try {
			marker.delete();
		} catch (CoreException e) {
			LOG.warn("Could not delete marker.", e);
		}

	}

	private void deleteTraces(List<Connection> toDelete) {
		if (toDelete.isEmpty()) {
			return;
		}
		IAdaptable adapter = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart()
				.getSite();
		DeleteTraceOperation deleteTraceOperation = new DeleteTraceOperation(TRACE_DELETION_TITLE, toDelete);
		deleteTraceOperation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);

		IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
		try {
			operationHistory.execute(deleteTraceOperation, null, adapter);
		} catch (ExecutionException e) {
			// Deliberately do nothing. Errors should be caught by the operation.
		}
	}

	private void recreateTraces(List<Connection> toUpdate, EObject artifactToDelete) {
		if (toUpdate.isEmpty()) {
			return;
		}
		IAdaptable adapter = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart()
				.getSite();
		IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
		for (Connection c : toUpdate) {
			List<EObject> newOrigins = new ArrayList<>(c.getOrigins());
			newOrigins.remove(artifactToDelete);

			List<EObject> newTargets = new ArrayList<>(c.getTargets());
			newTargets.remove(artifactToDelete);

			newOrigins.remove(artifactToDelete);
			CreateTraceOperation createTraceOperation = new CreateTraceOperation("Create trace link", newOrigins,
					newTargets);
			createTraceOperation.setChooseTraceType((traceTypes, selection) -> {
				return Optional.of(c.getTlink().eClass());
			});
			createTraceOperation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
			try {
				operationHistory.execute(createTraceOperation, null, adapter);
			} catch (ExecutionException e) {
				// Deliberately do nothing. Exceptions are caught by the operation.
			}

		}
	}
}
