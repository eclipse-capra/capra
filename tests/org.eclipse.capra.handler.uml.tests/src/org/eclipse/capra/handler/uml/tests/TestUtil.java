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
package org.eclipse.capra.handler.uml.tests;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;

public class TestUtil {

	/**
	 * Creates an empty UML model.
	 *
	 * @param name
	 *            the name of the model
	 * @return
	 */
	public static Model createUMLModel(String name) {
		Model model = UMLFactory.eINSTANCE.createModel();
		model.setName(name);
		return model;
	}

	/**
	 * Creates an EClass entity in the provided model.
	 *
	 * @param model
	 *            UML model
	 * @param name
	 *            the name of the created EClass entity
	 */
	public static void createClassInUMLModel(Model model, String name) {
		org.eclipse.uml2.uml.Class c = UMLFactory.eINSTANCE.createClass();
		c.setName(name);
		model.getPackagedElements().add(c);
	}

	/**
	 * Creates a generalization relationship between two classes
	 * 
	 * @param A
	 *            First class i.e the super class
	 * @param B
	 *            Second class
	 */
	public static void createGeneralizationBetweenClasses(org.eclipse.uml2.uml.Class A, org.eclipse.uml2.uml.Class B) {
		A.createGeneralization(B);
	}

	/**
	 * Returns an UML model entity from the specified project.
	 *
	 * @param project
	 *            the project containing the model
	 * @param p
	 *            the name of the model
	 * @param rs
	 *            the provided ResourceSet instance
	 * @return a UML model entity
	 * @throws IOException
	 */
	public static Model loadUMLModel(IProject project, String p, ResourceSet rs) throws IOException {
		URI path = URI.createFileURI(project.getLocation().toString() + "/" + p);
		return (Model) rs.getResource(path, true).getContents().get(0);
	}

	/**
	 * Persists (saves) the provided UML model in the specified project.
	 *
	 * @param project
	 *            a handle to the project in which the model is to be persisted
	 * @param model
	 *            the UML model to be persisted
	 * @throws IOException
	 */
	public static void save(IProject project, Model model) throws IOException {
		ResourceSet rs = new ResourceSetImpl();
		URI path = URI.createFileURI(project.getLocation().toString() + "/" + model.getName() + ".uml");
		Resource r = rs.createResource(path);
		r.getContents().add(model);
		r.save(null);
	}

}
