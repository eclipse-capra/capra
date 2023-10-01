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
package org.eclipse.capra.ui.asciidoc.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.asciidoc.AsciiDocArtifact;
import org.eclipse.capra.ui.asciidoc.IAsciiDocApiAccess;
import org.eclipse.core.runtime.FileLocator;

import de.jcup.asciidoctoreditor.outline.Item;

/**
 * Helper class to check if an AsciiDoc artifact exists.
 * 
 * @author Jan-Philipp Steghöfer
 */
public class AsciiDocArtifactExistenceChecker {

	private static final String ASCII_DOCTOR_API_ACCESS_ID = "org.eclipse.capra.ui.asciidoctor.apiaccess";
	private static final String ASCII_DOCTOR_API_ACCESS_CONFIG = "class";

	/**
	 * Checks if the given AsciiDoc artifact exists. This happens in two steps:
	 * first, it is determined whether the URI provided in the artifact points to a
	 * readable file. Then, it is checked whether the item described in the artifact
	 * exists in the file. Only if both conditions are true, does this method also
	 * return {@code true}.
	 * 
	 * @param artifact the AsciiDoc artifact to check
	 * @return {@code true} if the AsciiDoc artifact was found, {@code false}
	 *         otherwise
	 */
	public static boolean checkAsciiDocArtifactExistence(AsciiDocArtifact artifact) {
		boolean artifactExists = false;
		// 1. Load file
		try {
			URL asciiDocUrl = FileLocator.toFileURL(URI.create(artifact.getUri()).toURL());
			URLConnection urlConnection = asciiDocUrl.openConnection();
			InputStream inputStream = urlConnection.getInputStream();
			String asciiDocText = readFromInputStream(inputStream);

			// 2. Check if item exists
			Optional<IAsciiDocApiAccess> asciiDocApiAccessOpt = getAsciiDoctorAccess();
			if (asciiDocApiAccessOpt.isPresent()) {
				IAsciiDocApiAccess apiAccess = asciiDocApiAccessOpt.get();
				Item item = apiAccess.getItemFromAsciiDocText(0, asciiDocText);
				artifactExists = item != null;
			}

		} catch (IOException e) {
			// Deliberately do nothing. This happens when the file is not found or cannot be
			// accessed.
		}

		return artifactExists;
	}

	private static Optional<IAsciiDocApiAccess> getAsciiDoctorAccess() {
		try {
			Object extension = ExtensionPointHelper
					.getExtensions(ASCII_DOCTOR_API_ACCESS_ID, ASCII_DOCTOR_API_ACCESS_CONFIG).get(0);
			return Optional.of((IAsciiDocApiAccess) extension);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	private static String readFromInputStream(InputStream inputStream) throws IOException {
		StringBuilder resultStringBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				resultStringBuilder.append(line).append("\n");
			}
		}
		return resultStringBuilder.toString();
	}

}
