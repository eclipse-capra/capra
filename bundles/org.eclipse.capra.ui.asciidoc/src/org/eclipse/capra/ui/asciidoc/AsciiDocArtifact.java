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

import java.util.Objects;

import de.jcup.asciidoctoreditor.outline.Item;

/**
 * 
 * Contains all necessary information to locate an {@link Item} in an Asciidoc
 * document as maintained by the Eclipse Asciidoctor-Editor.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class AsciiDocArtifact {

	private Item item;
	private String uri;

	/**
	 * Construct a new AsciiDoc artifact.
	 * 
	 * @param uri  the URI of the document that contains the item
	 * @param item the item this artifact refers to
	 */
	public AsciiDocArtifact(String uri, Item item) {
		super();
		this.uri = uri;
		this.item = item;
	}

	/**
	 * The item this artifact refers to.
	 * 
	 * @return the item this artifact refers to
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * The URI of the document that contains the item.
	 * 
	 * @return the URI of the document that contains the item
	 */
	public String getUri() {
		return uri;
	}

	@Override
	public int hashCode() {
		return Objects.hash(item, uri);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AsciiDocArtifact other = (AsciiDocArtifact) obj;
		return itemEquals(item, other.item) && Objects.equals(uri, other.uri);
	}

	private boolean itemEquals(Item first, Item second) {
		if (first == second) {
			return true;
		} else if ((first == null && second != null) || (first != null && second == null)) {
			return false;
		}
		if (first.getClass() != second.getClass())
			return false;
		return first.getEndOffset() == second.getEndOffset()
				&& Objects.equals(first.getFilePathOrNull(), second.getFilePathOrNull())
				&& Objects.equals(first.getFullString(), second.getFullString())
				&& Objects.equals(first.getId(), second.getId()) && first.getLength() == second.getLength()
				&& Objects.equals(first.getName(), second.getName()) && first.getOffset() == second.getOffset()
				&& Objects.equals(first.getPrefix(), second.getPrefix()) && first.getItemType() == second.getItemType();
	}

}