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
import java.nio.file.NoSuchFileException;

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

	public static final String SHEET_PARAMETER = "sheet";
	public static final String ROW_PARAMETER = "row";

	/**
	 * The description of the object (row in Excel, requirement in Word)
	 */
	private String data = "";

	/**
	 * The uri of the object in the form of filePath/objectId
	 */
	private URI uri;

	/**
	 * A constructor that generates an empty instance of OfficeObject.
	 */
	public CapraOfficeObject() {
	}

	/**
	 * Returns the uri of the OfficeObject
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * Sets the uri of the OfficeObject
	 */
	public void setUri(URI uri) {
		this.uri = uri;
	}

	public void setUri(String uri) {
		try {
			this.uri = new URI(uri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	 * Returns the ID of the OfficeObject from its URI. The format of the URI should
	 * always be fileId + DELIMITER + objectId.
	 */
	public String getInternalLocation() {
		return uri.getFragment();
	}

	/**
	 * Returns the File reference of the file that contains the OfficeObject. The
	 * format of the URI should always be fileId + DELIMITER + objectId.
	 */
	public File getFile() throws NoSuchFileException {
		File officeFile = new File(uri);
		if (officeFile.exists()) {
			return officeFile;
		} else {
			throw new NoSuchFileException(uri.getPath());
		}
	}

	/**
	 * Returns the ID of the file - the first part of the URI. The format of the URI
	 * should always be fileId + DELIMITER + objectId.
	 */
	public String getPath() {
		return uri.getPath();
	}

	/**
	 * Opens the OfficeObject in its native environment.
	 * 
	 * @return an OK or ERROR message
	 * @throws CapraOfficeObjectNotFound
	 */
	public void showOfficeObjectInNativeEnvironment() throws CapraOfficeObjectNotFound {
		try {
			Desktop.getDesktop().open(getFile());
		} catch (IOException e) {
			throw new CapraOfficeObjectNotFound(getInternalLocation(), e);
		}
	}

	/**
	 * Returns the data of the OfficeObject.
	 */
	@Override
	public String toString() {
		return this.data;
	}

	/**
	 * Provides the hash code of the OfficeObject.
	 */
	@Override
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CapraOfficeObject other = (CapraOfficeObject) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data))
			return false;
		if (uri == null) {
			if (other.uri != null) {
				return false;
			}
		} else if (!uri.equals(other.uri)) {
			return false;
		}
		return true;
	}
}