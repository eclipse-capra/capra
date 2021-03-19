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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.apache.poi.ss.usermodel.Row;
import org.eclipse.capra.ui.office.exceptions.CapraOfficeObjectNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class extends the CapraExcelRow and provides an object to describe a
 * single MS Excel row, the file of which is only stored temporarily as it was
 * obtained directly from Google drive.
 * 
 * @author Dusan Kalanj
 *
 */
public class CapraGoogleSheetsRow extends CapraExcelRow {

	private static final Logger LOG = LoggerFactory.getLogger(CapraGoogleSheetsRow.class);

	/**
	 * Extracts the data from the Excel row the same way as its parent
	 * (CapraExcelRow), but sets a different URI. Because the excel file is only
	 * stored temporarily, it uses a Google drive fileId instead of a file path in
	 * the first part of the uri (the format of the uri is fileId + DELIMITER +
	 * sheetId + DELIMITER + rowId).
	 * 
	 * @param officeFile        the (temporarily stored) excel file that holds the
	 *                          row
	 * @param row               the row from which to extract the data
	 * @param idColumn          the column to be used to extract the ID of the row
	 * @param googleDriveFileId the Google drive file ID of the file (found in the
	 *                          URL when opening the file in Google Drive)
	 */
	public CapraGoogleSheetsRow(File officeFile, Row row, String idColumn, String googleDriveFileId) {
		super(officeFile, row, idColumn);
		String rowId = getRowIdFromExcelRow(row);

		URI uri;
		try {
			uri = new URIBuilder().setScheme("google").setPath(googleDriveFileId)
					.addParameter(CapraOfficeObject.SHEET_PARAMETER, row.getSheet().getSheetName())
					.addParameter(CapraOfficeObject.ROW_PARAMETER, rowId).build();

			this.setUri(uri);
		} catch (URISyntaxException e) {
			LOG.error("Could not build URI for ", googleDriveFileId);
		}
	}

	@Override
	public void showOfficeObjectInNativeEnvironment() throws CapraOfficeObjectNotFound {
		try {
			Desktop.getDesktop().browse(new URI("https://docs.google.com/spreadsheets/d/" + this.getPath()));
		} catch (IOException | URISyntaxException e) {
			LOG.info("Could not open Google spreadsheet.", e);
		}
	}
}
