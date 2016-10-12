package org.eclipse.capra.ui.office.objects;

import java.io.File;
import java.io.StringReader;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CapraWordRequirement extends CapraOfficeObject {

	/**
	 * Start and end XML tags of MS Word field commands
	 */
	private static final String wordFieldTag = "w:instrText";

	/**
	 * Regex of characters to be replaced with white-spaces in the Office View.
	 */
	private static final String replaceRegex = "[\r\n\t\\p{C}]+";

	/**
	 * Regex of characters to be used as delimiters when splitting the field
	 * contents.
	 */
	private static final String splitFieldRegex = "(\")|(\\\\\\*)";

	/**
	 * The name of the requirement field as defined in Word.
	 */
	// TODO define in properties?
	private static final String fieldName = "REQ";

	public CapraWordRequirement() {
		super();
	}

	public CapraWordRequirement(String data, String uri) {
		super(data, uri);
	}

	/**
	 * A constructor that generates a new instance of CapraWordRequirement where
	 * the parent properties are extracted from the provided Word paragraph and
	 * File object that contains containing the paragraph.
	 */
	public CapraWordRequirement(XWPFParagraph paragraph, File officeFile, int objectID) {
		// TODO This solution assumes that there is only one requirement per
		// paragraph. Should it be different?

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

		NodeList nodeList = doc.getElementsByTagName(wordFieldTag);
		if (nodeList.getLength() > 0) {
			// TODO Use a for loop if the solution needs to parse multiple
			// requirements in a single paragraph. In that case,
			// paragraph.getText() should be replaced with something from the
			// org.w3c.dom.Document class.
			String[] parts = nodeList.item(0).getTextContent().split(splitFieldRegex);
			if (Arrays.asList(parts).contains(fieldName) && parts.length > 2) {
				rText = paragraph.getText();
				rId = parts[2].trim();
			}
		}

		rText = rText.replaceAll(replaceRegex, " ").trim();
		if (!rText.isEmpty()) {
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
