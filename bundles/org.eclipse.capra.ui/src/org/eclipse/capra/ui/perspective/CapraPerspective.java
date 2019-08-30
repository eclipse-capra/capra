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
package org.eclipse.capra.ui.perspective;

import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Defines the Eclipse perspective for Capra, in particular the initial views
 * that will be shown when the perspective is chosen.
 */
public class CapraPerspective implements IPerspectiveFactory {

	private IPageLayout layout;

	@Override
	public void createInitialLayout(IPageLayout layout) {
		this.layout = layout;
		addViews();
	}

	private void addViews() {
		IFolderLayout bottom = layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.6f, layout.getEditorArea());
		bottom.addView(SelectionView.ID);

		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, layout.getEditorArea());
		topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);

		IFolderLayout topRight = layout.createFolder("topRight", IPageLayout.RIGHT, 0.75f, layout.getEditorArea());
		topRight.addView(IPageLayout.ID_OUTLINE);
	}

}
