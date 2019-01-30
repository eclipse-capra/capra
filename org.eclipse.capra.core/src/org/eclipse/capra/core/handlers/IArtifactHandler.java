/*******************************************************************************
 * Copyright (c) 2017 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.core.handlers;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.emf.ecore.EObject;

/**
 * This interface defines functionality required to map chosen Objects in the
 * Eclipse workspace to wrappers which can then be traced and persisted in EMF
 * models.
 *
 * @param <T>
 *            The type of artifact that this object can handle. A handler with a
 *            parameter T should return true when
 *            {@link IArtifactHandler#canHandleArtifact} is called with an
 *            object of that type.
 */
public interface IArtifactHandler<T> {

	/**
	 * Does the handler support this object?
	 *
	 * @param artifact
	 *            The object to be wrapped
	 * @return <code>true</code> if object can be handled, <code>false</code>
	 *         otherwise.
	 */
	boolean canHandleArtifact(Object artifact);

	/**
	 * If this handler can handle artifact, then call the functions with the
	 * argument and this handler as arguments.
	 * <p/>
	 * This methods can be used to handle artifacts in a type safe manner when
	 * the caller is sure that the artifact can be handled. Inside the handle
	 * function it will be possible to use the handler with this specific
	 * artifact only.
	 * 
	 * @param artifact
	 *            The artifact to be handled
	 * @param handleFunction
	 *            Must not return null.
	 * @return An optional with the result returned from the handle function if
	 *         the artifact can be handled; otherwise an empty optional.
	 */
	<R> Optional<R> withCastedHandler(Object artifact, BiFunction<IArtifactHandler<T>, T, R> handleFunction);

	/**
	 * Convenience method that calls {@link IArtifactHandler#withCastedHandler}
	 * if caller knows that the artifact can be handled.
	 * 
	 * @throws IllegalArgumentException
	 *             If this handler can not handle the artifact.
	 */
	<R> R withCastedHandlerUnchecked(Object artifact, BiFunction<IArtifactHandler<T>, T, R> handleFunction);

	/**
	 * Create a wrapper for the object
	 *
	 * @param artifact
	 *            The object to be wrapped
	 * @param artifactModel
	 * @return
	 */
	EObject createWrapper(T artifact, EObject artifactModel);

	/**
	 * Resolve the wrapper to the originally selected Object from the Eclipse
	 * workspace. This is essentially the inverse of the createWrapper
	 * operation.
	 *
	 * @param wrapper
	 *            The wrapped object
	 * @return originally selected object
	 */
	T resolveWrapper(EObject wrapper);

	/**
	 * Provide a name for the artifact to be used for display purposes.
	 * 
	 * @param artifact
	 */
	String getDisplayName(T artifact);

	/**
	 * Returns the type that is handled by this <code>IArtifactHandler</code>.
	 * 
	 * @return the type that
	 */
	Class<T> getHandledClass();

	/**
	 * When a change in the resource occurs, it generates the message that is to
	 * be displayed by the Capra marker.
	 * 
	 * @param delta
	 *            represents changes in the state of a resource
	 * @param wrapperUri
	 *            uri of the artifact that is associated with the change
	 * @return the Capra marker message. Every marker must return a unique
	 *         message. If the message already exists it will be ignoored and a
	 *         marker will not be created.
	 */
	String generateMarkerMessage(IResourceDelta delta, String wrapperUri);

	/**
	 * Adds internal links related to a given element.
	 * <p>
	 * This is helpful if Eclipse Capra should include links within a DSL, e.g.,
	 * associations between classes, in its visualisations and analysis. A
	 * handler that implements this method should return a list of
	 * {@code Connection}. This list will be used in visualisations and
	 * analysis, but not persisted in Capra's own trace model.
	 * 
	 * @param investigatedElement
	 *            element currently under investigation for links
	 * @param selectedRelationshipTypes
	 *            relationship types selected by the user; this ensures that
	 *            Capra only includes specific link types in the visualisation
	 *            and analysis and implementation of the method should filter on
	 *            this list
	 * 
	 * @return a list of {@link Connection} between the
	 *         {@code investigatedElement} and other elements the handler takes
	 *         care of
	 */
	List<Connection> addInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes);

	/**
	 * Decide if two objects have an internal link between them.
	 *
	 * @param first
	 *            First object
	 * @param second
	 *            Second object
	 * @param traceModel
	 *            Trace model to base decision on
	 * @return <code>true</code> if object are connected, <code>false</code>
	 *         otherwise
	 */
	boolean isThereAnInternalTraceBetween(EObject first, EObject second);

}
