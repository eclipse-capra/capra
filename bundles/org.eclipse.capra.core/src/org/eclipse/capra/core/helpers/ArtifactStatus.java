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
package org.eclipse.capra.core.helpers;

/**
 * Holds information about the status of an artifact, i.e., if it can be found,
 * if it has been removed, or if it has been renamed. If it has been renamed,
 * also provides the old and the new name.
 * 
 * @author Jan-Philipp Steghöfer
 */
public class ArtifactStatus {

	/**
	 * Signifies the status of an artifact, i.e., if it can be found, if it has been
	 * removed, or if it has been renamed.
	 */
	public enum Status {
		/**
		 * Indicates that an artifact can be found and accessed.
		 */
		NORMAL,
		/**
		 * Indicates that the artifact has been removed and can no longer be located by
		 * Capra.
		 */
		REMOVED,
		/**
		 * Indicates that the artifact can still be located, but its name has changed.
		 */
		RENAMED,
		/**
		 * Indicates that the status of the artifact is currently unknown. This can
		 * occur, e.g., when the status has not been checked yet or when an error
		 * occurred.
		 */
		UNKNOWN
	}

	private String oldName;

	private String newName;

	private Status status;

	public String getOldName() {
		return oldName;
	}

	public String getNewName() {
		return newName;
	}

	public Status getStatus() {
		return status;
	}

	public ArtifactStatus(Status status) {
		this(status, null, null);
	}

	public ArtifactStatus(Status status, String oldName, String newName) {
		super();
		this.status = status;
		this.oldName = oldName;
		this.newName = newName;
	}

}
