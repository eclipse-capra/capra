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
			String newMarkerUri = markerInfo.get(OLD_URI);

			IMarker[] existingMarkers = wrapperContainer.findMarkers(CAPRA_PROBLEM_MARKER_ID, false, 0);
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

	public static void deleteCapraMarker(String uri, String[] issues, IFile containingFile) {
		try {
			IMarker[] markers = containingFile.findMarkers(CAPRA_PROBLEM_MARKER_ID, false, 0);

			for (IMarker marker : markers) {
				String existingMarkerUri = marker.getAttribute(OLD_URI, null);
				String existingMarkerIssue = marker.getAttribute(ISSUE_TYPE, null);

				if (existingMarkerUri.equals(uri))
					if (issues == null)
						marker.delete();
					else
						for (String issue : issues)
							if (existingMarkerIssue.equals(issue))
								marker.delete();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
