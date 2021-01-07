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

import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.capra.core.handlers.AnnotationException;
import org.eclipse.capra.handler.jdt.preferences.JDTPreferences;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ChildPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class JDTAnnotate {

	private static final String TAG_PREFIX = "@";
	private static final String ANNOTATION_FAILED = "Annotation failed";

	/**
	 * Private constructor to hide implicit public one.
	 */
	private JDTAnnotate() {
		// Deliberately do nothing.
	}

	@SuppressWarnings("unchecked")
	public static void annotateArtifact(IJavaElement handle, String annotation) throws AnnotationException {
		if (handle instanceof ISourceReference) {
			try {
				ISourceReference sourceReference = (ISourceReference) handle;
				ISourceRange range = sourceReference.getSourceRange();
				ICompilationUnit cu = getCompilationUnit(sourceReference);
				if (cu == null) {
					Status status = new Status(IStatus.INFO, Activator.PLUGIN_ID, ANNOTATION_FAILED,
							new NoSuchElementException(
									"Could not find compilation unit for " + sourceReference.getSource()));
					throw new AnnotationException(status);
				}
				ASTParser parser = ASTParser.newParser(AST.JLS8);

				String source = cu.getSource();
				IDocument document = new Document(source);
				parser.setSource(cu);

				CompilationUnit root = (CompilationUnit) parser.createAST(null);
				ASTRewrite rewrite = ASTRewrite.create(root.getAST());
				ASTNode node = getNode(root, range.getOffset(), range.getLength());
				AST ast = node.getAST();

				ChildPropertyDescriptor property = getJavadocPropertyDescriptor(handle);
				Javadoc javaDoc = (Javadoc) rewrite.get(node, property);
				if (javaDoc == null)
					javaDoc = ast.newJavadoc();

				ListRewrite tagsRewriter = rewrite.getListRewrite(javaDoc, Javadoc.TAGS_PROPERTY);

				// Get preferred tag name
				IEclipsePreferences preferences = JDTPreferences.getPreferences();
				String tagName = TAG_PREFIX + preferences
						.get(JDTPreferences.ANNOTATE_JDT_TAG, JDTPreferences.ANNOTATE_JDT_TAG_DEFAULT).trim();

				// Remove existing tags
				((List<TagElement>) javaDoc.tags()).stream()
						.filter(t -> t.getTagName() != null && t.getTagName().equals(tagName))
						.forEach(t -> tagsRewriter.remove(t, null));

				// Create new tag
				TagElement tag = ast.newTagElement();
				TextElement text = ast.newTextElement();
				text.setText(annotation);
				tag.fragments().add(text);
				tag.setTagName(tagName);

				tagsRewriter.insertLast(tag, null);

				rewrite.set(node, property, javaDoc, null);
				TextEdit edit = rewrite.rewriteAST();
				edit.apply(document);

				String newSource = document.get();
				cu.getBuffer().setContents(newSource);
				cu.save(null, false);
			} catch (JavaModelException e) {
				throw new AnnotationException(e.getStatus());
			} catch (MalformedTreeException | BadLocationException e) {
				Status status = new Status(IStatus.INFO, Activator.PLUGIN_ID, ANNOTATION_FAILED, e.getCause());
				throw new AnnotationException(status);
			}
		}
	}

	private static ASTNode getNode(ASTNode root, int offset, int length) {
		NodeFinder finder = new NodeFinder(root, offset, length);
		ASTNode result = finder.getCoveringNode();
		if (result != null)
			return result;
		return finder.getCoveredNode();
	}

	private static ICompilationUnit getCompilationUnit(ISourceReference o) {
		if (o instanceof ICompilationUnit)
			return (ICompilationUnit) o;
		if (o instanceof IMethod)
			return ((IMethod) o).getCompilationUnit();
		if (o instanceof IJavaElement)
			return (ICompilationUnit) ((IJavaElement) o).getAncestor(IJavaElement.COMPILATION_UNIT);
		return null;
	}

	private static ChildPropertyDescriptor getJavadocPropertyDescriptor(IJavaElement handle) {
		switch (handle.getElementType()) {
		case IJavaElement.METHOD:
			return MethodDeclaration.JAVADOC_PROPERTY;
		case IJavaElement.TYPE:
			return TypeDeclaration.JAVADOC_PROPERTY;
		default:
			return MethodDeclaration.JAVADOC_PROPERTY;
		}
	}

}
