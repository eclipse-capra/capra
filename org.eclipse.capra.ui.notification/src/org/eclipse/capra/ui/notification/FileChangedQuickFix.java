package org.eclipse.capra.ui.notification;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;

public class FileChangedQuickFix implements IMarkerResolution {

	private String label;

	public FileChangedQuickFix (String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {
		try {
			marker.delete();
		} catch (CoreException e) {

			e.printStackTrace();
		}
	}

}
