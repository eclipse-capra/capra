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

import org.eclipse.nebula.widgets.nattable.hover.HoverLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;

/**
 * Class for setting the Layer Stack for the NatTable Office View.
 * 
 * @author Mihaela Grubii, Jan-Philipp Steghöfer
 */

public class BodyLayerStack extends AbstractLayerTransform {
	private SelectionLayer selectionLayer;

	public BodyLayerStack(OfficeTableDataProvider dataProvider) {
		DataLayer bodyDataLayer = new DataLayer(dataProvider);
		HoverLayer bodyHoverLayer = new HoverLayer(bodyDataLayer);
		this.selectionLayer = new SelectionLayer(bodyHoverLayer);
		ViewportLayer viewportLayer = new ViewportLayer(this.selectionLayer);
		viewportLayer.setVerticalScrollbarEnabled(true);
		setUnderlyingLayer(viewportLayer);

		// Attach the config label accumulator to the data layer
		bodyDataLayer.setConfigLabelAccumulator(new LinkedArtifactLabelAccumulator(dataProvider));

	}

	public SelectionLayer getSelectionLayer() {
		return this.selectionLayer;
	}

}