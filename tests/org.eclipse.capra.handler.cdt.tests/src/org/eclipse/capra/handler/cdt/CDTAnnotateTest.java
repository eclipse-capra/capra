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
package org.eclipse.capra.handler.cdt;

import static org.junit.Assert.assertEquals;

import org.eclipse.capra.handler.cdt.preferences.CDTPreferences;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the annotate capability of the CDTHandler. 
 */
public class CDTAnnotateTest {

	private static final String NL = "\n";

	private static ICProject project;
	private CDTHandler handler = new CDTHandler();
	private EObject artifactModel = TestUtil.setupModel();

	@Before
	public void setupTestProject() throws CoreException, BuildException {
		System.out.println("Setting up test project");
		project = TestUtil.setupTestProject("cdt");
		CDTPreferences.getPreferences().putBoolean(CDTPreferences.ANNOTATE_CDT, true);
		CDTPreferences.getPreferences().put(CDTPreferences.ANNOTATE_CDT_TAG, "req");
		CDTPreferences.getPreferences().put(CDTPreferences.ANNOTATE_CDT_TAG_PREFIX, "@");
		System.out.println("Done setting up test project");
	}

	@After
	public void deleteTestProject() throws CoreException {
		TestUtil.deleteTestProject(project);
	}

	/**
	 * An annotation appears on a function without a previous comment. 
	 */
	@Test
	public void shouldAnnotateMethod() throws Exception {
		String source = "" +
				"int foo() { return 0; }" + NL +
				"";

		ITranslationUnit tu = TestUtil.createTranslationUnit(project.getProject(), "bar.c", source);

		// Annotate method foo()
		EObject wrapper = TestUtil.createWrapper(artifactModel, "=cdt/{bar.c[foo#74", "foo");
		handler.annotateArtifact(wrapper, "annotation");
		String actual = tu.getBuffer().getContents();

		String expected = "" +
				"/**" + NL +
				" * @req annotation" + NL +
				" */" + NL +
				"int foo() { return 0; }" + NL +
				"";

		assertEquals(expected, actual);
	}
	
	@Test
	public void shouldReplaceAnnotation() throws Exception {
		String source = "" +
			"/**" + NL +
			" * @req annotation1" + NL +
			" */" + NL +
			"int foo() { return 0; }" + NL +
			"";

		ITranslationUnit tu = TestUtil.createTranslationUnit(project.getProject(), "bar2.c", source);

		// Annotate method foo()
		EObject wrapper = TestUtil.createWrapper(artifactModel, "=cdt/{bar2.c[foo#74", "foo");
		handler.annotateArtifact(wrapper, "annotation2");
		String actual = tu.getBuffer().getContents();

		String expected = "" +
				"/**" + NL +
				" * @req annotation2" + NL +
				" */" + NL +
				"int foo() { return 0; }" + NL +
				"";

		// Will fail, doesn't replace annotation
		assertEquals(expected, actual);
	}

	@Test
	public void shouldPreserveComments() throws Exception {
		String source = "" +
				"/**" + NL +
				" * Comment" + NL +
				" */" + NL +
				"int foo() { return 0; }" + NL +
				"";

		ITranslationUnit tu = TestUtil.createTranslationUnit(project.getProject(), "bar3.c", source);

		// Annotate method foo()
		EObject wrapper = TestUtil.createWrapper(artifactModel, "=cdt/{bar3.c[foo#74", "foo");
		handler.annotateArtifact(wrapper, "annotation");
		String actual = tu.getBuffer().getContents();

		String expected = "" +
				"/**" + NL +
				" * Comment" + NL +
				" * @req annotation" + NL +
				" */" + NL +
				"int foo() { return 0; }" + NL +
				"";

		assertEquals(expected, actual);
	}
}
