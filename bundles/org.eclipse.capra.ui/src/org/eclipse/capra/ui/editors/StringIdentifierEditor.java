package org.eclipse.capra.ui.editors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * Implementation of StringFieldEditor that overrides the doCheckState() method
 * and checks if the input matches a specific pattern. The implementation
 * restricts empty string input.
 * 
 * @author Mihaela Grubii
 *
 */
public class StringIdentifierEditor extends StringFieldEditor {
	private static final String ERROR_MESSAGE = "Given input is not valid!";

	private String matchingPattern = "";

	public StringIdentifierEditor(String name, String labelText, int width, Composite parent, String matchingPattern,
			boolean isEnabled) {
		super(name, labelText, width, VALIDATE_ON_KEY_STROKE, parent);
		setEmptyStringAllowed(false);
		setEnabled(isEnabled, parent);
		setErrorMessage(ERROR_MESSAGE);
		this.matchingPattern = matchingPattern;
	}

	/**
	 * Hook for string identifier validity status check.
	 * 
	 * @return <code>true</code> if the field value is matching the pattern that is
	 *         stored in MATCHING_PATTERN after the object of StringIdentifierEditor
	 *         type is being constructed, and <code>false</code> for
	 */
	@Override
	protected boolean doCheckState() {
		// textField;
		String textField = getStringValue();
		if (textField.equals(getPreferenceStore().getString(getPreferenceName()))) {
			return true;
		} else {
			Matcher matchedString = Pattern.compile(matchingPattern).matcher(textField);
			if (matchedString.find()) {
				return true;
			} else {
				return false;
			}

		}
	}

}
