/**
 */
package org.eclipse.capra.GenericTraceMetaModel;

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
 *   <li>{@link org.eclipse.capra.GenericTraceMetaModel.RelatedTo#getID <em>ID</em>}</li>
 *   <li>{@link org.eclipse.capra.GenericTraceMetaModel.RelatedTo#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.capra.GenericTraceMetaModel.RelatedTo#getItem <em>Item</em>}</li>
 * </ul>
 *
 * @see org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage#getRelatedTo()
 * @model
 * @generated
 */
public interface RelatedTo extends EObject {
	/**
	 * Returns the value of the '<em><b>ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>ID</em>' attribute.
	 * @see org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage#getRelatedTo_ID()
	 * @model unique="false" transient="true" changeable="false" volatile="true" derived="true"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel get='return <%org.eclipse.emf.ecore.util.EcoreUtil%>.generateUUID();'"
	 * @generated
	 */
	String getID();

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage#getRelatedTo_Name()
	 * @model unique="false"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.GenericTraceMetaModel.RelatedTo#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

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
	 * @see org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage#getRelatedTo_Item()
	 * @model
	 * @generated
	 */
	EList<EObject> getItem();

} // RelatedTo
