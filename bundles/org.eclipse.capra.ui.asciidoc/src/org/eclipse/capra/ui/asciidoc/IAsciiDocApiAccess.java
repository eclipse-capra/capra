/*******************************************************************************
 * Copyright (c) 2023-2024 Jan-Philipp Steghöfer
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *  
 * SPDX-License-Identifier: EPL-2.0
 *  
 * Contributors:
 *      Jan-Philipp Steghöfer - initial API and implementation
 *******************************************************************************/
package org.eclipse.capra.ui.asciidoc;

import de.jcup.asciidoctoreditor.outline.Item;

/**
 * Defines methods for the direct access to the AsciiDoctor Eclipse plug-in API.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public interface IAsciiDocApiAccess {

	/**
	 * Retrieves an {@link Item} with the provided offset from the provided AsciiDoc
	 * text. If there is no item at the offset, returns {@code null}.
	 * 
	 * @param offset       where to look for an item
	 * @param asciiDocText the text in which to look for an item
	 * @return the {@link Item} at the offset, or {@code null}
	 */
	public Item getItemFromAsciiDocText(int offset, String asciiDocText);

	/**
	 * Retrieves an {@link Item} which represents an <b>inline anchor</b> with the
	 * provided ID from the provided AsciiDoc text. If there is no item with the ID,
	 * returns {@code null}.
	 * 
	 * @param ID           the unique identifier of an inline anchor item
	 * @param asciiDocText the text in which to look for an item
	 * @return the {@link Item} with the given ID, or {@code null}
	 */
	Item getItemById(String ID, String asciiDocText);

}
