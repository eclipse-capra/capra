/*******************************************************************************
 * Copyright (c) 2022 University of Gothenburg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *  
 * SPDX-License-Identifier: EPL-2.0
 *  
 * Contributors:
 *      University of Gothenburg - initial API and implementation
 *******************************************************************************/
package org.eclipse.capra.ui.asciidoc;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.capra.ui.selections.ISelectionSupport;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IContributedContentsView;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.outline.AsciiDoctorContentOutlinePage;
import de.jcup.asciidoctoreditor.outline.Item;

/**
 * Provides support for selecting AsciiDoc elements either in an
 * AsciiDoctorEditor or in the corresponding outline view.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class AsciiDocSelectionSupport implements ISelectionSupport {

	private static final Logger LOG = LoggerFactory.getLogger(AsciiDocSelectionSupport.class);

	@Override
	public boolean supportsWorkbenchPart(IWorkbenchPart workbenchPart) {
		return (workbenchPart instanceof AsciiDoctorEditor || workbenchPart instanceof ContentOutline);
	}

	@Override
	public List<Object> extractSelectedElements(ISelection selection, IWorkbenchPart workbenchPart) {
		AsciiDoctorEditor editor = null;
		Item selectedElement = null;
		// Find the editor
		if (workbenchPart instanceof AsciiDoctorEditor) {
			editor = (AsciiDoctorEditor) workbenchPart;
		} else if (workbenchPart instanceof ContentOutline) {
			ContentOutline contentOutline = (ContentOutline) workbenchPart;
			IPage currentPage = contentOutline.getCurrentPage();
			if (currentPage instanceof AsciiDoctorContentOutlinePage) {
				IContributedContentsView contributedView = contentOutline.getAdapter(IContributedContentsView.class);
				editor = (AsciiDoctorEditor) contributedView.getContributingPart();
			}
		}
		if (editor == null) {
			return Collections.emptyList();
		}

		// Retrieve the item from the selection
		if (selection instanceof ITextSelection) {
			final ITextSelection textselection = (ITextSelection) selection;
			selectedElement = editor.getItemAt(textselection.getOffset());

		} else if (selection instanceof TreeSelection) {
			selectedElement = (Item) ((TreeSelection) selection).getFirstElement();
		}
		if (selectedElement == null) {
			return Collections.emptyList();
		}

		// Construct and return the artifact
		File editorFile = editor.getEditorFileOrNull();
		if (editorFile != null) {
			IPath path = new Path(editorFile.getAbsolutePath());
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IFile file = root.getFileForLocation(path);

			URI uri;
			try {
				if (file != null) {
					uri = new URIBuilder().setScheme("platform").setPath("/resource" + file.getFullPath())
							.setFragment(Integer.toString(selectedElement.getOffset())).build();
				} else {
					uri = new URIBuilder().setScheme("file").setPath(editorFile.getAbsolutePath())
							.setFragment(Integer.toString(selectedElement.getOffset())).build();
				}
				AsciiDocArtifact artifact = new AsciiDocArtifact(uri.toString(), selectedElement);
				return Collections.singletonList(artifact);
			} catch (URISyntaxException e) {
				LOG.error("Could not build URI for ", editorFile.getPath());
			}
		}
		return Collections.emptyList();
	}

	@Override
	public ResourceSet getResourceSet(IWorkbenchPart workbenchPart) {
		// Deliberately do nothing
		return null;
	}

}
