/*******************************************************************************
 * Copyright (c) 2022-2024 University of Gothenburg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *  
 * SPDX-License-Identifier: EPL-2.0
 *  
 * Contributors:
 *      University of Gothenburg - initial API and implementation
 *      Jan-Philipp Steghöfer - additional features
 *******************************************************************************/
package org.eclipse.capra.handler.asciidoc;

import java.lang.reflect.Field;
import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.IArtifactMetaModelAdapter;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.helpers.ArtifactStatus;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.asciidoc.AsciiDocArtifact;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.emf.ecore.EObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jcup.asciidoctoreditor.outline.Item;
import de.jcup.asciidoctoreditor.outline.ItemType;

/**
 * Handles AsciiDoc files via their {@link AsciiDocArtifact} representation
 * which is based on AsciiDoctor-Editor. This handler can distinguish two
 * different kinds of AsciiDoc artifacts: once that are identified by their
 * offset in the file or by an ID. The distinction is made using the
 * <code>ItemType</code>: if that type is set to <b>inline anchor</b>, the
 * artifact is identified by its ID.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class AsciiDocHandler extends AbstractArtifactHandler<AsciiDocArtifact> {

	private static final Logger LOG = LoggerFactory.getLogger(AsciiDocHandler.class);

	@Override
	public EObject createWrapper(AsciiDocArtifact artifact, EObject artifactModel) {
		IArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactMetaModelAdapter().orElseThrow();
		String internalResolver = "";
		if (artifact.getItem().getItemType() == ItemType.INLINE_ANCHOR) {
			internalResolver = artifact.getItem().getId();
		} else {
			internalResolver = String.valueOf(artifact.getItem().getOffset());
		}
		return adapter.createArtifact(artifactModel, this.getClass().getName(), artifact.getUri(), internalResolver,
				artifact.getItem().getName());
	}

	@Override
	public AsciiDocArtifact resolveWrapper(EObject wrapper) {
		IArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactMetaModelAdapter().orElseThrow();
		String uri = adapter.getArtifactUri(wrapper);
		Item item = new Item();

		// Unfortunately, we have to force this via reflection since the Item class does
		// not expose setters.
		try {
			String internalResolver = adapter.getArtifactInternalResolver(wrapper);

			if (isNumeric(internalResolver)) {
				// If the internal resolver is a number, we have referenced an item by its
				// offset.
				int offset = Integer.parseInt(adapter.getArtifactInternalResolver(wrapper));
				Field offsetField = item.getClass().getDeclaredField("offset");
				offsetField.setAccessible(true);
				offsetField.set(item, offset);
			} else {
				// Otherwise, we haver revered an internal anchor. This means we need to set the
				// right type.
				Field itemType = item.getClass().getDeclaredField("type");
				itemType.setAccessible(true);
				itemType.set(item, ItemType.INLINE_ANCHOR);

				Field id = item.getClass().getDeclaredField("id");
				id.setAccessible(true);
				id.set(item, internalResolver);
			}

			Field nameField = item.getClass().getDeclaredField("name");
			nameField.setAccessible(true);
			nameField.set(item, adapter.getArtifactName(wrapper));
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			LOG.error("Could not access fields of item", e);
		}

		return new AsciiDocArtifact(uri, item);
	}

	@Override
	public boolean doesArtifactExist(EObject wrapper) {
		AsciiDocArtifact asciiDocArtifact = resolveWrapper(wrapper);
		return asciiDocArtifact != null && asciiDocArtifact.exists();
	}

	@Override
	public ArtifactStatus getArtifactStatus(EObject wrapper) {
		AsciiDocArtifact asciiDocArtifact = resolveWrapper(wrapper);
		return asciiDocArtifact.getStatus();
	}

	@Override
	public String getDisplayName(AsciiDocArtifact artifact) {
		return artifact.getItem().getName();
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Connection> getInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes,
			boolean reverseDirection) {
		// Deliberately do nothing.
		return null;
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		return false;
	}

	private static boolean isNumeric(String input) {
		if (input == null) {
			return false;
		}
		try {
			double d = Double.parseDouble(input);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
