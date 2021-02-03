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
package org.eclipse.capra.handler.jdt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.handlers.AnnotationException;
import org.eclipse.capra.core.handlers.IAnnotateArtifact;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.handler.jdt.preferences.JDTPreferences;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;

/**
 * A handler to allow creating traces to and from java elements such as classes
 * and methods based on JDT.
 * </p>
 * This handler encodes a locator to the Java element in artifact URI:s in the
 * following way:
 * {@code platform:/Project_name/path/to/file.java#com.pack.ClassName/methodName}.
 */
public class JavaElementHandler extends AbstractArtifactHandler<IJavaElement> implements IAnnotateArtifact {

	@Override
	public EObject createWrapper(IJavaElement element, EObject artifactModel) {
		IType type = (IType) element.getParent().getAncestor(IJavaElement.TYPE);

		String fragment;
		if (type == null) {
			if (element.getElementType() == IJavaElement.TYPE) {
				// Top level classes get their fully qualified name
				fragment = ((IType) element).getFullyQualifiedName();
			} else {
				// This will probably never happen, if something doesn't
				// have a type ancestor it is always a type itself
				fragment = element.getElementName();
			}
		} else {
			// Case for members: A list of '/' separated type names, followed by
			// a member name
			fragment = type.getFullyQualifiedName().replace("\\$", "/") + "/" + element.getElementName();
		}

		URIBuilder uriBuilder = new URIBuilder().setScheme("platform").setPath("/resource" + element.getPath())
				.setFragment(fragment);

		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();

		String displayName = (type == null ? "" : (type.getElementName() + ".")) + element.getElementName();

		return adapter.createArtifact(artifactModel, this.getClass().getName(), uriBuilder.toString(),
				element.getHandleIdentifier(), displayName, element.getPath().toString());
	}

	@Override
	public IJavaElement resolveWrapper(EObject wrapper) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().orElseThrow();
		return JavaCore.create(adapter.getArtifactIdentifier(wrapper));
	}

	@Override
	public String getDisplayName(IJavaElement element) {
		return element.getElementName();
	}

	@Override
	public void annotateArtifact(EObject wrapper, String annotation) throws AnnotationException {
		IEclipsePreferences preferences = JDTPreferences.getPreferences();
		if (preferences.getBoolean(JDTPreferences.ANNOTATE_JDT, JDTPreferences.ANNOTATE_JDT_DEFAULT)) {
			IJavaElement handle = resolveWrapper(wrapper);
			JDTAnnotate.annotateArtifact(handle, annotation);
		}
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

					if ((linkedElement instanceof ISourceReference)
							&& (!previousState.contains(((ISourceReference) jElement).getSource()))) {
						message = linkedElement.getHandleIdentifier()
								+ " has been edited. Please check if associated trace links are still valid.";
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}

		return message;
	}

	@Override
	public List<Connection> getInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes) {
		// Method currently left empty to wait for user requirements of relevant
		// internal links for Java code
		return Collections.emptyList();
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		return false;
	}
}
