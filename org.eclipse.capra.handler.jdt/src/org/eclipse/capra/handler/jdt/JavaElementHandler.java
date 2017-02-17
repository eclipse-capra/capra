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
package org.eclipse.capra.handler.jdt;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.handlers.AnnotationException;
import org.eclipse.capra.core.handlers.IAnnotateArtifact;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.handler.jdt.preferences.JDTPreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;

/**
 * A handler to allow creating traces to and from java elements such as classes
 * and methods based on JDT.
 */
public class JavaElementHandler extends AbstractArtifactHandler<IJavaElement> implements IAnnotateArtifact {

	@Override
	public EObject createWrapper(IJavaElement element, EObject artifactModel) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		EObject wrapper = adapter.createArtifact(artifactModel, this.getClass().getName(), element.getHandleIdentifier(),
				element.getElementName());
		return wrapper;
	}

	@Override
	public IJavaElement resolveWrapper(EObject wrapper) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		String uri = adapter.getArtifactUri(wrapper);
		return JavaCore.create(uri);
	}

	@Override
	public String getDisplayName(IJavaElement element) {
		return element.getElementName();
	}

	@Override
	public void annotateArtifact(EObject wrapper, String annotation) throws AnnotationException {
		IEclipsePreferences preferences = JDTPreferences.getPreferences();
		if (preferences.getBoolean(JDTPreferences.ANNOTATE_JDT, JDTPreferences.ANNOTATE_JDT_DEFAULT)) {
			IJavaElement handle = resolveWrapper(wrapper);
			JDTAnnotate.annotateArtifact(handle, annotation);
		}
	}

}
