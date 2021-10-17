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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.capra.ui.office.model.CapraOfficeObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.ColumnSelectionEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.RowSelectionEvent;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Provides the ability to perform selection actions, which is necessary to be
 * able to visualize cell selections for the row header.
 * 
 * @author Mihaela Grubii
 */
public class OfficeSelectionProvider implements ISelectionProvider, ILayerListener {

	private Set<ISelectionChangedListener> listeners = new HashSet<>();

	/**
	 * The SelectionLayer this ISelectionProvider is connected to.
	 */
	private SelectionLayer selectionLayer;

	private CapraOfficeObject selectedObject;

	private OfficeTableDataProvider dataProvider;

	/**
	 * Locally stored previous selection which is used to determine if a
	 * SelectionChangedEvent should be fired. It is used to avoid firing events if
	 * the same cell is selected again (default).
	 */
	private ISelection previousSelection;

	public OfficeSelectionProvider(SelectionLayer selectionLayer, OfficeTableDataProvider dataProvider) {
		this.updateProvider(selectionLayer, dataProvider);
	}

	@Override
	public void handleLayerEvent(ILayerEvent event) {
		if ((event instanceof CellSelectionEvent) || (event instanceof RowSelectionEvent)
				|| (event instanceof ColumnSelectionEvent)) {
			ISelection selection = getSelection();
			if ((selection != null && !selection.equals(this.previousSelection))) {
				try {
					for (ISelectionChangedListener listener : this.listeners) {
						listener.selectionChanged(new SelectionChangedEvent(this, selection));
					}
				} finally {
					this.previousSelection = selection;
				}
			}
		}
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (listener != null) {
			this.listeners.add(listener);
		}
	}

	@Override
	public ISelection getSelection() {
		if (selectionLayer.getFullySelectedRowPositions().length > 0) {
			CapraOfficeObject selectedObject = dataProvider
					.getRowHighlight(selectionLayer.getFullySelectedRowPositions()[0]);
			if (selectedObject != null) {
				this.selectedObject = selectedObject;
				return new StructuredSelection(selectedObject);
			}
		} else if (selectionLayer.getSelectedCellPositions().length > 0) {
			Rectangle row = new Rectangle(0, selectionLayer.getSelectedCellPositions()[0].rowPosition, 2147483647, 1);
			selectionLayer.addSelection(row);
			CapraOfficeObject selectedObject = dataProvider
					.getRowHighlight(selectionLayer.getFullySelectedRowPositions()[0]);
			if (selectedObject != null) {
				this.selectedObject = selectedObject;
				return new StructuredSelection(selectedObject);
			}
		}
		return null;
	}

	public CapraOfficeObject returnSelectedItem() {

		getSelection();
		return this.selectedObject;
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.remove(listener);

	}

	@Override
	public void setSelection(ISelection selection) {
	}

	/**
	 * Sets the selection layer and the data provider used by this
	 * {@code SelectionProvider} to retrieve selected data from the underlying
	 * NatTable.
	 * 
	 * @param selectionLayer the new selection layer
	 */
	public void updateProvider(SelectionLayer selectionLayer, OfficeTableDataProvider dataProvider) {
		this.selectionLayer = selectionLayer;
		this.dataProvider = dataProvider;
		this.selectionLayer.addLayerListener(this);
	}

}