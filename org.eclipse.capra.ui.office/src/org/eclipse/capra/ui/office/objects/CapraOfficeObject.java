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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.capra.ui.office.exceptions.CapraOfficeObjectNotFound;

/**
 * This class provides a custom object for describing the contents of MS Excel
 * and MS Word related data.
 *
 * @author Dusan Kalanj
 *
 */
public class CapraOfficeObject {

	/**
	 * The MS Office file-types that are supported by the plugin.
	 */
	public static final String DOCX = "docx";
	public static final String XLS = "xls";
	public static final String XLSX = "xlsx";

	/**
	 * The description of the object (row in Excel, requirement in Word)
	 */
	private String data = "";

	/**
	 * The uri of the object in the form of filePath/objectId
	 */
	private String uri = "";

	/**
	 * A constructor that generates an empty instance of OfficeObject.
	 */
	public CapraOfficeObject() {
	}

	/**
	 * A constructor that generates a new instance of OfficeObject with defined
	 * OfficeData and rowUri properties.
	 */
	public CapraOfficeObject(String data, String uri) {
		this.data = data;
		this.uri = uri;
	}

	/**
	 * Returns the uri of the OfficeObject
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Sets the uri of the OfficeObject
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * Returns the description of the OfficeObject
	 */
	public String getData() {
		return this.data;
	}

	/**
	 * Sets the description of the OfficeObject
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * Returns the ID of the OfficeObject.
	 */
	public String getId() {
		int lastDelimiterIndex = uri.lastIndexOf(File.separator);
		return uri.substring(lastDelimiterIndex + 1);
	}

	/**
	 * Returns the File reference of the file that contains the OfficeObject
	 */
	public File getFile() {
		int lastDelimiterIndex = uri.lastIndexOf(File.separator);
		return new File(uri.substring(0, lastDelimiterIndex));
	}

	/**
	 * Generates a readable name for the purpose of supplying it to
	 * createArtifact method in the OfficeHandler.
	 * 
	 * @return a readable name of the OfficeObject
	 */
	public String getName() {
		return getFile().getName() + File.separator + getId();
	}
	
	/**
	 * Extracts the ID of the object from uri of the OfficeObject.
	 * 
	 * @param uri
	 *            uri of the object
	 * @return ID of the object
	 */
	public static String getIdFromUri(String uri) {
		int lastDelimiterIndex = uri.lastIndexOf(File.separator);
		return uri.substring(lastDelimiterIndex + 1);
	}

	/**
	 * Extracts the file-path from uri of the OfficeObject.
	 * 
	 * @param uri
	 *            uri of the object
	 * @return file-path of the file that contains the object
	 */
	public static String getFilePathFromUri(String uri) {
		int lastDelimiterIndex = uri.lastIndexOf(File.separator);
		return uri.substring(0, lastDelimiterIndex);
	}

	/**
	 * Opens the OfficeObject in its native environment. If object is of class
	 * OfficeObject (parent object), then the method does nothing but return an
	 * error string
	 * 
	 * @return an OK or ERROR message
	 * @throws CapraOfficeObjectNotFound
	 */
	public void showOfficeObjectInNativeEnvironment() throws CapraOfficeObjectNotFound {
		try {
			Desktop.getDesktop().open(getFile());
		} catch (IOException e) {
			e.printStackTrace();
			throw new CapraOfficeObjectNotFound(getId());
		}
	}

	/**
	 * Generates a uri given the file-path of the file that contains the object
	 * and an objectID
	 * 
	 * @param officeFile
	 *            File object containing the absolute file-path of the file that
	 *            contains the object
	 * @param objectID
	 *            ID of the object - usually its index in the file
	 *
	 * @return a uri of the object in the form of filePath/objectID
	 */
	public static String createUri(File officeFile, Object objectId) {
		return officeFile.getAbsolutePath() + File.separator + objectId;
	}

	/**
	 * Returns the description of the OfficeObject. If the description is too
	 * long, it limits the return value.
	 */
	@Override
	public String toString() {
		return this.data;
	}

	/**
	 * Provides the hash code of the OfficeObject.
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	/**
	 * Compares two instances of OfficeObject.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CapraOfficeObject other = (CapraOfficeObject) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
}