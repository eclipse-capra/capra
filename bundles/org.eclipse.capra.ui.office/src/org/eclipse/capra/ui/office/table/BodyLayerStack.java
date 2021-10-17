package org.eclipse.capra.ui.office.table;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.hover.HoverLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;

/**
 * Class for setting the Layer Stack for the NatTable Office View.
 * 
 * @author Mihaela Grubii
 */

public class BodyLayerStack extends AbstractLayerTransform {
	private SelectionLayer selectionLayer;

	public BodyLayerStack(IDataProvider dataProvider) {
		DataLayer bodyDataLayer = new DataLayer(dataProvider);
		HoverLayer bodyHoverLayer = new HoverLayer(bodyDataLayer);
		this.selectionLayer = new SelectionLayer(bodyHoverLayer);
		ViewportLayer viewportLayer = new ViewportLayer(this.selectionLayer);
		viewportLayer.setVerticalScrollbarEnabled(true);
		setUnderlyingLayer(viewportLayer);

	}

	public SelectionLayer getSelectionLayer() {
		return this.selectionLayer;
	}

}