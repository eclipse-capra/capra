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
package org.eclipse.capra.ui.adapters;

import java.util.Optional;

import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

/**
 * This utility class provides access to the extension point that provides
 * access to the property source provider for artifact and trace metadata.
 * <p>
 * Eclipse Capra uses this class instead of
 * {@code Platform.getAdapterManager().getAdapter()} since metadata is an
 * EObject and we cannot make a more concrete distinction. Since most EObjects
 * do not carry metadata, we cannot register the adapter for all of them. Using
 * this helper as well as the configuration extension point in
 * {@code org.eclipse.capra.core} provides us with the ability to get the
 * {@link IPropertySource} for metadata when we need it.
 * 
 * @author Jan-Philipp Steghöfer
 */
public class PropertySourceExtensionPointHelper {

	private static final String METADATA_PROPERTIES_SOURCE_PROVIDER_ID = "org.eclipse.capra.configuration.metadataPropertySourceProvider";
	private static final String METADATA_PROPERTIES_SOURCE_PROVIDER_CONFIG = "class";

	/**
	 * Gets the configured property source provider for metadata.
	 *
	 * @return The configured {@link IPropertySourceProvider} for metadata
	 */
	public static Optional<IPropertySourceProvider> getMetadataPropertySourceProvider() {
		try {
			Object extension = ExtensionPointHelper
					.getExtensions(METADATA_PROPERTIES_SOURCE_PROVIDER_ID, METADATA_PROPERTIES_SOURCE_PROVIDER_CONFIG)
					.get(0);
			return Optional.of((IPropertySourceProvider) extension);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

}
