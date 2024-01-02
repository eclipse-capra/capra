/*******************************************************************************
 * Copyright (c) 2016-2024 Chalmers | University of Gothenburg, rt-labs and others.
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

import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * Provides data for the headers of rows in the traceability matrix.
 * 
 * @author Fredrik Johansson
 * @author Themistoklis Ntoukolis
 * @author Jan-Philipp Steghöfer
 *
 */
public class TraceabilityMatrixRowHeaderDataProvider implements IDataProvider {

	private List<TraceabilityMatrixEntryData> rowData = new ArrayList<>();

	/**
	 * Creates a new data provider for row headers in a traceability matrix. The
	 * data that is provided will be used to generate the labels for the row
	 * headers.
	 * 
	 * @param data a list with the information about the row headers
	 */
	public TraceabilityMatrixRowHeaderDataProvider(List<TraceabilityMatrixEntryData> data,
			ArtifactHelper artifactHelper) {
		for (TraceabilityMatrixEntryData next : data) {
			this.rowData.add(next);
		}
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public int getRowCount() {
		return rowData.size();
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
		return rowData.get(rowIndex);
	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		throw new UnsupportedOperationException();
	}

}
