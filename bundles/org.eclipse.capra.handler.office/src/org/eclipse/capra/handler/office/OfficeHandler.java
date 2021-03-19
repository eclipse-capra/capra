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

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
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
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();
		return adapter.createArtifact(artifactModel, this.getClass().getName(), officeObject.getUri().toString(),
				this.getDisplayName(officeObject), officeObject.getUri().toString());
	}

	@Override
	public CapraOfficeObject resolveWrapper(EObject wrapper) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();
		String uri = adapter.getArtifactUri(wrapper);
		CapraOfficeObject object = new CapraOfficeObject();
		object.setUri(uri);
		object.setData(adapter.getArtifactName(wrapper));
		return object;
	}

	@Override
	public String getDisplayName(CapraOfficeObject officeObject) {
		StringBuilder displayName = new StringBuilder();
		if (officeObject.getUri().getQuery().contains(CapraOfficeObject.SHEET_PARAMETER)) {
			String sheetName = "";
			String rowId = "";
			List<NameValuePair> params = URLEncodedUtils.parse(officeObject.getUri(), Charset.forName("UTF-8"));
			for (NameValuePair param : params) {
				if (param.getName().equals(CapraOfficeObject.SHEET_PARAMETER)) {
					sheetName = param.getValue();
				} else if (param.getName().equals(CapraOfficeObject.ROW_PARAMETER)) {
					rowId = param.getValue();
				}
			}

			displayName.append(sheetName);
			displayName.append("!");
			displayName.append(rowId);
			displayName.append(" (");
			displayName.append(Paths.get(officeObject.getUri().getPath()).getFileName().toString());
			displayName.append(")");
		} else {
			int minAllowed = Activator.getDefault().getPreferenceStore().getInt(OfficePreferences.CHAR_COUNT);
			String text = officeObject.toString();
			int textLength = Math.min(text.length(), minAllowed);
			if (textLength == minAllowed) {
				text = text.substring(0, textLength) + "...";
			}
			// Remove new lines
			text = text.replaceAll("\\R+", " ");
			displayName.append(text);
		}
		return displayName.toString();
	}

	@Override
	public String generateMarkerMessage(org.eclipse.core.resources.IResourceDelta delta, String wrapperUri) {
		// We do not generate markers for Office documents.
		return null;
	}

	@Override
	public List<Connection> getInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes) {
		// Method currently left empty to wait for user requirements of relevant
		// internal links for Office documents.
		return Collections.emptyList();
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		return false;
	}

}
