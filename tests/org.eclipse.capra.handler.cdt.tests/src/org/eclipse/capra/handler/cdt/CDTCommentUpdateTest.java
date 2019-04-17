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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

/**
 * A non-plugin test for the trickiest parts of the comment updating. 
 */
public class CDTCommentUpdateTest {
	// Line separator that we tell the comment updater that the analysed file is using
	private static final String NL = "\n";

	@Test
	public void normalCommentNoTag() {
		String input = ""
			+ "/**\n"
			+ " * Comment\n"
			+ " */\n";

		String expected = ""
			+ "/**\n"
			+ " * Comment\n"
			+ " * @tag annotation\n"
			+ " */";

		String result = CDTAnnotate.createNewCommentString(Optional.of(input), "annotation", "@tag", NL, false);

		assertEquals(expected, result);
	}

	@Test
	public void noComment() {
		String expected = ""
			+ "/**\n"
			+ " * @tag annotation\n"
			+ " */\n";

		String result = CDTAnnotate.createNewCommentString(Optional.empty(), "annotation", "@tag", NL, false);

		assertEquals(expected, result);
	}
	
	@Test
	@Ignore("Empty comments are not handled. Should they be")
	public void emptyComment() {
		@SuppressWarnings("unused")
		String result = CDTAnnotate.createNewCommentString(Optional.of("  "), "annotation", "@tag", NL, false);
	}
	
	@Test
	public void commentEndOnSameLineNoTag() {
		String input = "/** Comment */\n";

		String expected = ""
			+ "/** Comment\n"
			+ " * @tag annotation\n"
			+ " */";

		String result = CDTAnnotate.createNewCommentString(Optional.of(input), "annotation", "@tag", NL, false);
		assertEquals(expected, result);
	}
	
	
	@Test
	public void normalCommentReplaceTag() {
		String input = ""
			+ "/**\n"
			+ " * Comment\n"
			+ " * @tag old_annotation\n"
			+ " */\n";

		String expected = ""
			+ "/**\n"
			+ " * Comment\n"
			+ " * @tag annotation\n"
			+ " */\n";

		String result = CDTAnnotate.createNewCommentString(Optional.of(input), "annotation", "@tag", NL, false);

		assertEquals(expected, result);
	}
	
	@Test
	public void commentReplaceTagAfterEmptyLine() {
		String input = ""
			+ "/**\n"
			+ " * Comment\n"
			+ " *\n"
			+ " * @tag old_annotation\n"
			+ " */\n";

		String expected = ""
			+ "/**\n"
			+ " * Comment\n"
			+ " *\n"
			+ " * @tag annotation\n"
			+ " */\n";

		String result = CDTAnnotate.createNewCommentString(Optional.of(input), "annotation", "@tag", NL, false);

		assertEquals(expected, result);
	}

	@Test
	public void commentReplaceTagSameLine() {
		String input = "/** Comment @tag old_annotation */\n";
		String expected = "/** Comment @tag annotation */\n";

		String result = CDTAnnotate.createNewCommentString(Optional.of(input), "annotation", "@tag", NL, false);

		System.out.println(result);
		assertEquals(expected, result);
	}
	
	@Test
	public void isFileTag() {
		String input = ""
			+ "/**\n"
			+ " * \\file\n"
			+ " */\n";

		assertTrue(CDTAnnotate.isDoxygenFileComment(input));
		assertTrue(CDTAnnotate.isDoxygenFileComment("/**@file */\n"));
		assertFalse(CDTAnnotate.isDoxygenFileComment("/**file */\n"));
		assertFalse(CDTAnnotate.isDoxygenFileComment("/**\\filed */\n"));
		assertFalse(CDTAnnotate.isDoxygenFileComment("/**\n TEXT \\file */\n"));
	}
}
