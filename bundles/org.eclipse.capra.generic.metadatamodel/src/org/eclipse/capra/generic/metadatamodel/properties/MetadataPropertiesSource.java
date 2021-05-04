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

import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * Abstract class for {@link IPropertySource} descendants that provide access to
 * metadata.
 * 
 * @author Jan-Philipp Steghöfer
 */
public abstract class MetadataPropertiesSource implements IPropertySource {

	/**
	 * The name of the category under which the entries are shown in the properties
	 * view.
	 */
	protected static final String CATEGORY_NAME = "Metadata";

	/**
	 * The metadata this {@link IPropertySource} accesses.
	 */
	protected EObject metadata;

	/**
	 * Updates the metadata using the given feature ID to the given value.
	 * 
	 * @param featureId the ID of the feature to update
	 * @param value     the new value of the feature
	 * @throws IllegalArgumentException if the feature is not present
	 * @throws IllegalStateException    if a rollback has occurred or the
	 *                                  transaction was interrupted
	 */
	protected void updateInTransaction(int featureId, Object value) {
		TransactionalEditingDomain editingDomain = EditingDomainHelper.getEditingDomain();
		// We're saving the trace model and the artifact model in the same transaction
		Command cmd = new RecordingCommand(editingDomain, "Update Trace Model") {
			@Override
			protected void doExecute() {
				EStructuralFeature feature = metadata.eClass().getEStructuralFeature(featureId);
				if (feature != null) {
					metadata.eSet(feature, value);
				} else {
					throw new IllegalArgumentException("Feature not found in metadata.");
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
