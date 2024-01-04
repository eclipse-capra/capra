/*******************************************************************************
 * Copyright (c) 2016-2024 Chalmers | University of Gothenburg, rt-labs and others.
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
 *     Fredrik Johansson and Themistoklis Ntoukolis - initial implementation of the Matrix View
 *     Jan-Philipp Steghöfer - additional features
 *******************************************************************************/
package org.eclipse.capra.ui.matrix;

import java.util.Objects;
import java.util.Set;

import org.eclipse.capra.ui.matrix.views.TraceabilityMatrixView;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.ui.menu.MenuItemProviders;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

/**
 * Populates a menu with the resolutions for the markers that are present at the
 * current column or row.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class QuickFixDynamicMenu extends ContributionItem {

	@Override
	public void fill(Menu menu, int index) {
		TraceabilityMatrixView traceMatrixView = TraceabilityMatrixView.getOpenedView();
		TraceabilityMatrixEntryData entryData = null;
		if (menu.getParentMenu() != null) {
			Object natEventDataRaw = menu.getParentMenu().getData(MenuItemProviders.NAT_EVENT_DATA_KEY);
			if (natEventDataRaw != null && (natEventDataRaw instanceof NatEventData)) {
				NatEventData natEventData = (NatEventData) natEventDataRaw;
				entryData = traceMatrixView.getHeaderEntryDataForTablePositions(natEventData.getColumnPosition(),
						natEventData.getRowPosition());
			}
		}

		if (Objects.isNull(entryData)) {
			return;
		}
		Set<IMarker> markers = entryData.getMarkers();
		if (Objects.isNull(markers) || markers.size() == 0)
			return;
		IMarkerResolutionGenerator resolutionGenerator = getMarkerResolutionGenerator();
		if (Objects.isNull(resolutionGenerator)) {
			return;
		}

		IMarker marker = markers.iterator().next();
		IMarkerResolution[] resolutions = resolutionGenerator.getResolutions(marker);
		for (IMarkerResolution resolution : resolutions) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index);
			menuItem.setText(resolution.getLabel());
			menuItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					resolution.run(marker);
				}
			});
		}
	}

	private IMarkerResolutionGenerator getMarkerResolutionGenerator() {
		IConfigurationElement[] configs = Platform.getExtensionRegistry()
				.getConfigurationElementsFor("org.eclipse.ui.ide.markerResolution");

		for (IConfigurationElement config : configs)
			if (config.getAttribute("class").equals("org.eclipse.capra.ui.notification.MarkerResolutionGenerator")) {
				try {
					return (IMarkerResolutionGenerator) config.createExecutableExtension("class");
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		return null;
	}

}
