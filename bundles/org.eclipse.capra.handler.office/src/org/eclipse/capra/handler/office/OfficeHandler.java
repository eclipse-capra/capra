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

package org.eclipse.capra.handler.office;

import java.util.Collections;
import java.util.List;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.office.Activator;
import org.eclipse.capra.ui.office.model.CapraOfficeObject;
import org.eclipse.capra.ui.office.preferences.OfficePreferences;
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
		EObject wrapper = adapter.createArtifact(artifactModel, this.getClass().getName(), officeObject.getUri(),
				this.getDisplayName(officeObject), officeObject.getUri());
		return wrapper;
	}

	@Override
	public CapraOfficeObject resolveWrapper(EObject wrapper) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		String uri = adapter.getArtifactUri(wrapper);
		CapraOfficeObject object = new CapraOfficeObject();
		object.setUri(uri);
		object.setData(adapter.getArtifactName(wrapper));
		return object;
	}

	@Override
	public String getDisplayName(CapraOfficeObject officeObject) {
		int minAllowed = Activator.getDefault().getPreferenceStore().getInt(OfficePreferences.CHAR_COUNT);
		String text = officeObject.toString();
		int textLength = Math.min(text.length(), minAllowed);
		if (textLength == minAllowed) {
			text = text.substring(0, textLength) + "...";
		}
		// Remove new lines
		text = text.replaceAll("\\R+", " ");
		return text;
	}

	@Override
	public String generateMarkerMessage(org.eclipse.core.resources.IResourceDelta delta, String wrapperUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Connection> addInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes) {
		// Method currently left empty to wait for user requirements of relevant
		// internal links for Office documents.
		return Collections.emptyList();
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		return false;
	}

}
