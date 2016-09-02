/*******************************************************************************
 *  Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *      Chalmers|Gothenburg University and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.ui.notification;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;

/**
 * Removes the marker associated with the trace link if the user decides to keep
 * the tracelink without any modifications
 * 
 * @author Salome Maro
 */
public class FileChangedQuickFix implements IMarkerResolution {

	private String label;

	public FileChangedQuickFix(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {
		try {
			marker.delete();
		} catch (CoreException e) {

			e.printStackTrace();
		}
	}

}
