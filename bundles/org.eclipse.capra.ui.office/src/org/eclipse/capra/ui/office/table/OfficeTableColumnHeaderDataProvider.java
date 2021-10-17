/*******************************************************************************
 * Copyright (c) 2016, 2021 Chalmers | University of Gothenburg, rt-labs and others.
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
package org.eclipse.capra.ui.office.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * Provides the names of the corresponding artifacts for the headers of the
 * columns in the office table.
 * 
 * @author Mihaela Grubii
 */

public class OfficeTableColumnHeaderDataProvider implements IDataProvider {

	private List<Object> labels = new ArrayList<>();

	/**
	 * Creates a new composite column header provider for the office data.
	 * 
	 * @param dataObjects
	 */
	public OfficeTableColumnHeaderDataProvider(List<Object> dataObjects) {
		this.labels.addAll(dataObjects);
	}

	/**
	 * Returns the column header object for the column index.
	 * 
	 * @param columnIndex
	 */
	private Object getColumnHeaderLabel(int columnIndex) {
		return this.labels.get(columnIndex);
	}

	@Override
	public int getColumnCount() {
		return labels.size();
	}

	@Override
	public int getRowCount() {
		if (this.labels.isEmpty()) {
			return this.labels.size();
		} else {
			return 1;
		}

	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
		if (columnIndex < 0 || columnIndex >= this.labels.size()) {
			return null;
		}
		return getColumnHeaderLabel(columnIndex);
	}

	@Override
	public void setDataValue(int arg0, int arg1, Object arg2) {
		throw new UnsupportedOperationException();
	}

}
