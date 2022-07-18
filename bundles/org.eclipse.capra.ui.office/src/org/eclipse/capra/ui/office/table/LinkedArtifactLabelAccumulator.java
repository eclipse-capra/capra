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

import org.eclipse.capra.ui.office.model.CapraOfficeObject;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;

/**
 * Provides config labels for incoming and outgoing links of a row's underlying
 * {@link CapraOfficeObject}. If an office object has incoming links, sets the
 * {@link LinkedArtifactLabelAccumulator#INCOMING_LINK_LABEL} label, if it has
 * outgoing links, sets
 * {@link LinkedArtifactLabelAccumulator#OUTGOING_LINK_LABEL}.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class LinkedArtifactLabelAccumulator implements IConfigLabelAccumulator {

	/**
	 * Label signifying that a {@link CapraOfficeObject} has incoming links, i.e.,
	 * that it is the target of existing traceability links.
	 */
	public static String INCOMING_LINK_LABEL = "INCOMING_LINK_LABEL";

	/**
	 * Label signifying that a {@link CapraOfficeObject} has outgoing links, i.e.,
	 * that it is the source of existing traceability links.
	 */
	public static String OUTGOING_LINK_LABEL = "OUTGOING_LINK_LABEL";

	private OfficeTableDataProvider dataProvider;

	/**
	 * Instantiate a new {@code LinkedArtifactLabelAccumulator}.
	 * 
	 * @param dataProvider the data provider used to retrieve the incoming and
	 *                     outgoing trace links.
	 */
	public LinkedArtifactLabelAccumulator(OfficeTableDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	@Override
	public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
		// We need to add 1 to the selected row since we ignore the header row
		if (!dataProvider.getOutgoingConnectionsForRow(rowPosition + 1).isEmpty()) {
			configLabels.addLabel(OUTGOING_LINK_LABEL);
		}
		if (!dataProvider.getIncomingConnectionsForRow(rowPosition + 1).isEmpty()) {
			configLabels.addLabel(INCOMING_LINK_LABEL);
		}

	}

}
