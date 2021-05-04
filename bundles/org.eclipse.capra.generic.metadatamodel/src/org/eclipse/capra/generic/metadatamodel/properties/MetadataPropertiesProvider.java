/*******************************************************************************
 * Copyright (c) 2016, 2021 Chalmers | University of Gothenburg, rt-labs and others.
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
package org.eclipse.capra.generic.metadatamodel.properties;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

/**
 * Provides access to the property sources to access the metadata of connections
 * and artifacts.
 * 
 * @author Jan-Philipp Steghöfer
 */
public class MetadataPropertiesProvider implements IPropertySourceProvider {

	@Override
	public IPropertySource getPropertySource(Object object) {
		if (object instanceof Connection) {
			return new ConnectionMetadataProperties((Connection) object);
		} else if (object instanceof EObject) {
			// TODO: Return the property source for artifacts
			return null;
		}
		return null;
	}

}
