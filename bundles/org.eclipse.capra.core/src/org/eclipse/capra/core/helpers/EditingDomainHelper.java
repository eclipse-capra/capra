/*******************************************************************************
 * Copyright (c) 2016-2022 Chalmers | University of Gothenburg, rt-labs and others.
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
package org.eclipse.capra.core.helpers;

import org.eclipse.capra.core.resources.CapraResourceFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * This class provides helper methods to access the
 * {@link TransactionalEditingDomain} used by Eclipse Capra to manage its
 * resources. Also makes sure that the correct type of resource set is used.
 * 
 * @author Jan-Philipp Steghöfer
 * @since 0.8.1
 *
 */
public class EditingDomainHelper {

	private final static TransactionalEditingDomain capraEditingDomain;

	static {
		capraEditingDomain = TransactionalEditingDomain.Registry.INSTANCE
				.getEditingDomain("org.eclipse.capra.core.EditingDomain");
		capraEditingDomain.getResourceSet().getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION, new CapraResourceFactory());

	}

	public static TransactionalEditingDomain getEditingDomain() {
		if (capraEditingDomain == null) {
			throw new IllegalStateException("Eclipse Capra Editing Domain not registered!");
		}
		return capraEditingDomain;
	}

	/**
	 * Retrieves the {@link ResourceSet} from the Eclipse Capra editing domain. The
	 * {@code ResourceSet} should be used to manage all resources created by Capra.
	 * 
	 * @return the {@code ResourceSet} that contains all resources used by Eclipse
	 *         Capra
	 */
	public static ResourceSet getResourceSet() {
		return getEditingDomain().getResourceSet();
	}

}
