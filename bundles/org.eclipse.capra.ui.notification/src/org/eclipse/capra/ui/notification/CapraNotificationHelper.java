/*******************************************************************************
 *  Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *      Chalmers|Gothenburg University and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.ui.notification;

import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Contains methods that support the Capra notification (marker) solution.
 * 
 * @author Dusan Kalanj
 */
public class CapraNotificationHelper {

	/**
	 * ID of Capra custom marker for reporting a generic problem.
	 */
	public static final String CAPRA_PROBLEM_MARKER_ID = "org.eclipse.capra.ui.notification.capraProblemMarker";

	/**
	 * Custom enum that describes possible changes made to a traced element.
	 */
	public enum IssueType {
		RENAMED("renamed"), MOVED("moved"), DELETED("deleted"), CHANGED("changed"), ADDED("added");

		private final String value;

		private IssueType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	/**
	 * Job ID for the capra notification solution.
	 */
	public static final String NOTIFICATION_JOB = "CapraNotificationJob";

	/**
	 * Key to be used to specify IssueType value in markerInfo HashMap
	 */
	public static final String ISSUE_TYPE = "issueType";

	/**
	 * Key to be used to specify oldArtifactUri value in markerInfo HashMap
	 */
	public static final String OLD_URI = "oldArtifactUri";

	/**
	 * Key to be used to specify newArtifactUri value in markerInfo HashMap
	 */
	public static final String NEW_URI = "newArtifactUri";

	/**
	 * Key to be used to specify newArtifactName value in markerInfo HashMap
	 */
	public static final String NEW_NAME = "newArtifactName";

	/**
	 * Key to be used to specify message value in markerInfo HashMap
	 */
	public static final String MESSAGE = "message";

	// TODO necessary to specify all the fields that have to be filled out in
	// order for the method to work! Maybe make a custom exception for when
	// something is not filled out?
	/**
	 * Creates a Capra marker from the provided information about the artifact
	 * and the change that occurred.
	 * 
	 * @param markerInfo
	 *            contains attributes that are to be assigned to the created
	 *            marker
	 * @param container
	 *            file that the created marker will be attached to
	 */
	public static void createCapraMarker(HashMap<String, String> markerInfo, IFile container) {

		try {
			String newMarkerIssue = markerInfo.get(ISSUE_TYPE);
			String newMarkerUri = markerInfo.get(OLD_URI);

			IMarker[] existingMarkers = container.findMarkers(CAPRA_PROBLEM_MARKER_ID, false, 0);
			for (IMarker existingMarker : existingMarkers) {
				String existingMarkerIssue = existingMarker.getAttribute(ISSUE_TYPE, null);
				String existingMarkerUri = existingMarker.getAttribute(OLD_URI, null);

				if (existingMarkerUri.equals(newMarkerUri) && existingMarkerIssue.equals(newMarkerIssue))
					existingMarker.delete();

				// The code bellow deletes the marker that signifies a delete
				// operation in case the new marker signifies a rename/move
				// operation. The only thing that doesn't work with this
				// solution is when a user renames/moves a file, renames/moves
				// it back, and then deletes it. Markers will appear correctly
				// for the first operation, but they will also stay there, if
				// the user then deletes the object (there will be two markers,
				// rename and delete). This problem disappears if automatic
				// marker removal is implemented (already done for EMF).
				if (existingMarkerUri.equals(newMarkerUri) && existingMarkerIssue.equals(IssueType.DELETED.getValue())
						&& newMarkerIssue.matches(IssueType.RENAMED.getValue() + "|" + IssueType.MOVED.getValue()))
					existingMarker.delete();
				
				if (existingMarkerUri.equals(newMarkerUri) && newMarkerIssue.equals(IssueType.ADDED))
					existingMarker.delete();
			}
			
			String message = markerInfo.get(MESSAGE);
			if (message == null || message.isEmpty())
				return;

			IMarker marker = container.createMarker(CAPRA_PROBLEM_MARKER_ID);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			marker.setAttribute(IMarker.MESSAGE, message);
			markerInfo.remove(MESSAGE);

			for (Entry<String, String> entry : markerInfo.entrySet())
				marker.setAttribute(entry.getKey(), entry.getValue());

		} catch (CoreException e) {
			if (container.exists())
				e.printStackTrace();
		}
	}

	/**
	 * Deletes an existing marker.
	 * 
	 * @param uri
	 *            the uri of the artifact/element that the marker points to
	 * @param issues
	 *            an array of issues - only markers that describe the provided
	 *            issues will be deleted. If null is provided, all will be
	 *            deleted.
	 * @param containingFile
	 *            the file that contains the marker to be deleted
	 */
	public static void deleteCapraMarker(String uri, IssueType[] issues, IFile containingFile) {
		try {
			IMarker[] markers = containingFile.findMarkers(CAPRA_PROBLEM_MARKER_ID, true, 0);

			for (IMarker marker : markers) {
				String existingMarkerUri = marker.getAttribute(OLD_URI, null);
				String existingMarkerIssue = marker.getAttribute(ISSUE_TYPE, null);

				if (existingMarkerUri.equals(uri))
					if (issues == null)
						marker.delete();
					else
						for (IssueType issue : issues)
							if (existingMarkerIssue.equals(issue.getValue()))
								marker.delete();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts the platform URI into a file URI. Returns the same object if it
	 * already is a file URI.
	 * 
	 * @param uri
	 *            the URI to be converted
	 * @return the same URI in a file scheme
	 */
	public static URI convertToFileUri(URI uri) {

		if (!uri.isPlatformResource())
			return uri;
		else {
			String platformUri = uri.toPlatformString(true);
			IPath filePath = ResourcesPlugin.getWorkspace().getRoot().findMember(platformUri).getRawLocation();
			URI fileUri = URI.createFileURI(filePath.toString());

			String fragment = uri.fragment();
			if (fragment != null)
				fileUri = fileUri.appendFragment(fragment);

			return fileUri;
		}
	}

	/**
	 * Gets the file-scheme URI of an EObject.
	 * 
	 * @param eObject
	 *            the eObject in question
	 * @return the URI of the eObject with a file scheme
	 */
	public static URI getFileUri(EObject eObject) {
		return convertToFileUri(EcoreUtil.getURI(eObject));
	}

	/**
	 * Gets the file-scheme URI of a resource.
	 * 
	 * @param resource
	 *            the resource in question
	 * @return the URI of the resource with a file scheme
	 */
	public static URI getFileUri(Resource resource) {
		return convertToFileUri(resource.getURI());
	}
}
