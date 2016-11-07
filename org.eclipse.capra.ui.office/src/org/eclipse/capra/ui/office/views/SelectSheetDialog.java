package org.eclipse.capra.ui.office.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * A class that extends Dialog in order to prompt the user for which excel sheet
 * to display.
 * 
 * Code adapted from:
 * http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/Howtocreateyourowndialogclasses.htm
 * 
 * @author Dusan Kalanj
 *
 */
public class SelectSheetDialog extends Dialog {

	/**
	 * Strings that are displayed in the Dialog.
	 */
	private static final String PROMPT_MESSAGE = "Please select the sheet to display.";
	private static final String SELECT_TIP = "Click here to select the sheet.";
	private static final String OK = "OK";
	private static final String CANCEL = "Cancel";

	/**
	 * Top margin between elements in the dialog.
	 */
	private static final int VERTICAL_INDENT = 10;

	private String[] sheetNames;
	private String selectedSheetName;

	/**
	 * SelectSheetDialog constructor.
	 * 
	 * @param parentShell
	 *            the shell that will accommodate the dialog.
	 * @param style
	 *            the style used by the dialog
	 * @param currentSheet
	 *            the currently displayed sheet
	 * @param totalNumOfSheets
	 *            the sheet that was selected in the dialog.
	 */
	public SelectSheetDialog(Shell parentShell, int style, String[] sheetNames) {
		super(parentShell, style);
		this.sheetNames = sheetNames;
		this.selectedSheetName = sheetNames[0];
	}

	/**
	 * Opens the dialog and returns the index of the selected sheet.
	 * 
	 * @return the number of the sheet to display
	 */
	public String open() {
		Shell shell = new Shell(getParent(), getStyle());
		createContents(shell);

		Rectangle shellBounds = getParent().getBounds();

		shell.setLocation(shellBounds.x + shellBounds.width / 2, shellBounds.y + shellBounds.height / 2);
		shell.pack();
		shell.open();

		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return selectedSheetName;
	}

	/**
	 * Fills the dialog window with content.
	 * 
	 * @param shell
	 *            the window of the dialog
	 */
	private void createContents(final Shell shell) {

		GridLayout gridLayout = new GridLayout(2, true);
		shell.setLayout(gridLayout);

		Label label = new Label(shell, SWT.NONE);
		label.setText(PROMPT_MESSAGE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		final ToolBar toolBar = new ToolBar(shell, SWT.NONE);
		data = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		data.verticalIndent = VERTICAL_INDENT;
		data.horizontalSpan = 2;
		toolBar.setLayoutData(data);
		final ToolItem itemDropDown = new ToolItem(toolBar, SWT.DROP_DOWN);
		final Menu menu = new Menu(shell, SWT.POP_UP);

		itemDropDown.setText(sheetNames[0]);
		itemDropDown.setToolTipText(SELECT_TIP);

		for (int i = 0; i < sheetNames.length; i++) {
			MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText(sheetNames[i]);
			item.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					selectedSheetName = item.getText();
					itemDropDown.setText(selectedSheetName);

					toolBar.pack();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					selectedSheetName = item.getText();
					itemDropDown.setText(selectedSheetName);
					toolBar.pack();
				}
			});
		}

		itemDropDown.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				Rectangle bounds = itemDropDown.getBounds();
				Point point = toolBar.toDisplay(bounds.x, bounds.y + bounds.height);
				menu.setLocation(point);
				menu.setVisible(true);
			}
		});

		Button ok = new Button(shell, SWT.PUSH);
		ok.setText(OK);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.verticalIndent = VERTICAL_INDENT;
		data.horizontalSpan = 1;
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText(CANCEL);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.verticalIndent = VERTICAL_INDENT;
		data.horizontalSpan = 1;
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				selectedSheetName = sheetNames[0];
				shell.close();
			}
		});

		shell.setDefaultButton(ok);
	}
}
