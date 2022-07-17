/*******************************************************************************
 * Copyright (c) 2016-2022 Chalmers | University of Gothenburg, rt-labs and others.
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
import org.eclipse.emf.ecore.EObject;

/**
 * This interface defines the functionality necessary to deal with meta models
 * that describe the artifacts to and from which trace links are created.
 * <p>
 * An artifact model is used to capture all wrappers, i.e., {@link EObject}
 * instances that describe artifacts that are not themselves {@code EObject}
 * instances. Since Eclipse Capra uses EMF internally to represent links,
 * artifacts, etc. all artifacts that are either the origin or the source of a
 * link need to be represented as {@code EObject}s as well. This means that
 * artifacts for which this is not true need to have a &quot;wrapper&quot; that
 * contains the relevant information about these artifacts such as their names
 * and URIs. These wrappers are stored in an artifact model and the structure of
 * this artifact model is in turn described by an artifact meta-model.
 * Descendents of this class provides access to the wrappers via the artifact
 * model.
 * <p>
 * In general, artifacts should always be uniquely identifiable via their URI.
 * It is the responsibility of the code that calls
 * {@link #createArtifact(EObject, String, String, String, String)} to ensure
 * that the URI is indeed unique. That is especially important if several
 * artifacts reside in the same file (e.g., different methods in the same Java
 * class). However, to allow handlers to store additional information about the
 * retrieval of the artifact, an addition {@code internalResolver} can be stored
 * with each artifact.
 */
public interface IArtifactMetaModelAdapter {

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
	 * @param artifactModel    the artifact model to add the artifact to
	 * @param artifactHandler  the handler responsible for dealing with the artifact
	 * @param artifactUri      the URI of the artifact
	 * @param internalResolver the internal information for resolving artifacts
	 * @param artifactName     the name of the artifact
	 * @return a newly created artifact or an existing artifact with the same
	 *         handler and URI
	 */
	EObject createArtifact(EObject artifactModel, String artifactHandler, String artifactUri, String internalResolver,
			String artifactName);

	/**
	 * Gets the artifact with the given handler, URI, and internal resolver.
	 *
	 * @param artifactHandler  Handler of artifact
	 * @param artifactUri      URI of artifact
	 * @param internalResolver the internal information for resolving artifacts
	 * @return artifact if found, null otherwise
	 */
	EObject getArtifact(EObject artifactModel, String artifactHandler, String artifactUri, String internalResolver);

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
	 * the resource. It can, e.g., consist of a sequence of sub-parts separated by
	 * '/'. Alternatively, it is possible to use parameters to identify elements as
	 * well. In that way tools that work with artifacts can use the sub-parts of the
	 * fragment for their own purposes.
	 * <p/>
	 * Examples:
	 * <p/>
	 * The JDT artifact handler uses the following encoding scheme for artifact
	 * URIs:
	 * {@code platform:/Project_name/path/to/file.java#com.pack.ClassName/methodName(int, String)}.
	 * <p/>
	 * The Office artifact handler uses parameters to identify rows in specific
	 * sheets in Excel files:
	 * {@code platform:/Project_name/path/to/file.xlsx?sheet=Sheet1&row=A1
	 *
	 * @param artifact the artifact whose URI should be retrieved
	 * 
	 * @return the URI of the given artifact
	 */
	String getArtifactUri(EObject artifact);

	/**
	 * Gets the artifact's internal resolver, i.e., additional information the
	 * artifact handler can use to retrieve relevant information about the artifact
	 * and to restore its state.
	 * <p>
	 * While an artifact's URI should contain the necessary information to
	 * reconstruct an artifact, this additional information storage can be helpful
	 * if some aspects cannot or should not be encoded in the URI. Can be
	 * {@code null}.
	 * 
	 * @param artifact the artifact whose internal resolver should be retrieved.
	 * @return a {@code String} containing internal information that handlers use to
	 *         locate and reconstruct the artifact
	 */
	String getArtifactInternalResolver(EObject artifact);

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
