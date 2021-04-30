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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

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
		return metadata;
	}

	@Override
	public void setMetadataForTrace(EObject trace, EObject metaData, EObject metadataContainer) {
		TraceMetadata existingMetadata = (TraceMetadata) this.getMetadataForTrace(trace, metadataContainer);
		for (EStructuralFeature feature : metaData.eClass().getEStructuralFeatures()) {
			try {
				existingMetadata.eSet(feature, metaData.eGet(feature));
			} catch (IllegalArgumentException ex) {
				// Deliberately do nothing.
			}
		}

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
		return metadata;
	}

	@Override
	public void setMetadataForArtifact(EObject wrapper, EObject metaData, EObject metadataContainer) {
		ArtifactMetadata existingMetadata = (ArtifactMetadata) this.getMetadataForArtifact(wrapper, metadataContainer);
		for (EStructuralFeature feature : metaData.eClass().getEStructuralFeatures()) {
			try {
				existingMetadata.eSet(feature, metaData.eGet(feature));
			} catch (IllegalArgumentException ex) {
				// Deliberately do nothing.
			}
		}
	}

}
