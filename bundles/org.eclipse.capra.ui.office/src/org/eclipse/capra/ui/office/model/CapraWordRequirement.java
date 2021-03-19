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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.utils.URIBuilder;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.eclipse.capra.ui.office.utils.CapraOfficeUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class extends the CapraOfficeObject and provides an object to describe a
 * single MS Word requirement, which is defined with a specific field.
 *
 * @author Dusan Kalanj
 *
 */
public class CapraWordRequirement extends CapraOfficeObject {

	private static final Logger LOG = LoggerFactory.getLogger(CapraWordRequirement.class);

	/**
	 * RegEx of characters (tabs, newlines, carriage returns and invisible control
	 * characters) to be replaced with white-spaces in the Office View.
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
	 * A constructor that generates a new instance of CapraWordRequirement where the
	 * parent properties are extracted from the provided paragraph, the file that
	 * contains the paragraph and the id (name) of the field that denotes the data
	 * that is to be extracted.
	 * 
	 * @param officeFile the file that contains the paragraph
	 * @param paragraph  a Word paragraph
	 * @param fieldName  the name of the field that denotes the data that is to be
	 *                   extracted from the paragraph
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
		} catch (IOException e) {
			LOG.info("Could not create DOM document: error reading file.", e);
			return;
		} catch (ParserConfigurationException e) {
			LOG.info("Could not create DOM document: parser not configured properly.", e);
			return;
		} catch (SAXException e) {
			LOG.info("Could not create DOM document: malformed XML.", e);
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
			IPath path = new Path(officeFile.getAbsolutePath());
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IFile file = root.getFileForLocation(path);

			URI uri;
			try {
				if (file != null) {
					uri = new URIBuilder().setScheme("platform").setPath("/resource" + file.getFullPath())
							.addParameter(CapraOfficeObject.ROW_PARAMETER, rId).build();
				} else {
					uri = new URIBuilder().setScheme("file").setPath(officeFile.getAbsolutePath())
							.addParameter(CapraOfficeObject.ROW_PARAMETER, rId).build();
				}
				this.setUri(uri);
			} catch (URISyntaxException e) {
				LOG.error("Could not build URI for ", officeFile.getPath());
			}
		}
	}
}
