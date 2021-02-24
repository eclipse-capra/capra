/*******************************************************************************
 * Copyright (c) 2016, 2021 Chalmers | University of Gothenburg, rt-labs and others.
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
package org.eclipse.capra.core.helpers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

/**
 * Contains methods to work with {@link EObject} instances encountered when
 * handling EMF models.
 */
public class EMFHelper {

	private EMFHelper() {
		super();
	}

	/**
	 * Builds an identifier String for the given EObject. This identifier starts
	 * with
	 * <ul>
	 * <li>the attribute of the EObject as a String, if the EObject does only have
	 * one attribute.</li>
	 * <li>the attribute called 'name' of the EObject, if it has such an
	 * attribute</li>
	 * <li>any attribute of the EObject, but String attributes are preferred</li>
	 * </ul>
	 * The identifier ends with " : " followed by the type of the EObject. <br>
	 * Example: A Node with the name "foo" will result in "foo : Node" <br>
	 * If the EObject does not have any attributes or all attributes have the value
	 * null, this function will only return the type of the EObject.
	 */
	public static String getIdentifier(final EObject eObject) {
		if (eObject == null) {
			return "<null>";
		}
		if (eObject.eClass() == null) {
			return eObject.toString();
		}

		boolean success = false;

		List<EAttribute> attributes = eObject.eClass().getEAllAttributes();
		StringBuilder identifier = new StringBuilder();

		success = tryGetNameAttribute(eObject, identifier);

		// We didn't find a name. Try finding something else that we can use.
		if (!success) {
			success = tryGetAnyAttribute(eObject, attributes, identifier);
		}

		if (success) {
			identifier.append(" : ");
		}

		identifier.append(eObject.eClass().getName());

		return identifier.toString();
	}

	/**
	 * Tries to retrieve a {@code String} representation of the provided
	 * {@code EAttribute} for the provided {@code EObject}. If the attribute is
	 * {@code null}, it is not a valid attribute of the given {@code EObject} or the
	 * value of the attribute is {@code} null, this method will return {@code false}
	 * and the provided {@code StringBuilder} will not be altered.
	 * 
	 * @param builder {@code StringBuilder} to construct the String representation
	 *                of the provided {@code EAttribute}. If this method returns
	 *                {@code true}, this parameter has been changed, if it returns
	 *                {@code false}, the {@code StringBuilder} is unaltered.
	 * @return indicates the success of this function and if the
	 *         {@code StringBuilder} contains output.
	 */
	public static boolean tryGetAttribute(final EObject eObject, final EAttribute attribute, final StringBuilder name) {
		boolean success = false;
		if (attribute != null) {
			try {
				Object obj = eObject.eGet(attribute);
				if (obj != null) {
					name.append(obj.toString());
					success = true;
				}
			} catch (IllegalArgumentException ex) {
				// Deliberately do nothing.
				// This can happen if the provided attribute is not an attribute of the eObject.
				// In this case, we just want the method to return false, which it does if we
				// just hold our feet still.
			}
		}
		return success;
	}

	/**
	 * Tries to retrieve a String representation of the attribute "name" from the
	 * provided {@code EObject}.
	 * 
	 * @param builder {@code StringBuilder} to construct the String representation
	 *                of the {@code EObject}'s "name" attribute. If this function
	 *                returns {@code true}, this parameter has been filled, if it
	 *                returns {@code false}, the {@code StringBuilder} is unaltered.
	 * @return indicates the success of this function and if the
	 *         {@code StringBuilder} contains output.
	 * @see #tryGetAttribute(EObject, EAttribute, StringBuilder)
	 */
	public static boolean tryGetNameAttribute(final EObject eObject, final StringBuilder builder) {
		boolean success = false;
		for (EAttribute attribute : eObject.eClass().getEAllAttributes()) {
			if (attribute.getName().equals("name")) {
				success = tryGetAttribute(eObject, attribute, builder);
			}
		}
		return success;
	}

	/**
	 * Attempts to retrieve a {@code String} representation of any of the given
	 * attributes. For that purpose, it iterates over all attributes until it finds
	 * one which is not null and non-empty. It then adds the String representation
	 * of that attribute's value to the provided {@code StringBuilder} and returns
	 * {@code true}.
	 * 
	 * @param builder {@code StringBuilder} to construct the String representation
	 *                of the first non-null, non-empty attribute. If this function
	 *                returns {@code true}, this parameter has been filled, if it
	 *                returns {@code false}, the {@code StringBuilder} is unaltered.
	 * @return indicates the success of this function and if the
	 *         {@code StringBuilder} contains output.
	 */
	public static boolean tryGetAnyAttribute(final EObject eObject, final List<EAttribute> attributes,
			final StringBuilder name) {
		boolean success = false;
		String stringName = "";
		for (EAttribute feature : attributes) {
			Object obj = eObject.eGet(feature);
			if (obj instanceof String) {
				stringName = (String) obj;
			} else if (obj != null) {
				stringName = obj.toString();
			}
			if (stringName != null && !stringName.equals("null")) {
				name.append(stringName);
				success = true;
				break;
			}
		}
		return success;
	}

	/**
	 * Linearizes a tree to a list. The function checks if the provided parameter is
	 * of type {@link EObject} and linearizes only if that is the case. The root of
	 * the tree will always be the first element in the list.
	 * 
	 * @param object the object to linearize
	 * @return a list of {@link EObject}s originally contained in the tree structure
	 *         of the parameter or an empty list if the paramter was not an
	 *         {@link EObject}
	 */
	public static List<EObject> linearize(Object object) {
		ArrayList<EObject> elementList = new ArrayList<>();
		if (object instanceof EObject) {
			EObject root = (EObject) object;
			elementList.add(root);
			root.eAllContents().forEachRemaining(elementList::add);
		}
		return elementList;
	}

	/**
	 * Public API access for other classes to get the name attribute of an EObject
	 * 
	 * @param eObject
	 * @return String
	 */
	public static String getNameAttribute(final EObject eObject) {
		StringBuilder name = new StringBuilder();
		tryGetNameAttribute(eObject, name);
		return name.toString();
	}

	/**
	 * Compares to {@link EObject} instances based on their identifier using
	 * {@link EMFHelper#getIdentifier(EObject)}.
	 * 
	 * @param first  the first {@code EObject} to compare
	 * @param second the second {@code EObject} to compare
	 * @return {@code true} if the identifiers of the two instances are equal,
	 *         {@code false} otherwise
	 */
	public static boolean hasSameIdentifier(EObject first, EObject second) {
		return EMFHelper.getIdentifier(first).equals(EMFHelper.getIdentifier(second));
	}

	/**
	 * Checks if the given {@link EObject} is in the list of {@code EObject}s using
	 * {@link #hasSameIdentifier(EObject, EObject)}.
	 * 
	 * @param list the list to check
	 * @param obj  the object to check for
	 * @return {@code true} if {@code obj} is in {@code list}, {@code false}
	 *         otherwise
	 */
	public static boolean isElementInList(List<EObject> list, EObject obj) {
		for (EObject next : list) {
			if (EMFHelper.hasSameIdentifier(obj, next)) {
				return true;
			}
		}
		return false;
	}
}
