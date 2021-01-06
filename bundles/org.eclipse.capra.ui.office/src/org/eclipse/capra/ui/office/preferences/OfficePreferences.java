/*******************************************************************************
 * Copyright (c) 2016, 2019 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *  
 * SPDX-License-Identifier: EPL-2.0
 *  
 * Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *      Chalmers | University of Gothenburg - additional features, updated API
 *******************************************************************************/

package org.eclipse.capra.ui.office.preferences;

import org.eclipse.capra.ui.office.Activator;
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
	public static final String CHAR_COUNT = "org.eclipse.capra.ui.office.preferences.charCount";
	public static final String EXCEL_COLUMN_RADIO_CHOICE = "org.eclipse.capra.ui.office.preferences.excelColumnRadioChoice";
	public static final String EXCEL_CUSTOM_COLUMN = "org.eclipse.capra.ui.office.preferences.excelCustomColumn";
	public static final String EXCEL_COLUMN_VALUE = "org.eclipse.capra.ui.office.preferences.excelColumnValue";
	public static final String WORD_FIELD_NAME = "org.eclipse.capra.ui.office.preferences.wordFieldId";

	/**
	 * Default preference values
	 */
	public static final String CHAR_COUNT_DEFAULT = "80";
	public static final boolean EXCEL_COLUMN_RADIO_ID_IS_LINE_NUMBER = true;
	public static final String EXCEL_CUSTOM_COLUMN_DEFAULT = "A";
	public static final String EXCEL_COLUMN_VALUE_DEFAULT = "0";
	public static final String WORD_FIELD_NAME_DEFAULT = "REQ";

	/**
	 * Description of controls
	 */
	private static final String CHAR_COUNT_DESC = "Number of characters that are shown per line in the Office view:";
	private static final String EXCEL_COLUMN_RADIO_CHOICE_DESC = "Unique identifier in spreadsheets:";
	private static final String EXCEL_COLUMN_IS_LINE_NUMBER_OPTION_DESC = "Use the line number as the ID";
	private static final String EXCEL_COLUMN_IS_CUSTOM_OPTION_DESC = "Use this column as the ID: ";
	private static final String EXCEL_COLUMN_IS_CUSTOM_OPTION_HINT = "(e.g. \"A\", \"BC\"...)";
	private static final String WORD_FIELD_NAME_DESC = "Name of Word field to display:";

	private static final int FIXED_TEXT_FIELD_WIDTH = 35;

	/**
	 * Controls
	 */
	private Text charCount;
	private Button excelRowIdIsRowNumber;
	private Button excelRowIdIsCustomColumn;
	private Text excelCustomIdColumnName;
	private Text wordFieldName;

	/**
	 * Creates a swt widget that takes the numColumns of grid space inside the
	 * parent.
	 * 
	 * @param parent     the element that will hold the widget
	 * @param numColumns the number of columns that the widget will take inside the
	 *                   grid of the parent.
	 * @return the created widget
	 */
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
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	/**
	 * Saves the input values on click-apply or click-ok
	 */
	private void storeValues() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		boolean idIsRowNumber;
		String idColumn;
		if (excelRowIdIsRowNumber.getSelection()) {
			idColumn = EXCEL_COLUMN_VALUE_DEFAULT;
			excelCustomIdColumnName.setText("");
			idIsRowNumber = true;
		} else {
			if (excelCustomIdColumnName.getText().isEmpty()) {
				excelCustomIdColumnName.setText(EXCEL_CUSTOM_COLUMN_DEFAULT);
			}
			idColumn = excelCustomIdColumnName.getText();
			idIsRowNumber = false;
		}

		store.setValue(CHAR_COUNT, charCount.getText());
		store.setValue(EXCEL_COLUMN_RADIO_CHOICE, idIsRowNumber);
		store.setValue(EXCEL_CUSTOM_COLUMN, excelCustomIdColumnName.getText());
		store.setValue(EXCEL_COLUMN_VALUE, idColumn);
		store.setValue(WORD_FIELD_NAME, wordFieldName.getText());
	}

	/**
	 * Fills the text fields and radio buttons with stored values when the
	 * preference page is opened.
	 */
	private void initializeValues() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		boolean idIsRowNumber = store.getBoolean(EXCEL_COLUMN_RADIO_CHOICE);
		if (idIsRowNumber) {
			excelRowIdIsRowNumber.setSelection(true);
			excelCustomIdColumnName.setText("");
			excelCustomIdColumnName.setEnabled(false);
		} else {
			excelRowIdIsCustomColumn.setSelection(true);
			excelCustomIdColumnName.setText(store.getString(EXCEL_CUSTOM_COLUMN));
		}

		charCount.setText(store.getString(CHAR_COUNT));
		wordFieldName.setText(store.getString(WORD_FIELD_NAME));
	}

	/**
	 * Sets the default values if the user clicks on restore defaults option.
	 */
	private void initializeDefaults() {
		excelRowIdIsRowNumber.setSelection(EXCEL_COLUMN_RADIO_ID_IS_LINE_NUMBER);
		excelRowIdIsCustomColumn.setSelection(!EXCEL_COLUMN_RADIO_ID_IS_LINE_NUMBER);

		if (excelRowIdIsRowNumber.getSelection()) {
			excelCustomIdColumnName.setText("");
			excelCustomIdColumnName.setEnabled(false);
		}
		charCount.setText(CHAR_COUNT_DEFAULT);

		wordFieldName.setText(WORD_FIELD_NAME_DEFAULT);
	}

	@Override
	protected Control createContents(Composite parent) {

		Composite compositeCharCount = createComposite(parent, 2);
		createLabel(compositeCharCount, CHAR_COUNT_DESC, 1);
		charCount = createTextField(compositeCharCount, 1, FIXED_TEXT_FIELD_WIDTH);

		Composite compositeExcelIdColumn = createComposite(parent, 2);
		createLabel(compositeExcelIdColumn, EXCEL_COLUMN_RADIO_CHOICE_DESC, 2);

		Composite compositeRadioButtons = createComposite(compositeExcelIdColumn, 3);
		excelRowIdIsRowNumber = createRadioButton(compositeRadioButtons, EXCEL_COLUMN_IS_LINE_NUMBER_OPTION_DESC, 3);
		excelRowIdIsCustomColumn = createRadioButton(compositeRadioButtons, EXCEL_COLUMN_IS_CUSTOM_OPTION_DESC, 1);
		excelCustomIdColumnName = createTextField(compositeRadioButtons, 1, FIXED_TEXT_FIELD_WIDTH);
		createLabel(compositeRadioButtons, EXCEL_COLUMN_IS_CUSTOM_OPTION_HINT, 1);

		excelRowIdIsRowNumber.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				excelCustomIdColumnName.setText("");
				excelCustomIdColumnName.setEnabled(false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// We do not need to respond to this event.
			}
		});

		excelRowIdIsCustomColumn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				excelCustomIdColumnName.setEnabled(true);
				excelCustomIdColumnName.setText(EXCEL_CUSTOM_COLUMN_DEFAULT);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// We do not need to respond to this event.
			}
		});

		Composite compositeWordRequirementFieldName = createComposite(parent, 2);
		createLabel(compositeWordRequirementFieldName, WORD_FIELD_NAME_DESC, 1);
		wordFieldName = createTextField(compositeWordRequirementFieldName, 1, FIXED_TEXT_FIELD_WIDTH);

		initializeValues();

		return new Composite(parent, SWT.NULL);
	}

	/**
	 * Creates a label widget that takes the width of numOfColumns grid cells inside
	 * a parent.
	 * 
	 * @param parent       the element that will hold the label.
	 * @param text         the text of the label.
	 * @param numOfColumns the width (in grid cells) of the label.
	 * @return newly created Label object
	 */
	private Label createLabel(Composite parent, String text, int numOfColumns) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText(text);
		GridData data = new GridData();
		data.horizontalSpan = numOfColumns;
		data.horizontalAlignment = GridData.FILL;
		label.setLayoutData(data);
		return label;
	}

	/**
	 * Creates a Text widget that takes the width of numOfColumns grid cells inside
	 * a parent.
	 * 
	 * @param parent       the element that will hold the label.
	 * @param numOfColumns the width (in grid cells) of the widget.
	 * @param minimumWidth the minimum width of the Text widget.
	 * @return the newly created Text widget.
	 */
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

	/**
	 * Creates a Button widget that takes the width of numOfColumns grid cells
	 * inside a parent.
	 * 
	 * @param parent       the element that will hold the label.
	 * @param label        the text of the widget.
	 * @param numOfColumns the width (in grid cells) of the widget.
	 * @return the newly created Button widget.
	 */
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
