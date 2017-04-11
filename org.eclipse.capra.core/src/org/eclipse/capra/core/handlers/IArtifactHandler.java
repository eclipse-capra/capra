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

import java.util.Optional;
import java.util.function.BiFunction;

import org.eclipse.emf.ecore.EObject;

/**
 * This interface defines functionality required to map chosen Objects in the
 * Eclipse workspace to wrappers which can then be traced and
 * persisted in EMF models.
 *
 * @param <T> The type of artifact that this object can handle. A handler with a parameter T should
 * 		return true when {@link IArtifactHandler#canHandleArtifact} is called with an object of that type.
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
	// It should be possible to test any object if it is of the right type, even if the type of this
	// handler is unknown. Therefore use Object here instead of T.
	boolean canHandleArtifact(Object artifact);
	

	/**
	 * If this handler can handle artifact, then call the functions with the argument and this handler
	 * as arguments.
	 * <p/>
	 * This methods can be used to handle artifacts in a type safe manner when the caller is sure that
	 * the artifact can be handled. Inside the handle function it will be possible to use the handler with
	 * this specific artifact only.
	 *  
	 * @param artifact The artifact to be handled
	 * @param handleFunction Must not return null.
	 * @return An optional with the result returned from the handle function if the artifact can be handled;
	 * 	otherwise an empty optional.
	 */
	// This method works by casting the artifact and the handler to the right type. This it not type safe
	// in itself, but by doing these casts with this methods a caller can be sure that artifacts and handlers
	// of incompatible types are not mixed up.
	<R> Optional<R> withCastedHandler(Object artifact, BiFunction<IArtifactHandler<T>, T, R> handleFunction);

	/**
	 * Convenience method that calls {@link IArtifactHandler#withCastedHandler} if caller knows that the artifact
	 * can be handled.
	 *  
	 * @throws IllegalArgumentException If this handler can not handle the artifact.
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
	 * Resolve the wrapper to the originally selected Object from the
	 * Eclipse workspace. This is essentially the inverse of the
	 * createWrapper operation.
	 *
	 * @param wrapper
	 *            The wrapped object
	 * @return originally selected object
	 */
	T resolveWrapper(EObject wrapper);

	/**
	 * Provide a name for the artifact to be used for display purposes.
	 * @param artifact
	 */
	String getDisplayName(T artifact);
}
