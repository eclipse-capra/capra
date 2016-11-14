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

import java.util.List;

import org.eclipse.capra.core.handlers.AnnotationException;
import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite.CommentPosition;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ISourceRange;
import org.eclipse.cdt.core.model.ISourceReference;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;

public class CDTAnnotate {

	public static void annotateArtifact(ICElement handle, String annotation) throws AnnotationException {
		if (handle instanceof ISourceReference) {
			try {
				ISourceReference sourceReference = (ISourceReference) handle;
				ISourceRange range = sourceReference.getSourceRange();
				ITranslationUnit tu = sourceReference.getTranslationUnit();
				IASTTranslationUnit ast = tu.getAST();
				IASTNodeSelector selector = ast.getNodeSelector(null);
				ASTRewrite rewrite = ASTRewrite.create(ast);

				IASTNode node = selector.findEnclosingNode(range.getStartPos(), range.getLength());
				List<IASTComment> comments = rewrite.getComments(node, CommentPosition.leading);
				IASTComment oldComment = getDoxygenComment(node, comments);
				String commentString = createNewCommentString(oldComment, annotation);
				IASTNode newComment = rewrite.createLiteralNode(commentString);

				if (oldComment != null)
					rewrite.remove(oldComment, null); // Has no effect

				rewrite.insertBefore(node.getParent(), node, newComment, null);

				Change change = rewrite.rewriteAST();
				change.perform(new NullProgressMonitor());
			} catch (CModelException e) {
				throw new AnnotationException(e.getStatus());
			} catch (CoreException e) {
				throw new AnnotationException(e.getStatus());
			}
		}
	}

	// TODO: Get tag from somewhere else
	private static String createNewCommentString(IASTComment comment, String annotation) {
		String text;
		if (comment != null) {
			text = comment.getRawSignature();
			if (text.contains("@req")) {
				text = text.replaceAll("@req .*", "@req " + annotation);
			} else {
				text = text.replaceAll("\\*/", "* @req " + annotation + System.lineSeparator() + " */");
			}
		} else {
			text = "/**" + System.lineSeparator() + " * @req " + annotation + System.lineSeparator() + " */";
		}

		return text;
	}

	private static IASTComment getDoxygenComment(IASTNode node, List<IASTComment> comments) {
		for (IASTComment comment : comments) {
			if (isDoxygenComment(comment)) {
				return comment;
			}
		}
		return null;
	}

	private static boolean isDoxygenComment(IASTComment comment) {
		String text = comment.getRawSignature();
		if (text.startsWith("/**")) {
			return true;
		}
		return false;
	}

}
