/*******************************************************************************
 * Copyright (c) 2016-2022 Chalmers | University of Gothenburg, rt-labs and others.
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

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;

/**
 * Provides tool tips that include the incoming and outgoing trace links of a
 * cell. Uses the underlying {@link NatTableContentTooltip} to first render the
 * "normal" content-based tool tip before adding the links if any are found.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class LinkedArtifactToolTip extends NatTableContentTooltip {

	private final OfficeTableDataProvider dataProvider;

	/**
	 * Create a new {@code LinkedArtifactToolTip}. Calls
	 * {@link NatTableContentTooltip#NatTableContentTooltip(NatTable, String...)} as
	 * a backing implementation.
	 * 
	 * @param natTable       The {@link NatTable} instance for which this
	 *                       {@link ToolTip} is used.
	 * @param dataProvider   The {@link OfficeTableDataProvider} instance which is
	 *                       used to retrieve information about the incoming and
	 *                       outgoing trace links.
	 * @param tooltipRegions The regions of the {@link NatTable} for which this
	 *                       {@link ToolTip} is active. If none are given, the
	 *                       tooltip will be active for all regions.
	 */
	public LinkedArtifactToolTip(NatTable natTable, OfficeTableDataProvider dataProvider, String... tooltipRegions) {
		super(natTable, tooltipRegions);
		this.dataProvider = dataProvider;
	}

	@Override
	protected String getText(Event event) {
		String tooltip = super.getText(event);
		if (tooltip != null) {
			tooltip = tooltip.trim();
		}
		int col = this.natTable.getColumnPositionByX(event.x);
		int row = this.natTable.getRowPositionByY(event.y);

		ILayerCell cell = this.natTable.getCellByPosition(col, row);
		if (cell != null) {
			List<Connection> links = new ArrayList<>();
			// Checking the labels is faster than checking the connections for each cell
			if (cell.getConfigLabels().getLabels().contains(LinkedArtifactLabelAccumulator.OUTGOING_LINK_LABEL)) {
				links.addAll(dataProvider.getOutgoingConnectionsForRow(row));
			}
			if (cell.getConfigLabels().getLabels().contains(LinkedArtifactLabelAccumulator.INCOMING_LINK_LABEL)) {
				links.addAll(dataProvider.getIncomingConnectionsForRow(row));
			}
			if (!links.isEmpty()) {
				StringBuilder builder = new StringBuilder(tooltip);
				if (!tooltip.isBlank()) {
					builder.append("\n\n");
				}
				builder.append("Links:\n");
				for (Connection con : links) {
					builder.append(" * ");
					builder.append(EMFHelper.getIdentifier(con.getTlink()));
					builder.append("\n");
				}
				tooltip = builder.toString().stripTrailing();
			}
		}
		return tooltip;
	}
}
