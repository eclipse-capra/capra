package org.eclipse.capra.ui.plantuml;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class ShowAnalysisFeatures extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (isShowAnalysis())
			setAnalysis(false);
		else
			setAnalysis(true);

		return null;
	}

	public static boolean isShowAnalysis() {
		Preferences showAnalysis = getPreference();

		return showAnalysis.get("option", "dontShow").equals("Show");
	}

	private static Preferences getPreference() {
		Preferences preferences = InstanceScope.INSTANCE.getNode("org.eclipse.capra.ui.plantuml.showAnalysisFeatures");
		Preferences transitivity = preferences.node("Show");
		return transitivity;
	}

	public static void setAnalysis(boolean value) {
		Preferences transitivity = getPreference();

		transitivity.put("option", value ? "Show" : "dontShow");

		try {
			// forces the application to save the preferences
			transitivity.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

}
