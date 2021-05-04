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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.operations.UpdateTraceOperation;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * An {@link IPropertySource} for trace links. It provides access to all
 * properties of the underlying {@link Connection}, i.e., all properties that
 * are defined in the traceability information model.
 * <p>
 * This implementation delegates to a metadata property source, if such a
 * property source is defined using the corresponding extension point. That
 * means that the properties will automatically include the metadata provided by
 * such an extension and storing any changes will also be delegated to that
 * extension.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class ConnectionAdapter implements IPropertySource {

	private enum DescriptorIDs {
		ORIGIN, TARGETS, TYPE
	}

	private static final String CATEGORY_NAME = "General";

	private final Connection connection;
	private final ArtifactHelper artifactHelper;
	private final IPropertySource connectionMetadataPropertySource;

	/**
	 * Create a new instance based on the provided {@link Connection}.
	 * 
	 * @param theItem the connection this adapter represents
	 */
	public ConnectionAdapter(Connection theItem) {
		this.connection = theItem;
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().orElseThrow();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(EditingDomainHelper.getResourceSet());
		artifactHelper = new ArtifactHelper(artifactModel);
		IPropertySourceProvider propSourceProvider = PropertySourceExtensionPointHelper
				.getMetadataPropertySourceProvider().orElse(null);
		// Get property source from extension point
		this.connectionMetadataPropertySource = Optional.ofNullable(propSourceProvider)
				.map(p -> p.getPropertySource(theItem)).orElse(null);
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		List<IPropertyDescriptor> propertyDescriptors = new ArrayList<>();
		// Add generic properties of a connection to their own category.
		PropertyDescriptor originDescriptor = new PropertyDescriptor(DescriptorIDs.ORIGIN, "Origin");
		originDescriptor.setCategory(CATEGORY_NAME);
		propertyDescriptors.add(originDescriptor);
		PropertyDescriptor targetDescriptor = new PropertyDescriptor(DescriptorIDs.TARGETS, "Targets");
		targetDescriptor.setCategory(CATEGORY_NAME);
		propertyDescriptors.add(targetDescriptor);
		PropertyDescriptor typeDescriptor = new PropertyDescriptor(DescriptorIDs.TYPE, "Type");
		typeDescriptor.setCategory(CATEGORY_NAME);
		propertyDescriptors.add(typeDescriptor);
		// All other properties of the underlying class are added without category.
		propertyDescriptors.addAll(connection.getTlink().eClass().getEAllAttributes().stream()
				.map(attribute -> new TextPropertyDescriptor(attribute.getName(), attribute.getName()))
				.collect(Collectors.toList()));

		if (this.connectionMetadataPropertySource != null) {
			propertyDescriptors.addAll(Arrays.asList(this.connectionMetadataPropertySource.getPropertyDescriptors()));
		}

		IPropertyDescriptor[] dummyList = new IPropertyDescriptor[propertyDescriptors.size()];
		return propertyDescriptors.toArray(dummyList);

	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals(DescriptorIDs.ORIGIN)) {
			return connection.getOrigins().stream().map(o -> artifactHelper.getArtifactLabel(o))
					.collect(Collectors.toList());
		} else if (id.equals(DescriptorIDs.TARGETS)) {
			return connection.getTargets().stream().map(artifactHelper::getArtifactLabel).collect(Collectors.toList());
		} else if (id.equals(DescriptorIDs.TYPE)) {
			return connection.getTlink().eClass().getName();
		} else if (this.connectionMetadataPropertySource != null
				&& this.connectionMetadataPropertySource.getPropertyValue(id) != null) {
			return this.connectionMetadataPropertySource.getPropertyValue(id);
		} else {
			if (id instanceof String) {
				EStructuralFeature a = connection.getTlink().eClass().getEStructuralFeature((String) id);
				return connection.getTlink().eGet(a);
			}
		}
		return null;
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (this.connectionMetadataPropertySource != null
				&& this.connectionMetadataPropertySource.getPropertyValue(id) != null) {
			return this.connectionMetadataPropertySource.isPropertySet(id);
		}
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (this.connectionMetadataPropertySource != null
				&& this.connectionMetadataPropertySource.getPropertyValue(id) != null) {
			this.connectionMetadataPropertySource.resetPropertyValue(id);
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (this.connectionMetadataPropertySource != null
				&& this.connectionMetadataPropertySource.getPropertyValue(id) != null) {
			this.connectionMetadataPropertySource.setPropertyValue(id, value);
			return;
		} else if (id instanceof String) {
			IAdaptable adapter = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart()
					.getSite();
			IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
			UpdateTraceOperation updateTraceOperation = new UpdateTraceOperation("Update trace link", this.connection,
					id, value);
			updateTraceOperation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
			try {
				operationHistory.execute(updateTraceOperation, null, adapter);
			} catch (ExecutionException e) {
				// Deliberately do nothing. Exceptions are caught by the operation.
			}
		}
	}

	/**
	 * Provides the connection represented by this adapter.
	 * 
	 * @return the connection
	 */
	public Connection getConnection() {
		return this.connection;
	}
}
