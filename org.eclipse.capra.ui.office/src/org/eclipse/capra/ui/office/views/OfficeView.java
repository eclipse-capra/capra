/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.capra.ui.office.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.SchemaTypeLoaderException;
import org.eclipse.capra.ui.office.Activator;
import org.eclipse.capra.ui.office.utils.OfficeSourceProvider;
import org.eclipse.capra.ui.office.exceptions.CapraOfficeFileNotSupportedException;
import org.eclipse.capra.ui.office.exceptions.CapraOfficeObjectNotFound;
import org.eclipse.capra.ui.office.objects.CapraExcelRow;
import org.eclipse.capra.ui.office.objects.CapraOfficeObject;
import org.eclipse.capra.ui.office.objects.CapraWordRequirement;
import org.eclipse.capra.ui.office.preferences.OfficePreferences;
import org.eclipse.capra.ui.office.utils.OfficeTransferType;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.ISourceProviderService;

import com.google.common.io.Files;

/**
 * Provides a Capra perspective view for displaying the contents of Excel and
 * Word documents. The view displays the contents if the user drags an Excel or
 * a Word document into the surface.
 *
 * @author Dusan Kalanj
 *
 */
public class OfficeView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.eclipse.capra.ui.views.OfficeView";

	/**
	 * A constant that is used to specify that the user should be prompted to
	 * select a sheet when opening an excel document.
	 */
	public static final boolean SHEET_SELECT_REQUIRED = true;

	/**
	 * A constant that is used to specify that the user doesn't have to be
	 * prompted to select a sheet and the currently active sheet will be
	 * displayed.
	 */
	public static final boolean SHEET_SELECT_NOT_REQUIRED = false;

	/**
	 * The caption that is shown when a message dialog appears describing an
	 * error.
	 */
	private static final String ERROR_TITLE = "Error";

	/**
	 * The actual view that contains the contents of the documents.
	 */
	private TableViewer viewer;

	/**
	 * The collection that contains the Excel/Word contents.
	 */
	private List<CapraOfficeObject> selection = new ArrayList<CapraOfficeObject>();

	/**
	 * Instance of OfficeSourceProvider (used for hiding context menu options)
	 */
	private OfficeSourceProvider provider = (OfficeSourceProvider) ((ISourceProviderService) PlatformUI.getWorkbench()
			.getService(ISourceProviderService.class)).getSourceProvider(OfficeSourceProvider.CAPRA_OFFICE_OBJECT);
	
	/**
	 * The content provider class used by the view.
	 */
	class ViewContentProvider implements IStructuredContentProvider {
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object parent) {
			return selection.toArray();
		}
	}

	/**
	 * The label provider class used by the view.
	 */
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public String getText(Object obj) {
			int minAllowed = Activator.getDefault().getPreferenceStore().getInt(OfficePreferences.CHAR_COUNT);
			String text = obj.toString();
			int textLength = Math.min(text.length(), minAllowed);
			if (textLength == minAllowed)
				text = text.substring(0, textLength) + "...";
			return text;
		};

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
	 * Adapter used by the view to handle drop events.
	 */
	class SelectionDropAdapter extends ViewerDropAdapter {

		public SelectionDropAdapter(TableViewer viewer) {
			super(viewer);
		}

		@Override
		public boolean performDrop(Object data) {
			try {
				dropToSelection(data);
			} catch (CapraOfficeFileNotSupportedException e) {
				e.printStackTrace();
				showMessage(viewer.getControl().getShell(), SWT.ERROR, ERROR_TITLE, e.getMessage());
				return false;
			}
			return true;
		}

		@Override
		public boolean validateDrop(Object target, int operation, TransferData transferType) {
			return true;
		}
	}

	/**
	 * Adapter used by the view to handle drag events.
	 */
	class SelectionDragAdapter extends ViewerDragAdapter {

		TableViewer viewer;

		public SelectionDragAdapter(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
		}

		@Override
		public void dragSetData(DragSourceEvent event) {

			if (OfficeTransferType.getInstance().isSupportedType(event.dataType)) {
				TableItem[] items = viewer.getTable().getSelection();
				ArrayList<CapraOfficeObject> officeObjects = new ArrayList<CapraOfficeObject>();

				for (int i = 0; i < items.length; i++) {
					officeObjects.add((CapraOfficeObject) items[i].getData());
				}

				event.data = officeObjects;
			}
		}
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void createPartControl(Composite parent) {

		viewer = new TableViewer(parent);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(getViewSite());

		getSite().setSelectionProvider(viewer);
		hookContextMenu();

		int ops = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transfersIn = new Transfer[] { org.eclipse.swt.dnd.FileTransfer.getInstance() };
		Transfer[] transfersOut = new Transfer[] { org.eclipse.capra.ui.office.utils.OfficeTransferType.getInstance() };

		viewer.addDropSupport(ops, transfersIn, new SelectionDropAdapter(viewer));
		viewer.addDragSupport(ops, transfersOut, new SelectionDragAdapter(viewer));
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				showObjectDetails(event, parent.getShell());
			}
		});
	}

	/**
	 * Getter method for the selection that is displayed by the view.
	 * 
	 * @return currently displayed selection
	 */
	public List<CapraOfficeObject> getSelection() {
		return this.selection;
	}

	/**
	 * A method that is called when the user drags a file into the OfficeView.
	 * Its main task is to parse the dragged file and display its contents in
	 * the OfficeView. It only parses the file if it is of type xlsx, xls, or
	 * docx.
	 *
	 * @param data
	 *            the object that was dragged into the view
	 * @throws CapraOfficeFileNotSupportedException
	 */
	private void dropToSelection(Object data) throws CapraOfficeFileNotSupportedException {

		File file = null;

		if (data instanceof String[])
			file = new File(((String[]) data)[0]);

		if (file == null)
			return;

		String fileExtension = Files.getFileExtension(file.getName());

		if (fileExtension.equals(CapraOfficeObject.XLSX) || fileExtension.equals(CapraOfficeObject.XLS))
			parseExcelDocument(file, SHEET_SELECT_NOT_REQUIRED);
		else if (fileExtension.equals(CapraOfficeObject.DOCX))
			parseWordDocument(file);
		else
			throw new CapraOfficeFileNotSupportedException(fileExtension);

		viewer.refresh();
	}

	/**
	 * Extracts the data from the Excel document and adds it to the view.
	 *
	 * @param officeFile
	 *            the File object pointing to the Excel document that was
	 *            dragged into the view.
	 * @param sheetSelect
	 *            true if the user has to be prompted to select a sheet, false
	 *            otherwise
	 */
	private void parseExcelDocument(File officeFile, boolean sheetSelect) {

		String fileType = Files.getFileExtension(officeFile.getAbsolutePath());
		Workbook workBook;

		try {
			if (fileType.equals(CapraOfficeObject.XLSX))
				workBook = new XSSFWorkbook(new FileInputStream(officeFile));
			else
				workBook = new HSSFWorkbook(new FileInputStream(officeFile));
		} catch (OldExcelFormatException e) {
			showMessage(viewer.getControl().getShell(), SWT.ERROR, ERROR_TITLE, e.getMessage());
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		String selectedSheetName = "";
		if (!sheetSelect || selection.isEmpty()) {
			selectedSheetName = workBook.getSheetName(workBook.getActiveSheetIndex());
			// If sheet is empty, prompt user to select another
			if (workBook.getSheet(selectedSheetName).getLastRowNum() == 0)
				sheetSelect = true;
		}

		if (sheetSelect) {
			String activeSheetName;
			if (selectedSheetName.isEmpty())
				activeSheetName = ((CapraExcelRow) selection.get(0)).getSheetName();
			else
				activeSheetName = selectedSheetName;

			String[] sNames = new String[workBook.getNumberOfSheets()];

			// Fill sNames with sheetNames, with the active sheet at index 0
			int counter = 0;
			sNames[counter++] = activeSheetName;
			for (int i = 0; i < sNames.length; i++) {
				String sName = workBook.getSheetName(i);
				if (sName.equals(activeSheetName))
					continue;
				sNames[counter++] = workBook.getSheetName(i);
			}

			selectedSheetName = new SelectSheetDialog(viewer.getControl().getShell(),
					SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, sNames).open();
		}

		if (selectedSheetName.isEmpty())
			return;

		Sheet sheet = workBook.getSheet(selectedSheetName);

		clearSelection();

		String idColumn = Activator.getDefault().getPreferenceStore().getString(OfficePreferences.EXCEL_COLUMN_VALUE);
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row != null) {
				CapraExcelRow cRow = new CapraExcelRow(officeFile, row, idColumn);
				if (!cRow.getData().isEmpty())
					selection.add(cRow);
			}
		}
		
		if (!selection.isEmpty())
			provider.setResource(selection.get(0));
	}

	/**
	 * Extracts the data from the Word document and adds it to the view.
	 *
	 * @param officeFile
	 *            the File object pointing of the Word document that was dragged
	 *            into the view.
	 */
	private void parseWordDocument(File officeFile) {
		List<XWPFParagraph> paragraphs;

		try (FileInputStream fs = new FileInputStream(officeFile)) {
			XWPFDocument xwpfDoc = new XWPFDocument(fs);
			paragraphs = (xwpfDoc).getParagraphs();
		} catch (IOException e) {
			e.printStackTrace();
			showMessage(viewer.getControl().getShell(), SWT.ERROR, ERROR_TITLE, e.getMessage());
			return;
		} catch (SchemaTypeLoaderException e) {
			e.printStackTrace();
			showMessage(viewer.getControl().getShell(), SWT.ERROR, ERROR_TITLE, e.getMessage());
			return;
		}

		clearSelection();

		for (int i = 0; i < paragraphs.size(); i++) {
			XWPFParagraph paragraph = paragraphs.get(i);
			if (paragraph != null) {
				CapraWordRequirement cRequirement = new CapraWordRequirement(paragraph, officeFile);
				if (!cRequirement.getData().isEmpty())
					selection.add(cRequirement);
			}
		}
		
		if (!selection.isEmpty())
			provider.setResource(selection.get(0));
	}

	/**
	 * Shows the details of the object in its native environment (MS Word or MS
	 * Excel).
	 * 
	 * @param event
	 *            Should be of type DoubleClickEvent or ExecutionEvent, hold the
	 *            event that triggered the request for details.
	 * @param parentShell
	 *            Shell which will be the parent of the dialog window.
	 */
	public void showObjectDetails(Object event, Shell parentShell) {
		CapraOfficeObject officeObject;

		if (event instanceof DoubleClickEvent) { // If called with double click
			IStructuredSelection selection = (IStructuredSelection) ((DoubleClickEvent) event).getSelection();
			officeObject = (CapraOfficeObject) selection.getFirstElement();

		} else if (event instanceof ExecutionEvent) { // If called from menu
			IStructuredSelection selection;

			try {
				selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked((ExecutionEvent) event);
				officeObject = (CapraOfficeObject) selection.getFirstElement();
			} catch (ExecutionException e) {
				e.printStackTrace();
				return;
			}
		} else {
			return;
		}

		try {
			officeObject.showOfficeObjectInNativeEnvironment();
		} catch (CapraOfficeObjectNotFound e) {
			e.printStackTrace();
			showMessage(viewer.getControl().getShell(), SWT.ERROR, ERROR_TITLE, e.getMessage());
		}

	}

	/**
	 * Clears the OfficeView.
	 */
	public void clearSelection() {
		selection.clear();
		viewer.refresh();
		provider.setResource(null);
	}

	/**
	 * Opens a file-chooser dialog and calls the parseOfficeFile method, which
	 * displays the contents of the selected file in the TableViewer (if the
	 * file is of type xlsx, xls or docx).
	 */
	public void openFile() {

		FileDialog fd = new FileDialog(viewer.getControl().getShell(), SWT.OK);
		String filePath = fd.open();

		if (filePath != null && !filePath.isEmpty()) {
			File file = new File(filePath);
			if (file != null) {
				try {
					dropToSelection(new String[] { file.getAbsolutePath() });
				} catch (CapraOfficeFileNotSupportedException e) {
					e.printStackTrace();
					showMessage(viewer.getControl().getShell(), SWT.ERROR, ERROR_TITLE, e.getMessage());
				}
				viewer.refresh();
			}
		}
	}

	/**
	 * Opens a pop-up window that allows the user to select which excel sheet
	 * should be displayed.
	 */
	public void selectSheet() {

		if (selection.isEmpty())
			return;
		else if (selection.get(0) instanceof CapraExcelRow) {
			parseExcelDocument(selection.get(0).getFile(), SHEET_SELECT_REQUIRED);
			viewer.refresh();
		}
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
			e.printStackTrace();
		}

		return null;
	}

	public void refreshView() {

		if (selection.isEmpty())
			return;
		else if (selection.get(0) instanceof CapraExcelRow)
			parseExcelDocument(selection.get(0).getFile(), SHEET_SELECT_NOT_REQUIRED);

		viewer.refresh();
	}

	private int showMessage(Shell parentShell, int style, String caption, String message) {
		MessageBox dialog = new MessageBox(parentShell, style);
		dialog.setText(caption);
		dialog.setMessage(message);

		return dialog.open();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
}
