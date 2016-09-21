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
package org.eclipse.capra.ui.helpers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

/**
 * Contains methods to work with {@link EObject} instances encountered when
 * handling EMF models.
 */
public class EMFHelper {

	/**
	 * Builds an identifier String for the given EObject. This identifier starts
	 * with
	 * <ul>
	 * <li>the attribute of the EObject as a String, if the EObject does only
	 * have one attribute.</li>
	 * <li>the attribute called 'name' of the EObject, if it has such an
	 * attribute</li>
	 * <li>any attribute of the EObject, but String attributes are preferred
	 * </li>
	 * </ul>
	 * The identifier ends with " : " followed by the type of the EObject. <br>
	 * Example: A Node with the name "foo" will result in "foo : Node" <br>
	 * If the EObject does not have any attributes or all attributes have the
	 * value null, this function will only return the type of the EObject.
	 */
	public static String getIdentifier(final EObject eObject) {
		if (eObject == null)
			return "<null>";
		if (eObject.eClass() == null)
			return eObject.toString();

		boolean success = false;

		List<EAttribute> attributes = eObject.eClass().getEAllAttributes();
		StringBuilder identifier = new StringBuilder();

		success = tryGetSingleAttribute(eObject, attributes, identifier);

		if (!success)
			success = tryGetNameAttribute(eObject, attributes, identifier);

		if (!success)
			success = tryGetAnyAttribute(eObject, attributes, identifier);

		if (success)
			identifier.append(" : ");

		identifier.append(eObject.eClass().getName());

		return identifier.toString();
	}

	/**
	 * @param name
	 *            Use an empty StringBuilder as input. If this function returns
	 *            true, this parameter has been filled, if it returns false,
	 *            nothing happened.
	 * @return Indicates the success of this function and if the last parameter
	 *         contains output.
	 */
	public static boolean tryGetSingleAttribute(final EObject eObject, final List<EAttribute> attributes,
			final StringBuilder name) {
		boolean success = false;
		if (attributes.size() == 1) {
			Object obj = eObject.eGet(attributes.get(0));
			if (obj != null) {
				name.append(obj.toString());
				success = true;
			}
		}
		return success;
	}

	/**
	 * @param name
	 *            Use an empty StringBuilder as input. If this function returns
	 *            true, this parameter has been filled, if it returns false,
	 *            nothing happened.
	 * @return Indicates the success of this function and if the last parameter
	 *         contains output.
	 */
	public static boolean tryGetNameAttribute(final EObject eObject, final List<EAttribute> attributes,
			final StringBuilder name) {
		boolean success = false;
		for (EAttribute feature : attributes) {
			if (feature.getName().equals("name")) {
				Object obj = eObject.eGet(feature);
				if (obj != null) {
					name.append(obj.toString());
					success = true;
					break;
				}
			}
		}
		return success;
	}

	/**
	 * @param name
	 *            Use an empty StringBuilder as input. If this function returns
	 *            true, this parameter has been filled, if it returns false,
	 *            nothing happened.
	 * @return Indicates the success of this function and if the last parameter
	 *         contains output.
	 */
	public static boolean tryGetAnyAttribute(final EObject eObject, final List<EAttribute> attributes,
			final StringBuilder name) {
		boolean success = false;
		String nonStringName = null;
		String stringName = null;
		for (EAttribute feature : attributes) {
			Object obj = eObject.eGet(feature);
			if (obj == null)
				continue;
			if (obj instanceof String) {
				stringName = (String) obj;
				break;
			} else {
				nonStringName = obj.toString();
			}
		}
		if (stringName != null && !stringName.equals("null")) {
			name.append(stringName);
			success = true;
		} else if (nonStringName != null && !nonStringName.equals("null")) {
			name.append(nonStringName);
			success = true;
		}
		return success;
	}

	/**
	 * Linearizes a tree to a list. The function checks if the provided
	 * parameter is of type {@link EObject} and linearizes only if that is the
	 * case.
	 * 
	 * @param object
	 *            the object to linearize
	 * @return a list of {@link EObject}s originally contained in the tree
	 *         structure of the parameter or an empty list if the paramter was
	 *         not an {@link EObject}
	 */
	public static List<EObject> linearize(Object object) {
		ArrayList<EObject> elementList = new ArrayList<EObject>();
		if (object instanceof EObject) {
			EObject root = (EObject) object;
			root.eAllContents().forEachRemaining(element -> elementList.add(element));
			elementList.add(root);
		}
		return elementList;
	}

}
