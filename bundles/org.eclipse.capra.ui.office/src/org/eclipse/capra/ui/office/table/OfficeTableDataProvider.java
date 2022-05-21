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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.capra.ui.office.model.CapraOfficeObject;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * The data provider for the cells of the office table.
 * 
 * @author Mihaela Grubii
 */
public class OfficeTableDataProvider implements IDataProvider {

	private class EntryData {
		private String officeObject;

		public EntryData(String officeObject) {
			this.officeObject = officeObject;
		}
	}

	private List<EntryData> rows = new ArrayList<>();
	private List<EntryData> columns = new ArrayList<>();
	private List<CapraOfficeObject> officeObjects;
	private Map<String, List<String>> cellValue = new HashMap<String, List<String>>();
	private Map<Integer, CapraOfficeObject> mappedObjects = new HashMap<Integer, CapraOfficeObject>();

	/**
	 * Creates a new data provider for the office data.
	 * 
	 * @param officeObjects
	 */
	public OfficeTableDataProvider(List<CapraOfficeObject> officeObjects) {
		this.officeObjects = officeObjects;
		int count = 0;
		for (CapraOfficeObject element : officeObjects) {
			EntryData rowEntry = new EntryData(getAllCapraOfficeObjectsIDForRow(element));
			mappedObjects.put(count, element);
			List<String> cols = new ArrayList<>();
			if (count == 0) {
				getAllCapraOfficeObjectsForRow(element).forEach(v -> {
					this.columns.add(new EntryData(v));
				});
			} else {
				getAllCapraOfficeObjectsForRow(element).forEach(v -> {
					cols.add(v);
				});
				cellValue.put(getAllCapraOfficeObjectsIDForRow(element), cols);
				this.rows.add(rowEntry);
			}
			count++;
		}
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	private List<String> getAllCapraOfficeObjectsForRow(CapraOfficeObject obj) {
		List<String> cellValue = Arrays.asList(obj.getData().split("[|:]"));
		List<String> elements = new ArrayList<>();
		elements.addAll(cellValue);
		elements.remove(0);
		return elements;
	}

	private String getAllCapraOfficeObjectsIDForRow(CapraOfficeObject obj) {
		List<String> cellValue = Arrays.asList(obj.getData().split(":"));
		return cellValue.get(0);
	}

	@Override
	public Object getDataValue(int colIndex, int rowIndex) {
		EntryData rowEntry = rows.get(rowIndex);
		return cellValue.get(rowEntry.officeObject).get(colIndex);
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
	 * Get the office Object Value displayed in the columns of the office view
	 * table.
	 * 
	 * @return a list of all office view values displayed as columns
	 */
	public List<Object> getColumns() {
		return columns.stream().map(e -> e.officeObject).collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Get the office Object Value displayed in a specific column.
	 * 
	 * @param index the index of the column whose artifact should be returned
	 * @return the office Object Value displayed in the column with the given index
	 */
	public String getColumn(int index) {
		return columns.get(index).officeObject;
	}

	/**
	 * Get the office Object Value displayed in a specific column.
	 * 
	 * @param index the index of the column whose artifact should be returned
	 * @return the office Object Value displayed in the column with the given index
	 */
	public CapraOfficeObject getColumnHighlight(int index) {
		return mappedObjects.get(index);
	}

	/**
	 * Get the office Object Value displayed in a specific row.
	 * 
	 * @param index the index of the row whose artifact should be returned
	 * @return the office Object Value displayed in the row with the given index
	 */
	public CapraOfficeObject getRowHighlight(int index) {
		return mappedObjects.get(index);
	}

	/**
	 * Get the office Object Value displayed in the rows of the office view table.
	 * 
	 * @return a list of all office view values displayed as rows
	 */
	public List<Object> getRows() {
		return rows.stream().map(e -> e.officeObject).collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Get the office Object Value displayed in a specific row.
	 * 
	 * @param index the index of the row whose artifact should be returned
	 * @return the office Object Value displayed in the row with the given index
	 */
	public String getRow(int index) {
		return rows.get(index).officeObject;
	}

	/**
	 * Get the office Object Value to be displayed.
	 * 
	 * @param index the index of the row whose artifact should be returned
	 * @return the office Object Value displayed in the row with the given index
	 */
	public List<CapraOfficeObject> getAllEllements() {
		return officeObjects;
	}

	public List<EntryData> getAllRows() {
		return rows;
	}

}
