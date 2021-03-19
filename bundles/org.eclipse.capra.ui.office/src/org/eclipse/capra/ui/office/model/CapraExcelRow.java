/*******************************************************************************
 * Copyright (c) 2016, 2019 Chalmers | University of Gothenburg, rt-labs and others.
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
package org.eclipse.capra.ui.office.model;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.eclipse.capra.ui.office.exceptions.CapraOfficeObjectNotFound;
import org.eclipse.capra.ui.office.preferences.OfficePreferences;
import org.eclipse.capra.ui.office.utils.CapraOfficeUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSelection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetViews;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

/**
 * This class extends the CapraOfficeObject and provides an object to describe a
 * single MS Excel row.
 *
 * @author Dusan Kalanj
 *
 */
public class CapraExcelRow extends CapraOfficeObject {

	private static final Logger LOG = LoggerFactory.getLogger(CapraExcelRow.class);

	/**
	 * RegEx of characters (tabs, newlines, carriage returns and invisible control
	 * characters) to be replaced with white-spaces in the Office View.
	 */
	private static final String LINE_BREAKS_AND_CONTROL_REQ = "[\r\n\t\\p{C}]+";

	private static final DataFormatter FORMATTER = new DataFormatter();

	/**
	 * Delimiter between excel cells as displayed in the Office View.
	 */
	private static final String CELL_DELIMITER = " | ";

	/**
	 * A constant that is used if the row index isn't found when opening object
	 * details.
	 */
	private static final int NO_ROW_INDEX = -1;

	/**
	 * A constant that is used if the last cell in the row isn't found.
	 */
	private static final String NO_LAST_CELL_REFERENCE = "-1";

	/**
	 * The ID of the column that is used to extract the identifier of the row (if
	 * value is OfficePreferences.EXCEL_COLUMN_VALUE_DEFAULT, line numbers are used
	 * as identifiers). The property is set according to the value in the Capra
	 * preference page in Eclipse preferences.
	 */
	private String idColumn;

	/**
	 * A constructor that generates a new instance of CapraExcelRow where the parent
	 * properties are extracted from the provided Excel row and File object that
	 * contains the row.
	 * 
	 * @param officeFile a File object representing an Excel file.
	 * @param row        an Excel row, extracted from the provided Excel file.
	 * @param idColumn   the excel column that is used to extract the ID of the row
	 */
	public CapraExcelRow(File officeFile, Row row, String idColumn) {
		super();
		this.idColumn = idColumn;

		String rowId = getRowIdFromExcelRow(row);
		StringBuilder rowBuilder = new StringBuilder();
		rowBuilder.append("ID " + rowId + ": ");

		// Gather the text from the whole Excel row into one StringBuilder
		boolean firstCellSet = false;
		for (int j = 1; j < row.getLastCellNum(); j++) {
			Cell cell = row.getCell(j);
			String cellValue = FORMATTER.formatCellValue(cell);
			if (!cellValue.isEmpty()) {
				if (!firstCellSet) {
					rowBuilder.append(cellValue);
					firstCellSet = true;
				} else {
					rowBuilder.append(CELL_DELIMITER + cellValue);
				}
			}
		}

		// Set data and uri fields if any data was extracted from the row. If no
		// data was found, data and uri will be empty strings (as defined in the
		// CapraOfficeObject - parent object).
		if (firstCellSet) {
			Pattern p = Pattern.compile(LINE_BREAKS_AND_CONTROL_REQ);
			Matcher m = p.matcher(rowBuilder);
			String rowData = (m.replaceAll(" ")).trim();

			IPath path = new Path(officeFile.getAbsolutePath());
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IFile file = root.getFileForLocation(path);

			URI uri;
			try {
				if (file != null) {
					uri = new URIBuilder().setScheme("platform").setPath("/resource" + file.getFullPath())
							.addParameter(CapraOfficeObject.SHEET_PARAMETER, row.getSheet().getSheetName())
							.addParameter(CapraOfficeObject.ROW_PARAMETER, rowId).build();
				} else {
					uri = new URIBuilder().setScheme("file").setPath(officeFile.getAbsolutePath())
							.addParameter(CapraOfficeObject.SHEET_PARAMETER, row.getSheet().getSheetName())
							.addParameter(CapraOfficeObject.ROW_PARAMETER, rowId).build();
				}
				this.setData(rowData);
				this.setUri(uri);
			} catch (URISyntaxException e) {
				LOG.error("Could not build URI for ", officeFile.getPath());
			}
		}
	}

	@Override
	public void showOfficeObjectInNativeEnvironment() throws CapraOfficeObjectNotFound {

		// Get Excel file from the CapraExcelRow object.
		File officeFile;
		try {
			officeFile = getFile();
		} catch (NoSuchFileException e) {
			LOG.warn("Could not find file {}", getPath());
			return;
		}

		// Extract relevant info from the object.
		String fileType = Files.getFileExtension(officeFile.getAbsolutePath());
		String rowId = getRowId();
		String sheetName = getSheetName();

		// Get the object's sheet
		Sheet sheet = null;
		try {
			Workbook workBook = CapraOfficeUtils.getExcelWorkbook(officeFile);
			sheet = CapraOfficeUtils.getSheet(workBook, sheetName);
		} catch (OldExcelFormatException | IOException e) {
			LOG.warn("Excel file is in old format or cannot be read.", e);
			return;
		}

		// Find the Excel row that the CapraExcelRow object points to.
		// lastCellReference is used to store the column ID (such as B or C2) of
		// the last cell that contains any data in the row.
		String lastCellReference = NO_LAST_CELL_REFERENCE;
		int rowIndex = NO_ROW_INDEX;
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row != null) {
				String currRowId = getRowIdFromExcelRow(row);
				if (currRowId.equals(rowId)) {
					rowIndex = i;
					lastCellReference = CellReference.convertNumToColString(row.getLastCellNum()) + (rowIndex + 1);
					break;
				}
			}
		}

		if (rowIndex == NO_ROW_INDEX || lastCellReference.equals(NO_LAST_CELL_REFERENCE)) {
			throw new CapraOfficeObjectNotFound(getRowId());
		}

		// firstDisplayedRowIndex is used to set the first visible row in the
		// view that opens to the user - for example if the row in question is
		// at index 50, the solution will open the Excel file at row 48.
		int firstDisplayedRowIndex = (rowIndex - 2 > 0) ? rowIndex - 2 : 1;

		// The next block of code highlights the selected row in the file by
		// setting some XML values in the excel file.
		if (fileType.equals(CapraOfficeObject.XLSX)) {
			XSSFSheet xssfSheet = XSSFSheet.class.cast(sheet);
			int sheetIndex = xssfSheet.getWorkbook().getSheetIndex(xssfSheet);
			xssfSheet.getWorkbook().setActiveSheet(sheetIndex);

			CTSheetViews ctSheetViews = xssfSheet.getCTWorksheet().getSheetViews();
			CTSheetView ctSheetView = ctSheetViews.getSheetViewArray(ctSheetViews.sizeOfSheetViewArray() - 1);
			ctSheetView.setTopLeftCell("A" + firstDisplayedRowIndex);

			CTSelection ctSelection = ctSheetView.addNewSelection();
			ctSelection.setActiveCell("A" + (rowIndex + 1));
			ctSelection.setSqref(Arrays.asList("A" + (rowIndex + 1) + ":" + lastCellReference));

		} else {
			HSSFSheet hssfSheet = HSSFSheet.class.cast(sheet);
			hssfSheet.setActive(true);
			hssfSheet.showInPane((short) (rowIndex), (short) 0);

			HSSFRow row = hssfSheet.getRow(rowIndex);
			HSSFCell cell = row.getCell(0);
			cell.setAsActiveCell(); // TODO doesn't work - bug in library?
		}

		// Overwrites the existing Excel file with the new XML values.
		try (FileOutputStream out = new FileOutputStream(getFile())) {
			sheet.getWorkbook().write(out);
		} catch (IOException e) {
			LOG.debug("Could not write to file.", e);
		}

		// Opens the Excel file with the Excel application.
		// TODO If Excel is already open, this doesn't trigger. Is there a way
		// to refresh the (Excel) application?
		try {
			Desktop.getDesktop().open(getFile());
		} catch (IOException e) {
			LOG.debug("Could not open file.", e);
		}
	}

	/**
	 * Extracts the name of the sheet (from the URI of the object) which the object
	 * is associated with. The URI of the object should always be in the format
	 * fileId + DELIMITER + sheetName + DELIMITER + rowId.
	 * 
	 * @return name of the sheet
	 */
	public String getSheetName() {
		List<NameValuePair> params = URLEncodedUtils.parse(getUri(), Charset.forName("UTF-8"));
		for (NameValuePair param : params) {
			if (param.getName().equals(CapraOfficeObject.SHEET_PARAMETER)) {
				return param.getValue();
			}
		}
		return "";
	}

	/**
	 * Extracts the ID of the row from the URI of the object. The URI of the object
	 * should always be in the format fileId + DELIMITER + sheetName + DELIMITER +
	 * rowId.
	 * 
	 * @return ID of the row
	 */
	public String getRowId() {
		List<NameValuePair> params = URLEncodedUtils.parse(getUri(), Charset.forName("UTF-8"));
		for (NameValuePair param : params) {
			if (param.getName().equals(CapraOfficeObject.ROW_PARAMETER)) {
				return param.getValue();
			}
		}
		return "";
	}

	/**
	 * Extracts the ID of the Excel row (not CapraExcelRow!) based on the idColumn
	 * value. If the value of idColumn is EXCEL_COLUMN_VALUE_DEFAULT, then rowID is
	 * the same as row number, otherwise a specific column (defined by idColumn
	 * property - such as A1) is used to extract the data that will serve as the ID
	 * of the row.
	 * 
	 * @param row the row to identify
	 * @return the ID of the row
	 */
	protected final String getRowIdFromExcelRow(Row row) {
		String rowId = "";
		if (idColumn.equals(OfficePreferences.EXCEL_COLUMN_VALUE_DEFAULT)) {
			rowId = Integer.toString(row.getRowNum() + 1);
		} else {
			rowId = FORMATTER.formatCellValue(row.getCell(CellReference.convertColStringToIndex(idColumn)));
		}
		return rowId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((idColumn == null) ? 0 : idColumn.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CapraExcelRow other = (CapraExcelRow) obj;
		if (idColumn == null) {
			if (other.idColumn != null)
				return false;
		} else if (!idColumn.equals(other.idColumn))
			return false;
		return true;
	}

}
