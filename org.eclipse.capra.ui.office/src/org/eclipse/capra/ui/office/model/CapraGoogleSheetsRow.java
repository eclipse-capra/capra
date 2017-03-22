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
package org.eclipse.capra.ui.office.model;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.poi.ss.usermodel.Row;
import org.eclipse.capra.ui.office.exceptions.CapraOfficeObjectNotFound;

/**
 * This class extends the CapraExcelRow and provides an object to describe a
 * single MS Excel row, the file of which is only stored temporarily as it was
 * obtained directly from Google drive.
 * 
 * @author Dusan Kalanj
 *
 */
public class CapraGoogleSheetsRow extends CapraExcelRow {

	/**
	 * Extracts the data from the Excel row the same way as its parent
	 * (CapraExcelRow), but sets a different URI. Because the excel file is only
	 * stored temporarily, it uses a Google drive fileId instead of a file path
	 * in the first part of the uri (the format of the uri is fileId + DELIMITER
	 * + sheetId + DELIMITER + rowId).
	 * 
	 * @param officeFile
	 *            the (temporarily stored) excel file that holds the row
	 * @param row
	 *            the row from which to extract the data
	 * @param idColumn
	 *            the column to be used to extract the ID of the row
	 * @param googleDriveFileId
	 *            the Google drive file ID of the file (found in the URL when
	 *            opening the file in Google Drive)
	 */
	public CapraGoogleSheetsRow(File officeFile, Row row, String idColumn, String googleDriveFileId) {
		super(officeFile, row, idColumn);
		String rowId = getRowIdFromExcelRow(row);
		String objectId = row.getSheet().getSheetName() + CapraOfficeObject.URI_DELIMITER + rowId;
		String uri = createUri(googleDriveFileId, objectId);
		this.setUri(uri);
	}

	@Override
	public void showOfficeObjectInNativeEnvironment() throws CapraOfficeObjectNotFound {
		try {
			Desktop.getDesktop().browse(new URI("https://docs.google.com/spreadsheets/d/" + this.getFileId()));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
