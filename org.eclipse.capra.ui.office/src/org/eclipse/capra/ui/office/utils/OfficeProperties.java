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

package org.eclipse.capra.ui.office.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This singleton class provides access to the plugin.properties file.
 *
 * @author Dusan Kalanj
 *
 */
public class OfficeProperties {

	private static OfficeProperties officeProperties = new OfficeProperties();

	private Properties properties;

	private OfficeProperties() {
		this.properties = new Properties();

		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("plugin.properties")){
			properties.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Provides an instance of OfficeProperties class.
	 * 
	 * @return instance of OfficeProperties
	 */
	public static OfficeProperties getInstance() {
		return officeProperties;
	}

	/**
	 * Returns the property String that corresponds to the provided key.
	 * 
	 * @param key
	 *            key of the property to be accessed
	 * @return value of the property that is defined by the provided key
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
}
