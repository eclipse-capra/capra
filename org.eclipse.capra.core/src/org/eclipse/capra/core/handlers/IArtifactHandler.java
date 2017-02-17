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

import org.eclipse.emf.ecore.EObject;

/**
 * This interface defines functionality required to map chosen Objects in the
 * Eclipse workspace to wrappers which can then be traced and
 * persisted in EMF models.
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
	boolean canHandleArtifact(T artifact);

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
