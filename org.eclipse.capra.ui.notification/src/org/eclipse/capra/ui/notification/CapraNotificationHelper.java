package org.eclipse.capra.ui.notification;

import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

public class CapraNotificationHelper {

	/**
	 * ID of Capra custom marker for reporting a generic problem.
	 */
	public static final String CAPRA_PROBLEM_MARKER_ID = "org.eclipse.capra.ui.notification.capraProblemMarker";

	public enum IssueType {
		RENAMED("renamed"), MOVED("moved"), DELETED("deleted"), CHANGED("changed");

		private final String value;

		private IssueType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public static final String NOTIFICATION_JOB = "CapraNotificationJob";
	public static final String ISSUE_TYPE = "issueType";
	public static final String OLD_URI = "oldArtifactUri";
	public static final String NEW_URI = "newArtifactUri";
	public static final String NEW_NAME = "newArtifactName";
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
	 * @param wrapperContainer
	 *            file that contains the artifact model and to which the markers
	 *            will be attached to
	 */
	public static void createCapraMarker(HashMap<String, String> markerInfo, IFile wrapperContainer) {
		if (markerInfo.isEmpty())
			return;

		try {
			String newMarkerIssue = markerInfo.get(ISSUE_TYPE);
			String artifactUri = markerInfo.get(OLD_URI);

			IMarker[] markers = wrapperContainer.findMarkers(CAPRA_PROBLEM_MARKER_ID, false, 0);
			for (IMarker marker : markers) {
				String existingMarkerIssue = marker.getAttribute(ISSUE_TYPE, null);
				String existingMarkerUri = marker.getAttribute(OLD_URI, null);

				// TODO: this looks cumbersome, but it works. The only thing
				// that doesn't work with this solution is when a user renames
				// or moves a file, renames/moves it back, and then deletes it.
				// Markers will appear correctly for the first operation, but if
				// they are not resolved, the deletion markers won't appear.
				// This problem disappears if automatic marker removal is
				// implemented (as described above at the elementChanged
				// method).
				if (existingMarkerUri.contentEquals(artifactUri) && (existingMarkerIssue.equals(newMarkerIssue)
						|| (existingMarkerIssue.matches(IssueType.RENAMED.getValue() + "|" + IssueType.MOVED.getValue())
								&& newMarkerIssue.equals(IssueType.DELETED.getValue()))))
					return;
			}

			IMarker marker = wrapperContainer.createMarker(CAPRA_PROBLEM_MARKER_ID);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			marker.setAttribute(IMarker.MESSAGE, markerInfo.get(MESSAGE));
			markerInfo.remove(MESSAGE);

			for (Entry<String, String> entry : markerInfo.entrySet())
				marker.setAttribute(entry.getKey(), entry.getValue());

		} catch (CoreException e) {
			if (wrapperContainer.exists())
				e.printStackTrace();
		}
	}
}
