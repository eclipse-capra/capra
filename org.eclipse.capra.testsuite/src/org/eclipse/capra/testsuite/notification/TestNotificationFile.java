/*******************************************************************************
 *  Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *      Chalmers|Gothenburg University and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.capra.testsuite.notification;

import static org.eclipse.capra.testsuite.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsuite.TestHelper.createEmptyFileInProject;
import static org.eclipse.capra.testsuite.TestHelper.createSimpleProject;
import static org.eclipse.capra.testsuite.TestHelper.createTraceForCurrentSelectionOfType;
import static org.eclipse.capra.testsuite.TestHelper.projectExists;
import static org.eclipse.capra.testsuite.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsuite.TestHelper.thereIsATraceBetween;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage;
import org.eclipse.capra.testsuite.TestHelper;
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

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
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
		createSimpleProject("TestProject");
		assertTrue(projectExists("TestProject"));
		IFile testFile1 = createEmptyFileInProject("TestFile1", "TestProject");
		IFile testFile2 = createEmptyFileInProject("TestFile2", "TestProject");

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(testFile1);
		SelectionView.getOpenedView().dropToSelection(testFile2);
		assertFalse(thereIsATraceBetween(testFile1, testFile2));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(testFile1, testFile2));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkersSize = markers.length;

		// Delete file and wait a bit for the ResourceChangedListener to trigger
		testFile1.delete(true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize + 1, markers.length);
		currMarkersSize = markers.length;

		// Repeat the process for the second file
		testFile2.delete(true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize + 1, markers.length);
		currMarkersSize = markers.length;
		
		// Assert issue types
		assertEquals(markers[0].getAttribute("issueType"), "deleted");
		assertEquals(markers[1].getAttribute("issueType"), "deleted");

		// Undo for first file
		createEmptyFileInProject("TestFile1", "TestProject");
		TimeUnit.MILLISECONDS.sleep(300);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize - 1, markers.length);
		currMarkersSize = markers.length;

		// Undo for second file
		createEmptyFileInProject("TestFile2", "TestProject");
		TimeUnit.MILLISECONDS.sleep(300);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize - 1, markers.length);
	}

	/**
	 * Tests if a marker appears after moving a file that is referenced in the
	 * trace model.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testMoveFile() throws CoreException, IOException, InterruptedException {

		// Create a project and put files in
		createSimpleProject("TestProject1");
		assertTrue(projectExists("TestProject1"));
		IFile testFile1 = createEmptyFileInProject("TestFile1", "TestProject1");
		IFile testFile2 = createEmptyFileInProject("TestFile2", "TestProject1");

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(testFile1);
		SelectionView.getOpenedView().dropToSelection(testFile2);
		assertFalse(thereIsATraceBetween(testFile1, testFile2));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(testFile1, testFile2));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkersSize = markers.length;

		// Move first file to another project and wait a bit for the
		// ResourceChangedListener to trigger
		IProject project2 = createSimpleProject("TestProject2");
		assertTrue(projectExists("TestProject2"));
		IPath oldPath_file1 = testFile1.getFullPath();
		Path movePath_file1 = new Path(oldPath_file1.toString().replaceFirst("TestProject1", "TestProject2"));
		testFile1.move(movePath_file1, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize + 1, markers.length);
		currMarkersSize = markers.length;

		// Repeat the process for the second file
		IPath oldPath_file2 = testFile2.getFullPath();
		Path movePath_file2 = new Path(oldPath_file2.toString().replaceFirst("TestProject1", "TestProject2"));
		testFile2.move(movePath_file2, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize + 1, markers.length);
		currMarkersSize = markers.length;
		
		// Assert issue types
		assertEquals(markers[0].getAttribute("issueType"), "moved");
		assertEquals(markers[1].getAttribute("issueType"), "moved");

		// Undo for first file
		testFile1 = project2.getFile("TestFile1");
		testFile1.move(oldPath_file1, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize - 1, markers.length);
		currMarkersSize = markers.length;

		// Undo for second file
		testFile2 = project2.getFile("TestFile2");
		testFile2.move(oldPath_file2, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);
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
		IProject project = createSimpleProject("TestProject");
		assertTrue(projectExists("TestProject"));
		IFile testFile1 = createEmptyFileInProject("TestFile1", "TestProject");
		IFile testFile2 = createEmptyFileInProject("TestFile2", "TestProject");

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(testFile1);
		SelectionView.getOpenedView().dropToSelection(testFile2);
		assertFalse(thereIsATraceBetween(testFile1, testFile2));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(testFile1, testFile2));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkersSize = markers.length;

		// Rename file and wait a bit for the ResourceChangedListener to trigger
		IPath oldPath_file1 = testFile1.getFullPath();
		Path renamePath_file1 = new Path(oldPath_file1.toString().replaceFirst("TestFile1", "TestFile3"));
		testFile1.move(renamePath_file1, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize + 1, markers.length);
		currMarkersSize = markers.length;

		// Repeat the process for the second file
		IPath oldPath_file2 = testFile2.getFullPath();
		Path renamePath_file2 = new Path(oldPath_file2.toString().replaceFirst("TestFile2", "TestFile4"));
		testFile2.move(renamePath_file2, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize + 1, markers.length);
		currMarkersSize = markers.length;
		
		// Assert issue types
		assertEquals(markers[0].getAttribute("issueType"), "renamed");
		assertEquals(markers[1].getAttribute("issueType"), "renamed");

		// Undo for first file
		testFile2 = project.getFile("TestFile3");
		testFile2.move(oldPath_file1, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize - 1, markers.length);
		currMarkersSize = markers.length;

		// Undo for second file
		testFile2 = project.getFile("TestFile4");
		testFile2.move(oldPath_file2, true, new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);
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
		createSimpleProject("TestProject");
		assertTrue(projectExists("TestProject"));
		IFile testFile1 = createEmptyFileInProject("TestFile1", "TestProject");
		IFile testFile2 = createEmptyFileInProject("TestFile2", "TestProject");

		// Create a trace via the selection view
		assertTrue(SelectionView.getOpenedView().getSelection().isEmpty());
		SelectionView.getOpenedView().dropToSelection(testFile1);
		SelectionView.getOpenedView().dropToSelection(testFile2);
		assertFalse(thereIsATraceBetween(testFile1, testFile2));
		createTraceForCurrentSelectionOfType(GenericTraceMetaModelPackage.eINSTANCE.getRelatedTo());
		assertTrue(thereIsATraceBetween(testFile1, testFile2));

		// Get current number of markers
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IMarker[] markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		int currMarkersSize = markers.length;

		// Edit file and wait a bit for the ResourceChangedListener to trigger
		testFile1.appendContents(new ByteArrayInputStream("\nhello again 1!".getBytes()), true, true,
				new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);

		// Check if there are new markers
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize + 1, markers.length);
		currMarkersSize = markers.length;

		// Repeat the process for the second file
		testFile2.appendContents(new ByteArrayInputStream("\nhello again 2!".getBytes()), true, true,
				new NullProgressMonitor());
		TimeUnit.MILLISECONDS.sleep(300);
		markers = root.findMarkers(TestHelper.CAPRA_PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
		assertEquals(currMarkersSize + 1, markers.length);
		
		// Assert issue types
		assertEquals(markers[0].getAttribute("issueType"), "changed");
		assertEquals(markers[1].getAttribute("issueType"), "changed");
	}
}
