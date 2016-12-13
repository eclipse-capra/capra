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
package org.eclipse.capra.core.handlers;

import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.emf.ecore.EObject;

/**
 * This interface defines functionality required to map chosen Objects in the
 * Eclipse workspace to EObjects of some kind, which can then be traced and
 * persisted in EMF models.
 * 
 * Implementations can use the provided concepts of an {@link ArtifactWrapper}
 * if this is suitable.
 * 
 * @author Anthony Anjorin, Salome Maro
 */
public interface ArtifactHandler {

	/**
	 * Can the handler map selection to an EObject as required?
	 * 
	 * @param selection
	 *            The object to be mapped to an EObject
	 * @return <code>true</code> if selection can be handled, <code>false</code>
	 *         otherwise.
	 */
	boolean canHandleSelection(Object selection);

	/**
	 * Map the object selection to an EObject.
	 * 
	 * @param selection
	 *            The object to be mapped
	 * @param artifactModel
	 * @return
	 */
	EObject getEObjectForSelection(Object selection, EObject artifactModel);

	/**
	 * Resolve the persisted EObject to the originally selected Object from the
	 * Eclipse workspace. This is essentially the inverse of the
	 * getEObjectForSelection operation.
	 * 
	 * @param artifact
	 *            The persisted EObject
	 * @return originally selected object
	 */
	Object resolveArtifact(EObject artifact);

	/**
	 * When a change in the resource occurs, it generates the message that is to
	 * be displayed by the Capra marker.
	 * 
	 * @param delta
	 *            represents changes in the state of a resource
	 * @param wrapperUri
	 *            uri of the artifact that is associated with the change
	 * @return the Capra marker message. Every marker must return a unique message.
         *         If the message already exists it will be ignoored and a marker will 
         *         not be created.
	 */
	String generateMarkerMessage(IResourceDelta delta, String wrapperUri);

	/**
	 * Provide the display name to be displayed in the selection view when an
	 * object is dropped. This is a default implementation but gives flexibility
	 * for each handler to decide how its objects should look like when dropped
	 * to the selection view.
	 * 
	 * @param selection
	 *            The selected object to be added to the selection view
	 * @return The string that should be displayed in the selection view.
	 */
	default String getDisplayName(Object selection) {
		if (selection instanceof EObject) {
			return EMFHelper.getIdentifier((EObject) selection);
		} else
			return selection.toString();
	}

}
