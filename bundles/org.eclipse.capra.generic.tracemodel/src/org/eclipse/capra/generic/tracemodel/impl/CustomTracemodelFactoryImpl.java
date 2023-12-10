/*******************************************************************************
 * Copyright (c) 2016-2023 Chalmers | University of Gothenburg, rt-labs and others.
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
 *     Jan-Philipp Steghöfer - additional features
 *******************************************************************************/
package org.eclipse.capra.generic.tracemodel.impl;

import org.eclipse.capra.generic.tracemodel.RelatedTo;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * A custom implementation of @{link {@link TracemodelFactoryImpl} that sets a
 * UUID for a newly created {@link RelatedTo} instance. Since we are using XCore
 * to generate the trace model and EMF to handle it, we cannot set a default
 * value that is complex, such as one derived from a method at runtime. We also
 * cannot easily override the getter to check if a value has already been
 * assigned since this is only allowed for derived values in XCore. Thus, even
 * though it is discouraged to modify the creation of new instances (see, e.g.,
 * https://www.eclipse.org/forums/index.php/t/154772/), we still do this here.
 * As we are not initialising a complex value, we will not run into the isuses
 * on deserialisation that are mentioned in the forum post.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class CustomTracemodelFactoryImpl extends TracemodelFactoryImpl {

	@Override
	public RelatedTo createRelatedTo() {
		RelatedTo relatedTo = super.createRelatedTo();
		relatedTo.setID(EcoreUtil.generateUUID());
		return relatedTo;
	}

}
