/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Related To</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.RelatedTo#getItem <em>Item</em>}</li>
 * </ul>
 *
 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getRelatedTo()
 * @model
 * @generated
 */
public interface RelatedTo extends GenericTraceLink {
	/**
	 * Returns the value of the '<em><b>Item</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Item</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Item</em>' reference list.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getRelatedTo_Item()
	 * @model
	 * @generated
	 */
	EList<EObject> getItem();

} // RelatedTo
