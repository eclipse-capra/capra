/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *   Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.handler.cdt;

import static org.junit.Assert.*;

import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CDTAnnotateTest {

	private static IProject project;
	private CDTHandler handler = new CDTHandler();
	private EObject artifactModel = TestUtil.setupModel();

	@Before
	public void setupTestProject() throws CoreException, BuildException {
		project = TestUtil.setupTestProject("cdt");
	}

	@After
	public void deleteTestProject() throws CoreException {
		TestUtil.deleteTestProject(project);
	}

	@Test
	public void shouldAnnotateMethod() throws Exception {
		String source = "" +
				"int foo() { return 0; }" + System.lineSeparator() +
				"";

		ITranslationUnit tu = TestUtil.createTranslationUnit(project, "bar.c", source);

		// Annotate method foo()
		EObject wrapper = TestUtil.createWrapper(artifactModel, "=cdt/{bar.c[foo#74", "foo");
		handler.annotateArtifact(wrapper, "annotation");
		String actual = tu.getBuffer().getContents();

		String expected = "" +
				"/**" + System.lineSeparator() +
				" * @req annotation" + System.lineSeparator() +
				" */" + System.lineSeparator() +
				"int foo() { return 0; }" + System.lineSeparator() +
				"";

		assertEquals(expected, actual);
	}

	@Test(expected = AssertionError.class)
	public void shouldReplaceAnnotation() throws Exception {
		String source = "" +
			"/**" + System.lineSeparator() +
			" * @req annotation1" + System.lineSeparator() +
			" */" + System.lineSeparator() +
			"int foo() { return 0; }" + System.lineSeparator() +
			"";

		ITranslationUnit tu = TestUtil.createTranslationUnit(project, "bar2.c", source);

		// Annotate method foo()
		EObject wrapper = TestUtil.createWrapper(artifactModel, "=cdt/{bar2.c[foo#74", "foo");
		handler.annotateArtifact(wrapper, "annotation2");
		String actual = tu.getBuffer().getContents();

		String expected = "" +
				"/**" + System.lineSeparator() +
				" * @req annotation2" + System.lineSeparator() +
				" */" + System.lineSeparator() +
				"int foo() { return 0; }" + System.lineSeparator() +
				"";

		// Will fail, doesn't replace annotation
		assertEquals(expected, actual);
	}

	@Test(expected = AssertionError.class)
	public void shouldPreserveComments() throws Exception {
		String source = "" +
				"/**" + System.lineSeparator() +
				" * Comment" + System.lineSeparator() +
				" */" + System.lineSeparator() +
				"int foo() { return 0; }" + System.lineSeparator() +
				"";

		ITranslationUnit tu = TestUtil.createTranslationUnit(project, "bar3.c", source);

		// Annotate method foo()
		EObject wrapper = TestUtil.createWrapper(artifactModel, "=cdt/{bar3.c[foo#74", "foo");
		handler.annotateArtifact(wrapper, "annotation");
		String actual = tu.getBuffer().getContents();

		String expected = "" +
				"/**" + System.lineSeparator() +
				" * Comment" + System.lineSeparator() +
				" * @req annotation" + System.lineSeparator() +
				" */" + System.lineSeparator() +
				"int foo() { return 0; }" + System.lineSeparator() +
				"";

		// Will fail, doesn't preserve comments
		assertEquals(expected, actual);
	}

}
