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

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;

/**
 * Provides a tool tip when mousing over a header in one the columns in a
 * traceability matrix. The tool tip contains information about the location of
 * the artifact and when it was last changed.
 * 
 * @author Fredrik Johansson
 * @author Themistoklis Ntoukolis
 * @author Jan-Philipp Steghöfer
 */
public class TraceabilityMatrixHeaderToolTip extends NatTableContentTooltip {

	private TraceabilityMatrixDataProvider dataProvider;
	private ArtifactHelper artifactHelper;

	public TraceabilityMatrixHeaderToolTip(NatTable natTable, TraceabilityMatrixDataProvider dataProvider,
			ArtifactHelper artifactHelper) {
		super(natTable, GridRegion.COLUMN_HEADER);
		this.natTable = natTable;
		this.dataProvider = dataProvider;
		this.artifactHelper = artifactHelper;
	}

	@Override
	protected String getText(Event event) {
		int col = this.natTable.getColumnIndexByPosition(this.natTable.getColumnPositionByX(event.x));
		EObject artifact = dataProvider.getColumn(col);
		StringBuilder tooltipBuilder = new StringBuilder();
		tooltipBuilder.append(artifactHelper.getArtifactLabel(artifact));
		tooltipBuilder.append("\nLocated at:\n");
		tooltipBuilder.append(artifactHelper.getArtifactLocation(artifact));

		URI uri;
		try {
			uri = new URI(artifactHelper.getArtifactLocation(artifact));
			IPath path = new Path(uri.getPath());
			path = path.removeFirstSegments(1);
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if (file != null) {
				if (file.exists()) {
					tooltipBuilder.append("\nLast modified: ");
					LocalDate date = Instant.ofEpochMilli(file.getLocalTimeStamp()).atZone(ZoneId.systemDefault())
							.toLocalDate();
					tooltipBuilder.append(date.format(
							DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(new Locale("no", "NO"))));
				} else {
					tooltipBuilder.append("\nArtifact does not exist.");
				}
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tooltipBuilder.toString();
	}
}
