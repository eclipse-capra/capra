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
package org.eclipse.capra.handler.reqif;

import java.util.Collections;
import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.handler.reqif.preferences.ReqifPreferences;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.rmf.reqif10.AttributeValue;
import org.eclipse.rmf.reqif10.SpecHierarchy;
import org.eclipse.rmf.reqif10.SpecObject;
import org.eclipse.rmf.reqif10.common.util.ReqIF10Util;

public class ReqIfHandler extends AbstractArtifactHandler<SpecHierarchy> {

	// TODO: This used to expect IStructuredSelection input, why?

	@Override
	public EObject createWrapper(SpecHierarchy spec, EObject artifactModel) {
		return spec;
	}

	@Override
	public SpecHierarchy resolveWrapper(EObject wrapper) {
		return (SpecHierarchy) wrapper;
	}

	@Override
	public String getDisplayName(SpecHierarchy spec) {
		IEclipsePreferences preference = ReqifPreferences.getPreferences();
		String idAttribute = preference.get(ReqifPreferences.REQIF_ID_ATTRIBUTE,
				ReqifPreferences.REQIF_ID_ATTRIBUTE_DEFAULT);
		SpecObject specObject = spec.getObject();
		if (specObject != null) {
			AttributeValue attributeValue = ReqIF10Util.getAttributeValueForLabel(specObject, idAttribute);
			if (attributeValue != null) {
				return ReqIF10Util.getTheValue(attributeValue).toString();
			} else {
				// as a default fall back use the first column as an ID
				// column
				AttributeValue defaultAttributeValue = specObject.getValues().get(0);
				return ReqIF10Util.getTheValue(defaultAttributeValue).toString();
			}
		} else // empty row is selected
			return "";
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		return null;
	}

	@Override
	public List<Connection> addInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes) {
		// Method currently left empty to wait for user requirements of relevant
		// internal links for REQIF requirements.
		return Collections.emptyList();
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		return false;
	}
}
