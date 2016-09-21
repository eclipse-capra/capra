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
package org.eclipse.capra.ui.notification;

import java.io.IOException;

import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapper;
import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapperContainer;
import org.eclipse.capra.GenericArtifactMetaModel.GenericArtifactMetaModelFactory;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.IMarkerResolution;

/**
 * TODO: Document this class. I am not sure what it actually does.
 * 
 * @author Michael Warne
 *
 */
public class DummyURIOnlyQuickFix implements IMarkerResolution {

	private URI uri;
	private TracePersistenceAdapter tracePersistenceAdapter;
	private ResourceSet resourceSet;
	private EObject awc;
	private String label;
	private ArtifactWrapper art;
	private Resource resourceForArtifacts;
	private ArtifactWrapperContainer container;
	private String movedToPath;
	private String fullPath;
	// private String fileName;

	DummyURIOnlyQuickFix(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {
		try {
			fullPath = (String) marker.getAttribute("DeltaFullPath");
			movedToPath = (String) marker.getAttribute("DeltaMovedToPath");
			// fileName = (String)marker.getAttribute("FileName");
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		resourceSet = new ResourceSetImpl();
		tracePersistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		awc = tracePersistenceAdapter.getArtifactWrappers(resourceSet);
		art = GenericArtifactMetaModelFactory.eINSTANCE.createArtifactWrapper();
		uri = EcoreUtil.getURI(awc);
		resourceForArtifacts = resourceSet.createResource(uri);
		EList<ArtifactWrapper> list = ((ArtifactWrapperContainer) awc).getArtifacts();
		container = (ArtifactWrapperContainer) awc;
		int counter = -1;
		for (ArtifactWrapper aw : list) {
			counter++;
			String s = aw.getUri().replace("<{", "/");
			s = s.substring(1);
			s = s.replace("<", "/");
			s = s.replace("{", "/");
			s = "/" + s;
			if (s.equals(fullPath)) {
				art.setArtifactHandler(aw.getArtifactHandler());
				art.setName(aw.getName());
				art.setUri(movedToPath);
				break;
			}
		}

		if (art.getUri() != null) {
			container.getArtifacts().set(counter, art);
			resourceForArtifacts.getContents().add(container);
			try {
				resourceForArtifacts.save(null);
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		try {
			marker.delete();
		} catch (CoreException e) {

			e.printStackTrace();
		}
	}
}
