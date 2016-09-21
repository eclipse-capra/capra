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
import org.eclipse.capra.core.handlers.ArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;

/**
 * A handler to allow creating traces to and from java elements such as classes
 * and methods based on JDT.
 */
public class JavaElementHandler implements ArtifactHandler {

	@Override
	public boolean canHandleSelection(Object selection) {
		if (selection instanceof IJavaElement) {
			return true;
		}

		return false;
	}

	@Override
	public EObject getEObjectForSelection(Object selection, EObject artifactModel) {
		IJavaElement cu = (IJavaElement) selection;
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		EObject wrapper = adapter.createArtifact(artifactModel, this.getClass().getName(), cu.getHandleIdentifier(),
				cu.getElementName());
		return wrapper;
	}

	@Override
	public IJavaElement resolveArtifact(EObject artifact) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		String uri = adapter.getArtifactUri(artifact);
		return JavaCore.create(uri);
	}

}
