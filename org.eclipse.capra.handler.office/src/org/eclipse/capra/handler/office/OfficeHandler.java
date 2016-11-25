/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.capra.handler.office;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.handlers.ArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.office.objects.CapraOfficeObject;
import org.eclipse.emf.ecore.EObject;

/**
 * A handler to create trace links from and to content of Office files.
 * 
 * @author Dusan Kalanj
 * 
 */
public class OfficeHandler implements ArtifactHandler {

	@Override
	public boolean canHandleSelection(Object selection) {
		return selection instanceof CapraOfficeObject;
	}

	@Override
	public EObject getEObjectForSelection(Object selection, EObject artifactModel) {
		// Returns the EObject corresponding to the input object if the input is
		// an EObject, or if it is Adaptable to an EObject
		CapraOfficeObject officeObject = (CapraOfficeObject) selection;
		if (officeObject != null) {
			ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
			// TODO here artifactName is the same as the row/paragraph
			// description. Should it be different?
			EObject wrapper = adapter.createArtifact(artifactModel, this.getClass().getName(), officeObject.getUri(),
					officeObject.getName());
			return wrapper;
		} else {
			return null;
		}
	}

	@Override
	public Object resolveArtifact(EObject artifact) {
		// TODO Decide on how to resolve the artifact.
		// Just return the appropriate row / requirement or display it in the
		// OfficeView?
		return null;
	}

}
