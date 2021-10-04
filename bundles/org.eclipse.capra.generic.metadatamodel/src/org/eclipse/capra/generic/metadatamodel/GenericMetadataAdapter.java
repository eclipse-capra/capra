/*******************************************************************************
 * Copyright (c) 2016, 2021 Chalmers | University of Gothenburg, rt-labs and others.
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
package org.eclipse.capra.generic.metadatamodel;

import org.eclipse.capra.core.adapters.IMetadataAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * Implements the methods of {@link IMetadataAdapter} for Eclipse Capra's
 * default metadata implementation.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class GenericMetadataAdapter implements IMetadataAdapter {

	private MetadataContainer getContainer(EObject container) {
		if (container.eClass().equals(MetadatamodelPackage.eINSTANCE.getMetadataContainer())) {
			return (MetadataContainer) container;
		}
		throw new IllegalArgumentException("The provider metadata container is not of the correct type");
	}

	@Override
	public EObject createModel() {
		return MetadatamodelFactory.eINSTANCE.createMetadataContainer();
	}

	@Override
	public EObject getMetadataForTrace(EObject trace, EObject metadataContainer) {
		MetadataContainer container = this.getContainer(metadataContainer);
		for (TraceMetadata metaData : container.getTraceMetadata()) {
			if (EcoreUtil.equals(trace, metaData.getTrace())) {
				return metaData;
			}
		}
		TraceMetadata metadata = MetadatamodelFactory.eINSTANCE.createTraceMetadata();
		metadata.setTrace(trace);
		addInTransaction(container.getTraceMetadata(), metadata);
		return metadata;
	}

	@Override
	public void setMetadataForTrace(EObject trace, EObject metaData, EObject metadataContainer) {
		EObject existingMetadata = this.getMetadataForTrace(trace, metadataContainer);
		if (metaData.equals(existingMetadata)) {
			return;
		}
		// TODO: Check if the trace property is set and do so if necessary.
		updateObjectInTransaction(metaData, existingMetadata);
	}

	@Override
	public EObject getMetadataForArtifact(EObject wrapper, EObject metadataContainer) {
		MetadataContainer container = this.getContainer(metadataContainer);
		for (ArtifactMetadata metaData : container.getArtifactMetadata()) {
			if (EcoreUtil.equals(wrapper, metaData.getArtifact())) {
				return metaData;
			}
		}
		ArtifactMetadata metadata = MetadatamodelFactory.eINSTANCE.createArtifactMetadata();
		metadata.setArtifact(wrapper);
		addInTransaction(container.getArtifactMetadata(), metadata);
		return metadata;
	}

	@Override
	public void setMetadataForArtifact(EObject wrapper, EObject metaData, EObject metadataContainer) {
		ArtifactMetadata existingMetadata = (ArtifactMetadata) this.getMetadataForArtifact(wrapper, metadataContainer);
		if (metaData.equals(existingMetadata)) {
			return;
		}
		updateObjectInTransaction(metaData, existingMetadata);
	}

	/**
	 * Adds the provided element to the given list inside a transaction.
	 * 
	 * @param <T>    the type of the object to add
	 * @param list   the list to which to add the element
	 * @param object the object to add to the list
	 */
	private <T> void addInTransaction(EList<T> list, T object) {
		TransactionalEditingDomain editingDomain = EditingDomainHelper.getEditingDomain();
		// We're saving the trace model and the artifact model in the same transaction
		Command cmd = new RecordingCommand(editingDomain, "Add trace") {
			@Override
			protected void doExecute() {
				list.add(object);
			}
		};

		try {
			((TransactionalCommandStack) editingDomain.getCommandStack()).execute(cmd, null); // default options
		} catch (RollbackException e) {
			throw new IllegalStateException("Adding to the list was rolled back.", e);
		} catch (InterruptedException e) {
			throw new IllegalStateException("Adding to the list was interrupted.", e);
		}
	}

	/**
	 * Updates the existing object with values from the updated object by copying
	 * the values of all structural features.
	 * 
	 * @param updatedObject  the object containing the new values
	 * @param existingObject the existing object to be updated
	 */
	private void updateObjectInTransaction(EObject updatedObject, EObject existingObject) {
		TransactionalEditingDomain editingDomain = EditingDomainHelper.getEditingDomain();
		// We're saving the trace model and the artifact model in the same transaction
		Command cmd = new RecordingCommand(editingDomain, "Update Trace Model") {
			@Override
			protected void doExecute() {
				for (EStructuralFeature feature : updatedObject.eClass().getEStructuralFeatures()) {
					try {
						existingObject.eSet(feature, updatedObject.eGet(feature));
					} catch (IllegalArgumentException ex) {
						// Deliberately do nothing.
					}
				}
			}
		};

		try {
			((TransactionalCommandStack) editingDomain.getCommandStack()).execute(cmd, null);
		} catch (RollbackException e) {
			throw new IllegalStateException("Updating trace metadata was rolled back.", e);
		} catch (InterruptedException e) {
			throw new IllegalStateException("Updating trace metadata was interrupted.", e);
		}
	}

}
