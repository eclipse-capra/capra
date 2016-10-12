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
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.SchemaTypeLoaderException;
import org.eclipse.capra.ui.office.objects.CapraExcelRow;
import org.eclipse.capra.ui.office.objects.CapraOfficeObject;
import org.eclipse.capra.ui.office.objects.CapraWordRequirement;
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
	 * A constant that is used for checking whether the ID of the object is
	 * provided or not
	 */
	public static final String OBJECT_ID_NOT_SPECIFIED = "-1";

	/**
	 * The MS Office file-types that are supported by the plugin.
	 */
	public static final String DOCX = "docx";
	public static final String XLS = "xls";
	public static final String XLSX = "xlsx";

	/**
	 * This file-type is not supported, but is needed to display the correct
	 * error message.
	 */
	public static final String DOC = "doc";

	/**
	 * The actual view that contains the contents of the documents.
	 */
	private TableViewer viewer;

	/**
	 * The collection that contains the Excel/Word contents.
	 */
	private ArrayList<Object> selection = new ArrayList<Object>();

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
			return obj.toString();
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
			dropToSelection(data);
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
	 * A method that is called when the user drags a file into the OfficeView.
	 * Its main task is to parse the dragged file and display its contents in
	 * the OfficeView. It only parses the file if it is of type xlsx, xls, or
	 * docx.
	 *
	 * @param data
	 *            the object that was dragged into the view
	 */
	public void dropToSelection(Object data) {

		File officeFile = null;

		if (data instanceof String[])
			officeFile = new File(((String[]) data)[0]);

		if (officeFile != null)
			parseFile(officeFile, OBJECT_ID_NOT_SPECIFIED);
		else
			showMessage(viewer.getControl().getShell(), SWT.ERROR, "Error", "Not an Excel or Word file.");

		viewer.refresh();
	}

	/**
	 * Calls the appropriate parse method (according to the file extension),
	 * which displays the data in the Office view.
	 *
	 * @param officeFile
	 *            the File object that points to the file that is to be parsed
	 * @param objectID
	 *            if provided, only the object with this ID will be displayed
	 */
	private void parseFile(File officeFile, String objectID) {

		String fileType = Files.getFileExtension(officeFile.getAbsolutePath());

		if (fileType.equals(XLSX) || fileType.equals(XLS)) {
			getOpenedView().clearSelection();
			parseExcelDocument(officeFile, objectID);

		} else if (fileType.equals(DOCX)) {
			getOpenedView().clearSelection();
			parseWordDocument(officeFile, objectID);

		} else if (fileType.equals(DOC)) {
			showMessage(viewer.getControl().getShell(), SWT.ERROR, "Error",
					".doc file format not supported, use .docx");

		} else {
			showMessage(viewer.getControl().getShell(), SWT.ERROR, "Error", "Not an Excel or Word file.");
		}
	}

	/**
	 * Extracts the data from the Excel document and adds it to the view.
	 *
	 * @param officeFile
	 *            the File object pointing to the Excel document that was
	 *            dragged into the view.
	 * @param objectID
	 *            the row ID; if "-1" (the value of constant
	 *            OBJECT_ID_NOT_SPECIFIED), the whole document will print out,
	 *            otherwise only the selected row
	 */
	private void parseExcelDocument(File officeFile, String objectID) {

		String fileType = Files.getFileExtension(officeFile.getAbsolutePath());
		Sheet sheet;

		try {
			if (fileType.equals(XLSX)) {
				sheet = new XSSFWorkbook(OPCPackage.open(officeFile)).getSheetAt(0);
			} else {
				sheet = new HSSFWorkbook(new FileInputStream(officeFile)).getSheetAt(0);
			}
		} catch (OldExcelFormatException e) {
			showMessage(viewer.getControl().getShell(), SWT.ERROR, "Error", "This version of Excel is not supported.");
			return;
		} catch (Exception e) {
			e.printStackTrace();
			showMessage(viewer.getControl().getShell(), SWT.ERROR, "Error", "Couldn't open the file.");
			return;
		}

		DataFormatter formatter = new DataFormatter();

		getOpenedView().clearSelection();

		// TODO Currently, the first block of code is only accessed
		// if the method is triggered via OfficeHandler. Does this have to
		// change if the handler is not allowed to have a dependency on UI?
		// Also, the solution assumes that the location of the row is also the
		// rowId, should that be different? Should the ID be defined, for
		// example, in the first cell?
		if (!objectID.equals(OBJECT_ID_NOT_SPECIFIED)) {
			Row row = sheet.getRow(Integer.parseInt(objectID));
			if (row != null) {
				CapraExcelRow cRow = new CapraExcelRow(row, officeFile, formatter);

				if (!cRow.getData().isEmpty())
					selection.add(cRow);
			}
		} else {
			for (int i = 0; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					CapraExcelRow cRow = new CapraExcelRow(row, officeFile, formatter);

					if (!cRow.getData().isEmpty())
						selection.add(cRow);
				}
			}
		}
	}

	/**
	 * Extracts the data from the Word document and adds it to the view.
	 *
	 * @param officeFile
	 *            the File object pointing of the Word document that was dragged
	 *            into the view.
	 * @param objectID
	 *            the paragraph/requirement ID; if "-1" (the value of constant
	 *            OBJECT_ID_NOT_SPECIFIED), the whole document will print out,
	 *            otherwise only the selected object
	 */
	private void parseWordDocument(File officeFile, String objectID) {
		List<XWPFParagraph> paragraphs;

		try {
			FileInputStream fs = new FileInputStream(officeFile);
			// TODO The following line always triggers an exception!
			XWPFDocument xwpfDoc = new XWPFDocument(fs);
			paragraphs = (xwpfDoc).getParagraphs();
			// If poi 3.15 is used, it allows closing the XWPFDocument. If 3.9
			// is used it is not possible.
			// xwpfDoc.close();
		} catch (IOException e) {
			e.printStackTrace();
			showMessage(viewer.getControl().getShell(), SWT.ERROR, "Error", "Couldn't open the file.");
			return;
		} catch (SchemaTypeLoaderException e) {
			// TODO This is the exception for the error!
			e.printStackTrace();
			showMessage(viewer.getControl().getShell(), SWT.ERROR, "Error", "Couldn't open the file.");
			return;
		}

		for (int i = 0; i < paragraphs.size(); i++) {
			XWPFParagraph paragraph = paragraphs.get(i);
			if (paragraph != null) {
				CapraWordRequirement cRequirement = new CapraWordRequirement(paragraph, officeFile, i);

				// TODO Currently, this condition is only true if the method
				// is triggered via OfficeHandler. Does this have to change
				// if the handler is not allowed to have a dependency on UI?
				if (!objectID.equals(OBJECT_ID_NOT_SPECIFIED)) {
					if (cRequirement.getId().equals(objectID)) {
						selection.add(cRequirement);
						break;
					}
				} else if (!cRequirement.getData().isEmpty())
					selection.add(cRequirement);
			}
		}
	}

	/**
	 * Shows only the selected OfficeObject in the Office Selection View.
	 *
	 * @param uri
	 *            the uri of the row/word_requirement, containing the file path
	 *            and the index of the row/word_requirement
	 */
	public void showSingleOfficeObjectInOfficeView(String uri) {

		if (uri.isEmpty())
			return;

		File officeFile = new File(CapraOfficeObject.getFilePathFromUri(uri));
		String objectID = CapraOfficeObject.getIdFromUri(uri);

		// Check if the file (still) exists.
		if (officeFile.exists())
			parseFile(officeFile, objectID);
		else
			showMessage(viewer.getControl().getShell(), SWT.ERROR, "Error", "Resource not found.");

		viewer.refresh();
	}

	/**
	 * Shows the dialog with the information about the selected element in the
	 * view.
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
				officeObject = null;
			}
		} else {
			officeObject = null;
		}

		String caption;
		String message;

		if (officeObject == null) {
			caption = "Notification";
			message = "No information to show.";
		} else {
			caption = "Row " + officeObject.getId();
			message = officeObject.getData();
		}

		showMessage(parentShell, SWT.OK, caption, message);
	}

	/**
	 * Clears the OfficeView.
	 */
	public void clearSelection() {
		selection.clear();
		viewer.refresh();
	}

	/**
	 * Opens a file-chooser dialog and calls the parseFile method, which
	 * displays the contents of the selected file in the TableViewer (if the
	 * file is of type xlsx, xls or docx).
	 */
	public void openFile() {

		FileDialog fd = new FileDialog(viewer.getControl().getShell(), SWT.OK);
		File officeFile = new File(fd.open());

		parseFile(officeFile, OBJECT_ID_NOT_SPECIFIED);

		viewer.refresh();
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
