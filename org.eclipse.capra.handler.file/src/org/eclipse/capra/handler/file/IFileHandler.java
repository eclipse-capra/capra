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
package org.eclipse.capra.handler.file;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.emf.ecore.EObject;

/**
 * Handler to allow tracing to and from arbitrary files in the file system.
 */
public class IFileHandler extends AbstractArtifactHandler<IFile> {

	@Override
	public EObject createWrapper(IFile file, EObject artifactModel) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		EObject wrapper = adapter.createArtifact(artifactModel, this.getClass().getName(),
				file.getFullPath().toString(), file.getName(), file.getFullPath().toString());
		return wrapper;
	}

	@Override

	public IFile resolveWrapper(EObject wrapper) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		String uri = adapter.getArtifactUri(wrapper);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(uri));
		return file;
	}

	@Override
	public String getDisplayName(IFile file) {
		return file.getName();
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		return delta.getResource().getName()
				+ " has been changed. Please check if associated trace links are still valid.";
	}

}
