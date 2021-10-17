package org.eclipse.capra.ui.office.table;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;

/**
 * Class for setting the Row Header Layer Stack for the NatTable Office View.
 * 
 * @author Mihaela Grubii
 */

public class RowHeaderLayerStack extends AbstractLayerTransform {

	public RowHeaderLayerStack(IDataProvider dataProvider, BodyLayerStack bodyLayer) {
		DataLayer dataLayer = new DataLayer(dataProvider, 50, 20);
		RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(dataLayer, bodyLayer, bodyLayer.getSelectionLayer());
		setUnderlyingLayer(rowHeaderLayer);
	}
}
