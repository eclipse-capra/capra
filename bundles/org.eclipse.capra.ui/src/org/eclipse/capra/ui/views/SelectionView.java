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
package org.eclipse.capra.ui.views;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.handlers.PriorityHandler;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.ViewPart;

public class SelectionView extends ViewPart {

	/** The ID of the view as specified by the extension. */
	public static final String ID = "org.eclipse.capra.ui.views.SelectionView";

	/**
	 * Identifier of the extension point that contains the transfer definitions.
	 */
	private static final String TRANSFER_EXTENSION_POINT_ID = "org.eclipse.capra.ui.transfers";

	/**
	 * Standard transfers that are always used in the SelectionView.
	 */
	private static final Transfer[] DEFAULT_TRANSFERS = new Transfer[] {
			org.eclipse.ui.part.ResourceTransfer.getInstance(), org.eclipse.ui.part.EditorInputTransfer.getInstance(),
			org.eclipse.swt.dnd.FileTransfer.getInstance(), org.eclipse.swt.dnd.RTFTransfer.getInstance(),
			org.eclipse.swt.dnd.TextTransfer.getInstance(), org.eclipse.swt.dnd.URLTransfer.getInstance(),
			org.eclipse.jface.util.LocalSelectionTransfer.getTransfer(),
			org.eclipse.emf.edit.ui.dnd.LocalTransfer.getInstance() };

	/**
	 * The actual table containing selected elements.
	 */
	public TableViewer artifactTable;

	/**
	 * The combo box to select the trace type.
	 */
	public ComboViewer traceTypeCombo;

	/**
	 * The maintained selection of EObjects .
	 */
	private Set<Object> selection = new LinkedHashSet<>();

	/**
	 * The appropriate undo context. We are using the global context to ensure that
	 * trace creation can be undone in all viewers and editors.
	 */
	private IUndoContext undoContext = IOperationHistory.GLOBAL_UNDO_CONTEXT;

	/**
	 * Action handler to undo the creation of a trace.
	 */
	private UndoActionHandler undoAction;

	/**
	 * Action handler to redo the creation of a trace.
	 */
	private RedoActionHandler redoAction;

	private Collection<EClass> traceTypes = new ArrayList<>();

	/**
	 * Content provider for the viewer that lists the artifacts.
	 */
	class ArtifactTableContentProvider implements IStructuredContentProvider {

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

		@Override
		public Object[] getElements(Object parent) {
			return selection.toArray();
		}
	}

	class TraceTypeContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object parent) {
			return traceTypes.toArray();
		}
	}

	class TraceTypeLabelProvider extends LabelProvider implements IBaseLabelProvider {

		@Override
		public String getText(Object element) {
			return (element == null || !(element instanceof EClass)) ? "" : ((EClass) element).getName();//$NON-NLS-1$
		}

	}

	/**
	 * Label provider for the viewer that lists the artifacts.
	 */
	class ArtifactTableLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public String getText(Object element) {
			TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
			EObject artifactModel = persistenceAdapter.getArtifactWrappers(new ResourceSetImpl());
			ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
			IArtifactHandler<?> handler = artifactHelper.getHandler(element).get();
			return handler.withCastedHandler(element, (h, o) -> h.getDisplayName(o)).orElseGet(element::toString);
		}

		@Override
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		@Override
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * Leaves the order of objects unchanged by returning 0 for all combinations of
	 * objects.
	 *
	 * @see ViewerComparator#compare(Viewer, Object, Object)
	 */
	class NoChangeComparator extends ViewerComparator {

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			// Retain order in which the user dragged in the elements
			return 0;
		}

	}

	class SelectionDropAdapter extends ViewerDropAdapter {
		TableViewer view;

		public SelectionDropAdapter(TableViewer view) {
			super(artifactTable);
			this.view = view;
		}

		@Override
		public boolean performDrop(Object data) {
			dropToSelection(data);
			return true;
		}

		@Override
		public boolean validateDrop(Object target, int operation, TransferData transferType) {
			return true;
		}

	}

	@Override
	public void createPartControl(Composite parent) {
		Composite area = new Composite(parent, SWT.NONE);
		area.setLayout(new GridLayout(1, false));
		area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		traceTypeCombo = new ComboViewer(area);
		traceTypeCombo.setContentProvider(new TraceTypeContentProvider());
		traceTypeCombo.setLabelProvider(new TraceTypeLabelProvider());
		traceTypeCombo.setInput(getViewSite());
		traceTypeCombo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		artifactTable = new TableViewer(area, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		artifactTable.setContentProvider(new ArtifactTableContentProvider());
		artifactTable.setLabelProvider(new ArtifactTableLabelProvider());
		artifactTable.setComparator(new NoChangeComparator());
		artifactTable.setInput(getViewSite());
		artifactTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		getSite().setSelectionProvider(artifactTable);
		hookContextMenu();

		int ops = DND.DROP_COPY | DND.DROP_MOVE;

		List<Transfer> transfers = new ArrayList<Transfer>(Arrays.asList(DEFAULT_TRANSFERS));

		// Get all additionally configured transfers from the extension point.
		transfers.addAll(ExtensionPointHelper.getExtensions(TRANSFER_EXTENSION_POINT_ID, "class").stream()
				.map(Transfer.class::cast).collect(Collectors.toList()));

		artifactTable.addDropSupport(ops, transfers.toArray(DEFAULT_TRANSFERS),
				new SelectionDropAdapter(artifactTable));

		createGlobalActionHandlers();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {

			}
		});
		Menu menu = menuMgr.createContextMenu(artifactTable.getControl());
		artifactTable.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, artifactTable);
	}

	@Override
	public void setFocus() {
		artifactTable.getControl().setFocus();
	}

	@SuppressWarnings("unchecked")
	public void dropToSelection(Object data) {
		if (data instanceof TreeSelection) {
			TreeSelection tree = (TreeSelection) data;
			if (tree.toList().stream().allMatch(this::validateSelection))
				selection.addAll(tree.toList());
		} else if (data instanceof Collection<?>) {
			Collection<Object> arrayselection = (Collection<Object>) data;
			if (arrayselection.stream().allMatch(this::validateSelection))
				selection.addAll(arrayselection);
		} else if (data instanceof IStructuredSelection) {
			IStructuredSelection iselection = (IStructuredSelection) data;
			if (iselection.toList().stream().allMatch(this::validateSelection))
				selection.addAll(iselection.toList());
		} else if (validateSelection(data))
			selection.add(data);

		artifactTable.refresh();
		refreshAvailableTraceTypes();
	}

	private void refreshAvailableTraceTypes() {
		TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();

		ResourceSet resourceSet = new ResourceSetImpl();
		// add artifact model to resource set
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);

		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);

		// Create the artifact wrappers
		List<EObject> wrappers = artifactHelper.createWrappers(new ArrayList<Object>(selection));

		// Get the type of trace to be created
		traceTypes = traceAdapter.getAvailableTraceTypes(wrappers);
		traceTypeCombo.refresh();
	}

	private boolean validateSelection(Object target) {
		Collection<IArtifactHandler<?>> artifactHandlers = ExtensionPointHelper.getArtifactHandlers();
		List<IArtifactHandler<?>> availableHandlers = artifactHandlers.stream()
				.filter(handler -> handler.canHandleArtifact(target)).collect(toList());

		Optional<PriorityHandler> priorityHandler = ExtensionPointHelper.getPriorityHandler();
		if (availableHandlers.size() == 0) {
			MessageDialog.openWarning(getSite().getShell(), "No handler for selected item",
					"There is no handler for " + target + " so it will be ignored.");
		} else if (availableHandlers.size() > 1 && !priorityHandler.isPresent()) {
			MessageDialog.openWarning(getSite().getShell(), "Multiple handlers for selected item",
					"There are multiple handlers for " + target + " so it will be ignored.");
		} else if (availableHandlers.size() > 1 && priorityHandler.isPresent()) {
			return true;
		} else {
			return true;
		}
		return false;
	}

	public List<Object> getSelection() {
		return new ArrayList<Object>(selection);
	}

	public void clearSelection() {
		selection.clear();
		artifactTable.refresh();
		refreshAvailableTraceTypes();
	}

	public static SelectionView getOpenedView() {
		try {
			return (SelectionView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void removeFromSelection(List<Object> currentselection) {
		selection.removeAll(currentselection);
		artifactTable.refresh();
		refreshAvailableTraceTypes();
	}

	public EClass getSelectedTraceType() {
		return (EClass) traceTypeCombo.getStructuredSelection().getFirstElement();
	}

	private void createGlobalActionHandlers() {
		// set up action handlers that operate on the current context
		undoAction = new UndoActionHandler(this.getSite(), undoContext);
		redoAction = new RedoActionHandler(this.getSite(), undoContext);
		IActionBars actionBars = getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction);
		actionBars.updateActionBars();
	}
}
