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
package org.eclipse.capra.handler.mylyn;

import java.util.Collections;
import java.util.List;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * A handler to allow tracing from and to tasks handled by Mylyn.
 */
public class MylynHandler extends AbstractArtifactHandler<ITask> {

	@Override
	public EObject createWrapper(ITask task, EObject artifactModel) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		EObject wrapper = adapter.createArtifact(artifactModel, this.getClass().getName(), task.getUrl(),
				task.getSummary(), task.getUrl());
		return wrapper;
	}

	@Override
	public ITask resolveWrapper(EObject wrapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayName(ITask task) {
		return task.getTaskId() + " : " + task.getSummary();
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		return null;
	}

	@Override
	public List<Connection> addInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes) {
		// Method currently left empty to wait for user requirements of relevant
		// internal links for EMF models
		return Collections.emptyList();
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		return false;
	}
}
