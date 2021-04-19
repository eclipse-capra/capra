package org.eclipse.capra.core.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * Core package class that is used for giving bundles access to Eclipse Capra
 * User Preferences.
 * 
 * @author Mihaela Grubii
 *
 */
public class CapraPreferences {
	public static final String CAPRA_PREFERENCE_PAGE_ID = "org.eclipse.capra.ui";
	public static final String CAPRA_PERSISTENCE_PROJECT_NAME = "org.eclipse.capra.persistence.projectName";

	/**
	 * Provides access to the preferences of Eclipse Capra.
	 * 
	 * @return the preferences of Eclipse Capra
	 */
	public static IEclipsePreferences getPreferences() {
		return InstanceScope.INSTANCE.getNode(CAPRA_PREFERENCE_PAGE_ID);
	}

}
