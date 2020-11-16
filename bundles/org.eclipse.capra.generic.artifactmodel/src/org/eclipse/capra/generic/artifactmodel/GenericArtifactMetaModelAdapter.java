package org.eclipse.capra.generic.artifactmodel;
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

import java.util.List;

import org.eclipse.capra.core.adapters.AbstractArtifactMetaModelAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * Provides generic functionality to deal with artifact meta models.
 */
public class GenericArtifactMetaModelAdapter extends AbstractArtifactMetaModelAdapter {

	private ArtifactWrapperContainer getContainer(EObject artifactModel) {
		return (ArtifactWrapperContainer) artifactModel;
	}

	@Override
	public EObject createModel() {
		return ArtifactmodelFactory.eINSTANCE.createArtifactWrapperContainer();
	}

	@Override
	public EObject getArtifact(EObject artifactModel, String artifactHandler, String artifactUri) {
		ArtifactWrapperContainer container = getContainer(artifactModel);
		for (ArtifactWrapper artifact : container.getArtifacts()) {
			if (getArtifactHandler(artifact).equals(artifactHandler) && getArtifactUri(artifact).equals(artifactUri))
				return artifact;
		}
		return null;
	}

	@Override
	public EObject createArtifact(EObject artifactModel, String artifactHandler, String artifactUri, String artifactId,
			String artifactName, String artifactPath) {
		ArtifactWrapperContainer container = getContainer(artifactModel);
		EObject existingWrapper = getArtifact(artifactModel, artifactHandler, artifactUri);
		if (existingWrapper != null)
			return existingWrapper;

		ArtifactWrapper wrapper = ArtifactmodelFactory.eINSTANCE.createArtifactWrapper();
		wrapper.setArtifactHandler(artifactHandler);
		wrapper.setUri(artifactUri);
		wrapper.setName(artifactName);
		wrapper.setPath(artifactPath);
		wrapper.setIdentifier(artifactId);

		TransactionalEditingDomain editingDomain = EditingDomainHelper.getEditingDomain();
		// We're saving the trace model and the artifact model in the same transaction
		Command cmd = new RecordingCommand(editingDomain, "Add trace") {
			@Override
			protected void doExecute() {
				container.getArtifacts().add(wrapper);
			}
		};

		try {
			((TransactionalCommandStack) editingDomain.getCommandStack()).execute(cmd, null); // default options
		} catch (RollbackException e) {
			throw new IllegalStateException("Adding a trace link was rolled back.", e);
		} catch (InterruptedException e) {
			throw new IllegalStateException("Adding a trace link was interrupted.", e);
		}

		return wrapper;
	}

	@Override
	public String getArtifactHandler(EObject artifact) {
		if (artifact instanceof ArtifactWrapper) {
			ArtifactWrapper wrapper = (ArtifactWrapper) artifact;
			return wrapper.getArtifactHandler();
		}
		return null;
	}

	@Override
	public String getArtifactName(EObject artifact) {
		if (artifact instanceof ArtifactWrapper) {
			ArtifactWrapper wrapper = (ArtifactWrapper) artifact;
			return wrapper.getName();
		}
		return null;
	}

	@Override
	public String getArtifactUri(EObject artifact) {
		if (artifact instanceof ArtifactWrapper) {
			ArtifactWrapper wrapper = (ArtifactWrapper) artifact;
			return wrapper.getUri();
		}
		return null;
	}

	@Override
	public String getArtifactIdentifier(EObject artifact) {
		if (artifact instanceof ArtifactWrapper) {
			ArtifactWrapper wrapper = (ArtifactWrapper) artifact;
			return wrapper.getIdentifier();
		}
		return null;
	}

	@Override
	public IPath getArtifactPath(EObject artifact) {
		if (artifact instanceof ArtifactWrapper) {
			ArtifactWrapper wrapper = (ArtifactWrapper) artifact;
			return new Path(wrapper.getPath());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EObject> getAllArtifacts(EObject artifactModel) {
		ArtifactWrapperContainer container = getContainer(artifactModel);
		return (List<EObject>) (Object) container.getArtifacts();
	}

}
