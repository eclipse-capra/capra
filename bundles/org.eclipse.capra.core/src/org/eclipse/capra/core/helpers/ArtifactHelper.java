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
package org.eclipse.capra.core.helpers;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.handlers.PriorityHandler;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Provides support methods for working with artifacts and wrappers.
 */
public class ArtifactHelper {

	private static final String QUOTE_CHARACTERS = "[\"\']";
	private static final String NEWLINE_CHARACTERS = "[\r\n]+";

	private EObject artifactModel;

	// Switch to Optional here to express potential absence in the type
	private static Optional<PriorityHandler> priorityHandler = ExtensionPointHelper.getPriorityHandler();

	// This is a tricky type... It is not really necessary here, but let's take
	// it as a generics tutorial example!
	//
	// I used it during development because this type can contain collections
	// both of type IArtifactHandler<?>
	// AND of IArtifactHandler<Object>. The simpler type
	// Collection<IArtifactHandler<?>> can only hold
	// IArtifactHandler<?>.
	private static Collection<? extends IArtifactHandler<?>> handlers = ExtensionPointHelper.getArtifactHandlers();

	private String sanitize(String input) {
		return input.replaceAll(QUOTE_CHARACTERS, " ").replaceAll(NEWLINE_CHARACTERS, " ");
	}

	/**
	 * Constructs a new {@code ArtifactHelper} for the given artifact model.
	 * 
	 * @param artifactModel the model containing the artifact this helper should
	 *                      access
	 */
	public ArtifactHelper(EObject artifactModel) {
		this.artifactModel = artifactModel;
	}

	/**
	 * Creates wrappers for the given artifacts. For this purpose, this method
	 * identifies the handler for each artifact and retrieves a wrapper from the
	 * handler.
	 *
	 * @param artifacts the artifacts that should be wrapped
	 * @return a list of wrappers for the given artifacts
	 * @see IArtifactHandler#createWrapper(Object, EObject)
	 */
	public List<EObject> createWrappers(List<?> artifacts) {
		return artifacts.stream()
				.map(vagueArtifact -> getHandler(vagueArtifact).map(h -> h.withCastedHandlerUnchecked(vagueArtifact,
						(handler, artifact) -> handler.createWrapper(artifact, artifactModel))))
				.filter(Optional::isPresent).map(Optional::get).collect(toList());

	}

	/**
	 * Creates a wrapper for the given artifact using the {@link ArtifactHandler}
	 * returned by {@link #getHandler(Object)}.
	 *
	 * @param vagueArtifact the object that should be wrapped
	 * @return the wrapped artifact or {@code null} if no handler exists
	 */
	public EObject createWrapper(Object vagueArtifact) {
		Optional<EObject> wrapped = getHandler(vagueArtifact)
				.map(vagueHandler -> vagueHandler.withCastedHandlerUnchecked(vagueArtifact,
						(handler, artifact) -> handler.createWrapper(artifact, artifactModel)));

		return wrapped.orElse(null);
	}

	/**
	 * Unwraps an artifact wrapper to get its original object. If the original
	 * object is <code>null</code>, the wrapper is returned as it is. If the
	 * provided object is not a wrapper, it is also returned as is.
	 * 
	 * @param wrapper to be unwrapped
	 * @return the original artifact
	 */
	public Object unwrapWrapper(Object wrapper) {
		if (wrapper instanceof EObject) {
			ArtifactMetaModelAdapter artifactMetaModelAdapter = ExtensionPointHelper
					.getArtifactWrapperMetaModelAdapter().orElseThrow();
			IArtifactHandler<?> handler = artifactMetaModelAdapter.getArtifactHandlerInstance((EObject) wrapper);
			if (handler != null && handler.resolveWrapper((EObject) wrapper) != null) {
				return handler.resolveWrapper((EObject) wrapper);
			} else
				return wrapper;
		} else
			return wrapper;
	}

	/**
	 * Retrieves the {@link IArtifactHandler} able to handle artifacts of the type
	 * of the provided instance. If several handlers are able to handle artifacts of
	 * the given type, the {@link PriorityHandler} is used to find a fitting one.
	 * 
	 * @param artifact the artifact for which the handler should be found
	 * @return an {@link Optional} containing either an artifact handler or
	 *         {@link Optional#empty()}
	 */
	public <T> Optional<IArtifactHandler<?>> getHandler(T artifact) {
		List<IArtifactHandler<?>> availableHandlers = handlers.stream().filter(h -> h.canHandleArtifact(artifact))
				.collect(toList());
		if (availableHandlers.isEmpty()) {
			return Optional.empty();
		} else if (availableHandlers.size() == 1) {
			return Optional.of(availableHandlers.get(0));
		} else {
			return priorityHandler.map(h -> h.getSelectedHandler(availableHandlers, artifact));
		}
	}

	/**
	 * Gets the label of the element to be used for display, e.g., in graphical
	 * views. It either uses the {@link IArtifactHandler#getDisplayName(Object)
	 * method for wrapped objects or {@link EMFHelper#getIdentifier(EObject)} for
	 * {@link EObject) instances.
	 *
	 * @param object The object for which the label is needed. This can be an EMF
	 *               original representation or an artifact wrapper if the original
	 *               object was not an EMF element
	 * @return the label to be displayed
	 */
	public String getArtifactLabel(EObject object) {
		String artifactLabel = null;
		Object originalObject = this.unwrapWrapper(object);

		if (originalObject != null) {
			IArtifactHandler<?> handler = this.getHandler(originalObject).orElseThrow();
			artifactLabel = handler.withCastedHandler(originalObject, (h, o) -> h.getDisplayName(o))
					.orElseThrow(IllegalArgumentException::new);
		} else { // original object cannot be resolved
			// therefore use the wrapper name
			String label = EMFHelper.getIdentifier(object);
			if (label.indexOf(':') > 0) {
				artifactLabel = label.substring(0, label.indexOf(':'));
			}
		}
		// remove unwanted characters like ", '
		if (artifactLabel != null) {
			return sanitize(artifactLabel);
		} else {
			// This can happen if the trace model contains elements for which
			// the artifact handler is not available.
			// While this should not happen in a user installation, it is not
			// uncommon during testing.
			return "Unknown (no fitting artifact handler found)";
		}
	}

	/**
	 * Gets the location of the given object in the workspace as a platform URI,
	 * resolving artifact wrappers as necessary.
	 * <p>
	 * This method relies on the correct setting of the {@code URI} attribute of the
	 * corresponding {@link ArtifactWrapper}. The URI should be recognisable by the
	 * platform to ensure that the correct editor is opened.
	 * 
	 * @param object the {@code EObject} (or {@code ArtifactWrapper}) to get the
	 *               link for
	 * @return a platform URI that can be used to resolve the object or {@code null}
	 *         if none can be found
	 */
	public String getArtifactLocation(EObject object) {
		String artifactLink = null;
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();

		artifactLink = adapter.getArtifactUri(object);
		if (artifactLink == null) {
			try {
				artifactLink = EcoreUtil.getURI(object).toPlatformString(false);
			} catch (IllegalArgumentException ex) {
				// Deliberately do nothing
			}
		}
		if (artifactLink != null && !artifactLink.isEmpty() && artifactLink.startsWith("/")) {
			artifactLink = "platform:/resource" + artifactLink;
		}
		return artifactLink;
	}

}
