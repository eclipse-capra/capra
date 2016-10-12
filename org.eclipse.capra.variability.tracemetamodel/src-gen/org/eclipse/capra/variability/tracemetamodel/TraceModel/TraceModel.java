/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Trace Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModel#getGenericTraceLinks <em>Generic Trace Links</em>}</li>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModel#getVariableTraceLinks <em>Variable Trace Links</em>}</li>
 * </ul>
 *
 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getTraceModel()
 * @model
 * @generated
 */
public interface TraceModel extends EObject {
	/**
	 * Returns the value of the '<em><b>Generic Trace Links</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.capra.variability.tracemetamodel.TraceModel.GenericTraceLink}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Generic Trace Links</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Generic Trace Links</em>' containment reference list.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getTraceModel_GenericTraceLinks()
	 * @model containment="true"
	 * @generated
	 */
	EList<GenericTraceLink> getGenericTraceLinks();

	/**
	 * Returns the value of the '<em><b>Variable Trace Links</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.capra.variability.tracemetamodel.TraceModel.VariabilityTraceLink}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Variable Trace Links</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Variable Trace Links</em>' containment reference list.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getTraceModel_VariableTraceLinks()
	 * @model containment="true"
	 * @generated
	 */
	EList<VariabilityTraceLink> getVariableTraceLinks();

} // TraceModel
