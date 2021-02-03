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
package org.eclipse.capra.handler.cdt;

import java.util.Collections;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.handlers.AnnotationException;
import org.eclipse.capra.core.handlers.IAnnotateArtifact;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.handler.cdt.preferences.CDTPreferences;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.emf.ecore.EObject;

/**
 * Handler to allow tracing to and from elements of C such as files and
 * functions. Uses CDT as the foundation.
 * </p>
 * This handler encodes a locator to the C element in artifact URI:s in the
 * following way:
 * {@code platform:/Project_name/path/to/file.c#classOrStructName/memberName}
 * <p/>
 * {@code platform:/Project_name/path/to/file.c#functionClassOrStructName}.
 */
public class CDTHandler extends AbstractArtifactHandler<ICElement> implements IAnnotateArtifact {

	@Override
	public EObject createWrapper(ICElement element, EObject artifactModel) {
		ICompositeType type = (ICompositeType) element.getParent().getAncestor(ICElement.C_CLASS);
		if (type == null)
			type = (ICompositeType) element.getParent().getAncestor(ICElement.C_STRUCT);
		if (type == null)
			type = (ICompositeType) element.getParent().getAncestor(ICElement.C_UNION);

		// TODO: Will this be unique? I don't know.
		String typePrefix = type == null ? "" : type.getName() + "/";

		// TODO: This does not take C++ function overloading on argument types
		// into account when
		// constructing the URI
		String uri = new URIBuilder().setScheme("platform").setPath("/resource" + element.getPath())
				.setFragment(typePrefix + element.getElementName()).toString();

		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();
		return adapter.createArtifact(artifactModel, this.getClass().getName(), uri, element.getHandleIdentifier(),
				element.getElementName(), element.getPath().toString());
	}

	@Override
	public ICElement resolveWrapper(EObject wrapper) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();
		return CoreModel.create(adapter.getArtifactIdentifier(wrapper));
	}

	@Override
	public String getDisplayName(ICElement element) {
		return element.getElementName();
	}

	@Override
	public void annotateArtifact(EObject wrapper, String annotation) throws AnnotationException {
		IEclipsePreferences preferences = CDTPreferences.getPreferences();

		if (!preferences.getBoolean(CDTPreferences.ANNOTATE_CDT, CDTPreferences.ANNOTATE_CDT_DEFAULT))
			return;

		ICElement handle = resolveWrapper(wrapper);

		try {
			CDTAnnotate.annotateArtifact(handle, annotation);
		} catch (CoreException e) {
			throw new AnnotationException(e.getStatus());
		}
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		return null;
	}

	@Override
	public List<Connection> getInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes) {
		// Method currently left empty to wait for user requirements of relevant
		// internal links for C code
		return Collections.emptyList();
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		return false;
	}
}
