package org.eclipse.capra.ui.office.table;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;

/**
 * Class for setting the Column Header Layer Stack for the NatTable Office View.
 * 
 * @author Mihaela Grubii
 */
public class ColumnHeaderLayerStack extends AbstractLayerTransform {

	public ColumnHeaderLayerStack(IDataProvider dataProvider, BodyLayerStack bodyLayer) {
		DataLayer dataLayer = new DataLayer(dataProvider);
		ColumnHeaderLayer colHeaderLayer = new ColumnHeaderLayer(dataLayer, bodyLayer, bodyLayer.getSelectionLayer());
		setUnderlyingLayer(colHeaderLayer);
	}

}
