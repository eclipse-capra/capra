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

/**
 * This interface defines the methods used to access the metadata for trace
 * links and for artifacts. It should be implemented by classes that provide
 * metadata information.
 * 
 * @author Jan-Philipp Steghöfer
 */
public interface IMetadataAdapter {

	/**
	 * Create a new metadata model. This method should not be called by clients, but
	 * rather only by persistence adapters that cannot load an existing model.
	 * 
	 * @return the newly created metadata model
	 */
	EObject createModel();

	/**
	 * Retrieves the metadata for the provided trace link from the given metadata
	 * container. If no metadata is found for the trace link, an empty metadata
	 * object is created and returned.
	 * 
	 * @param trace             the trace link whose metadata should be retrieved
	 * @param metadataContainer the container that contains the metadata for the
	 *                          trace link
	 * @return the metadata for the trace link
	 */
	EObject getMetadataForTrace(EObject trace, EObject metadataContainer);

	/**
	 * Sets the metadata for the provided trace link to the provided value and sets
	 * it within the given metadata container.
	 * 
	 * @param trace             the trace link whose metadata should be set
	 * @param metaData          the metadata to set
	 * @param metadataContainer the container that should contain the metadata for
	 *                          the trace link
	 */
	void setMetadataForTrace(EObject trace, EObject metaData, EObject metadataContainer);

	/**
	 * Retrieves the metadata for the provided artifact from the given metadata
	 * container. If no metadata is found for the artifact, an empty metadata object
	 * is created and returned.
	 * 
	 * @param wrapper           the artifact whose metadata should be retrieved
	 * @param metadataContainer the container that contains the metadata for the
	 *                          trace link
	 * @return the metadata for the trace link
	 */
	EObject getMetadataForArtifact(EObject wrapper, EObject metadataContainer);

	/**
	 * Sets the metadata for the provided artifact to the provided value and sets it
	 * within the given metadata container.
	 * 
	 * @param wrapper           the artifact whose metadata should be set
	 * @param metaData          the metadata to set
	 * @param metadataContainer the container that should contain the metadata for
	 *                          the artifact
	 */
	void setMetadataForArtifact(EObject wrapper, EObject metaData, EObject metadataContainer);

	/**
	 * Removes the metadata for the provided object from the metadata container.
	 * This method does not persist the changes and should not be called by clients
	 * directly. Instead, it should be called when a trace link or an artifact is
	 * removed by the respective implementation of
	 * {@link ITraceabilityInformationModelAdapter#deleteTrace(java.util.List, EObject)}.
	 *
	 * 
	 * @param object            the object whose metadata should be removed
	 * @param metadataContainer the container which contains the metadata to be
	 *                          removed
	 */
	void removeMetadata(EObject object, EObject metadataContainer);
}
