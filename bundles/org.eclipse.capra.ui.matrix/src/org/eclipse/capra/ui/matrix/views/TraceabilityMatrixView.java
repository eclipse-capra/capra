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
 *     Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *     Chalmers | University of Gothenburg - additional features, updated API
 *     Fredrik Johansson and Themistoklis Ntoukolis - initial implementation of the Matrix View
 *******************************************************************************/
package org.eclipse.capra.ui.matrix.views;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.handlers.IArtifactUnpacker;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.core.helpers.TraceHelper;
import org.eclipse.capra.ui.adapters.ConnectionAdapter;
import org.eclipse.capra.ui.helpers.SelectionSupportHelper;
import org.eclipse.capra.ui.matrix.TraceabilityMatrixBodyToolTip;
import org.eclipse.capra.ui.matrix.TraceabilityMatrixColumnHeaderDataProvider;
import org.eclipse.capra.ui.matrix.TraceabilityMatrixDataProvider;
import org.eclipse.capra.ui.matrix.TraceabilityMatrixHeaderToolTip;
import org.eclipse.capra.ui.matrix.TraceabilityMatrixRowHeaderDataProvider;
import org.eclipse.capra.ui.matrix.selection.TraceabilityMatrixSelectionProvider;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.export.command.ExportCommand;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.hover.HoverLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.resize.action.ColumnResizeCursorAction;
import org.eclipse.nebula.widgets.nattable.resize.action.RowResizeCursorAction;
import org.eclipse.nebula.widgets.nattable.resize.event.ColumnResizeEventMatcher;
import org.eclipse.nebula.widgets.nattable.resize.event.RowResizeEventMatcher;
import org.eclipse.nebula.widgets.nattable.resize.mode.ColumnResizeDragMode;
import org.eclipse.nebula.widgets.nattable.resize.mode.RowResizeDragMode;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

/**
 * Provides a traceability matrix view, i.e., a tabular view of how the
 * different artifacts are related.
 * <p>
 * The traceability matrix shows all selected artifacts and the artifacts they
 * are directly connected to via trace links. It also displays the type of link
 * between the artifacts, allows navigating to the artifact by double-clicking
 * the column header, and shows information about the artifact and the trace
 * link as a tool-tip. It is also possible to export the currently viewed
 * traceability matrix as a Microsoft Excel file.
 * <p>
 * The implementation uses NatTable.
 * 
 * @author Fredrik Johansson
 * @author Themistoklis Ntoukolis
 * @author Jan-Philipp Steghöfer
 *
 */
public class TraceabilityMatrixView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.eclipse.capra.ui.matrix.views.TraceabilityMatrixView";

	private static final String SAME_LABEL = "SAME"; // When column header and row header are the same
	private static final String LINK_LABEL = "LINKED"; // When there is a link between

	private NatTable traceMatrixTable;
	private Action deleteLinkAction;
	private Action refreshAction;
	private Action showAllAction;
	private Action exportExcelAction;
	private Composite parent;

	private ResourceSet resourceSet = EditingDomainHelper.getResourceSet();

	private final TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
	private TraceMetaModelAdapter metamodelAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
	private TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();

	private TraceabilityMatrixDataProvider bodyDataProvider;
	private TraceabilityMatrixSelectionProvider selectionProvider;
	private BodyLayerStack bodyLayer;

	private List<Object> selectedModels = new ArrayList<>();
	private boolean selectionModified = false;

	/**
	 * Listener to detect changes in the selection of other views. Redraws the table
	 * if necessary.
	 */
	private ISelectionListener selectionListener = new ISelectionListener() {
		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			populateSelectedModels(part, selection);
			if (traceMatrixTable != null && selectionModified) {
				updateTraceabilityMatrix();
			}
		}

		/**
		 * Populates the {@link #selectedModels} property with the objects included in
		 * the provided selection.
		 * 
		 * @param part      the workbench part whose selection provider is used
		 * @param selection the selection from which the elements are extracted
		 */
		private void populateSelectedModels(IWorkbenchPart part, ISelection selection) {
			List<Object> newSelectedObjects = SelectionSupportHelper.extractSelectedElements(selection, part);
			if (!listEqualsIgnoreOrder(selectedModels, newSelectedObjects)) {
				selectionModified = true;
				selectedModels.clear();
				selectedModels.addAll(newSelectedObjects);
			} else {
				selectionModified = false;
			}
		}
	};

	/**
	 * Color configuration. First changing the background color for the labels.
	 * After that is the coloring for hovering.
	 */
	private AbstractRegistryConfiguration capraNatTableStyleConfiguration = new AbstractRegistryConfiguration() {
		@Override
		public void configureRegistry(IConfigRegistry configRegistry) {
			// Black background for cells where there should not be any links
			Style cellStyle = new Style();
			cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_BLACK);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
					SAME_LABEL);

			// Green background for cells where there is a link.
			cellStyle = new Style();
			cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_GREEN);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
					LINK_LABEL);

			// Style that is applied when cells are hovered
			Style style = new Style();
			style.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_YELLOW);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, style, DisplayMode.HOVER);

			// Style that is applied when selected cells are hovered
			style = new Style();
			style.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_GREEN);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, style, DisplayMode.SELECT_HOVER);
		}
	};

	/**
	 * Making it possible to resize columns and rows.
	 */
	private AbstractUiBindingConfiguration capraUiBindingConfiguration = new AbstractUiBindingConfiguration() {

		@Override
		public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
			uiBindingRegistry.registerFirstMouseMoveBinding(
					new ColumnResizeEventMatcher(SWT.NONE, GridRegion.ROW_HEADER, 0), new ColumnResizeCursorAction());
			uiBindingRegistry.registerFirstMouseDragMode(
					new ColumnResizeEventMatcher(SWT.NONE, GridRegion.ROW_HEADER, 1), new ColumnResizeDragMode());
			uiBindingRegistry.registerFirstMouseMoveBinding(
					new RowResizeEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, 0), new RowResizeCursorAction());
			uiBindingRegistry.registerFirstMouseDragMode(
					new RowResizeEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, 1), new RowResizeDragMode());
			// Make the corner on the top left also resizable
			uiBindingRegistry.registerFirstMouseMoveBinding(
					new ColumnResizeEventMatcher(SWT.NONE, GridRegion.CORNER, 0), new ColumnResizeCursorAction());
			uiBindingRegistry.registerFirstMouseDragMode(new ColumnResizeEventMatcher(SWT.NONE, GridRegion.CORNER, 1),
					new ColumnResizeDragMode());
			uiBindingRegistry.registerFirstMouseMoveBinding(new RowResizeEventMatcher(SWT.NONE, GridRegion.CORNER, 0),
					new RowResizeCursorAction());
			uiBindingRegistry.registerFirstMouseDragMode(new RowResizeEventMatcher(SWT.NONE, GridRegion.CORNER, 1),
					new RowResizeDragMode());
		}
	};

	/**
	 * Setting labels based on links and later use the labels for customized layout.
	 */
	private IConfigLabelAccumulator cellLabelAccumulator = new IConfigLabelAccumulator() {
		@Override
		public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
			int columnIndex = bodyLayer.getColumnIndexByPosition(columnPosition);
			int rowIndex = bodyLayer.getRowIndexByPosition(rowPosition);
			if (EMFHelper.hasSameIdentifier(bodyDataProvider.getRow(rowIndex),
					bodyDataProvider.getColumn(columnIndex))) {
				configLabels.addLabel(SAME_LABEL);
			} else {
				String cellText = (String) bodyDataProvider.getDataValue(columnIndex, rowIndex);
				if (!cellText.equals("")) {
					configLabels.addLabel(LINK_LABEL);
				}
			}
		}
	};

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		updateTraceabilityMatrix();

		makeActions();
		contributeToActionBars();

		// Adding support to react to selections in other views
		getViewSite().getPage().addSelectionListener(selectionListener);

	}

	@Override
	public void dispose() {
		getViewSite().getPage().removeSelectionListener(selectionListener);
		traceMatrixTable.dispose();
		traceMatrixTable = null;
		super.dispose();
	}

	@Override
	public void setFocus() {
		if (traceMatrixTable != null) {
			traceMatrixTable.setFocus();
		}
	}

	/**
	 * Updates the traceability matrix with data from the current selection or with
	 * data from all traced artifacts if the selection is empty. In essence, it
	 * disposes of the current table and creates a new one with the current data.
	 * Also layouts the parent to make sure the table is displayed correctly.
	 */
	@SuppressWarnings("unchecked")
	protected void updateTraceabilityMatrix() {
		EObject selectedObject;
		List<Connection> traces = new ArrayList<>();

		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);
		TraceHelper traceHelper = new TraceHelper(traceModel);

		if (!selectedModels.isEmpty()) {
			// Show the matrix for the selected objects
			for (Object model : selectedModels) {
				IArtifactHandler<Object> handler = (IArtifactHandler<Object>) artifactHelper.getHandler(model)
						.orElse(null);
				if (handler != null) {
					Object unpackedElement = null;
					if (handler instanceof IArtifactUnpacker) {
						unpackedElement = IArtifactUnpacker.class.cast(handler).unpack(model);
					} else {
						unpackedElement = model;
					}
					EObject wrappedElement = handler.createWrapper(unpackedElement, artifactModel);
					if (traceHelper.isArtifactInTraceModel(wrappedElement)) {
						selectedObject = wrappedElement;
						traces.addAll(this.traceAdapter.getConnectedElements(selectedObject, traceModel));
					}
				}
			}
		} else {
			// Without a selection, show a matrix of all traces
			if (traceModel != null) {
				traces = metamodelAdapter.getAllTraceLinks(traceModel);
			}
		}
		// If the current selection does not contain elements from the trace model, it
		// is possible that the list of traces is empty. By catching this case, we only
		// update the traceability matrix when there is actually something to show.
		if (!traces.isEmpty()) {
			if (traceMatrixTable != null) {
				traceMatrixTable.dispose();
				traceMatrixTable = null;
			}
			// Creating data providers for body, column and row. The data provider for the
			// body provides the data which will be shown in the cells. For columns and
			// rows, the labels are created.
			this.bodyDataProvider = new TraceabilityMatrixDataProvider(traces, traceModel, traceAdapter);
			IDataProvider colHeaderDataProvider = new TraceabilityMatrixColumnHeaderDataProvider(
					this.bodyDataProvider.getColumns(), artifactHelper);
			IDataProvider rowHeaderDataProvider = new TraceabilityMatrixRowHeaderDataProvider(
					this.bodyDataProvider.getRows(), artifactHelper);

			// Putting the data providers to their respective stacks
			this.bodyLayer = new BodyLayerStack(this.bodyDataProvider);
			ColumnHeaderLayerStack columnHeaderLayer = new ColumnHeaderLayerStack(colHeaderDataProvider);
			RowHeaderLayerStack rowHeaderLayer = new RowHeaderLayerStack(rowHeaderDataProvider);
			DefaultCornerDataProvider cornerDataProvider = new DefaultCornerDataProvider(colHeaderDataProvider,
					rowHeaderDataProvider);
			CornerLayer cornerLayer = new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer,
					columnHeaderLayer);

			// Adding the labels to the body (cells).
			bodyLayer.setConfigLabelAccumulator(cellLabelAccumulator);

			// Putting all the layers to the grid.
			GridLayer gridLayer = new GridLayer(this.bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);
			gridLayer.addConfiguration(capraUiBindingConfiguration);

			// Creating the table
			traceMatrixTable = new NatTable(parent, gridLayer, false);

			// Adding Configuration to the table
			traceMatrixTable.addConfiguration(new DefaultNatTableStyleConfiguration());
			traceMatrixTable.addConfiguration(this.capraNatTableStyleConfiguration);
			traceMatrixTable.configure();

			// Attach the selection provider
			if (this.selectionProvider == null) {
				this.selectionProvider = new TraceabilityMatrixSelectionProvider(bodyLayer.getSelectionLayer(),
						this.bodyDataProvider);
			} else {
				selectionProvider.updateProvider(bodyLayer.getSelectionLayer(), this.bodyDataProvider);
			}
			getSite().setSelectionProvider(this.selectionProvider);

			// Adding the tool tips
			attachToolTip();

			// Finally, make sure everything is in the right place.
			parent.layout();
		}
	}

	// ********************************************************************

	/**
	 * Compares to lists ignoring order and duplicates.
	 * 
	 * @param <T>   the type of the two lists
	 * @param list1 the first list to compare
	 * @param list2 the second list to compare
	 * @return <code>true</code> if the lists are the same, <code>false</code>
	 *         otherwise
	 */
	private <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
		return new HashSet<>(list1).equals(new HashSet<>(list2));
	}

	/**
	 * Create the tool tip instances.
	 */
	private void attachToolTip() {
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		new TraceabilityMatrixBodyToolTip(this.traceMatrixTable, this.bodyDataProvider, artifactHelper);
		new TraceabilityMatrixHeaderToolTip(this.traceMatrixTable, this.bodyDataProvider, artifactHelper);
	}

	/**
	 * Opens an editor for the provided {@code element} based on the URL stored in
	 * the artifact.
	 * 
	 * @param element the element for which an editor should be opened
	 * @throws PartInitException if the editor could not be initialised
	 */
	private void openEditor(EObject element) throws PartInitException {
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			URI uri = new URI(artifactHelper.getArtifactLocation(element));
			IPath artifactPath = new Path(uri.getPath());
			artifactPath = artifactPath.removeFirstSegments(1);
			IFile artifactFile = ResourcesPlugin.getWorkspace().getRoot().getFile(artifactPath);
			IDE.openEditor(page, artifactFile);
		} catch (URISyntaxException e) {
		}
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(deleteLinkAction);
		manager.add(new Separator());
		manager.add(refreshAction);
		manager.add(showAllAction);
		manager.add(new Separator());
		manager.add(exportExcelAction);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(deleteLinkAction);
		manager.add(refreshAction);
		manager.add(showAllAction);
	}

	private void makeActions() {
		deleteLinkAction = new Action() {
			@Override
			public void run() {
				if (selectedModels != null && !selectedModels.isEmpty()
						&& selectedModels.get(0) instanceof ConnectionAdapter) {
					ConnectionAdapter adapter = (ConnectionAdapter) selectedModels.get(0);
					Connection connection = adapter.getConnection();
					EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);
					traceAdapter.deleteTrace(List.of(connection), traceModel);
					showAllAction.run();
				}
			}
		};
		deleteLinkAction.setText("Delete Link");
		deleteLinkAction.setToolTipText("Delete Link");
		refreshAction = new Action() {
			@Override
			public void run() {
				updateTraceabilityMatrix();
			}
		};
		refreshAction.setText("Refresh");
		refreshAction.setToolTipText("Refresh");

		showAllAction = new Action() {
			@Override
			public void run() {
				selectedModels.clear();
				updateTraceabilityMatrix();
			}
		};
		showAllAction.setText("Show all");
		showAllAction.setToolTipText("Show all");

		exportExcelAction = new Action() {
			@Override
			public void run() {
				ExportCommand cmd = new ExportCommand(traceMatrixTable.getConfigRegistry(),
						traceMatrixTable.getShell());
				traceMatrixTable.doCommand(cmd);
			}
		};
		exportExcelAction.setText("Export to Excel...");
		exportExcelAction.setToolTipText("Exports the currently shown traceability matrix as an Excel file.");

	}

	/**
	 * Class for the column stack
	 */
	private class ColumnHeaderLayerStack extends AbstractLayerTransform {

		public ColumnHeaderLayerStack(IDataProvider dataProvider) {
			DataLayer dataLayer = new DataLayer(dataProvider);
			ColumnHeaderLayer colHeaderLayer = new ColumnHeaderLayer(dataLayer, TraceabilityMatrixView.this.bodyLayer,
					TraceabilityMatrixView.this.bodyLayer.getSelectionLayer());

			// Adding double click to the column header
			colHeaderLayer.addConfiguration(new AbstractUiBindingConfiguration() {

				@Override
				public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
					uiBindingRegistry.registerDoubleClickBinding(MouseEventMatcher.columnHeaderLeftClick(0),
							new IMouseAction() {

								@Override
								public void run(NatTable natTable, MouseEvent event) {
									try {
										int columnPosition = natTable.getColumnPositionByX(event.x);
										int columnIndex = natTable.getColumnIndexByPosition(columnPosition);
										EObject element = bodyDataProvider.getColumn(columnIndex);
										openEditor(element);
									} catch (PartInitException e) {
										// Deliberately do nothing.
									}
								}
							});
				}
			});

			setUnderlyingLayer(colHeaderLayer);
		}
	}

	/**
	 * Class for the row stack
	 */
	private class RowHeaderLayerStack extends AbstractLayerTransform {

		public RowHeaderLayerStack(IDataProvider dataProvider) {
			DataLayer dataLayer = new DataLayer(dataProvider, 50, 20);
			RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(dataLayer, TraceabilityMatrixView.this.bodyLayer,
					TraceabilityMatrixView.this.bodyLayer.getSelectionLayer());

			// Adding double click to the row header
			rowHeaderLayer.addConfiguration(new AbstractUiBindingConfiguration() {

				@Override
				public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
					uiBindingRegistry.registerDoubleClickBinding(MouseEventMatcher.rowHeaderLeftClick(0),
							new IMouseAction() {

								@Override
								public void run(NatTable natTable, MouseEvent event) {
									try {
										int rowPosition = natTable.getRowPositionByY(event.y);
										int rowIndex = natTable.getRowIndexByPosition(rowPosition);
										EObject element = bodyDataProvider.getRow(rowIndex);
										openEditor(element);
									} catch (PartInitException e) {
										// Deliberately do nothing.
									}
								}

							});
				}
			});
			setUnderlyingLayer(rowHeaderLayer);
		}
	}

	/**
	 * Class for the body stack
	 */
	private class BodyLayerStack extends AbstractLayerTransform {
		private SelectionLayer selectionLayer;

		public BodyLayerStack(IDataProvider dataProvider) {
			DataLayer bodyDataLayer = new DataLayer(dataProvider);
			HoverLayer bodyHoverLayer = new HoverLayer(bodyDataLayer);
			this.selectionLayer = new SelectionLayer(bodyHoverLayer);
			ViewportLayer viewportLayer = new ViewportLayer(this.selectionLayer);
			setUnderlyingLayer(viewportLayer);
		}

		public SelectionLayer getSelectionLayer() {
			return this.selectionLayer;
		}
	}
}
