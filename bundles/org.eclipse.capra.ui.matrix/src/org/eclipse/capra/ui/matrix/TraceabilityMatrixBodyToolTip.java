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
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;

/**
 * Provides the tool tip for cells in the body of the table. Displays
 * information about the link, including all artifacts that are connected by the
 * link.
 * 
 * @author Fredrik Johansson
 * @author Themistoklis Ntoukolis
 * @author Jan-Philipp Steghöfer
 */
public class TraceabilityMatrixBodyToolTip extends NatTableContentTooltip {

	private TraceabilityMatrixDataProvider dataProvider;
	private ArtifactHelper artifactHelper;

	/**
	 * Create a new tool tip for the cells in the body of the table.
	 * 
	 * @param natTable       the table the tool tip should be applied to
	 * @param dataProvider   the provider of table data
	 * @param artifactHelper the provider of information about artifacts
	 */
	public TraceabilityMatrixBodyToolTip(NatTable natTable, TraceabilityMatrixDataProvider dataProvider,
			ArtifactHelper artifactHelper) {
		super(natTable, GridRegion.BODY);
		this.natTable = natTable;
		this.dataProvider = dataProvider;
		this.artifactHelper = artifactHelper;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.window.ToolTip#getToolTipArea(org.eclipse.swt.widgets
	 * .Event)
	 *
	 * Implementation here means the tooltip is not redrawn unless mouse hover moves
	 * outside of the current cell (the combination of ToolTip.NO_RECREATE style and
	 * override of this method).
	 */
	@Override
	protected Object getToolTipArea(Event event) {
		int col = this.natTable.getColumnIndexByPosition(this.natTable.getColumnPositionByX(event.x) - 1);
		int row = this.natTable.getRowIndexByPosition(this.natTable.getRowPositionByY(event.y) - 1);
		return new Point(col, row);
	}

	@Override
	protected String getText(Event event) {
		int col = this.natTable.getColumnIndexByPosition(this.natTable.getColumnPositionByX(event.x));
		int row = this.natTable.getRowIndexByPosition(this.natTable.getRowPositionByY(event.y));
		Connection connection = dataProvider.getCellConnection(col, row);
		if (connection != null) {
			EObject eClass = connection.getTlink().eClass();
			String traceType = (eClass == null ? "" : ((EClass) eClass).getName());
			Set<String> artifactNames = new LinkedHashSet<>();
			artifactNames.addAll(connection.getOrigins().stream().map(a -> artifactHelper.getArtifactLabel(a))
					.collect(Collectors.toCollection(ArrayList::new)));
			artifactNames.addAll(connection.getTargets().stream().map(a -> artifactHelper.getArtifactLabel(a))
					.collect(Collectors.toCollection(ArrayList::new)));
			StringBuilder tooltipBuilder = new StringBuilder();
			tooltipBuilder.append("Trace link \"").append(traceType).append("\":\n");
			tooltipBuilder.append(artifactNames.stream().collect(Collectors.joining("\n")));
			return tooltipBuilder.toString();
		}
		return null;
	}

}
