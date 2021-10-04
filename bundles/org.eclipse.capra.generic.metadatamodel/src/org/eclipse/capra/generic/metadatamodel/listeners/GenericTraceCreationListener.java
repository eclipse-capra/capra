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
package org.eclipse.capra.generic.metadatamodel.listeners;

import java.util.Date;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.IMetadataAdapter;
import org.eclipse.capra.core.adapters.IPersistenceAdapter;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.core.listeners.ITraceCreationListener;
import org.eclipse.capra.generic.metadatamodel.MetadataContainer;
import org.eclipse.capra.generic.metadatamodel.MetadatamodelFactory;
import org.eclipse.capra.generic.metadatamodel.Person;
import org.eclipse.capra.generic.metadatamodel.TraceMetadata;

/**
 * Populates the creation date and the creation user with the current date and
 * the user name of the logged in user.
 * 
 * @author Jan-Philipp Steghöfer
 */
public class GenericTraceCreationListener implements ITraceCreationListener {

	@Override
	public void onTraceCreation(Connection newTrace) {
		IPersistenceAdapter persistenceAdapter = ExtensionPointHelper.getPersistenceAdapter().orElseThrow();
		IMetadataAdapter metadataAdapter = ExtensionPointHelper.getTraceMetadataAdapter().orElseThrow();
		MetadataContainer metadataContainer = (MetadataContainer) persistenceAdapter
				.getMetadataContainer(EditingDomainHelper.getResourceSet());

		// Get a new metadata instance
		TraceMetadata metadata = MetadatamodelFactory.eINSTANCE.createTraceMetadata();

		// Set the relevant data
		metadata.setCreationDate(new Date());
		Person creator = MetadatamodelFactory.eINSTANCE.createPerson();
		creator.setName(System.getProperty("user.name"));
		creator.setEmail(System.getProperty("user.email"));
		metadata.setCreationUser(creator);
		metadata.setTrace(newTrace.getTlink());

		// And finally, we set the metadata again
		metadataAdapter.setMetadataForTrace(newTrace.getTlink(), metadata, metadataContainer);
	}

}
