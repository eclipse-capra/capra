/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *   Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.ui.zest;

import java.util.List;

import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.helpers.TraceCreationHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.gef4.common.adapt.inject.AdapterInjectionSupport;
import org.eclipse.gef4.common.adapt.inject.AdapterInjectionSupport.LoggingMode;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef4.zest.fx.jface.ZestContentViewer;
import org.eclipse.gef4.zest.fx.jface.ZestFxJFaceModule;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * This class creates the view to hold the content of the model displayed as a
 * graph in the Zest view. The view is connected to the model using a content
 * provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */
public class ZestView extends ViewPart {

	static ZestContentViewer viewer = null;
	private ISelectionListener selectionListener;

	public class ZestViewModule extends ZestFxJFaceModule {
		@Override
		protected void enableAdapterMapInjection() {
			install(new AdapterInjectionSupport(LoggingMode.PRODUCTION));
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		TracePersistenceAdapter tracePersistenceAdapter;

		tracePersistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();

		viewer = new ZestContentViewer(new ZestViewModule());
		viewer.createControl(parent, SWT.NONE);
		viewer.setLabelProvider(new TraceNodeLabelProvider());
		ILayoutAlgorithm layout = new TreeLayoutAlgorithm();
		viewer.setLayoutAlgorithm(layout);

		selectionListener = new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				EObject traceModel;
				TraceMetaModelAdapter metaModelAdapter;
				ResourceSet resourceSet;

				List<Object> selectedModels = TraceCreationHelper.extractSelectedElements(selection);

				if (selectedModels.size() >= 1 && selectedModels.get(0) instanceof EObject) {
					EObject selectedEObject = (EObject) selectedModels.get(0);
					resourceSet = selectedEObject.eResource().getResourceSet();
					traceModel = tracePersistenceAdapter.getTraceModel(resourceSet);
					metaModelAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
					viewer.setContentProvider(
							new TraceNodeContentProvider(traceModel, metaModelAdapter, selectedModels));
					viewer.setInput(null);
					viewer.refresh();

				}
				// TODO Implement what should be displayed when more than one
				// element has been selected
			}

		};
		getViewSite().getPage().addSelectionListener(selectionListener);

	}

	@Override
	public void dispose() {
		getSite().getPage().removeSelectionListener(selectionListener);
		super.dispose();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
}
