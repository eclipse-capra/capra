/*******************************************************************************
 * Copyright (c) 2016, 2019 Chalmers | University of Gothenburg, rt-labs and others.
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
package org.eclipse.capra.ui.notification;

import org.eclipse.capra.ui.notification.CapraNotificationHelper.IssueType;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

/**
 * Registers the possible quick fix resolutions for issues that are detected in
 * the traced objects.
 * 
 * @author Michael Warne
 */
public class MarkerResolutionGenerator implements IMarkerResolutionGenerator {

	@Override
	public IMarkerResolution[] getResolutions(IMarker marker) {
		try {
			String issue = (String) marker.getAttribute(CapraNotificationHelper.ISSUE_TYPE);

			if (issue.equals(IssueType.RENAMED.getValue()) || issue.equals(IssueType.MOVED.getValue()))
				return new IMarkerResolution[] { new RenameOrMoveQuickFix("Update the EMF presentation.") };

			if (issue.equals(IssueType.DELETED.getValue()))
				return new IMarkerResolution[] { new DeleteQuickFix("Delete the affected trace link.") };

			if (issue.equals(IssueType.CHANGED.getValue()))
				return new IMarkerResolution[] { new DeleteQuickFix("Delete the affected trace link."),
						new ChangeQuickFix("Do not update existing trace link.") };

			return new IMarkerResolution[0];

		} catch (CoreException e) {
			return new IMarkerResolution[0];
		}
	}
}
