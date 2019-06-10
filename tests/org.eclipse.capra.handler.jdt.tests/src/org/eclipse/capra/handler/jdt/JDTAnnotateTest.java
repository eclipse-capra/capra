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
package org.eclipse.capra.handler.jdt;

import static org.junit.Assert.*;

import org.eclipse.capra.handler.jdt.preferences.JDTPreferences;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JDTAnnotateTest {

	private JavaElementHandler handler = new JavaElementHandler();
	private EObject artifactModel = TestUtil.setupModel();
	private IProject project;

	@Before
	public void createTestProject() throws CoreException {
		project = TestUtil.createTestProject("jdt");
		JDTPreferences.getPreferences().putBoolean(JDTPreferences.ANNOTATE_JDT, true);
		JDTPreferences.getPreferences().put(JDTPreferences.ANNOTATE_JDT_TAG, "req");
	}

	@After
	public void deleteTestProject() throws CoreException {
		TestUtil.deleteTestProject(project);
	}

	@Test
	public void shouldAnnotateClass() throws Exception {
		String source = "" +
				"package jdt\n" +
				"public class bar {\n" +
				"};\n";

		ICompilationUnit cu = TestUtil.createCompilationUnit(project, "bar.java", source);

		// Annotate class bar
		EObject wrapper = TestUtil.createWrapper(artifactModel, "=jdt/src<jdt{bar.java[bar", "bar");
		handler.annotateArtifact(wrapper, "annotation");
		String actual = cu.getSource();

		String expected = "" +
				"package jdt\n" +
				"/**\n" +
				" * @req annotation\n" +
				" */\n" +
				"public class bar {\n" +
				"};\n";

		assertEquals(expected, actual);
	}

	@Test
	public void shouldAnnotateMethod() throws Exception {
		String source = "" +
				"package jdt\n" +
				"public class bar {\n" +
				"int foo() { return 0; }\n" +
				"};\n";

		ICompilationUnit cu = TestUtil.createCompilationUnit(project, "bar.java", source);

		// Annotate method foo()
		EObject wrapper = TestUtil.createWrapper(artifactModel, "=jdt/src<jdt{bar.java[bar~foo", "foo");
		handler.annotateArtifact(wrapper, "annotation");
		String actual = cu.getSource();

		String expected = "" +
				"package jdt\n" +
				"public class bar {\n" +
				"/**\n" +
				" * @req annotation\n" +
				" */\n" +
				"int foo() { return 0; }\n" +
				"};\n";

		assertEquals(expected, actual);
	}

	@Test
	public void shouldReplaceAnnotation() throws Exception {
		String source = "" +
				"package jdt\n" +
				"public class bar {\n" +
				"/**\n" +
				" * @req annotation1\n" +
				" */\n" +
				"int foo() { return 0; }\n" +
				"};\n";

		ICompilationUnit cu = TestUtil.createCompilationUnit(project, "bar.java", source);

		// Annotate method foo()
		EObject wrapper = TestUtil.createWrapper(artifactModel, "=jdt/src<jdt{bar.java[bar~foo", "foo");
		handler.annotateArtifact(wrapper, "annotation2");
		String actual = cu.getSource();

		String expected = "" +
				"package jdt\n" +
				"public class bar {\n" +
				"/**\n" +
				" * @req annotation2\n" +
				" */\n" +
				"int foo() { return 0; }\n" +
				"};\n";

		assertEquals(expected, actual);
	}

	@Test
	public void shouldPreserveComments() throws Exception {
		String source = "" +
				"package jdt\n" +
				"public class bar {\n" +
				"/**\n" +
				" * Comment\n" +
				" */\n" +
				"int foo() { return 0; }\n" +
				"};\n";

		ICompilationUnit cu = TestUtil.createCompilationUnit(project, "bar.java", source);

		// Annotate method foo()
		EObject wrapper = TestUtil.createWrapper(artifactModel, "=jdt/src<jdt{bar.java[bar~foo", "foo");
		handler.annotateArtifact(wrapper, "annotation");
		String actual = cu.getSource();

		String expected = "" +
				"package jdt\n" +
				"public class bar {\n" +
				"/**\n" +
				" * Comment\n" +
				" * @req annotation\n" +
				" */\n" +
				"int foo() { return 0; }\n" +
				"};\n";

		assertEquals(expected, actual);
	}

}
