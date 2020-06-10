/*******************************************************************************
 * Copyright (c) 2016, 2020 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *   
 * SPDX-License-Identifier: EPL-2.0
 *   
 * Contributors:
 *     Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *     Chalmers | University of Gothenburg - additional features, updated API
 *******************************************************************************/
package org.eclipse.capra.ui.adapters;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * Provides adapters for the properties view of trace links and artifacts. This
 * can be used in visualisations to show the properties of a selected link or a
 * selected artifact.
 * <p>
 * To enable the property view, two conditions have to be met:
 * <ol>
 * <li>The view of the visualisation needs to implement a selection provider
 * that returns a {@link Connection} for a selected trace link and an
 * {@code EObject} for a selected artifact.
 * <li>The {@code plugin.xml} of a visualisation needs to contain an extension
 * of {@code org.eclipse.core.runtime.adapters} that maps the
 * {@link ConnectionAdapter} and the {@link ArtifactAdapter} to this class.
 * </ol>
 * 
 * @author Jan-Philipp Steghöfer
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TraceItemAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IPropertySource.class && adaptableObject instanceof Connection) {
			return new ConnectionAdapter((Connection) adaptableObject);
		} else if (adapterType == IPropertySource.class && adaptableObject instanceof EObject) {
			return new ArtifactAdapter((EObject) adaptableObject);
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { ConnectionAdapter.class, ArtifactAdapter.class };
	}
}
