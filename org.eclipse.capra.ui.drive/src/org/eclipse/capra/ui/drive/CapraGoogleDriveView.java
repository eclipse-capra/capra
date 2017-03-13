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
package org.eclipse.capra.ui.drive;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.capra.ui.office.views.OfficeView;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.IOUtils;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

/**
 * Provides a Capra perspective view for displaying the contents of Google
 * sheets from Google Drive.
 * 
 * @author Dusan Kalanj
 *
 */
public class CapraGoogleDriveView extends ViewPart {

	/**
	 * The actual view that contains the contents of the documents.
	 */
	private TableViewer viewer;

	/**
	 * Contains the File objects that are listed in the view.
	 */
	private List<File> selection = new ArrayList<File>();

	/**
	 * Application name for authentication purposes.
	 */
	private static final String APPLICATION_NAME = "CapraDrive";

	/**
	 * Directory to store user credentials for this application.
	 */
	private static final java.io.File DATA_STORE_DIR_DRIVE = new java.io.File(System.getProperty("user.home"),
			".capra-drive-credentials" + java.io.File.separator + "drive.googleapis.capra-drive");

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static FileDataStoreFactory DATA_STORE_FACTORY_DRIVE;
	private static HttpTransport HTTP_TRANSPORT;
	private static Drive driveService;

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY_DRIVE = new FileDataStoreFactory(DATA_STORE_DIR_DRIVE);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private GoogleClientSecrets getClientSecrets() {
		GoogleClientSecrets googleClientSecrets = null;
		InputStream in = CapraGoogleDriveView.class.getResourceAsStream("/client_secret.json");
		try {
			googleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return googleClientSecrets;
	}

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	private Credential authorizeDrive() throws IOException {
		// Load client secrets.
		GoogleClientSecrets clientSecrets = getClientSecrets();

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, Arrays.asList(DriveScopes.DRIVE)).setDataStoreFactory(DATA_STORE_FACTORY_DRIVE)
						.setAccessType("offline").build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		// Credentials saved to " + DATA_STORE_DIR_DRIVE.getAbsolutePath()

		return credential;
	}

	/**
	 * Build and return an authorized Drive client service.
	 * 
	 * @return an authorized Drive client service
	 * @throws IOException
	 */
	private Drive getAuthorizedDriveService() throws IOException {
		Credential credential = authorizeDrive();
		return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	}

	/**
	 * Fills the Capra Drive view with Google sheet files from the Drive
	 * 
	 * @param driveService
	 *            authorized Drive client service
	 */
	private void fillSelection() {
		try {
			driveService = getAuthorizedDriveService();
			List<File> files = driveService.files().list().setFields("nextPageToken, files(id, name, mimeType)")
					.execute().getFiles();
			if (files != null && files.size() > 0)
				for (File file : files)
					if (file.getMimeType().equals("application/vnd.google-apps.spreadsheet"))
						selection.add(file);
			viewer.refresh();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The content provider class used by the view.
	 */
	class ViewContentProvider implements IStructuredContentProvider {

		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object parent) {
			return selection.toArray();
		}
	}

	/**
	 * The label provider class used by the view.
	 */
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		ImageDescriptor imgDesc = AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.capra.ui.drive",
				"icons/excel.png");
		Image image = imgDesc.createImage();

		@Override
		public String getText(Object obj) {
			if (obj instanceof File)
				return ((File) obj).getName();
			else
				return obj.toString();
		};

		@Override
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		@Override
		public Image getImage(Object obj) {
			return image;
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		viewer = new TableViewer(parent);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(getViewSite());

		getSite().setSelectionProvider(viewer);

		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) ((DoubleClickEvent) event).getSelection();

				if (selection.getFirstElement() instanceof String)
					fillSelection();
				else {
					File file = (File) selection.getFirstElement();
					String spreadSheetId = file.getId();
					if (!spreadSheetId.isEmpty())
						displaySheetInOfficeView(spreadSheetId);
				}
			}
		});

		viewer.add("Double click to fetch sheets from Drive.");
		hookContextMenu();
	}

	/**
	 * Getter for the authorized drive service object.
	 * 
	 * @return authorized drive service
	 */
	public static Drive getDriveService() {
		return driveService;
	}

	/**
	 * Displays the sheet with the provided ID in the Capra Office view.
	 * 
	 * @param spreadSheetId
	 *            the ID of the sheet
	 */
	public static void displaySheetInOfficeView(String spreadSheetId) {
		try {
			java.io.File tempFile = java.io.File.createTempFile("tmpFile", ".xlsx");
			InputStream in = driveService.files()
					.export(spreadSheetId, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
					.executeAsInputStream();
			IOUtils.copy(in, new FileOutputStream(tempFile));
			OfficeView.getOpenedView().parseExcelDocument(tempFile, spreadSheetId, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
