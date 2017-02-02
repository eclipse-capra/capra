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
package org.eclipse.capra.handler.jdt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.handlers.ArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaCore;

/**
 * A handler to allow creating traces to and from java elements such as classes
 * and methods based on JDT.
 */
public class JavaElementHandler implements ArtifactHandler {

	@Override
	public boolean canHandleSelection(Object selection) {
		if (selection instanceof IJavaElement) {
			return true;
		}

		return false;
	}

	@Override
	public EObject getEObjectForSelection(Object selection, EObject artifactModel) {
		IJavaElement cu = (IJavaElement) selection;
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		EObject wrapper = adapter.createArtifact(artifactModel, this.getClass().getName(), cu.getHandleIdentifier(),
				cu.getElementName(), cu.getHandleIdentifier());
		return wrapper;
	}

	@Override
	public IJavaElement resolveArtifact(EObject artifact) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		String uri = adapter.getArtifactUri(artifact);
		return JavaCore.create(uri);
	}

	@Override
	public String getDisplayName(Object selection) {
		IJavaElement cu = (IJavaElement) selection;
		return cu.getElementName();
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		// Satisfied condition: delta contains change info about the file that
		// is either directly referenced by the wrapperUri or contains a child
		// element that is referenced by the wrapperUri.
		String message = "";

		IJavaElement linkedElement = JavaCore.create(wrapperUri);
		IJavaElement changedResource = JavaCore.create(delta.getResource());

		// changedResource is the Java file that is either directly
		// referenced by the wrapperUri or contains a child element that is
		// referenced by the wrapperUri.
		if (linkedElement == null || changedResource == null)
			return null;

		if (changedResource.getHandleIdentifier().equals(wrapperUri)) {
			// The object in the wrapper is a Java file.

			message = delta.getResource().getFullPath().toString()
					+ " has been edited. Please check if associated trace links are still valid.";

		} else {
			// The object referenced by the wrapper isn't a Java file, but a
			// child of the changed Java file (changedResource).

			IJavaElement[] jElements = ((ICompilationUnit) changedResource).findElements(linkedElement);
			if (jElements == null) {
				// The element from the wrapper has either been deleted,
				// renamed, or had its signature changed.

				message = linkedElement.getHandleIdentifier()
						+ " has either been deleted or had its signature changed. Please check if associated trace links are still valid.";
			} else {
				// The element from the wrapper has been changed (but not
				// renamed or deleted or had its signature changed).
				IJavaElement jElement = jElements[0];

				try {
					// Get the previous local version of the file.
					IFileState fs = ((IFile) delta.getResource()).getHistory(new NullProgressMonitor())[0];
					String previousState = new BufferedReader(new InputStreamReader(fs.getContents())).lines()
							.collect(Collectors.joining("\n"));

					if (linkedElement instanceof ISourceReference) {
						if (!previousState.contains(((ISourceReference) jElement).getSource())) {
							message = linkedElement.getHandleIdentifier()
									+ " has been edited. Please check if associated trace links are still valid.";
						}
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}

		return message;
	}
}
