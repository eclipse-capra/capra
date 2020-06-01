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
 *     Fredrik Johansson and Themistoklis Ntoukolis - initial implementation of the Sunburst View
 *******************************************************************************/
package org.eclipse.capra.ui.sunburst.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.handlers.IArtifactUnpacker;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.helpers.SelectionSupportHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * Provides a view of the trace model using a navigable sunburst view.
 * <p>
 * The sunburst view visualises the traced artifacts as circle segments centred
 * around a circle that represents the currently selected artifact. Each circle
 * segment is in turn surrounded by circle fragments that represent the
 * artifacts that are traced from it. This way, it is possible to visualise even
 * complex trace models in a compact way.
 * <p>
 * The view is navigable. When clicking on one of the circle segments, the view
 * centres on the artifact represented by the circle segment. Clicking on the
 * centre zooms out again.
 * <p>
 * The sunburst view is based on <a href="https://d3js.org/d3.js</a> and
 * <a href="https://github.com/vasturiano/sunburst-chart">sunburst-chart</a>.
 * The library d3.js is licensed under BSD-3-Clause and sunburst-chart is
 * licensed under MIT.
 * 
 * 
 * @author Fredrik Johansson
 * @author Themistoklis Ntoukolis
 *
 */
public class SunburstView extends ViewPart {

	/**
	 * The location of the HTML file the JSON code of the sunburst data will be
	 * injected in.
	 */
	private static final String HTML_SOURCE_LOCATION = "platform:/plugin/org.eclipse.capra.ui.sunburst/src/html/index.html";

	private static final int MAX_RECURSION_LEVEL = 5;

	private Browser browser;

	final TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
	TraceMetaModelAdapter metamodelAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
	TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();

	private ResourceSet resourceSet = new ResourceSetImpl();
	private EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);
	private EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);

	private ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);

	private List<Object> selectedModels = new ArrayList<>();

	private ISelectionListener listener = new ISelectionListener() {
		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			getSelected(part, selection);
			browser.setText(createHTML());
		}
	};

	/**
	 * Retrieves the selected elements from {@code selection} and stores them in
	 * {@link #selectedModels}.
	 * 
	 * @param part      the workbench part
	 * @param selection the selection
	 */
	private void getSelected(IWorkbenchPart part, ISelection selection) {
		selectedModels.clear();
		if (part.getSite().getSelectionProvider() != null) {
			selectedModels.addAll(SelectionSupportHelper
					.extractSelectedElements(part.getSite().getSelectionProvider().getSelection(), part));
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		browser.setText(createHTML());
		getViewSite().getPage().addSelectionListener(listener);
	}

	/**
	 * Disposes the internal browser widget.
	 */
	@Override
	public void dispose() {
		getViewSite().getPage().removeSelectionListener(listener);
		browser.dispose();
		browser = null;
		super.dispose();
	}

	@Override
	public void setFocus() {
		// Deliberately do nothing.
	}

	/**
	 * Retrieves the code of the HTML container for the sunburst view from the
	 * filesystem.
	 * 
	 * @return the HMTL code stored in {@link SunburstView#HTML_SOURCE_LOCATION}
	 */
	private String getHTML() {
		URL url;
		StringBuilder html = new StringBuilder();
		try {
			url = new URL(HTML_SOURCE_LOCATION);
			InputStream inputStream = url.openConnection().getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				html.append(inputLine);
				html.append(System.lineSeparator());
			}

			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return html.toString();
	}

	/**
	 * Injects the generated JSON code into the HTML page and returns the full page
	 * to be displayed in the browser.
	 * 
	 * @return the HTML code to display in the browser
	 */
	private String createHTML() {
		String json = convertToJSON();
		String html = getHTML();
		return html.replace("const nodeData;", "const nodeData = " + json + ";");
	}

	/**
	 * Converts the trace model into a JSON format that serves as input for the
	 * sunburst visualisation.
	 * 
	 * @return the trace model in JSON format
	 */
	@SuppressWarnings("unchecked")
	private String convertToJSON() {
		List<EObject> secondLevelNodes = new ArrayList<>();
		String start = "";
		String end = "]}";
		StringBuilder inner = new StringBuilder();

		EObject selectedObject;
		List<EObject> alreadySeen = new ArrayList<>();

		if (!selectedModels.isEmpty()) {
			ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
			for (Object selection : selectedModels) {

				IArtifactHandler<Object> handler = (IArtifactHandler<Object>) artifactHelper.getHandler(selection)
						.orElse(null);
				if (handler != null) {
					Object unpackedElement = null;
					if (handler instanceof IArtifactUnpacker) {
						unpackedElement = IArtifactUnpacker.class.cast(handler).unpack(selection);
					} else {
						unpackedElement = selection;
					}
					EObject wrappedElement = handler.createWrapper(unpackedElement, artifactModel);
					if (isInTraceModel(wrappedElement)) {
						selectedObject = wrappedElement;
						if (selectedModels.size() == 1) {
							secondLevelNodes = getUniqueChildren(null, selectedObject);
							start = "{\"name\": \"" + artifactHelper.getArtifactLabel(selectedObject)
									+ "\",\"children\":[";
							alreadySeen.add(selectedObject);
						} else {
							// if more than one artifact is selected, we introduce a "virtual" node and add
							// them both to the second level
							start = "{\"name\":\"\",\"children\":[";
							secondLevelNodes.add(selectedObject);
						}
					}
				}
			}
		}
		for (int secondLevelIndex = 0; secondLevelIndex < secondLevelNodes.size(); secondLevelIndex++) {
			processHierarchyLevel(inner, secondLevelNodes.get(secondLevelIndex), alreadySeen, 1);
			if (secondLevelIndex < secondLevelNodes.size() - 1) {
				inner.append(",");
			}
		}

		String json = start + inner.toString() + end;
		return json;
	}

	/**
	 * Recursive method that traverses the trace model starting from {@code parent}.
	 * It adds the JSON code for the parent and all children to the given
	 * {@link StringBuilder} as long as the maximum recursion depth has not been
	 * reached.
	 * 
	 * @param builder      the {@code StringBuilder} to which the JSON code should
	 *                     be added
	 * @param parent       the node that should be traversed in this iteration of
	 *                     the recursion
	 * @param alreadySeen  a list of nodes that have already been treated
	 * @param currentLevel the current recursion level
	 */
	private void processHierarchyLevel(StringBuilder builder, EObject parent, List<EObject> alreadySeen,
			int currentLevel) {
		if (alreadySeen == null)
			alreadySeen = new ArrayList<>();

		// Add the current parent to the view
		String text = artifactHelper.getArtifactLabel(parent);
		getEntryJSON(builder, text);
		alreadySeen.add(parent);

		// Stop the recursion if we have reached the maximum depth
		if (currentLevel < MAX_RECURSION_LEVEL) {
			List<EObject> children = getUniqueChildren(alreadySeen, parent);
			if (!children.isEmpty()) {
				builder.append("\"children\":[");
				// Go through the children and recurse
				for (int childLevelIndex = 0; childLevelIndex < children.size(); childLevelIndex++) {
					processHierarchyLevel(builder, children.get(childLevelIndex), alreadySeen, currentLevel++);
					alreadySeen.remove(children.get(childLevelIndex));

					if (childLevelIndex < children.size() - 1) {
						builder.append(",");
					}
				}
				builder.append("]}");
			} else {
				builder.append("\"size\": 1}");
			}
		} else {
			builder.append("\"size\": 1}");
		}
	}

	/**
	 * Adds the JSON code for the entry with the provided {@code name} to the
	 * provided {@code builder}.
	 * 
	 * @param builder   the {@link StringBuilder} to add the code to
	 * @param entryName the name of the entry to add
	 */
	private void getEntryJSON(StringBuilder builder, String entryName) {
		builder.append("{\"name\":\"").append(entryName).append("\",");
	}

	/**
	 * Retrieves a unique list of {@link EObject} instances that are connected to
	 * the provided {@code artifact}. The elements in the result are unique in the
	 * sense that they will not contain {@code artifact}, will be free of
	 * duplicates, and will not contain any elements contained in {@code prev}.
	 * <p>
	 * The method will always return a valid list which can be empty.
	 * 
	 * @param prev     a list of elements that should be excluded from the result
	 * @param artifact the artifact whose children are requested
	 * @return a list of unique elements connected to {@code artifact}
	 */
	public List<EObject> getUniqueChildren(List<EObject> prev, EObject artifact) {
		List<Connection> conNow = traceAdapter.getConnectedElements(artifact, traceModel);
		List<EObject> allConnected = getAllUniqueElements(conNow);
		if (isElementInList(allConnected, artifact)) {
			allConnected.remove(artifact);
		}
		if (prev != null) {
			for (EObject x : prev) {
				if (isElementInList(allConnected, x)) {
					allConnected.remove(x);
				}
			}
		}
		return allConnected;
	}

	/**
	 * Compares to {@link EObject} instances based on their identifier using
	 * {@link EMFHelper#getIdentifier(EObject)}.
	 * 
	 * @param first  the first {@code EObject} to compare
	 * @param second the second {@code EObject} to compare
	 * @return {@code true} if the identifiers of the two instances are equal,
	 *         {@code false} otherwise
	 */
	private boolean sameElement(EObject first, EObject second) {
		return EMFHelper.getIdentifier(first).equals(EMFHelper.getIdentifier(second));
	}

	/**
	 * Checks if the given {@link EObject} is in the list of {@code EObject}s using
	 * {@link #sameElement(EObject, EObject)}.
	 * 
	 * @param list the list to check
	 * @param obj  the object to check for
	 * @return {@code true} if {@code obj} is in {@code list}, {@code false}
	 *         otherwise
	 */
	private boolean isElementInList(List<EObject> list, EObject obj) {
		for (EObject next : list) {
			if (sameElement(obj, next)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retrieves a list of all unique artifacts connected by the provided list of
	 * {@code traces}.
	 * <p>
	 * The method will always return a valid list which can be empty.
	 * 
	 * @param traces the list of traces whose artifacts
	 * @return a list of unique artifacts connected by the provided list of
	 *         {@code traces}
	 */
	private List<EObject> getAllUniqueElements(List<Connection> traces) {
		List<EObject> inserted = new ArrayList<>();
		for (Connection trace : traces) {
			EObject element = trace.getOrigin();
			if (!isElementInList(inserted, element)) {
				inserted.add(element);
			}

			for (EObject target : trace.getTargets()) {
				if (!isElementInList(inserted, target)) {
					inserted.add(target);
				}
			}
		}
		return inserted;
	}

	/**
	 * Checks if the given {@code artifact} is part of the trace model.
	 * 
	 * @param artifact the artifact whose presence is checked
	 * @return {@code true} if the artifact is contained in the trace model,
	 *         {@code false} otherwise
	 */
	private boolean isInTraceModel(EObject artifact) {
		List<EObject> artifacts = getAllUniqueElements(metamodelAdapter.getAllTraceLinks(traceModel));

		if (isElementInList(artifacts, artifact)) {
			return true;
		} else {
			return false;
		}
	}

}