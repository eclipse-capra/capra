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
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.office.model.CapraOfficeObject;
import org.eclipse.emf.ecore.EObject;

/**
 * A handler to create trace links from and to content of Office files.
 * 
 * @author Dusan Kalanj
 * 
 */
public class OfficeHandler extends AbstractArtifactHandler<CapraOfficeObject> {

	@Override
	public EObject createWrapper(CapraOfficeObject officeObject, EObject artifactModel) {
		// Returns the EObject corresponding to the input object if the input is
		// an EObject, or if it is Adaptable to an EObject
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		// TODO here artifactName is the same as the row/paragraph
		// description. Should it be different?
		EObject wrapper = adapter.createArtifact(artifactModel, this.getClass().getName(), officeObject.getUri(),
				officeObject.getId());
		return wrapper;
	}

	@Override
	public CapraOfficeObject resolveWrapper(EObject wrapper) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		String uri = adapter.getArtifactUri(wrapper);
		CapraOfficeObject object = new CapraOfficeObject();
		object.setUri(uri);
		return object;
	}

	@Override
	public String getDisplayName(CapraOfficeObject officeObject) {
		return officeObject.getId();
	}

}
