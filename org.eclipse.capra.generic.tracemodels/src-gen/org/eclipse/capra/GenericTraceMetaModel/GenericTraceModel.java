/**
 */
package org.eclipse.capra.GenericTraceMetaModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Generic Trace Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.GenericTraceMetaModel.GenericTraceModel#getTraces <em>Traces</em>}</li>
 * </ul>
 *
 * @see org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage#getGenericTraceModel()
 * @model
 * @generated
 */
public interface GenericTraceModel extends EObject {
	/**
	 * Returns the value of the '<em><b>Traces</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.capra.GenericTraceMetaModel.RelatedTo}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Traces</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Traces</em>' containment reference list.
	 * @see org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage#getGenericTraceModel_Traces()
	 * @model containment="true"
	 * @generated
	 */
	EList<RelatedTo> getTraces();

} // GenericTraceModel
