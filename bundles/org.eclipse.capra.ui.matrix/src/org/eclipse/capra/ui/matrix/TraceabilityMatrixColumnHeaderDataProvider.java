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
 * Provides the names of the corresponding artifacts for the headers of the
 * columns in the traceability matrix.
 * 
 * @author Fredrik Johansson
 * @author Themistoklis Ntoukolis
 * @author Jan-Philipp Steghöfer
 */
public class TraceabilityMatrixColumnHeaderDataProvider implements IDataProvider {

	private List<TraceabilityMatrixEntryData> columnData = new ArrayList<>();

	public TraceabilityMatrixColumnHeaderDataProvider(List<TraceabilityMatrixEntryData> data,
			ArtifactHelper artifactHelper) {
		for (TraceabilityMatrixEntryData next : data) {
			this.columnData.add(next);
		}
	}

	@Override
	public int getColumnCount() {
		return columnData.size();
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
		if (columnIndex < 0 || columnIndex >= this.columnData.size()) {
			return null;
		}
		return this.columnData.get(columnIndex);
	}

	@Override
	public void setDataValue(int arg0, int arg1, Object arg2) {
		throw new UnsupportedOperationException();
	}

}
