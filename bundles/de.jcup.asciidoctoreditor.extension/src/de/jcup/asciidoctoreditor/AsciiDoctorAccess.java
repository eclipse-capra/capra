/*******************************************************************************
 * Copyright (c) 2023 Jan-Philipp Steghöfer
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
package de.jcup.asciidoctoreditor;

import org.eclipse.capra.ui.asciidoc.IAsciiDocApiAccess;

import de.jcup.asciidoctoreditor.outline.AsciiDoctorEditorTreeContentProvider;
import de.jcup.asciidoctoreditor.outline.ContentProviderAccess;
import de.jcup.asciidoctoreditor.outline.Item;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModelBuilder;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModelException;
import de.jcup.asciidoctoreditor.script.DefaultAsciiDoctorScriptModelBuilder;

/**
 * Provides access to the API of the AsciiDoctor Eclipse Plug-in, in particular
 * to use the integrated model builder and content provider to check if an item
 * exists in an AsciiDoc text.
 * 
 * @author Jan-Philipp Steghöfer
 *
 */
public class AsciiDoctorAccess implements IAsciiDocApiAccess {

	private static AsciiDoctorScriptModelBuilder modelBuilder = new DefaultAsciiDoctorScriptModelBuilder();
	private static AsciiDoctorEditorTreeContentProvider contentProvider = ContentProviderAccess.getContentProvider();

	@Override
	public Item getItemFromAsciiDocText(int offset, String asciiDocText) {
		AsciiDoctorScriptModel model = null;
		Item item = null;
		try {
			model = modelBuilder.build(asciiDocText);
		} catch (AsciiDoctorScriptModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (model != null) {
			// We rebuild first to empty cache
			contentProvider.rebuildTree(null);
			// Then we rebuild again with the current model
			contentProvider.rebuildTree(model);
			item = contentProvider.tryToFindByOffset(offset);
		}

		return item;
	}

}
