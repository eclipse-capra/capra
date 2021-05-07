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
package org.eclipse.capra.generic.metadatamodel.properties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.IMetadataAdapter;
import org.eclipse.capra.core.adapters.IPersistenceAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.generic.metadatamodel.MetadatamodelPackage;
import org.eclipse.capra.generic.metadatamodel.TraceMetadata;
import org.eclipse.capra.generic.metadatamodel.impl.TraceMetadataImpl;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Provides access to the metadata of a connection.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class ConnectionMetadataProperties extends MetadataPropertiesSource {

	private enum DescriptorIDs {
		CREATION_DATE, COMMENT
	}

	/**
	 * Creates a new metadata properties source for the provided {@link Connection}.
	 * 
	 * @param connection the {@code Connection} which contains the trace link whose
	 *                   metadata should be accessed by this {@code IPropertySource}
	 */
	public ConnectionMetadataProperties(Connection connection) {
		IMetadataAdapter metadataAdapter = ExtensionPointHelper.getTraceMetadataAdapter().orElseThrow();
		IPersistenceAdapter persistenceAdapter = ExtensionPointHelper.getPersistenceAdapter().orElseThrow();
		this.metadata = metadataAdapter.getMetadataForTrace(connection.getTlink(),
				persistenceAdapter.getMetadataContainer(EditingDomainHelper.getResourceSet()));
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		List<IPropertyDescriptor> propertyDescriptors = new ArrayList<>();
		// Add generic properties of a connection to their own category.
		PropertyDescriptor creationDateDescriptor = new PropertyDescriptor(DescriptorIDs.CREATION_DATE,
				"Creation Date");
		creationDateDescriptor.setCategory(CATEGORY_NAME);
		propertyDescriptors.add(creationDateDescriptor);

		PropertyDescriptor commentDescriptor = new TextPropertyDescriptor(DescriptorIDs.COMMENT, "Comment");
		commentDescriptor.setCategory(CATEGORY_NAME);
		propertyDescriptors.add(commentDescriptor);

		IPropertyDescriptor[] dummyList = new IPropertyDescriptor[propertyDescriptors.size()];
		return propertyDescriptors.toArray(dummyList);
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals(DescriptorIDs.CREATION_DATE)) {
			return ((TraceMetadata) this.metadata).getCreationDate();
		} else if (id.equals(DescriptorIDs.COMMENT)) {
			return ((TraceMetadata) this.metadata).getComment();
		}
		return null;
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (id.equals(DescriptorIDs.CREATION_DATE)) {
			return ((TraceMetadataImpl) this.metadata).eIsSet(MetadatamodelPackage.TRACE_METADATA__CREATION_DATE);
		} else if (id.equals(DescriptorIDs.COMMENT)) {
			return ((TraceMetadataImpl) this.metadata).eIsSet(MetadatamodelPackage.TRACE_METADATA__COMMENT);
		}
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (id.equals(DescriptorIDs.CREATION_DATE)) {
			((TraceMetadataImpl) this.metadata).eUnset(MetadatamodelPackage.TRACE_METADATA__CREATION_DATE);
		} else if (id.equals(DescriptorIDs.COMMENT)) {
			((TraceMetadataImpl) this.metadata).eUnset(MetadatamodelPackage.TRACE_METADATA__COMMENT);
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (id.equals(DescriptorIDs.CREATION_DATE) && value instanceof Date) {
			updateInTransaction(MetadatamodelPackage.TRACE_METADATA__CREATION_DATE, value);
		} else if (id.equals(DescriptorIDs.COMMENT) && value instanceof String) {
			updateInTransaction(MetadatamodelPackage.TRACE_METADATA__COMMENT, value);
		}
	}

}
