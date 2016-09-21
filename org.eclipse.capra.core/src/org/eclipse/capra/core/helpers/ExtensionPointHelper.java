/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.core.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.handlers.ArtifactHandler;
import org.eclipse.capra.core.handlers.PriorityHandler;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Provides functionality to work with relevant Capra extension points.
 */
public class ExtensionPointHelper {

	private static final String TRACE_ID = "org.eclipse.capra.configuration.TraceabilityMetaModel";
	private static final String TRACE_CONFIG = "class";
	private static final String PERSISTENCE_ID = "org.eclipse.capra.configuration.persistenceHandler";
	private static final String PERSISTENCE_CONFIG = "class";
	private static final String ARTIFACT_ID = "org.eclipse.capra.configuration.ArtifactMetaModel";
	private static final String ARTIFACT_CONFIG = "class";
	private static final String ARTIFACT_HANDLER_ID = "org.eclipse.capra.configuration.artifactHandler";
	private static final String ARTIFACT_HANDLER_CONFIG = "class";
	private static final String PRIORITY_HANDLER_ID = "org.eclipse.capra.configuration.priorityHandler";
	private static final String PRIORITY_HANDLER_CONFIG = "class";

	/**
	 * Gets all extensions from the extension point ID and attribute passed.
	 * 
	 * @param ID
	 *            the ID of the extension point
	 * 
	 * @param CONFIG
	 *            the name of the attribute
	 * 
	 * @return List of extensions
	 */
	public static List<Object> getExtensions(final String ID, final String CONFIG) {
		try {
			IConfigurationElement[] configs = Platform.getExtensionRegistry().getConfigurationElementsFor(ID);

			List<Object> extensions = new ArrayList<>();
			for (IConfigurationElement config : configs)
				extensions.add(config.createExecutableExtension(CONFIG));

			return extensions;
		} catch (Exception ex) {
			return Collections.emptyList();
		}
	}

	/**
	 * Get the executable extension for the extension ID.
	 * 
	 * @param extensionID
	 *            The ID of the extension
	 * @return extension
	 */
	public static Optional<ArtifactHandler> getExtension(String extensionID, String ID, String CONFIG) {
		try {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtension extension = registry.getExtension(ID, extensionID);
			IConfigurationElement[] elements = extension.getConfigurationElements();
			return Optional.of((ArtifactHandler) elements[0].createExecutableExtension(CONFIG));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Gets the configured {@link TraceMetaModelAdapter}.
	 * 
	 * @return The configured {@code TraceMetaModelAdapter}. If none is
	 *         configured, an empty instance of {@link Optional} is returned.
	 */
	public static Optional<TraceMetaModelAdapter> getTraceMetamodelAdapter() {
		try {
			Object extension = getExtensions(TRACE_ID, TRACE_CONFIG).get(0);
			return Optional.of((TraceMetaModelAdapter) extension);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Gets the configured {@link TracePersistenceAdapter}.
	 * 
	 * @return The configured {@code TracePersistenceAdapter}. If none is
	 *         configured, an empty instance of {@link Optional} is returned.
	 */
	public static Optional<TracePersistenceAdapter> getTracePersistenceAdapter() {
		try {
			Object extension = getExtensions(PERSISTENCE_ID, PERSISTENCE_CONFIG).get(0);
			return Optional.of((TracePersistenceAdapter) extension);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Gets the configured {@link ArtifactMetaModelAdapter}.
	 * 
	 * @return The configured {@code ArtifactMetaModelAdapter}. If none is
	 *         configured, an empty instance of {@link Optional} is returned.
	 */
	public static Optional<ArtifactMetaModelAdapter> getArtifactWrapperMetaModelAdapter() {
		try {
			Object extension = getExtensions(ARTIFACT_ID, ARTIFACT_CONFIG).get(0);
			return Optional.of((ArtifactMetaModelAdapter) extension);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Gets the available {@link ArtifactHandler} instances.
	 * 
	 * @return A collection of all the artifact handlers available. This method
	 *         collects all plugins that have an extension to the
	 *         ArtifactHandler Extension point
	 */
	public static Collection<ArtifactHandler> getArtifactHandlers() {
		try {
			return getExtensions(ARTIFACT_HANDLER_ID, ARTIFACT_HANDLER_CONFIG).stream().map(ArtifactHandler.class::cast)
					.collect(Collectors.toList());
		} catch (Exception e) {
			return Collections.<ArtifactHandler>emptyList();
		}
	}

	/**
	 * Return the artifact handler with the given ID.
	 * 
	 * @param ID
	 * @return ArtifactHandler
	 */
	public static Optional<ArtifactHandler> getArtifactHandler(String ID) {
		return getExtension(ID, ARTIFACT_HANDLER_ID, ARTIFACT_CONFIG);
	}

	/**
	 * Gets the configured {@link PriorityHandler}.
	 * 
	 * @return The configured {@code PriorityHandler}. If none is configured, an
	 *         empty instance of {@link Optional} is returned.
	 */
	public static Optional<PriorityHandler> getPriorityHandler() {
		try {
			Object extension = getExtensions(PRIORITY_HANDLER_ID, PRIORITY_HANDLER_CONFIG).get(0);
			return Optional.of((PriorityHandler) extension);
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}
