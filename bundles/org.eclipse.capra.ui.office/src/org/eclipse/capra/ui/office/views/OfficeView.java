/*******************************************************************************
 * Copyright (c) 2016, 2021 Chalmers | University of Gothenburg, rt-labs and others.
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

package org.eclipse.capra.ui.office.views;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.SchemaTypeLoaderException;
import org.eclipse.capra.ui.office.Activator;
import org.eclipse.capra.ui.office.exceptions.CapraOfficeFileNotSupportedException;
import org.eclipse.capra.ui.office.exceptions.CapraOfficeObjectNotFound;
import org.eclipse.capra.ui.office.model.CapraExcelRow;
import org.eclipse.capra.ui.office.model.CapraGoogleSheetsRow;
import org.eclipse.capra.ui.office.model.CapraOfficeObject;
import org.eclipse.capra.ui.office.model.CapraWordRequirement;
import org.eclipse.capra.ui.office.preferences.OfficePreferences;
import org.eclipse.capra.ui.office.table.BodyLayerStack;
import org.eclipse.capra.ui.office.table.ColumnHeaderLayerStack;
import org.eclipse.capra.ui.office.table.OfficeSelectionProvider;
import org.eclipse.capra.ui.office.table.OfficeTableColumnHeaderDataProvider;
import org.eclipse.capra.ui.office.table.OfficeTableDataProvider;
import org.eclipse.capra.ui.office.table.OfficeTableRowHeaderDataProvider;
import org.eclipse.capra.ui.office.table.RowHeaderLayerStack;
import org.eclipse.capra.ui.office.utils.CapraOfficeUtils;
import org.eclipse.capra.ui.office.utils.CellPopupMenuAction;
import org.eclipse.capra.ui.office.utils.OfficeSourceProvider;
import org.eclipse.capra.ui.office.utils.OfficeTransferType;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.LineBorderDecorator;
import org.eclipse.nebula.widgets.nattable.reorder.RowReorderLayer;
import org.eclipse.nebula.widgets.nattable.resize.action.ColumnResizeCursorAction;
import org.eclipse.nebula.widgets.nattable.resize.event.ColumnResizeEventMatcher;
import org.eclipse.nebula.widgets.nattable.resize.mode.ColumnResizeDragMode;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.ISourceProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

/**
 * Provides a Capra perspective view for displaying the contents of Excel and
 * Word documents. The view displays the contents if the user drags an Excel or
 * a Word document into the surface.
 *
 * @author Dusan Kalanj, Mihaela Grubii
 *
 */
public class OfficeView extends ViewPart {

	private static final Logger LOG = LoggerFactory.getLogger(OfficeView.class);

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.eclipse.capra.ui.views.OfficeView";

	/**
	 * The caption that is shown when a message dialog appears describing an error.
	 */
	private static final String ERROR_TITLE = "Error";

	/**
	 * The URL for the Bugzilla Office handler page
	 */
	private static final String BUGZILLA_OFFICE_URL = "https://bugs.eclipse.org/bugs/show_bug.cgi?id=503313#add_comment";

	/**
	 * The collection that contains the Excel/Word contents.
	 */
	private List<CapraOfficeObject> selection = new ArrayList<>();

	/**
	 * The names (String) of all the sheets, contained in the selected workbook and
	 * information about whether they are empty or not (Boolean).
	 */
	private Map<String, Boolean> isSheetEmptyMap;

	/**
	 * The name of the sheet that is currently displayed in the Office view.
	 */
	private String selectedSheetName;

	/**
	 * The file that is currently displayed in the view.
	 */
	private File selectedFile;

	/**
	 * The ID of the file that is currently displayed in the view (non-null only if
	 * acquired from Google Drive).
	 */
	private String selectedFileId;

	/**
	 * The parent control represents the upper most view in which the table will be
	 * displayed.
	 */
	private Composite parent;

	/**
	 * The NatTable table instance that is being manipulated(populated, redrawn,
	 * cleared, re-sized) within the view.
	 */
	private NatTable officeTable;

	/**
	 * The data provider used to populate and transfer data for the office table.
	 */
	private OfficeTableDataProvider bodyDataProvider;

	/**
	 * The selection provider keeps truck of the currently selected layer.
	 */
	private OfficeSelectionProvider selectionProvider;

	/**
	 * The lowermost layer in the stack that is responsible for providing data to
	 * the grid.
	 */
	private BodyLayerStack bodyLayer;

	/**
	 * Instance of OfficeSourceProvider (used for hiding context menu options)
	 */
	private OfficeSourceProvider provider = (OfficeSourceProvider) PlatformUI.getWorkbench()
			.getService(ISourceProviderService.class).getSourceProvider(OfficeSourceProvider.CAPRA_OFFICE_OBJECT);

	/**
	 * The content provider class used by the view.
	 */
	class ViewContentProvider implements IStructuredContentProvider {
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			// We do not need to react to this event.
		}

		@Override
		public void dispose() {
			officeTable.dispose();
			officeTable = null;

		}

		@Override
		public Object[] getElements(Object parent) {
			return selection.toArray();
		}
	}

	/**
	 * The label provider class used by the view.
	 */
	static class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public String getText(Object obj) {
			int minAllowed = Activator.getDefault().getPreferenceStore().getInt(OfficePreferences.CHAR_COUNT);
			String text = obj.toString();
			int textLength = Math.min(text.length(), minAllowed);
			if (textLength == minAllowed) {
				text = text.substring(0, textLength) + "...";
			}
			return text;
		}

		@Override
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		@Override
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * The class that processes the drag and drop support.
	 */
	class DragAndDropSupport implements DragSourceListener, DropTargetListener {

		private final NatTable officeTable;
		private final SelectionLayer selectionLayer;

		public DragAndDropSupport(NatTable natTable, SelectionLayer selectionLayer, List<CapraOfficeObject> data) {
			this.officeTable = natTable;
			this.selectionLayer = selectionLayer;
		}

		@Override
		public void dragEnter(DropTargetEvent event) {
			event.detail = DND.DROP_COPY;
		}

		@Override
		public void dragLeave(DropTargetEvent event) {
			// Deliberately do nothing
		}

		@Override
		public void dragOperationChanged(DropTargetEvent event) {
			// Deliberately do nothing
		}

		@Override
		public void dragOver(DropTargetEvent event) {
			String[] data = (String[]) event.data;
			if (data != null) {
				File file = new File(data[0]);
				event.data = file;
			}
		}

		@Override
		public void drop(DropTargetEvent event) {
			try {
				if (event.data instanceof String[]) {
					String[] data = (String[]) event.data;
					File file = new File(data[0]);
					if (file.exists()) {
						parseGenericFile(file);
					}
				}
			} catch (CapraOfficeFileNotSupportedException e) {
				LOG.debug("Capra does not support this file.", e);
				showErrorMessage(ERROR_TITLE, e.getMessage(), null);
			}
			try {
				this.officeTable.refresh();
			} catch (SWTException e) {
				LOG.debug("Office table failed to refresh.", e);
			}

		}

		@Override
		public void dropAccept(DropTargetEvent event) {
			// Deliberately do nothing
		}

		@Override
		public void dragStart(DragSourceEvent event) {
			if (this.selectionLayer.getSelectedRowCount() == 0) {
				event.doit = false;
			} else if (!this.officeTable.getRegionLabelsByXY(event.x, event.y).hasLabel(GridRegion.BODY)) {
				event.doit = false;
			}
		}

		@Override
		public void dragSetData(DragSourceEvent event) {

			if (OfficeTransferType.getInstance().isSupportedType(event.dataType)) {

				List<CapraOfficeObject> tr = new ArrayList<CapraOfficeObject>();
				// Add one to the row position to account for the header row.
				tr.add(bodyDataProvider.getAllEllements().get(selectionLayer.getFullySelectedRowPositions()[0]+1));
				event.data = tr;

			}
		}

		@Override
		public void dragFinished(DragSourceEvent event) {
			// Deliberately do nothing
		}

	}

	@Override
	public void setFocus() {
		this.officeTable.setFocus();
	}

	@Override
	public void createPartControl(Composite parent) {

		this.parent = parent;
		updateOfficeObject();

	}

	/**
	 * A method that is called when the user drags file (word or excel) into the
	 * OfficeView. Its main task is to parse the dragged file and display its
	 * contents in the OfficeView. It only parses the file if it is of type xlsx,
	 * xls, or docx.
	 *
	 * @param data the object that was dragged into the view
	 * @throws CapraOfficeFileNotSupportedException
	 */
	public void parseGenericFile(File file) throws CapraOfficeFileNotSupportedException {
		String fileExtension = Files.getFileExtension(file.getName());

		if (fileExtension.equals(CapraOfficeObject.XLSX) || fileExtension.equals(CapraOfficeObject.XLS)) {
			parseExcelDocument(file, null, null);
			updateOfficeObject();
		} else if (fileExtension.equals(CapraOfficeObject.DOCX)) {
			parseWordDocument(file);
		} else {
			throw new CapraOfficeFileNotSupportedException(fileExtension);
		}
	}

	/**
	 * Extracts the data from the Excel document and adds it to the view. Disposes
	 * of the current table and creates a new one based on the updated data. The
	 * method also layouts the parent to display the table correctly.
	 *
	 * @param officeFile        the File object pointing to the Excel document that
	 *                          was dragged into the view
	 * @param googleDriveFileId the id of the file from Google drive (shown in the
	 *                          URL when a user opens a file inside Google Drive).
	 *                          If provided it will be used when creating the URI of
	 *                          the objects, otherwise (if null) the path of the
	 *                          containing file will be used instead. That also
	 *                          means that, if googleDriveFileId is provided, the
	 *                          Objects in the OfficeView will be of type
	 *                          CapraGoogleSheetsRow, otherwise of type
	 *                          CapraExcelRow.
	 * @param sheetName         the name of the sheet that should be displayed in
	 *                          the Office view. If null, the currently active sheet
	 *                          will be displayed.
	 */
	public void parseExcelDocument(File officeFile, String googleDriveFileId, String sheetName) {

		// Get Excel Workbook
		Workbook workBook;
		try {
			workBook = CapraOfficeUtils.getExcelWorkbook(officeFile);
		} catch (OldExcelFormatException e) {
			showErrorMessage(ERROR_TITLE, e.getMessage(), null);
			return;
		} catch (IOException e) {
			showErrorMessage(ERROR_TITLE, "Could not open the Excel workbook.", null);
			LOG.warn("Could not open the Excel workbook", e);
			return;
		}

		// Get Excel sheet with provided sheetName from provided workBook
		Sheet sheet = CapraOfficeUtils.getSheet(workBook, sheetName);
		if (sheet == null) {
			// In theory, this could only happen if someone uses the selectSheet
			// (public) method and provides a non-valid sheetName. The method is
			// currently only used for changing the displayed sheet through the
			// tool-bar menu, where all the names are valid. TODO The best way
			// to tackle this would probably be to introduce a new exception
			// (CapraOfficeSheetNotFoundException?), but to do that, a bit of
			// reordering and partitioning of the methods would be required -
			// ideally, the selectSheet (public) method would throw the
			// exception, not this one.
			String hyperlinkMessage = "It appears that the file doesn't contain any sheets. If that is not true, please report the issue to our <a href=\""
					+ BUGZILLA_OFFICE_URL + "\"> Bugzilla project page </a> and we will do our best to resolve it.";
			showErrorMessage(ERROR_TITLE, hyperlinkMessage, BUGZILLA_OFFICE_URL);
			return;
		}

		// Check if the whole workbook (all of the sheets) is empty.
		Map<String, Boolean> isNewSheetEmptyMap = CapraOfficeUtils.getSheetsEmptinessInfo(workBook);
		if (!isNewSheetEmptyMap.values().contains(false)) {
			showErrorMessage(ERROR_TITLE, "There are no rows to display in any of the sheets.", null);
			clearSelection();
			return;
		}

		// Clear the Office view and all static variables
		clearSelection();

		// Save new values to properties
		this.selectedSheetName = sheet.getSheetName();
		this.selectedFile = officeFile;
		this.selectedFileId = googleDriveFileId;
		this.isSheetEmptyMap = isNewSheetEmptyMap;

		// Populate the view with Excel rows
		String idColumn = Activator.getDefault().getPreferenceStore().getString(OfficePreferences.EXCEL_COLUMN_VALUE);
		boolean isGoogleSheet = googleDriveFileId != null;
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row != null) {
				CapraOfficeObject cRow;
				// If the file is in the java's "temporary-file" folder, it was
				// obtained from Google drive
				if (isGoogleSheet) {
					cRow = new CapraGoogleSheetsRow(officeFile, row, idColumn, googleDriveFileId);
				} else {
					cRow = new CapraExcelRow(officeFile, row, idColumn);
				}
				if (!cRow.getData().isEmpty()) {
					selection.add(cRow);
				}
			}
		}

		// Save info about the type of the data displayed in the Office view.
		if (!selection.isEmpty()) {
			provider.setResource(selection.get(0));
		}
	}

	/**
	 * Responsible for updating office objects as well as configuring the NatTable
	 * and its components.
	 */
	private void updateOfficeObject() {
		// Creating data providers for body, column and row. The data provider for the
		// body provides the data which will be shown in the cells. For columns and
		// rows, the labels are created.

		// get the bodyDataProvider
		this.bodyDataProvider = new OfficeTableDataProvider(selection);
		// Putting the data providers to their respective stacks, getting the first body
		// layer
		bodyLayer = new BodyLayerStack(bodyDataProvider);
		// create a columnHeaderDataProvider
		IDataProvider colHeaderDataProvider = new OfficeTableColumnHeaderDataProvider(bodyDataProvider.getColumns());
		// create a rowHeaderDataProvider
		IDataProvider rowHeaderDataProvider = new OfficeTableRowHeaderDataProvider(this.bodyDataProvider.getRows());
		// create a cornerDataProvider
		DefaultCornerDataProvider cornerDataProvider = new DefaultCornerDataProvider(colHeaderDataProvider,
				rowHeaderDataProvider);

		// create all the necessary layers for the NatTable
		ColumnHeaderLayerStack columnHeaderLayer = new ColumnHeaderLayerStack(colHeaderDataProvider, this.bodyLayer);
		RowHeaderLayerStack rowHeaderLayer = new RowHeaderLayerStack(rowHeaderDataProvider, this.bodyLayer);
		CornerLayer cornerLayer = new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer, columnHeaderLayer);
		DataLayer bodyDataLayer = new DataLayer(this.bodyDataProvider);

		RowReorderLayer rowReorderLayer = new RowReorderLayer(bodyDataLayer);
		SelectionLayer selectionLayer = new SelectionLayer(rowReorderLayer);

		// Putting all the layers to the grid.
		GridLayer gridLayer = new GridLayer(bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

		// Creating the NatTable
		if (this.officeTable != null) {
			this.officeTable.dispose();
		}
		this.officeTable = new NatTable(parent, gridLayer, false);

		setStyleConfigiguration();

		// adding right click ui binding
		this.officeTable.addConfiguration(new AbstractUiBindingConfiguration() {

			@Override
			public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
				uiBindingRegistry.registerMouseDownBinding(
						new MouseEventMatcher(SWT.NONE, GridRegion.BODY, MouseEventMatcher.RIGHT_BUTTON),
						new CellPopupMenuAction(hookContextMenu(), selectionLayer));
				uiBindingRegistry.registerFirstMouseMoveBinding(
						new ColumnResizeEventMatcher(SWT.NONE, GridRegion.CORNER, 0), new ColumnResizeCursorAction());
				uiBindingRegistry.registerFirstMouseDragMode(
						new ColumnResizeEventMatcher(SWT.NONE, GridRegion.CORNER, 1), new ColumnResizeDragMode());

			}
		});

		this.officeTable.setLayer(gridLayer);
		this.officeTable.refresh();
		this.officeTable.getVerticalBar().setVisible(true);
		this.officeTable.getHorizontalBar().setVisible(false);
		this.officeTable.setRedraw(true);
		this.officeTable.setVisible(true);

		this.officeTable.addConfiguration(new AbstractUiBindingConfiguration() {
			@Override
			public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
				if (!selection.isEmpty()) {
					uiBindingRegistry.registerDoubleClickBinding(MouseEventMatcher.bodyLeftClick(0),
							new DoubleClickEventAction());
				}
			}

		});

		// complete configuration
		this.officeTable.configure();

		// Fire command carrying the selected columns
		this.officeTable.redraw();

		// Attach the selection provider
		if (this.selectionProvider == null) {
			this.selectionProvider = new OfficeSelectionProvider(bodyLayer.getSelectionLayer(), this.bodyDataProvider);
		} else {
			this.selectionProvider.updateProvider(bodyLayer.getSelectionLayer(), this.bodyDataProvider);
		}
		getSite().setSelectionProvider(this.selectionProvider);

		addDragSupport();

		// Finally, make sure everything is in the right place.
		parent.layout();
	}

	private void addDragSupport() {
		DragAndDropSupport dndSupport = new DragAndDropSupport(officeTable, bodyLayer.getSelectionLayer(),
				this.selection);
		int ops = DND.DROP_COPY | DND.DROP_MOVE | DND.DragStart | DND.DragSetData | DND.DROP_LINK;
		Transfer[] transfersIn = new Transfer[] { org.eclipse.swt.dnd.FileTransfer.getInstance() };
		Transfer[] transfersOut = new Transfer[] { org.eclipse.capra.ui.office.utils.OfficeTransferType.getInstance() };
		this.officeTable.addDropSupport(ops, transfersIn, dndSupport);
		this.officeTable.addDragSupport(ops, transfersOut, dndSupport);
	}

	private void setStyleConfigiguration() {
		this.officeTable.addConfiguration(new AbstractRegistryConfiguration() {

			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {
				Style columnHeaderStyle = new Style();
				Style cellStyle = new Style();
				HorizontalAlignmentEnum hAlign = HorizontalAlignmentEnum.LEFT;
				ICellPainter cellPainter = new LineBorderDecorator(new TextPainter(false, true, 5, true));

				columnHeaderStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, hAlign);
				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, columnHeaderStyle,
						DisplayMode.NORMAL, GridRegion.COLUMN_HEADER);
				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, cellPainter);
				cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, hAlign);
				configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
						new DefaultDisplayConverter());
				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle);
			}
		});
	}

	/**
	 * Extracts the data from the Word document and adds it to the view.
	 *
	 * @param officeFile the File object pointing of the Word document that was
	 *                   dragged into the view.
	 */
	private void parseWordDocument(File officeFile) {

		List<XWPFParagraph> paragraphs;
		try {
			paragraphs = CapraOfficeUtils.getWordParagraphs(officeFile);
		} catch (IOException | SchemaTypeLoaderException e) {
			LOG.debug("Could not read Word file.", e);
			showErrorMessage(ERROR_TITLE, e.getMessage(), null);
			return;
		}

		if (paragraphs.isEmpty()) {
			return;
		}
		// Clear the Office view and all static variables
		clearSelection();

		// Save new values to properties
		this.selectedFile = officeFile;

		// Populate the view with Word requirements
		String fieldName = Activator.getDefault().getPreferenceStore().getString(OfficePreferences.WORD_FIELD_NAME);
		for (int i = 0; i < paragraphs.size(); i++) {
			XWPFParagraph paragraph = paragraphs.get(i);
			if (paragraph != null) {
				CapraWordRequirement cRequirement = new CapraWordRequirement(officeFile, paragraph, fieldName);
				if (!cRequirement.getData().isEmpty()) {
					selection.add(cRequirement);
				}
			}
		}

		if (!selection.isEmpty()) {
			provider.setResource(selection.get(0));
		} else {
			showErrorMessage(ERROR_TITLE, "There are no fields with the specified field name in this document.", null);
			clearSelection();
			return;
		}

	}

	/**
	 * Shows the details of the currently selected object in its native environment (MS Word, MS Excel
	 * or Google Drive (sheets)).
	 */
	public void showObjectDetails() {
		try {
			selectionProvider.returnSelectedItem().showOfficeObjectInNativeEnvironment();				
		} catch (CapraOfficeObjectNotFound e) {
			LOG.debug("Could not find office object.", e);
			showErrorMessage(ERROR_TITLE, e.getMessage(), null);
		}
	}

	/**
	 * Clears the OfficeView as well as all the static variables.
	 */
	public void clearSelection() {
		selection.clear();
		this.officeTable.dispose();
		selectedSheetName = null;
		selectedFile = null;
		selectedFileId = null;
		isSheetEmptyMap = null;
		updateOfficeObject();
	}

	/**
	 * Opens a file-chooser dialog and calls the parseOfficeFile method, which
	 * displays the contents of the selected file in the TableViewer (if the file is
	 * of type xlsx, xls or docx).
	 */
	public void openFile() {
		FileDialog fd = new FileDialog(this.getSite().getShell(), SWT.OK);
		String filePath = fd.open();

		if (filePath != null && !filePath.isEmpty()) {
			File file = new File(filePath);
			if (file.exists()) {
				try {
					parseGenericFile(file);
				} catch (CapraOfficeFileNotSupportedException e) {
					LOG.debug("Capra does not support the file.", e);
					showErrorMessage(ERROR_TITLE, e.getMessage(), null);
				}
			}
		}
	}

	/**
	 * Displays the provided sheet from the current workbook.
	 * 
	 * @param sheetName the name of the sheet to be displayed in the Office view.
	 */
	public void displaySheet(String sheetName) {
		if (!selection.isEmpty() && selection.get(0) instanceof CapraExcelRow) {
			parseExcelDocument(selectedFile, selectedFileId, sheetName);
		}
	}

	/**
	 * Getter method for the HashMap that contains the sheet names and information
	 * about whether they are empty or not
	 * 
	 * @return names and information about "emptiness" of all the sheets contained
	 *         in the current workbook or null if no workbook is opened.
	 */
	public Map<String, Boolean> getIsSheetEmptyMap() {

		// isSheetEmptyMap is used by the SelectSheetDynamicMenu class.
		if (isSheetEmptyMap == null && !selection.isEmpty()) {
			try {
				isSheetEmptyMap = CapraOfficeUtils.getSheetsEmptinessInfo(
						CapraOfficeUtils.getExcelWorkbook(((CapraExcelRow) (selection.get(0))).getFile()));
			} catch (OldExcelFormatException | IOException e) {
				LOG.debug("Could not open Excel file.", e);
			}
		}

		return isSheetEmptyMap;
	}

	/**
	 * Getter method for the name of currently displayed sheet.
	 * 
	 * @return name of currently displayed sheet or null if none is displayed.
	 */
	public String getSelectedSheetName() {
		return selectedSheetName;
	}

	/**
	 * Provides the instance of the view.
	 * 
	 * @return instance of the view.
	 */
	public static OfficeView getOpenedView() {
		try {
			return (OfficeView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ID);
		} catch (PartInitException e) {
			LOG.debug("Could not open office view.", e);
		}

		return null;
	}

	/**
	 * Refreshes the Office view.
	 */
	public void refreshView() {

		if (selection.isEmpty()) {
			return;
		}
		if (selection.get(0) instanceof CapraExcelRow) {
			parseExcelDocument(selectedFile, selectedFileId, selectedSheetName);
			updateOfficeObject();
		} else if (selection.get(0) instanceof CapraWordRequirement) {
			parseWordDocument(selectedFile);
		}
	}

	private void showErrorMessage(String caption, String message, String url) {
		new HyperlinkDialog(new HyperlinkDialog.HyperlinkDialogParameter(this.officeTable.getShell(), caption, null,
				MessageDialog.ERROR, new String[] { "OK" }, 0), message, url).open();
	}

	/**
	 * A pop-up dialog that can contain a hyperlink that, on click, opens a browser
	 * window at the provided url.
	 */
	static class HyperlinkDialog extends MessageDialog {

		private static final int PREFERRED_DIALOG_WIDTH = 300;
		private String hyperlinkMessage;
		private String url;

		public static class HyperlinkDialogParameter {
			public Shell parentShell;
			public String dialogTitle;
			public Image dialogTitleImage;
			public int dialogImageType;
			public String[] dialogButtonLabels;
			public int defaultIndex;

			public HyperlinkDialogParameter(Shell parentShell, String dialogTitle, Image dialogTitleImage,
					int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
				this.parentShell = parentShell;
				this.dialogTitle = dialogTitle;
				this.dialogTitleImage = dialogTitleImage;
				this.dialogImageType = dialogImageType;
				this.dialogButtonLabels = dialogButtonLabels;
				this.defaultIndex = defaultIndex;
			}
		}

		/**
		 * A constructor that creates the dialog with the provided parameters. Call
		 * open() in order to display the dialog.
		 * 
		 * @param parameterObject  TODO
		 * @param hyperlinkMessage a String that will be shown to the user and can
		 *                         contain a hyperlink, that will, on click, open a
		 *                         browser window at the provided url
		 * @param url              the hyperlink to the web page, or null, if not
		 *                         required
		 */
		public HyperlinkDialog(HyperlinkDialogParameter parameterObject, String hyperlinkMessage, String url) {
			super(parameterObject.parentShell, parameterObject.dialogTitle, parameterObject.dialogTitleImage, null,
					parameterObject.dialogImageType, parameterObject.dialogButtonLabels, parameterObject.defaultIndex);
			this.hyperlinkMessage = hyperlinkMessage;
			this.url = url;
		}

		@Override
		protected Control createCustomArea(Composite parent) {
			Link link = new Link(parent, SWT.None);
			link.setText(hyperlinkMessage);
			GridData gd = new GridData();
			gd.widthHint = PREFERRED_DIALOG_WIDTH;
			link.setLayoutData(gd);
			if (url != null && !url.contentEquals("")) {
				link.setToolTipText(url);
				link.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						try {
							Desktop.getDesktop().browse(new URI(url));
							HyperlinkDialog.this.okPressed();
						} catch (IOException e1) {
							LOG.info("No default browser found.", e1);
						} catch (URISyntaxException e1) {
							LOG.info("Provided malformed URI.", e1);
						}
					}
				});
			}
			return link;
		}
	}

	/**
	 * Enable context menu for this view. Returning the menu that will be displayed.
	 */
	private Menu hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				// No action performed.
			}
		});
		Menu menu = menuMgr.createContextMenu(this.officeTable.getParent());
		this.officeTable.getParent().setMenu(menu);
		getSite().registerContextMenu(menuMgr, this.selectionProvider);
		return menu;
	}

	/**
	 * Adding double-click click UI binding.
	 */
	class DoubleClickEventAction implements IMouseAction {

		@Override
		public void run(NatTable natTable, MouseEvent event) {
			try {
				selectionProvider.returnSelectedItem().showOfficeObjectInNativeEnvironment();				
			} catch (CapraOfficeObjectNotFound e) {
				LOG.debug("Could not find office object.", e);
				showErrorMessage(ERROR_TITLE, e.getMessage(), null);
			}
		}
	}

}
