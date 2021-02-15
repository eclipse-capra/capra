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
package org.eclipse.capra.testsupport.extension;

import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

public class UiTransferExtensionDummy extends Transfer {

	private static final String MIME_TYPE = "UiTransferExtensionDummy";
	private static final int MIME_TYPE_ID = registerType(MIME_TYPE);

	@Override
	public TransferData[] getSupportedTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSupportedType(TransferData transferData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { MIME_TYPE_ID };
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { MIME_TYPE };
	}

	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Object nativeToJava(TransferData transferData) {
		// TODO Auto-generated method stub
		return null;
	}

}
