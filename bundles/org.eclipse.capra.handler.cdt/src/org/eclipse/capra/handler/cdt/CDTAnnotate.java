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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.capra.handler.cdt.preferences.CDTPreferences;
import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite.CommentPosition;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.IBuffer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ISourceRange;
import org.eclipse.cdt.core.model.ISourceReference;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Code for updating requirement annotation tag in Doxygen comments. Gets
 * comments from the CDT element, matches out the requirement tag and replaces
 * it with the new annotation.
 * 
 * TODO: This code doesn't work for the declaration of C++ class members. For
 * some reason comments on C++ members are parsed as trailing to previous
 * element instead of leading the one we want it to be associated with. Because
 * of that we don't find them.
 */
public class CDTAnnotate {

	private static final Pattern FILE_TAG_PATTERN = Pattern.compile("" + "^((/\\*[!*])" // Start of Doxygen comment
			+ "|" + "(\\s*\\*?))" // Comment body: Optionally leading star and spaces
			+ "\\s*" + "[@\\\\]file\\W" // Tag: Either \file or @file, with non-word char afterwards
			, Pattern.MULTILINE);

	private CDTAnnotate() {
		// Private constructor to hide implicit public one.
	}

	/**
	 * Creates or replaces the annotation on the handle element.
	 * 
	 * @param handle     The CDT object that contains the source element where the
	 *                   tag annotation comment should be placed.
	 * @param annotation The artifact annotation text (not including the tag)
	 * @throws CoreException If an error occur while working with the C model.
	 *                       Potential errors include: IO error while
	 *                       reading/writing to the file; the argument handle
	 *                       element has an unexpected structure which cannot be
	 *                       handled.
	 */
	public static void annotateArtifact(ICElement handle, String annotation) throws CoreException {
		if (!(handle instanceof ISourceReference))
			return;

		ISourceReference sourceRef = (ISourceReference) handle;

		IEclipsePreferences preferences = CDTPreferences.getPreferences();
		String tag = preferences
				.get(CDTPreferences.ANNOTATE_CDT_TAG_PREFIX, CDTPreferences.ANNOTATE_CDT_TAG_PREFIX_DEFAULT).trim()
				+ preferences.get(CDTPreferences.ANNOTATE_CDT_TAG, CDTPreferences.ANNOTATE_CDT_TAG_DEFAULT).trim();

		if (sourceRef instanceof ITranslationUnit) {
			annotateFile(annotation, tag, (ITranslationUnit) sourceRef);
		} else {
			annotateDeclaration(annotation, tag, sourceRef);
		}
	}

	private static void annotateFile(String annotation, String tag, ITranslationUnit translationUnit)
			throws CoreException {
		Optional<IASTComment> oldCommentNode = Arrays.stream(translationUnit.getAST().getComments())
				.filter(c -> isDoxygenFileComment(c.getRawSignature())).findFirst();

		String newline = findFirstNewline(translationUnit.getBuffer());

		String newCommentText = createNewCommentString(oldCommentNode.map(c -> c.getRawSignature()), annotation, tag,
				newline, true);

		if (oldCommentNode.isPresent()) {
			IASTFileLocation loc = oldCommentNode.orElse(null).getFileLocation();
			translationUnit.getBuffer().replace(loc.getNodeOffset(), loc.getNodeLength(), newCommentText);
		} else {
			// Put some space after new comment
			translationUnit.getBuffer().replace(0, 0, newCommentText + newline);
		}

		translationUnit.getBuffer().save(new NullProgressMonitor(), false);
	}

	static boolean isDoxygenFileComment(String comment) {
		return comment != null && FILE_TAG_PATTERN.matcher(comment).find();
	}

	private static void annotateDeclaration(String annotation, String tag, ISourceReference sourceRef)
			throws CoreException {
		ITranslationUnit translationUnit = sourceRef.getTranslationUnit();
		IASTTranslationUnit ast = translationUnit.getAST();

		IASTNode node = findNode(sourceRef, ast);
		String newline = findNewlineAndIndentationBefore(translationUnit.getBuffer(),
				node.getFileLocation().getNodeOffset());

		// Use ASTRewrite just to get comments for node
		ASTRewrite rewrite = ASTRewrite.create(ast);
		Optional<IASTComment> oldCommentNode = getDoxygenComment(rewrite.getComments(node, CommentPosition.leading));

		String newCommentText = createNewCommentString(oldCommentNode.map(c -> c.getRawSignature()), annotation, tag,
				newline, false);

		if (oldCommentNode.isPresent()) {
			IASTFileLocation loc = oldCommentNode.get().getFileLocation();
			translationUnit.getBuffer().replace(loc.getNodeOffset(), loc.getNodeLength(), newCommentText);
		} else {
			translationUnit.getBuffer().replace(node.getFileLocation().getNodeOffset(), 0, newCommentText);
		}

		translationUnit.getBuffer().save(new NullProgressMonitor(), false);
	}

	private static IASTNode findNode(ISourceReference sourceRef, IASTTranslationUnit ast) throws CModelException {
		ISourceRange sourceRange = sourceRef.getSourceRange();
		IASTNode node = ast.getNodeSelector(null).findEnclosingNode(sourceRange.getStartPos(), sourceRange.getLength());

		// If this is a struct with a typedef then the annotation should go on the
		// typedef instead
		if (node instanceof IASTCompositeTypeSpecifier) {
			IASTNode parent = ((IASTCompositeTypeSpecifier) node).getParent();

			if (parent instanceof IASTSimpleDeclaration) {
				return parent;
			}
		}

		return node;
	}

	/**
	 * @return A new comment, either with the tag section in comment substituted by
	 *         the content of annotation, or newly constructed comment with
	 *         annotation as its content.
	 */
	// Package visibility for testing
	static String createNewCommentString(Optional<String> oldCom, String annotation, String tag, String nl,
			boolean addFileTag) {

		if (!oldCom.isPresent()) {
			// There were no previous comment, create a new one
			String newComment = "/**" + nl;
			if (addFileTag)
				newComment += " * @file" + nl;
			newComment += " * " + tag + " " + annotation + nl + " */" + nl;
			return newComment;
		}

		String oldComment = oldCom.get();

		int tagIx = oldComment.indexOf(tag);

		if (tagIx != -1) {
			// There is an old tag. Match it and all of its annotation and replace with new
			// content.

			// Match the first thing after the tag and the annotations
			Matcher endOfTagMatcher = Pattern.compile(
					// Empty line, with star and optional spaces
					"(\r?\n\\s*\\*\\s*\r?\n)" + "|"
					// Comment end, including leading space and optional newline
							+ "(\\s*(\r?\n)?\\s*\\*/(\r?\n)?)")
					.matcher(oldComment);

			if (endOfTagMatcher.find(tagIx + tag.length())) {
				String beforeTagText = oldComment.substring(0, tagIx);
				String afterTagText = oldComment.substring(endOfTagMatcher.start());
				return beforeTagText + tag + " " + annotation + afterTagText;
			}
		} else {
			// There is no tag. Insert tag in the end of the comment.
			Matcher commentEndMatcher = Pattern.compile(".*?" // The whole comment, except the end, non-greedy
					+ "(" // A group for all text that should be replaced
					+ "(\r?\n)?" // Remove last newline (if there is one)
					+ "\\s*\\*/)", // Leading spaces and end-of-comment
					Pattern.DOTALL).matcher(oldComment);

			if (commentEndMatcher.find()) {
				return oldComment.substring(0, commentEndMatcher.start(1)) + nl + " * " + tag + " " + annotation + nl
						+ " */";
			}
		}

		// This is a weird comment indeed. What to do?
		throw new IllegalStateException("Weird comment: " + oldComment);
	}

	/**
	 * @return The first comment that is in Doxygen format (starts with /**).
	 */
	private static Optional<IASTComment> getDoxygenComment(List<IASTComment> comments) {
		// Begging search from the comment nearest to the node
		Collections.reverse(comments);
		return comments.stream().filter(c -> isDoxygenComment(c))
				.filter(c -> !isDoxygenFileComment(c.getRawSignature())).findAny();
	}

	private static boolean isDoxygenComment(IASTComment comment) {
		String text = comment.getRawSignature();
		return text.startsWith("/**") || text.startsWith("/*!");
	}

	/**
	 * Returns the newline chars of the first line in text.
	 */
	private static String findFirstNewline(IBuffer text) {
		for (int charIx = 0; charIx < text.getLength(); charIx++) {
			String newline = getNewline(text, charIx);
			if (newline != null)
				return newline;
		}

		// The argument text has no newlines, default to \n
		return "\n";
	}

	private static String getNewline(IBuffer text, int offset) {
		if (text.getChar(offset) == '\n') {
			if (offset > 0 && text.getChar(offset - 1) == '\r')
				return "\r\n";
			else
				return "\n";
		} else if (text.getChar(offset) == '\r') {
			return "\r";
		} else {
			return null;
		}
	}

	private static final Pattern NON_SPACES_PATTERN = Pattern.compile("\\S");

	/**
	 * Returns the newline and indentation chars for the line on offset.
	 */
	private static String findNewlineAndIndentationBefore(IBuffer text, int offset) {
		for (int charIx = offset; charIx >= 0; charIx--) {
			String newline = getNewline(text, charIx);
			if (newline != null) {
				// Get text from end of newline, to end of indentation (first non-space),
				String indentation = text.getText(charIx + 1, offset - charIx - 1);

				// Even if offset was for something with other char before it, we indent using
				// only whitespace
				return newline + NON_SPACES_PATTERN.matcher(indentation).replaceAll(" ");
			}
		}

		return findFirstNewline(text);
	}
}
