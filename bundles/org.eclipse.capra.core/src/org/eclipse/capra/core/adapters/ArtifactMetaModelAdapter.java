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
package org.eclipse.capra.core.adapters;

import java.util.List;

import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

/**
 * This interface defines the functionality necessary to deal with meta models
 * that describe the artifacts to and from which trace links are created.
 * 
 * An artifact model is used to capture all wrappers, i.e., {@link EObject}
 * instances that describe artifacts that are not themselves {@code EObject}
 * instances. Since Eclipse Capra uses EMF internally to represent links,
 * artifacts, etc. all artifacts that are either the origin or the source of a
 * link need to be represented as {@code EObject}s as well. This means that
 * artifacts for which this is not true need to have a &quot;wrapper&quot; that
 * contains the relevant information about these artifacts such as their names
 * and URIs. These wrappers are stored in an artifact model and the structure of
 * this artifact model is in turn described by an artifact meta-model. This
 * class provides access to the wrappers via the artifact model.
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
	 * artifact with the same handler and URI. If found, the existing artifact is
	 * returned, otherwise a new artifact is created.
	 * 
	 * TODO: The implementation of this method delegates to the other methods with
	 * the same name. It exists to enable implementing objects to work with both the
	 * old and the new API during a transition period. This implementation uses the
	 * artifacts URI as its unique identifier.
	 * 
	 * @param artifactModel
	 * @param artifactHandler
	 * @param artifactUri
	 * @param artifactId
	 * @param artifactName
	 * @param artifactFilePath
	 * @return
	 */
	default EObject createArtifact(EObject artifactModel, String artifactHandler, String artifactUri,
			String artifactName, String artifactFilePath) {
		return createArtifact(artifactModel, artifactHandler, artifactUri, artifactUri, artifactName, artifactFilePath);
	}

	/**
	 * Create a new artifact. The list of artifacts is searched for an existing
	 * artifact with the same handler and URI. If found, the existing artifact is
	 * returned, otherwise a new artifact is created.
	 * 
	 * @param artifactModel    the artifact model to add the artifact to
	 * @param artifactHandler  the handler responsible for dealing with the artifact
	 * @param artifactUri      the URI of the artifact
	 * @param artifactId       the unique identifier of the artifact
	 * @param artifactName     the name of the artifact
	 * @param artifactFilePath the file path to the artifact
	 * @return a newly created artifact or an existing artifact with the same
	 *         handler and URI
	 */
	EObject createArtifact(EObject artifactModel, String artifactHandler, String artifactUri, String artifactId,
			String artifactName, String artifactFilePath);

	/**
	 * Gets the artifact with the given handler and URI.
	 *
	 * @param artifactHandler Handler of artifact
	 * @param artifactUri     URI of artifact
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
	 * The path part should refer to a concrete resource, such as a file or a web
	 * page.
	 * <p/>
	 * The fragment part should (if necessary) uniquely identify the artifact within
	 * the resource. It can consists of a sequence of sub-parts separated '/'. In
	 * that way tools that work with artifacts can used the sub-parts of the
	 * fragment for their own purposes.
	 * <p/>
	 * Example: The JDT artifact handler uses the following encoding scheme for
	 * artifact URI:s:
	 * {@code platform:/Project_name/path/to/file.java#com.pack.ClassName/methodName(int, String)}.
	 *
	 * @param artifact
	 * @return artifact uri
	 */
	String getArtifactUri(EObject artifact);

	/**
	 * @return An internal string that handlers use to locate and reconstruct the
	 *         artifact.
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
