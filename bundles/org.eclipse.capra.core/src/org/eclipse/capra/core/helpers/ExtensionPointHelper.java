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
package org.eclipse.capra.core.helpers;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.handlers.PriorityHandler;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Provides functionality to work with relevant Capra extension points.
 */
public class ExtensionPointHelper {
	
	/**
	 * Hide the default constructor.
	 */
	private ExtensionPointHelper() {
		super();
	}

	private static final String TRACE_ID = "org.eclipse.capra.configuration.traceabilityMetaModel";
	private static final String TRACE_CONFIG = "class";
	private static final String PERSISTENCE_ID = "org.eclipse.capra.configuration.persistenceHandler";
	private static final String PERSISTENCE_CONFIG = "class";
	private static final String ARTIFACT_ID = "org.eclipse.capra.configuration.artifactMetaModel";
	private static final String ARTIFACT_CONFIG = "class";
	private static final String ARTIFACT_HANDLER_ID = "org.eclipse.capra.configuration.artifactHandlers";
	private static final String ARTIFACT_HANDLER_CONFIG = "class";
	private static final String PRIORITY_HANDLER_ID = "org.eclipse.capra.configuration.priorityHandlers";
	private static final String PRIORITY_HANDLER_CONFIG = "class";

	/**
	 * Gets all extensions from the extension point ID and attribute passed.
	 *
	 * @param id
	 *            the ID of the extension point
	 *
	 * @param attributeName
	 *            the name of the attribute
	 *
	 * @return List of extensions
	 */
	public static List<Object> getExtensions(final String id, final String attributeName) {
		try {
			IConfigurationElement[] configs = Platform.getExtensionRegistry().getConfigurationElementsFor(id);

			List<Object> extensions = new ArrayList<>();
			for (IConfigurationElement config : configs)
				extensions.add(config.createExecutableExtension(attributeName));

			return extensions;
		} catch (Exception ex) {
			return Collections.emptyList();
		}
	}

	/**
	 * Get the executable extension for the extension ID, extension point ID and property name.
	 *
	 * @param extensionId
	 *            the ID of the extension
	 * @param extensionPointId
	 *            the ID of the extension point
	 * @param propertyName
	 *            the name of the property
	 * @return extension
	 */
	public static Optional<IArtifactHandler<?>> getExtension(String extensionId, String extensionPointId, String propertyName) {
		try {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtension extension = registry.getExtension(extensionPointId, extensionId);
			IConfigurationElement[] elements = extension.getConfigurationElements();
			return Optional.of((IArtifactHandler<?>) elements[0].createExecutableExtension(propertyName));
		} catch (Exception e) {
			// Don't catch Exception! It can easily hide bugs!
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
	// Change type to IArtifactHandler<?>, since IArtifactHandler<Object> means a handler which
	// can handle ALL kinds of objects.
	public static Collection<IArtifactHandler<?>> getArtifactHandlers() {
			List<Object> extensions = getExtensions(ARTIFACT_HANDLER_ID, ARTIFACT_HANDLER_CONFIG);

			List<Object> illegalClasses = extensions.stream()
				.filter(c -> !(c instanceof IArtifactHandler))
				.collect(toList());
			
			if (!illegalClasses.isEmpty()) {
				throw new IllegalStateException("Illegal classes at " + ARTIFACT_HANDLER_ID + ": "+ illegalClasses);
			}
			
			return extensions.stream()
				.map(IArtifactHandler.class::cast)
				.collect(toList());
	}

	/**
	 * Return the artifact handler with the given ID.
	 *
	 * @param id the id of the artifact handler
	 * @return ArtifactHandler
	 */
	public static Optional<IArtifactHandler<?>> getArtifactHandler(String id) {
		return getExtension(id, ARTIFACT_HANDLER_ID, ARTIFACT_CONFIG);
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
