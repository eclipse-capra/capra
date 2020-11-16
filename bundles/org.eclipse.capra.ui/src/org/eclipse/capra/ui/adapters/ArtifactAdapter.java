/*******************************************************************************
 * Copyright (c) 2016, 2020 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *   
 * SPDX-License-Identifier: EPL-2.0
 *   
 * Contributors:
 *     Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *     Chalmers | University of Gothenburg - additional features, updated API
 *******************************************************************************/
package org.eclipse.capra.ui.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * An {@link IPropertySource} for artifacts. It provides access to all
 * properties of the {@link EObject} representing the artifact. In practice,
 * that means that it either shows the properties of the {@link ArtifactWrapper}
 * or the {@code EObject} that is linked to.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class ArtifactAdapter implements IPropertySource {

	private static enum DescriptorIDs {
		LABEL, LOCATION
	}

	private static final String CATEGORY_NAME = "General";

	private final EObject artifact;

	private final ArtifactHelper artifactHelper;

	/**
	 * Creates a new {@link ArtifactAdapter} that represents the provided artifact.
	 * 
	 * @param theItem the artifact this adapter represents.
	 */
	public ArtifactAdapter(EObject theItem) {
		this.artifact = theItem;
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().orElseThrow();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(EditingDomainHelper.getResourceSet());
		artifactHelper = new ArtifactHelper(artifactModel);
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		List<IPropertyDescriptor> propertyDescriptors = new ArrayList<>();
		if (!isArtifactWrapper(artifact)) {
			PropertyDescriptor labelDescriptor = new PropertyDescriptor(DescriptorIDs.LABEL, "Label");
			labelDescriptor.setCategory(CATEGORY_NAME);
			propertyDescriptors.add(labelDescriptor);
			PropertyDescriptor locationDescriptor = new PropertyDescriptor(DescriptorIDs.LOCATION, "Location");
			locationDescriptor.setCategory(CATEGORY_NAME);
			propertyDescriptors.add(locationDescriptor);
		}
		propertyDescriptors.addAll(artifact.eClass().getEAllAttributes().stream()
				.map(attribute -> new TextPropertyDescriptor(attribute.getName(), attribute.getName()))
				.collect(Collectors.toList()));

		IPropertyDescriptor[] dummyList = new IPropertyDescriptor[propertyDescriptors.size()];
		return propertyDescriptors.toArray(dummyList);
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals(DescriptorIDs.LABEL)) {
			return artifactHelper.getArtifactLabel(artifact);
		} else if (id.equals(DescriptorIDs.LOCATION)) {
			return artifactHelper.getArtifactLocation(artifact);
		} else {
			EStructuralFeature a = artifact.eClass().getEStructuralFeature((String) id);
			return artifact.eGet(a);
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
		// Deliberately do nothing
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		EStructuralFeature feature = artifact.eClass().getEStructuralFeature((String) id);
		artifact.eSet(feature, value);
	}

	private boolean isArtifactWrapper(EObject artifact) {
		return !artifact.equals(artifactHelper.unwrapWrapper(artifact));
	}

}
