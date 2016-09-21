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
package org.eclipse.capra.core.adapters;

import org.eclipse.capra.core.handlers.ArtifactHandler;
import org.eclipse.emf.ecore.EObject;

/**
 * This interface defines the functionality necessary to deal with meta models
 * that describe the artifacts to and from which trace links are created.
 */
public interface ArtifactMetaModelAdapter {

	/**
	 * Create a new model for artifacts.
	 * 
	 * @return the new model
	 */
	EObject createModel();

	/**
	 * Create a new artifact. The list of artifacts is searched for an existing
	 * artifact with the same handler and uri. If found, the existing artifact
	 * is returned, otherwise a new artifact is created.
	 * 
	 * @param artifactHandler
	 *            Handler of artifact
	 * @param artifactUri
	 *            Uri of artifact
	 * @param artifactName
	 *            Name of artifact
	 * @return new or existing artifact
	 */
	EObject createArtifact(EObject artifactModel, String artifactHandler, String artifactUri, String artifactName);

	/**
	 * Get artifact with given handler and uri.
	 * 
	 * @param artifactHandler
	 *            Handler of artifact
	 * @param artifactUri
	 *            Uri of artifact
	 * @return artifact if found, null otherwise
	 */
	EObject getArtifact(EObject artifactModel, String artifactHandler, String artifactUri);

	/**
	 * Get a handler for the given artifact
	 * 
	 * @param artifact
	 * @return artifact handler
	 */
	String getArtifactHandler(EObject artifact);

	/**
	 * Get the name of the given artifact.
	 * 
	 * @param artifact
	 * @return artifact name
	 */
	String getArtifactName(EObject artifact);

	/**
	 * Get the URI of the given artifact.
	 * 
	 * @param artifact
	 * @return artifact uri
	 */
	String getArtifactUri(EObject artifact);

	/**
	 * Get an instance of the artifact handler.
	 * 
	 * @param artifact
	 * @return artifact handler instance
	 */
	ArtifactHandler getArtifactHandlerInstance(EObject artifact);

}
