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

package org.eclipse.capra.ui.office.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.SchemaTypeLoaderException;
import org.eclipse.capra.ui.office.model.CapraOfficeObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Strings;
import com.google.common.io.Files;

/**
 * This class contains convenient static methods that support the Capra Office
 * plugin.
 * 
 * @author Dusan Kalanj
 *
 */
public final class CapraOfficeUtils {

	/**
	 * Hide default constructor.
	 */
	private CapraOfficeUtils() {
		super();
	}

	/**
	 * Generates a HashMap that contains sheet names and info about their emptiness:
	 * <ul>
	 * <li>keySet (String) - names of all the sheets, contained in the selected
	 * workbook</li>
	 * <li>valueSet (Boolean) - information about whether sheets are empty or
	 * not</li>
	 * </ul>
	 * 
	 * @param workBook an Excel Workbook object
	 * @return a <code>Map</code> that contains the names (<code>String</code>) of
	 *         all the sheets, contained in the selected workbook and information
	 *         about whether they are empty or not (<code>Boolean</code>).
	 */
	public static Map<String, Boolean> getSheetsEmptinessInfo(Workbook workBook) {
		HashMap<String, Boolean> isSheetEmptyMap = new HashMap<>();
		for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
			Sheet s = workBook.getSheetAt(i);
			isSheetEmptyMap.put(s.getSheetName(), s.getLastRowNum() < 1);
		}
		return isSheetEmptyMap;
	}

	/**
	 * Gets the sheet with the provided sheetName from inside the provided Excel
	 * Workbook.
	 * 
	 * @param workBook  an Excel Workbook object
	 * @param sheetName the name of the sheet
	 * @return the sheet with the provided sheetName from the provided workBook
	 * @throws NullPointerException
	 */
	public static Sheet getSheet(Workbook workBook, String sheetName) throws NullPointerException {
		String actualSheetName = sheetName;
		if (Strings.isNullOrEmpty(sheetName)) {
			// This try block is necessary as there is a bug in the
			// Workbook.getActiveSheetIndex() and Workbook.getFirstVisibleTab()
			// methods; they throw a NullPointerException whenever the
			// woorkbook doesn't hold information about which sheet is active
			// and/or visible (maybe it occurs when the file is auto-generated).
			int activeSheetIndex;
			try {
				activeSheetIndex = workBook.getActiveSheetIndex();
			} catch (NullPointerException e1) {
				try {
					activeSheetIndex = workBook.getFirstVisibleTab();
				} catch (NullPointerException e2) {
					activeSheetIndex = 0;
				}
			}

			actualSheetName = workBook.getSheetName(activeSheetIndex);
		}

		return workBook.getSheet(actualSheetName);
	}

	/**
	 * Gets the Excel Workbook from the provided Excel file.
	 * 
	 * @param excelFile a MS Excel file.
	 * @return an Excel Workbook object
	 * @throws OldExcelFormatException
	 * @throws IOException
	 */
	public static Workbook getExcelWorkbook(File excelFile) throws OldExcelFormatException, IOException {
		String fileType = Files.getFileExtension(excelFile.getAbsolutePath());
		Workbook workBook;

		if (fileType.equals(CapraOfficeObject.XLSX)) {
			workBook = new XSSFWorkbook(new FileInputStream(excelFile));
		} else {
			workBook = new HSSFWorkbook(new FileInputStream(excelFile));
		}

		return workBook;
	}

	/**
	 * Gets a list of paragraph objects that are contained in the provided MS Word
	 * file.
	 * 
	 * @param wordFile a MS Word file
	 * @return a list of XWPFParagraph objects that describe the paragraphs
	 *         contained by the provided MS Word file.
	 * @throws SchemaTypeLoaderException
	 * @throws IOException
	 */
	public static List<XWPFParagraph> getWordParagraphs(File wordFile) throws SchemaTypeLoaderException, IOException {
		FileInputStream fs = new FileInputStream(wordFile);
		XWPFDocument xwpfDoc = new XWPFDocument(fs);
		return xwpfDoc.getParagraphs();
	}

	/**
	 * Creates a DOM document from the provided xml String.
	 * 
	 * @param xml a valid XML String
	 * @return DOM document that describes the XML String
	 * @throws ParserConfigurationException if a DocumentBuilder cannot be created
	 *                                      which satisfies the configuration
	 *                                      requested.
	 * @throws IOException                  if any IO errors occur.
	 * @throws SAXException                 if any parse error occurs
	 */
	public static Document createDOMDocument(String xml)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}

}
