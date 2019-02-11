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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.capra.ui.office.model.CapraOfficeObject;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

/**
 * This class provides the plugin with information about the contents of the
 * Office view. It does that by providing a sample object from the view, from
 * which the following useful information can be deducted: if view is populated
 * (object is null if it is empty); the type of the objects in the view; the
 * file-path of the file that was used to extract the objects.
 *
 * @author Dusan Kalanj
 *
 */
public class OfficeSourceProvider extends AbstractSourceProvider {

	/**
	 * The ID of the variable that corresponds to the one in the plugin.xml and
	 * is used to identify the resource that is being held by the class.
	 */
	public static final String CAPRA_OFFICE_OBJECT = "org.eclipse.capra.ui.office.utils.capraOfficeObject";

	private CapraOfficeObject capraOfficeObject = null;

	@Override
	public void dispose() {
	}

	/**
	 * Sets the resource (an instance of CapraOfficeObject).
	 * 
	 * @param capraOfficeObject
	 */
	public void setResource(CapraOfficeObject capraOfficeObject) {
		this.capraOfficeObject = capraOfficeObject;
		fireSourceChanged(ISources.WORKBENCH, CAPRA_OFFICE_OBJECT, this.capraOfficeObject);
	}

	@Override
	public Map<String, CapraOfficeObject> getCurrentState() {
		HashMap<String, CapraOfficeObject> map = new HashMap<String, CapraOfficeObject>();
		map.put(CAPRA_OFFICE_OBJECT, capraOfficeObject);
		return map;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { CAPRA_OFFICE_OBJECT };
	}
}