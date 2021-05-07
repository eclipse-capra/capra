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
package org.eclipse.capra.core.adapters;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * This interface defines all functionality used to persist the trace model, the
 * artifact wrappers, and the metadata.
 * 
 * @author Anthony Anjorin, Salome Maro, Jan-Philipp Steghöfer
 *
 */
public interface IPersistenceAdapter {

	/**
	 * Load and return the trace model in the given resource set
	 * 
	 * @param resourceSet Resource set to load the trace model in
	 * @return Root of loaded trace model, Optional can be empty to indicate that
	 *         loading failed or was not possible (there is no trace model to load
	 *         at the moment)
	 */
	EObject getTraceModel(ResourceSet resourceSet);

	/**
	 * Load and return the container for all artifact wrappers in the given resource
	 * set
	 * 
	 * @param resourceSet Resource set to load the container for artifact wrappers
	 *                    in
	 * @return Container for all artifact wrappers, Optional can be empty to
	 *         indicate that loading failed or was not possible (no container exists
	 *         at the moment)
	 */
	EObject getArtifactWrappers(ResourceSet resourceSet);

	/**
	 * Load and return the container for all trace and artifact metadata in the
	 * given resource set.
	 * 
	 * @param resourceSet the {@code ResourceSet} to load the metadata container
	 *                    from
	 * @return the container for all metadata stored in this {@code ResourceSet}.
	 *         Can be empty to indicate that loading failed or was not possible
	 *         (there is no meta-data model to load at the moment)
	 */
	EObject getMetadataContainer(ResourceSet resourceSet);

	/**
	 * Save the trace model, the artifact wrappers, and the metadata.
	 * 
	 * @param traceModel    The updated trace model to be saved
	 * @param artifactModel The updated artifacts to be saved
	 * @param metadataModel The updated metadata to be saved
	 */
	void saveModels(EObject traceModel, EObject artifactModel, EObject metadataModel);

	/**
	 * Save the trace model, the artifact wrappers, and the metadata.
	 * 
	 * @param resourceSet the resource set that contains the models
	 */
	void saveModels(ResourceSet resourceSet);

}