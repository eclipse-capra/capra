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

package org.eclipse.capra.ui.office.objects;

import java.io.File;
import java.io.StringReader;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.eclipse.capra.ui.office.utils.OfficeProperties;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * This class extends the CapraOfficeObject and provides an object to describe a
 * single MS Word requirement, which is defined with a specific field.
 *
 * @author Dusan Kalanj
 *
 */
public class CapraWordRequirement extends CapraOfficeObject {

	/**
	 * RegEx of characters (tabs, newlines, carriage returns and invisible
	 * control characters) to be replaced with white-spaces in the Office View.
	 */
	private static final String LINE_BREAKS_AND_CONTROL_REGEX = "[\r\n\t\\p{C}]+";

	/**
	 * Regex of characters to be used as delimiters when splitting the field
	 * contents.
	 */
	private static final String WORD_FIELD_SPLIT_DELIMITERS = "(\")|(\\\\\\*)";

	/**
	 * Start and end XML tags of MS Word field commands
	 */
	private static final String REQ_FIELD_TAG = "w:instrText";

	/**
	 * The name of the requirement field as defined in Word.
	 */
	private static final String REQ_FIELD_NAME = OfficeProperties.getInstance().getProperty("req_fieldName");

	/**
	 * A constructor that generates a new instance of CapraWordRequirement where
	 * the parent properties are extracted from the provided Word paragraph and
	 * File object that contains containing the paragraph.
	 */
	public CapraWordRequirement(XWPFParagraph paragraph, File officeFile) {
		// TODO This solution assumes that there is only one requirement per
		// paragraph. Should it be different?
		super();

		String rText = "";
		String rId = "";

		CTP pCtp = paragraph.getCTP();
		Document doc;
		try {
			doc = loadXMLFromString(pCtp.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		NodeList nodeList = doc.getElementsByTagName(REQ_FIELD_TAG);
		if (nodeList.getLength() > 0) {
			// TODO Use a for loop if the solution needs to parse multiple
			// requirements in a single paragraph. In that case,
			// paragraph.getText() should be replaced with something from the
			// org.w3c.dom.Document class.
			String[] parts = nodeList.item(0).getTextContent().split(WORD_FIELD_SPLIT_DELIMITERS);
			if (Arrays.asList(parts).contains(REQ_FIELD_NAME) && parts.length > 2) {
				rText = paragraph.getText();
				rId = parts[2].trim();
			}
		}

		rText = rText.replaceAll(LINE_BREAKS_AND_CONTROL_REGEX, " ").trim();
		if (!rText.isEmpty()) {
			rText = "ID " + rId + ": " + rText;
			String pUri = CapraOfficeObject.createUri(officeFile, rId);

			this.setData(rText);
			this.setUri(pUri);
		}
	}

	private Document loadXMLFromString(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}
}
