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

import java.util.List;

import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.core.runtime.IPath;
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
	 * TODO: The implementation of this method delegates to the other methods
	 * with the same name. It exists to enables implementing objects to work
	 * with both the old and the new API during a transmission period.
	 */
	default EObject createArtifact(EObject artifactModel, String artifactHandler, String artifactUri, 
		String artifactName, String artifactFilePath) {
		return createArtifact(artifactModel, artifactHandler, artifactUri, artifactUri, artifactName, artifactFilePath);
	}

	/**
	 * Create a new artifact. The list of artifacts is searched for an existing
	 * artifact with the same handler and URI. If found, the existing artifact
	 * is returned, otherwise a new artifact is created.
	 * <p/>
	 * TODO: The implementation of this method delegates to the other methods
	 * with the same name. It exists to enables implementing objects to work
	 * with both the old and the new API during a transmission period.
	 */
	default EObject createArtifact(EObject artifactModel, String artifactHandler, String artifactUri, 
		String artifactId, String artifactName, String artifactFilePath) {
		return createArtifact(artifactModel, artifactHandler, artifactId, artifactName, artifactFilePath);
	}

	/**
	 * Get artifact with given handler and URI.
	 *
	 * @param artifactHandler
	 *            Handler of artifact
	 * @param artifactUri
	 *            URI of artifact
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
	 * Get the URI of the given artifact. The URI be a string with a valid URI
	 * syntax. 
	 * <p/>
	 * The path part should refer to a concrete resource, such as a
	 * file or a web page. 
	 * <p/>
	 * The fragment part should (if necessary) uniquely
	 * identify the artifact within the resource. It can consists of a sequence of sub-parts separated '/'. In that
	 * way tools that work with artifacts can used the sub-parts of the fragment for their own purposes.
	 * <p/>
	 * Example: The JDT artifact handler uses the following encoding scheme for artifact URI:s:
	 * {@code platform:/Project_name/path/to/file.java#com.pack.ClassName/methodName(int, String)}.
	 *
	 * @param artifact
	 * @return artifact uri
	 */
	String getArtifactUri(EObject artifact);

	/**
	 * @return An internal string that handlers use to locate and reconstruct the artifact.
	 */
	default String getArtifactIdentifier(EObject artifact) {
		return getArtifactUri(artifact);
	}
	
	/**
	 * Get the path of the given artifact.
	 *
	 * @param artifact
	 * @return path of the file, referenced by the artifact
	 */
	IPath getArtifactPath(EObject artifact);

	/**
	 * Get an instance of the artifact handler.
	 *
	 * @param artifact
	 * @return artifact handler instance
	 */
	IArtifactHandler<?> getArtifactHandlerInstance(EObject artifact);

	/**
	 * Returns a list of all artifacts in an artifact model
	 *
	 * @param artifactModel
	 * @return a list of all artifacts in the artifact model as EObjects
	 */
	List<EObject> getAllArtifacts(EObject artifactModel);

}
