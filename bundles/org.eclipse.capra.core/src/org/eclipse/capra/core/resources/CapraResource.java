/*******************************************************************************
 * Copyright (c) 2016-2022 Chalmers | University of Gothenburg, rt-labs and others.
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
package org.eclipse.capra.core.resources;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

/**
 * An extension of {@link XMIResourceImpl} that enables the use of UUIDs to
 * reference other EObjects.
 * 
 * TODO: This is an EMF-specific feature and should therefore be in the
 *       generic persistence adapter.
 * 
 * @author Jan-Philipp Steghöfer
 */
public class CapraResource extends XMIResourceImpl {
	
	/**
	 * The default file extension used for all files associated with Eclipse Capra.
	 */
	public static final String DEFAULT_CAPRA_FILE_EXTENSION = "capra";

	/**
	 * Constructs a new Capra resource.
	 */
	public CapraResource() {
		super();
	}

	/**
	 * Constructs a new Capra resource from the given {@link URI}.
	 * 
	 * @param uri the new resource's URI
	 */
	public CapraResource(URI uri) {
		super(uri);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#useUUIDs()
	 */
	@Override
	protected boolean useUUIDs() {
		return true;
	}
}
