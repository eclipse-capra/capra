package org.eclipse.capra.ui.office.objects;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

public class CapraExcelRow extends CapraOfficeObject {

	/**
	 * Delimiter between excel cells as displayed in the Office View.
	 */
	private static final String cellDelimiter = " | ";

	/**
	 * Regex of characters to be replaced with white-spaces in the Office View.
	 */
	private static final String replaceRegex = "[\r\n\t\\p{C}]+";

	public CapraExcelRow() {
		super();
	}

	public CapraExcelRow(String data, String uri) {
		super(data, uri);
	}

	/**
	 * A constructor that generates a new instance of CapraExcelRow where the
	 * parent properties are extracted from the provided Excel row and File
	 * object that contains the row.
	 */
	public CapraExcelRow(Row row, File officeFile, DataFormatter formatter) {
		int rowNum = row.getRowNum();

		StringBuilder rowBuilder = new StringBuilder();
		rowBuilder.append("Row " + (rowNum + 1) + ": ");

		boolean firstCellSet = false;

		for (int j = 0; j < row.getLastCellNum(); j++) {
			Cell cell = row.getCell(j);
			String cellValue = formatter.formatCellValue(cell);
			if (!cellValue.isEmpty()) {
				if (!firstCellSet) {
					rowBuilder.append(cellValue);
					firstCellSet = true;
				} else {
					rowBuilder.append(cellDelimiter + cellValue);
				}
			}
		}

		if (firstCellSet) {
			Pattern p = Pattern.compile(replaceRegex);
			Matcher m = p.matcher(rowBuilder);

			String rowContent = (m.replaceAll(" ")).trim();
			String rowUri = CapraOfficeObject.createUri(officeFile, rowNum);

			this.setData(rowContent);
			this.setUri(rowUri);
		}
	}
}
