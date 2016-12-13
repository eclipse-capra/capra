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
package org.eclipse.capra.handler.cdt;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.handlers.AnnotationException;
import org.eclipse.capra.core.handlers.IAnnotateArtifact;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.handler.cdt.preferences.CDTPreferences;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.emf.ecore.EObject;

/**
 * Handler to allow tracing to and from elements of C such as files and
 * functions. Uses CDT as the foundation.
 */
public class CDTHandler extends AbstractArtifactHandler<ICElement> implements IAnnotateArtifact {

	@Override
	public EObject createWrapper(ICElement element, EObject artifactModel) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		EObject wrapper = adapter.createArtifact(artifactModel, this.getClass().getName(),
				element.getHandleIdentifier(), element.getElementName(), element.getPath().toString());
		return wrapper;
	}

	@Override
	public ICElement resolveWrapper(EObject wrapper) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		String uri = adapter.getArtifactUri(wrapper);
		return CoreModel.create(uri);
	}

	@Override
	public String getDisplayName(ICElement element) {
		return element.getElementName();
	}

	@Override
	public void annotateArtifact(EObject wrapper, String annotation) throws AnnotationException {
		IEclipsePreferences preferences = CDTPreferences.getPreferences();
		
		if (!preferences.getBoolean(CDTPreferences.ANNOTATE_CDT, CDTPreferences.ANNOTATE_CDT_DEFAULT)) return;
		
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
}
