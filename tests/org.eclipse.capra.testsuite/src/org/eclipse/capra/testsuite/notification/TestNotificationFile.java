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

package org.eclipse.capra.testsuite.notification;

import static org.eclipse.capra.testsupport.TestHelper.UI_REACTION_WAITING_TIME;
import static org.eclipse.capra.testsupport.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsupport.TestHelper.createEmptyFileInProject;
import static org.eclipse.capra.testsupport.TestHelper.createSimpleProject;
import static org.eclipse.capra.testsupport.TestHelper.createTraceForCurrentSelectionOfType;
import static org.eclipse.capra.testsupport.TestHelper.projectExists;
import static org.eclipse.capra.testsupport.TestHelper.purgeModels;
import static org.eclipse.capra.testsupport.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsupport.TestHelper.thereIsATraceBetween;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.testsupport.TestHelper;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Test;

/**
 * Contains tests to check the functioning of the Capra notification system when
 * deleting, renaming, moving or changing a file that is referenced in the trace
 * model.
 * 
 * @author Dusan Kalanj
 *
 */
public class TestNotificationFile {

	private static final String MARKER_ATTRIBUTE_ISSUE_TYPE = "issueType";

	private static final String TEST_PROJECT_NAME = "TestProject";
	private static final String TEST_PROJECT1_NAME = "TestProject1";
	private static final String TEST_PROJECT2_NAME = "TestProject2";

	private static final String TEST_FILE2_NAME = "TestFile2";
	private static final String TEST_FILE1_NAME = "TestFile1";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
		purgeModels();
	}

	/**
	 * Tests if a marker appears after deleting a file that is referenced in the
	 * trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testDeleteFile() throws CoreException, IOException, InterruptedException {

		// Create a project and put files in
		createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));
		IFile testFile1 = createEmptyFileInProject(TEST_FILE1_NAME, TEST_PROJECT_NAME);
		IFile testFile2 = createEmptyFileInProject(TEST_FILE2_NAME, TEST_PROJECT_NAME);

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(testFile1);
		SelectionView.getOpenedView().dropToSelection(testFile2);
		assertFalse(thereIsATraceBetween(testFile1, testFile2));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(testFile1, testFile2));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkersSize = markers.length;

		// Delete file and wait a bit for the ResourceChangedListener to trigger
		testFile1.delete(true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize + 1, markers.length);
		currMarkersSize = markers.length;

		// Repeat the process for the second file
		testFile2.delete(true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize + 1, markers.length);
		currMarkersSize = markers.length;

		// Assert issue types
		assertEquals(markers[0].getAttribute(MARKER_ATTRIBUTE_ISSUE_TYPE), "deleted");
		assertEquals(markers[1].getAttribute(MARKER_ATTRIBUTE_ISSUE_TYPE), "deleted");

		// Undo for first file
		createEmptyFileInProject(TEST_FILE1_NAME, TEST_PROJECT_NAME);
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize - 1, markers.length);
		currMarkersSize = markers.length;

		// Undo for second file
		createEmptyFileInProject(TEST_FILE2_NAME, TEST_PROJECT_NAME);
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize - 1, markers.length);
	}

	/**
	 * Tests if a marker appears after moving a file that is referenced in the trace
	 * model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testMoveFile() throws CoreException, IOException, InterruptedException {

		// Create a project and put files in
		createSimpleProject(TEST_PROJECT1_NAME);
		assertTrue(projectExists(TEST_PROJECT1_NAME));
		IFile testFile1 = createEmptyFileInProject(TEST_FILE1_NAME, TEST_PROJECT1_NAME);
		IFile testFile2 = createEmptyFileInProject(TEST_FILE2_NAME, TEST_PROJECT1_NAME);

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(testFile1);
		SelectionView.getOpenedView().dropToSelection(testFile2);
		assertFalse(thereIsATraceBetween(testFile1, testFile2));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(testFile1, testFile2));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkersSize = markers.length;

		// Move first file to another project and wait a bit for the
		// ResourceChangedListener to trigger
		IProject project2 = createSimpleProject(TEST_PROJECT2_NAME);
		assertTrue(projectExists(TEST_PROJECT2_NAME));
		IPath oldPath_file1 = testFile1.getFullPath();
		Path movePath_file1 = new Path(oldPath_file1.toString().replaceFirst(TEST_PROJECT1_NAME, TEST_PROJECT2_NAME));
		testFile1.move(movePath_file1, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize + 1, markers.length);
		currMarkersSize = markers.length;

		// Repeat the process for the second file
		IPath oldPath_file2 = testFile2.getFullPath();
		Path movePath_file2 = new Path(oldPath_file2.toString().replaceFirst(TEST_PROJECT1_NAME, TEST_PROJECT2_NAME));
		testFile2.move(movePath_file2, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize + 1, markers.length);
		currMarkersSize = markers.length;

		// Assert issue types
		assertEquals(markers[0].getAttribute(MARKER_ATTRIBUTE_ISSUE_TYPE), "moved");
		assertEquals(markers[1].getAttribute(MARKER_ATTRIBUTE_ISSUE_TYPE), "moved");

		// Undo for first file
		testFile1 = project2.getFile(TEST_FILE1_NAME);
		testFile1.move(oldPath_file1, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize - 1, markers.length);
		currMarkersSize = markers.length;

		// Undo for second file
		testFile2 = project2.getFile(TEST_FILE2_NAME);
		testFile2.move(oldPath_file2, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize - 1, markers.length);
	}

	/**
	 * Tests if a marker appears after renaming a file that is referenced in the
	 * trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testRenameFile() throws CoreException, IOException, InterruptedException {

		// Create a project and put files in
		IProject project = createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));
		IFile testFile1 = createEmptyFileInProject(TEST_FILE1_NAME, TEST_PROJECT_NAME);
		IFile testFile2 = createEmptyFileInProject(TEST_FILE2_NAME, TEST_PROJECT_NAME);

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(testFile1);
		SelectionView.getOpenedView().dropToSelection(testFile2);
		assertFalse(thereIsATraceBetween(testFile1, testFile2));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(testFile1, testFile2));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkersSize = markers.length;

		// Rename file and wait a bit for the ResourceChangedListener to trigger
		IPath oldPath_file1 = testFile1.getFullPath();
		Path renamePath_file1 = new Path(oldPath_file1.toString().replaceFirst(TEST_FILE1_NAME, "TestFile3"));
		testFile1.move(renamePath_file1, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize + 1, markers.length);
		currMarkersSize = markers.length;

		// Repeat the process for the second file
		IPath oldPath_file2 = testFile2.getFullPath();
		Path renamePath_file2 = new Path(oldPath_file2.toString().replaceFirst(TEST_FILE2_NAME, "TestFile4"));
		testFile2.move(renamePath_file2, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize + 1, markers.length);
		currMarkersSize = markers.length;

		// Assert issue types
		assertEquals(markers[0].getAttribute(MARKER_ATTRIBUTE_ISSUE_TYPE), "renamed");
		assertEquals(markers[1].getAttribute(MARKER_ATTRIBUTE_ISSUE_TYPE), "renamed");

		// Undo for first file
		testFile2 = project.getFile("TestFile3");
		testFile2.move(oldPath_file1, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize - 1, markers.length);
		currMarkersSize = markers.length;

		// Undo for second file
		testFile2 = project.getFile("TestFile4");
		testFile2.move(oldPath_file2, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize - 1, markers.length);
	}

	/**
	 * Tests if a marker appears after editing a file that is referenced in the
	 * trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testEditFile() throws CoreException, IOException, InterruptedException {

		// Create a project and put files in
		createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));
		IFile testFile1 = createEmptyFileInProject(TEST_FILE1_NAME, TEST_PROJECT_NAME);
		IFile testFile2 = createEmptyFileInProject(TEST_FILE2_NAME, TEST_PROJECT_NAME);

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(testFile1);
		SelectionView.getOpenedView().dropToSelection(testFile2);
		assertFalse(thereIsATraceBetween(testFile1, testFile2));
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(testFile1, testFile2));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkersSize = markers.length;

		// Edit file and wait a bit for the ResourceChangedListener to trigger
		testFile1.appendContents(new ByteArrayInputStream("\nhello again 1!".getBytes()), true, true,
				new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize + 1, markers.length);
		currMarkersSize = markers.length;

		// Repeat the process for the second file
		testFile2.appendContents(new ByteArrayInputStream("\nhello again 2!".getBytes()), true, true,
				new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(UI_REACTION_WAITING_TIME);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize + 1, markers.length);

		// Assert issue types
		assertEquals(markers[0].getAttribute(MARKER_ATTRIBUTE_ISSUE_TYPE), "changed");
		assertEquals(markers[1].getAttribute(MARKER_ATTRIBUTE_ISSUE_TYPE), "changed");
	}
}
