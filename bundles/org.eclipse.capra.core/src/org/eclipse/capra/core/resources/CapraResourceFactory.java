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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * Resource factory that constructs {@link CapraResource} instances.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class CapraResourceFactory extends XMIResourceFactoryImpl {

	/**
	 * Constructs a new Capra resource factory.
	 */
	public CapraResourceFactory() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl#createResource(org.
	 * eclipse.emf.common.util.URI)
	 */
	@Override
	public Resource createResource(URI uri) {
		return new CapraResource(uri);
	}
}