package org.eclipse.capra.ui.office.utils;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Menu;

/**
 * Create a cell pop-up menu for the context menu.The constructor will require
 * the MenuContext and the valid selection layer. The pop-up will appear a
 * little bit bellow the selected cells.
 * 
 * @author Mihaela Grubii
 *
 */
public class CellPopupMenuAction implements IMouseAction {

	private final Menu menu;
	private final SelectionLayer selectionLayer;

	public CellPopupMenuAction(Menu menu, SelectionLayer selectionLayer) {
		this.menu = menu;
		this.selectionLayer = selectionLayer;
	}

	@Override
	public void run(NatTable natTable, MouseEvent event) {
		if (selectionLayer.getSelectedRowCount() <= 1) {
			int colPosition = LayerUtil.convertColumnPosition(natTable, natTable.getColumnPositionByX(event.x),
					selectionLayer);
			int rowPosition = LayerUtil.convertRowPosition(natTable, natTable.getRowPositionByY(event.y),
					selectionLayer);

			natTable.doCommand(new SelectCellCommand(selectionLayer, colPosition, rowPosition, false, false));
		}
		menu.setData(event.data);
		menu.setVisible(true);
	}
}