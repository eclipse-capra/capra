/*******************************************************************************
 * Copyright (c) 2023 Jan-Philipp Steghöfer
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *   
 * SPDX-License-Identifier: EPL-2.0
 *   
 * Contributors:
 *     Jan-Philipp Steghöfer - initial implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.ui.linktable.views;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.IPersistenceAdapter;
import org.eclipse.capra.core.adapters.ITraceabilityInformationModelAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.linktable.nattable.ConnectionColumnPropertyAccessor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultRowSelectionLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.theme.ModernNatTableThemeConfiguration;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

/**
 * Provides a tabular view of traceability links, including their
 * meta-information.
 * <p>
 * The implementation uses NatTable.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class TraceabilityLinkTable extends ViewPart {

	private final class TraceabilityLinkTableRegistryConfiguration extends AbstractRegistryConfiguration {
		@Override
		public void configureRegistry(final IConfigRegistry configRegistry) {
			Style cellStyle = new Style();
			cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_RED);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
					ARTIFACT_DOES_NOT_EXIST_LABEL);
		}
	}

	private final class ArtifactExistenceConfigLabelAccumulator implements IConfigLabelAccumulator {
		private final IDataProvider bodyDataProvider;

		private ArtifactExistenceConfigLabelAccumulator(IDataProvider bodyDataProvider) {
			this.bodyDataProvider = bodyDataProvider;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
			List<EObject> artifacts = new ArrayList<>();
			Connection connection = ((ListDataProvider<Connection>) bodyDataProvider).getRowObject(rowPosition);
			if (columnPosition == 0) {
				artifacts = connection.getOrigins();
			} else if (columnPosition == 1) {
				artifacts = connection.getTargets();
			}
			for (EObject obj : artifacts) {
				ArtifactHelper artifactHelper = new ArtifactHelper(persistenceAdapter.getArtifactWrappers(resourceSet));
				Optional<IArtifactHandler<?>> handler = artifactHelper.getHandler(artifactHelper.unwrapWrapper(obj));
				if (handler.isPresent()) {
					if (!handler.get().doesArtifactExist(obj)) {
						configLabels.addLabelOnTop(ARTIFACT_DOES_NOT_EXIST_LABEL);
					}
				}
			}
		}
	}

	private static final String ARTIFACT_DOES_NOT_EXIST_LABEL = "ARTIFACT_DOES_NOT_EXIST_LABEL";

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.eclipse.capra.ui.linktable.views.TraceabilityLinkTable";

	private NatTable traceLinkTable;
	private Composite parent;

	private Action refreshAction;

	private ResourceSet resourceSet = EditingDomainHelper.getResourceSet();

	private final ITraceabilityInformationModelAdapter traceAdapter = ExtensionPointHelper
			.getTraceabilityInformationModelAdapter().get();
	private IPersistenceAdapter persistenceAdapter = ExtensionPointHelper.getPersistenceAdapter().get();

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(refreshAction);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshAction);
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		makeActions();
		contributeToActionBars();
		updateTraceabilityLinkTable();
	}

	@Override
	public void dispose() {
		traceLinkTable.dispose();
		traceLinkTable = null;
		super.dispose();
	}

	@Override
	public void setFocus() {
		if (traceLinkTable != null) {
			traceLinkTable.setFocus();
		}
	}

	private void makeActions() {
		refreshAction = new Action() {
			@Override
			public void run() {
				updateTraceabilityLinkTable();
			}
		};
		refreshAction.setText("Refresh");
		refreshAction.setToolTipText("Refresh");
		try {
			URL refreshImageUrl = new URL("platform:/plugin/org.eclipse.search/icons/full/elcl16/refresh.png");
			ImageDescriptor refreshImage = ImageDescriptor.createFromURL(refreshImageUrl);
			refreshAction.setImageDescriptor(refreshImage);
		} catch (MalformedURLException ex) {
			// Swallow this exception since we will just use the default behaviour of
			// showing text
		}
	}

	private void updateTraceabilityLinkTable() {
		EObject traceModel = persistenceAdapter.getTraceModel(this.resourceSet);
		Connection connection = null;
		try {
			connection = traceAdapter.getAllTraceLinks(traceModel).get(0);
			if (connection == null) {
				return;
			}
		} catch (IndexOutOfBoundsException e) {
			// There are no traces in the trace model, thus also nothing to show
			return;
		}

		// Data access
		ConnectionColumnPropertyAccessor columnPropertyAccessor = new ConnectionColumnPropertyAccessor(connection);
		IDataProvider bodyDataProvider = new ListDataProvider<Connection>(traceAdapter.getAllTraceLinks(traceModel),
				columnPropertyAccessor);

		// Body and selection layer
		DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);
		IConfigLabelAccumulator artifactExistenceLabelAcculumaltor = new ArtifactExistenceConfigLabelAccumulator(
				bodyDataProvider);
		bodyDataLayer.setConfigLabelAccumulator(artifactExistenceLabelAcculumaltor);

		SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer);
		selectionLayer.addConfiguration(new DefaultRowSelectionLayerConfiguration());

		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		// Column header
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(
				columnPropertyAccessor.getColumns().toArray(new String[0]));
		DataLayer headerDataLayer = new DataLayer(columnHeaderDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(headerDataLayer, viewportLayer, selectionLayer);

		// Row header
		IDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyDataProvider);
		DataLayer rowHeaderDataLayer = new DataLayer(rowHeaderDataProvider, 40, 20);
		ILayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, viewportLayer, selectionLayer);

		// Corner
		ILayer cornerLayer = new CornerLayer(
				new DataLayer(new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider)),
				rowHeaderLayer, columnHeaderLayer);

		// Put the layers together in a grid
		GridLayer gridLayer = new GridLayer(viewportLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

		NatTable traceabilityLinkTable = new NatTable(parent, gridLayer, false);
		// Adding Configuration to the table
		traceabilityLinkTable.addConfiguration(new ModernNatTableThemeConfiguration());
		traceabilityLinkTable.addConfiguration(new TraceabilityLinkTableRegistryConfiguration());
		traceabilityLinkTable.configure();

		GridDataFactory.fillDefaults().grab(true, true).applyTo(traceabilityLinkTable);

	}
}
