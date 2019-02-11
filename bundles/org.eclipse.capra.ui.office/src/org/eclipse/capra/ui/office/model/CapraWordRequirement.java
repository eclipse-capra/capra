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

import java.io.File;
import java.util.Arrays;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.eclipse.capra.ui.office.utils.CapraOfficeUtils;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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
	private static final String FIELD_TAG = "w:instrText";

	/**
	 * A constructor that generates a new instance of CapraWordRequirement where
	 * the parent properties are extracted from the provided paragraph, the file
	 * that contains the paragraph and the id (name) of the field that denotes
	 * the data that is to be extracted.
	 * 
	 * @param officeFile
	 *            the file that contains the paragraph
	 * @param paragraph
	 *            a Word paragraph
	 * @param fieldName
	 *            the name of the field that denotes the data that is to be
	 *            extracted from the paragraph
	 */
	public CapraWordRequirement(File officeFile, XWPFParagraph paragraph, String fieldName) {
		// TODO This solution assumes that there is only one requirement per
		// paragraph. Should it be different?
		super();

		String rText = "";
		String rId = "";

		CTP pCtp = paragraph.getCTP();
		Document doc;
		try {
			doc = CapraOfficeUtils.createDOMDocument(pCtp.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// Get all nodes from the paragraph (there should be just one node if
		// the TODO bellow isn't implemented)
		NodeList nodeList = doc.getElementsByTagName(FIELD_TAG);
		if (nodeList.getLength() > 0) {
			// TODO Use a for loop if the solution needs to parse multiple
			// requirements in a single paragraph. In that case,
			// paragraph.getText() should be replaced with something from the
			// org.w3c.dom.Document class.
			String[] parts = nodeList.item(0).getTextContent().split(WORD_FIELD_SPLIT_DELIMITERS);
			// Extract text from the paragraph and the ID of the requirement.
			if (Arrays.asList(parts).contains(fieldName) && parts.length > 2) {
				rText = paragraph.getText();
				rId = parts[2].trim();
			}
		}

		rText = rText.replaceAll(LINE_BREAKS_AND_CONTROL_REGEX, " ").trim();
		// Set the data and uri properties of the CapraOfficeObject
		if (!rText.isEmpty()) {
			rText = "ID " + rId + ": " + rText;
			String pUri = createUri(officeFile.getAbsolutePath(), rId);

			this.setData(rText);
			this.setUri(pUri);
		}
	}
}
