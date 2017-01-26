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
import java.util.List;

import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapper;
import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapperContainer;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.IMarkerResolution;

/**
 * Renames and updates the properties in the associated artifact wrapper to
 * reflect changes in the original object represented by the wrapper.
 * 
 * @author Michael Warne
 */
public class RenameOrMoveQuickFix implements IMarkerResolution {

	private String label;

	RenameOrMoveQuickFix(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {
		ResourceSet resourceSet = new ResourceSetImpl();
		TracePersistenceAdapter tracePersistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		EObject awc = tracePersistenceAdapter.getArtifactWrappers(resourceSet);
		List<ArtifactWrapper> artifacts = ((ArtifactWrapperContainer) awc).getArtifacts();

		String oldArtifactUri = marker.getAttribute(CapraNotificationHelper.OLD_URI, null);
		for (ArtifactWrapper aw : artifacts) {
			if (aw.getUri().equals(oldArtifactUri)) {
				String newArtifactUri = marker.getAttribute(CapraNotificationHelper.NEW_URI, null);
				aw.setUri(newArtifactUri);
				// TODO maybe this attribute can be deleted now? Wait and see if
				// it is necessary in any of the handlers.
				aw.setPath(newArtifactUri);
				aw.setName(marker.getAttribute(CapraNotificationHelper.NEW_NAME, null));
				break;
			}
		}

		Resource resourceForArtifacts = resourceSet.createResource(EcoreUtil.getURI(awc));
		resourceForArtifacts.getContents().add((ArtifactWrapperContainer) awc);

		try {
			resourceForArtifacts.save(null);
			marker.delete();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}