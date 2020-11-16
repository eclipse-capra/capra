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
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *      Chalmers | University of Gothenburg - additional features, updated API
 *******************************************************************************/
package org.eclipse.capra.core.editingDomain;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl.FactoryImpl;

/**
 * Eclipse Capra's default editing domain. Can be used to access the current
 * resource set.
 * 
 * @author Jan-Philipp Steghöfer
 * @since 0.8.1
 *
 */
public class EditingDomainFactory extends FactoryImpl {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TransactionalEditingDomain createEditingDomain() {
		return super.createEditingDomain();
	}

}
