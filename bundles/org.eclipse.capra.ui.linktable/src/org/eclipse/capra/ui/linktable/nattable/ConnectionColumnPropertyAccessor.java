/*******************************************************************************
 * Copyright (c) 2023 Jan-Philipp Steghöfer
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *   
 * SPDX-License-Identifier: EPL-2.0
 *   
 * Contributors:
 *     Jan-Philipp Steghöfer - initial implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.ui.linktable.nattable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.ui.adapters.ConnectionAdapter;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * Simple accessor for {@link Connection} instances that delegates data
 * retrieval to the connection's property source.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class ConnectionColumnPropertyAccessor implements IColumnPropertyAccessor<Connection> {

	private List<String> columns = new ArrayList<String>();

	public ConnectionColumnPropertyAccessor(Connection archetype) {
		ConnectionAdapter adapter = new ConnectionAdapter(archetype);
		IPropertyDescriptor[] descriptors = adapter.getPropertyDescriptors();
		Arrays.stream(descriptors).forEach(d -> columns.add(d.getDisplayName()));
	}

	@Override
	public Object getDataValue(Connection rowObject, int columnIndex) {
		ConnectionAdapter adapter = new ConnectionAdapter(rowObject);
		IPropertyDescriptor[] descriptors = adapter.getPropertyDescriptors();
		Arrays.stream(descriptors).map(d -> columns.add(d.getDisplayName()));
		return adapter.getPropertyValue(this.getColumnProperty(columnIndex));
	}

	@Override
	public void setDataValue(Connection rowObject, int columnIndex, Object newValue) {
		throw new UnsupportedOperationException();

	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public String getColumnProperty(int columnIndex) {
		return columns.get(columnIndex);
	}

	@Override
	public int getColumnIndex(String propertyName) {
		return columns.indexOf(propertyName);
	}

	public List<String> getColumns() {
		return columns;
	}

}
