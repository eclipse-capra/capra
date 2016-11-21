/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.capra.ui.office.preferences;

import org.eclipse.capra.ui.office.views.OfficeView;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Provides a preference page for Capra-Office, where a user can specify custom
 * settings for the Office feature.
 * 
 * Code adapted from:
 * http://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Fguide%2Fpreferences_prefs_implement.htm
 * 
 * @author Dusan Kalanj
 *
 */
public class OfficePreferences extends PreferencePage implements IWorkbenchPreferencePage {

	/**
	 * IDs of preferences
	 */
	public static final String CHAR_COUNT = "org.eclipse.capra.ui.office.preferences.charcount";
	public static final String EXCEL_COLUMN_RADIO_CHOICE = "org.eclipse.capra.ui.office.preferences.excelcolumnradiochoice";
	public static final String EXCEL_CUSTOM_COLUMN = "org.eclipse.capra.ui.office.preferences.excelcustomcolumn";
	public static final String EXCEL_COLUMN_VALUE = "org.eclipse.capra.ui.office.preferences.excelcolumnvalue";

	/**
	 * Default preference values
	 */
	public static final String CHAR_COUNT_DEFAULT = "30";
	public static final boolean EXCEL_COLUMN_RADIO_ID_IS_LINE_NUMBER = true;
	public static final String EXCEL_CUSTOM_COLUMN_DEFAULT = "A";
	public static final String EXCEL_COLUMN_VALUE_DEFAULT = "0";

	/**
	 * Description of controls
	 */
	private static final String CHAR_COUNT_DESC = "Number of characters that are shown per line in the Office view:";
	private static final String EXCEL_COLUMN_RADIO_CHOICE_DESC = "Setting the ID of Excel rows:";
	private static final String EXCEL_COLUMN_IS_LINE_NUMBER_OPTION_DESC = "Line number is used as ID";
	private static final String EXCEL_COLUMN_IS_CUSTOM_OPTION_DESC = "Custom ID column: ";
	private static final String EXCEL_COLUMN_IS_CUSTOM_OPTION_HINT = "(e.g. \"A\", \"BC\"...)";

	private static final int FIXED_TEXT_FIELD_WIDTH = 35;

	/**
	 * Controls
	 */
	private Button idIsRowNumberRadio;
	private Button idIsCustomRadio;
	private Text charCount;
	private Text customIdColumn;

	private Composite createComposite(Composite parent, int numColumns) {
		Composite composite = new Composite(parent, SWT.NULL);

		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(PreferenceActivator.getDefault().getPreferenceStore());
	}

	private void storeValues() {
		IPreferenceStore store = PreferenceActivator.getDefault().getPreferenceStore();

		boolean idIsRowNumber;
		String idColumn;
		if (idIsRowNumberRadio.getSelection()) {
			idColumn = EXCEL_COLUMN_VALUE_DEFAULT;
			customIdColumn.setText("");
			idIsRowNumber = true;
		} else {
			if (customIdColumn.getText().isEmpty())
				customIdColumn.setText(EXCEL_CUSTOM_COLUMN_DEFAULT);
			idColumn = customIdColumn.getText();
			idIsRowNumber = false;
		}

		store.setValue(CHAR_COUNT, charCount.getText());
		store.setValue(EXCEL_COLUMN_RADIO_CHOICE, idIsRowNumber);
		store.setValue(EXCEL_CUSTOM_COLUMN, customIdColumn.getText());
		store.setValue(EXCEL_COLUMN_VALUE, idColumn);
	}

	private void initializeValues() {
		IPreferenceStore store = PreferenceActivator.getDefault().getPreferenceStore();

		boolean idIsRowNumber = store.getBoolean(EXCEL_COLUMN_RADIO_CHOICE);
		if (idIsRowNumber) {
			idIsRowNumberRadio.setSelection(true);
			customIdColumn.setText("");
			customIdColumn.setEnabled(false);
		} else {
			idIsCustomRadio.setSelection(true);
			customIdColumn.setText(store.getString(EXCEL_CUSTOM_COLUMN));
		}

		charCount.setText(store.getString(CHAR_COUNT));
	}

	private void initializeDefaults() {
		idIsRowNumberRadio.setSelection(EXCEL_COLUMN_RADIO_ID_IS_LINE_NUMBER);
		idIsCustomRadio.setSelection(!EXCEL_COLUMN_RADIO_ID_IS_LINE_NUMBER);

		if (idIsRowNumberRadio.getSelection()) {
			customIdColumn.setText("");
			customIdColumn.setEnabled(false);
		}
		charCount.setText(CHAR_COUNT_DEFAULT);
	}

	@Override
	protected Control createContents(Composite parent) {

		Composite compositeCharCount = createComposite(parent, 2);
		createLabel(compositeCharCount, CHAR_COUNT_DESC, 1);
		charCount = createTextField(compositeCharCount, 1, FIXED_TEXT_FIELD_WIDTH);

		Composite compositeExcelIdColumn = createComposite(parent, 2);
		createLabel(compositeExcelIdColumn, EXCEL_COLUMN_RADIO_CHOICE_DESC, 2);

		Composite compositeRadioButtons = createComposite(compositeExcelIdColumn, 3);
		idIsRowNumberRadio = createRadioButton(compositeRadioButtons, EXCEL_COLUMN_IS_LINE_NUMBER_OPTION_DESC, 3);
		idIsCustomRadio = createRadioButton(compositeRadioButtons, EXCEL_COLUMN_IS_CUSTOM_OPTION_DESC, 1);
		customIdColumn = createTextField(compositeRadioButtons, 1, FIXED_TEXT_FIELD_WIDTH);
		createLabel(compositeRadioButtons, EXCEL_COLUMN_IS_CUSTOM_OPTION_HINT, 1);

		idIsRowNumberRadio.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				customIdColumn.setText("");
				customIdColumn.setEnabled(false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		idIsCustomRadio.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				customIdColumn.setEnabled(true);
				customIdColumn.setText(EXCEL_CUSTOM_COLUMN_DEFAULT);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		initializeValues();

		return new Composite(parent, SWT.NULL);
	}

	private Label createLabel(Composite parent, String text, int numOfColumns) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText(text);
		GridData data = new GridData();
		data.horizontalSpan = numOfColumns;
		data.horizontalAlignment = GridData.FILL;
		label.setLayoutData(data);
		return label;
	}

	private Text createTextField(Composite parent, int numOfColumns, int minimumWidth) {
		Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.verticalAlignment = GridData.CENTER;
		data.grabExcessVerticalSpace = false;
		data.horizontalSpan = numOfColumns;
		data.minimumWidth = minimumWidth;
		text.setLayoutData(data);
		return text;
	}

	private Button createRadioButton(Composite parent, String label, int numOfColumns) {
		Button button = new Button(parent, SWT.RADIO | SWT.LEFT);
		button.setText(label);
		GridData data = new GridData();
		data.horizontalSpan = numOfColumns;
		button.setLayoutData(data);
		return button;
	}

	@Override
	public void performDefaults() {
		super.performDefaults();
		initializeDefaults();
	}

	@Override
	public void performApply() {
		super.performApply();
		storeValues();
	}

	@Override
	public boolean performOk() {
		super.performOk();
		storeValues();
		OfficeView.getOpenedView().refreshView();
		return true;
	}
}
