/*******************************************************************************
 * Copyright (c) 2016, 2020 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *   
 * SPDX-License-Identifier: EPL-2.0
 *   
 * Contributors:
 *     Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *     Chalmers | University of Gothenburg - additional features, updated API
 *     Fredrik Johansson and Themistoklis Ntoukolis - initial implementation of the Matrix View
 *******************************************************************************/
package org.eclipse.capra.ui.matrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.IPersistenceAdapter;
import org.eclipse.capra.core.adapters.ITraceabilityInformationModelAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.notification.CapraNotificationHelper;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * The data provider for the cells of the traceability matrix.
 * 
 * @author Fredrik Johansson
 * @author Themistoklis Ntoukolis
 * @author Jan-Philipp Steghöfer
 *
 */
public class TraceabilityMatrixDataProvider implements IDataProvider {

	private ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
	private IPersistenceAdapter persistenceAdapter = ExtensionPointHelper.getPersistenceAdapter().get();

	/**
	 * A comparator that provides a lexicographical ordering of the class names of
	 * the artifact handlers that take care of the {@link EObject} instances.
	 * 
	 * Note: this comparator imposes orderings that are inconsistent with equals
	 */
	private class ArtifactHandlerClassNameComparator implements Comparator<Object> {

		@Override
		@SuppressWarnings("unchecked")
		public int compare(Object o1, Object o2) {
			if (o1 == null && o2 == null) {
				return 0;
			} else if (o1 == null || o2 == null) {
				return o1 == null ? 1 : -1;
			}
			EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
			ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
			IArtifactHandler<Object> o1Handler = (IArtifactHandler<Object>) artifactHelper
					.getHandler(artifactHelper.unwrapWrapper(o1)).orElse(null);
			IArtifactHandler<Object> o2Handler = (IArtifactHandler<Object>) artifactHelper
					.getHandler(artifactHelper.unwrapWrapper(o2)).orElse(null);
			if (o1Handler != null && o2Handler != null) {
				return o1Handler.getClass().getSimpleName().compareTo(o2Handler.getClass().getSimpleName());
			} else if (o1Handler == null && o2Handler == null) {
				return 0;
			} else {
				return o1Handler == null ? 1 : -1;
			}
		}

	}

	private List<TraceabilityMatrixEntryData> rows = new ArrayList<>();
	private List<TraceabilityMatrixEntryData> columns = new ArrayList<>();

	/**
	 * Returns all unique artifacts of the connections as defined by {@code func},
	 * sorted by the name of the {@link IArtifactHandler} that deals with them.
	 * 
	 * @param connections connections the traces for which to retrieve the targets
	 * @param func        the function to retrieve the EObjects; can
	 *                    be @code{Connection::getOrigins}
	 *                    or @code{Connection::getTargets}
	 * @return a sorted list of {@code EObject} instances
	 */
	private Set<EObject> getSortedSetOfArtifacts(List<Connection> connections,
			Function<? super Connection, ? extends List<EObject>> func) {
		List<EObject> inserted = connections.stream().map(func).flatMap(Collection::stream)
				.collect(Collectors.toList());
		Collections.sort(inserted, new ArtifactHandlerClassNameComparator());
		return new LinkedHashSet<>(inserted);
	}

	/**
	 * Returns all unique origin artifacts of the connections, sorted by the name of
	 * the {@link IArtifactHandler} that deals with them.
	 * 
	 * @param connections the traces for which to retrieve the targets
	 * @return a sorted list of {@code EObject} instances
	 */
	private Set<EObject> getTraceOrigins(List<Connection> connections) {
		return getSortedSetOfArtifacts(connections, Connection::getOrigins);
	}

	/**
	 * Returns all unique target artifacts of the connections, sorted by the name of
	 * the {@link IArtifactHandler} that deals with them.
	 * 
	 * @param connections the traces for which to retrieve the targets
	 * @return a sorted list of {@code EObject} instances
	 */
	private Set<EObject> getTraceTargets(List<Connection> connections) {
		return getSortedSetOfArtifacts(connections, Connection::getTargets);
	}

	/**
	 * Creates a new data provider for the traceability matrix.
	 * 
	 * @param connections  the connections that should be visible in the matrix
	 * @param traceModel   the trace model in which the trace links are stored
	 * @param traceAdapter the trace meta-model adapter that provides data about the
	 *                     traces
	 */
	public TraceabilityMatrixDataProvider(List<Connection> connections, EObject traceModel,
			ITraceabilityInformationModelAdapter traceAdapter, ArtifactHelper artifactHelper) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = null;
		try {
			markers = root.findMarkers(CapraNotificationHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			return;
		}
		if (markers == null || markers.length == 0) {
			return;
		}

		// Rows contain the origins
		for (EObject element : this.getTraceOrigins(connections)) {
			if (element != null) {
				TraceabilityMatrixEntryData rowEntry = new TraceabilityMatrixEntryData(element,
						artifactHelper.getArtifactLabel(element));
				rowEntry.setConnections(traceAdapter.getConnectedElements(element, traceModel));
				findMarkers(artifactHelper, markers, element, rowEntry);
				this.rows.add(rowEntry);
			}
		}
		// Columns contain the targets
		for (EObject element : this.getTraceTargets(connections)) {
			if (element != null) {
				TraceabilityMatrixEntryData colEntry = new TraceabilityMatrixEntryData(element,
						artifactHelper.getArtifactLabel(element));
				findMarkers(artifactHelper, markers, element, colEntry);
				this.columns.add(colEntry);
			}
		}
	}

	private void findMarkers(ArtifactHelper artifactHelper, IMarker[] markers, EObject element,
			TraceabilityMatrixEntryData entry) {
		for (IMarker marker : markers) {
			try {

				if (!(Objects.equals(marker.getAttribute(CapraNotificationHelper.OLD_URI),
						artifactHelper.getArtifactLocation(element)))) {
					// TODO: this does not quite work since the marker can contain additional
					// information such as a parameter
					continue;
				}
				entry.getMarkers().add(marker);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public Object getDataValue(int colIndex, int rowIndex) {
		Connection connection = getCellConnection(colIndex, rowIndex);
		if (connection != null) {
			EObject eClass = connection.getTlink().eClass();
			return (eClass == null ? "" : ((EClass) eClass).getName());
		} else {
			return "";
		}
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public void setDataValue(int arg0, int arg1, Object arg2) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the artifacts displayed in the columns of the traceability matrix.
	 * 
	 * @return a list of all artifacts displayed as columns
	 */
	public List<TraceabilityMatrixEntryData> getColumns() {
		return columns.stream().collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Get the artifact displayed in a specific column.
	 * 
	 * @param index the index of the column whose artifact should be returned
	 * @return the artifact displayed in the column with the given index
	 */
	public TraceabilityMatrixEntryData getColumn(int index) {
		return columns.get(index);
	}

	/**
	 * Get the artifacts displayed in the rows of the traceability matrix.
	 * 
	 * @return a list of all artifacts displayed as rows
	 */
	public List<TraceabilityMatrixEntryData> getRows() {
		return rows.stream().collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Get the artifact displayed in a specific row.
	 * 
	 * @param index the index of the row whose artifact should be returned
	 * @return the artifact displayed in the row with the given index
	 */
	public TraceabilityMatrixEntryData getRow(int index) {
		return rows.get(index);
	}

	/**
	 * Gets the connection that is represented by a specific cell in the
	 * traceability matrix. The cell is identified with its row and column index.
	 * 
	 * @param column the index of the column
	 * @param row    the index of the row
	 * @return the connection represented in the chosen cell
	 */
	public Connection getCellConnection(int column, int row) {
		TraceabilityMatrixEntryData colEntry = columns.get(column);
		TraceabilityMatrixEntryData rowEntry = rows.get(row);
		for (Connection connection : rowEntry.getConnections()) {
			for (EObject target : connection.getTargets()) {
				if (!EMFHelper.hasSameIdentifier(rowEntry.getArtifact(), target)
						&& EMFHelper.hasSameIdentifier(colEntry.getArtifact(), target)) {
					return connection;
				}
			}
		}
		return null;
	}
}
