/*******************************************************************************
 * Copyright (c) 2017 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.core.helpers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.handlers.IAnnotateArtifact;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * Helper class for creating traces
 */
public class TraceHelper {

	private EObject traceModel;
	private TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
	private ArtifactMetaModelAdapter artifactAdapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();

	/**
	 * @param traceModel
	 */
	public TraceHelper(EObject traceModel) {
		this.traceModel = traceModel;
	}

	/**
	 * Create a trace of the given traceType
	 *
	 * @param wrappers
	 * @param traceType
	 */
	public void createTrace(List<EObject> wrappers, EClass traceType) {
		traceAdapter.createTrace(traceType, traceModel, wrappers);
	}

	/**
	 * Annotate artifacts represented by wrappers
	 *
	 * @param wrappers
	 */
	public void annotateTrace(List<EObject> wrappers) {
		// Annotate if possible
		for (EObject wrapper : wrappers) {
			IArtifactHandler<Object> handler = artifactAdapter.getArtifactHandlerInstance(wrapper);
			if (handler instanceof IAnnotateArtifact) {
				IAnnotateArtifact h = (IAnnotateArtifact) handler;
				try {
					// Get unique connected artifacts, not including this
					// element
					// TODO: maybe add an adapter method for this?
					Set<EObject> connectedElements = new HashSet<EObject>();
					final StringBuilder annotation = new StringBuilder();
					List<Connection> connections = traceAdapter.getConnectedElements(wrapper, traceModel);
					connections.forEach(c -> {
						c.getTargets().forEach(t -> {
							if (t != wrapper) {
								connectedElements.add(t);
							}
						});
					});

					// Build annotation string
					connectedElements.forEach(e -> {
						if (annotation.length() > 0) {
							annotation.append(", ");
						}
						String name = artifactAdapter.getArtifactName(e);
						annotation.append(name);
					});

					h.annotateArtifact(wrapper, annotation.toString());
				} catch (Exception e) {
					// Ignore
				}
			}
		}
	}

}
