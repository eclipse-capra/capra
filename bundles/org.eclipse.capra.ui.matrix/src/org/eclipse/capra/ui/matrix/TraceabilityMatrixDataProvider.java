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
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.capra.core.helpers.TraceHelper;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
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

	private class EntryData {
		public EObject artifact;
		public List<Connection> connections;

		public EntryData(EObject artifact) {
			this.artifact = artifact;
		}
	}

	private List<EntryData> rows = new ArrayList<>();
	private List<EntryData> columns = new ArrayList<>();

	/**
	 * Creates a new data provider for the traceability matrix.
	 * 
	 * @param connections  the connections that should be visible in the matrix
	 * @param traceModel   the trace model in wich the trace links are stored
	 * @param traceAdapter the trace meta-model adapter that provides data about the
	 *                     traces
	 */
	public TraceabilityMatrixDataProvider(List<Connection> connections, EObject traceModel,
			TraceMetaModelAdapter traceAdapter) {
		for (EObject element : TraceHelper.getTracedElements(connections)) {
			EntryData colEntry = new EntryData(element);
			colEntry.connections = traceAdapter.getConnectedElements(element, traceModel);
			this.columns.add(colEntry);
			EntryData rowEntry = new EntryData(element);
			this.rows.add(rowEntry);
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
	public List<EObject> getColumns() {
		return columns.stream().map(e -> e.artifact).collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Get the artifact displayed in a specific column.
	 * 
	 * @param index the index of the column whose artifact should be returned
	 * @return the artifact displayed in the column with the given index
	 */
	public EObject getColumn(int index) {
		return columns.get(index).artifact;
	}

	/**
	 * Get the artifacts displayed in the rows of the traceability matrix.
	 * 
	 * @return a list of all artifacts displayed as rows
	 */
	public List<EObject> getRows() {
		return rows.stream().map(e -> e.artifact).collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Get the artifact displayed in a specific row.
	 * 
	 * @param index the index of the row whose artifact should be returned
	 * @return the artifact displayed in the row with the given index
	 */
	public EObject getRow(int index) {
		return rows.get(index).artifact;
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
		EntryData colEntry = columns.get(column);
		EntryData rowEntry = columns.get(row);
		for (Connection connection : colEntry.connections) {
			for (EObject target : connection.getTargets()) {
				if (!EMFHelper.hasSameIdentifier(colEntry.artifact, target)
						&& EMFHelper.hasSameIdentifier(rowEntry.artifact, target)) {
					return connection;
				}
			}
		}
		return null;
	}

}
