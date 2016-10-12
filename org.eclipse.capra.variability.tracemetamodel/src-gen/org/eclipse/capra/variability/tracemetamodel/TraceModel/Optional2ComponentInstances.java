/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel;

import org.eclipse.app4mc.variability.ample.Optional;
import org.eclipse.app4mc.variability.ample.Or;

import org.eclipse.app4mc.variability.components.ComponentInstance;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Optional2 Component Instances</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances#getOptionalFeature <em>Optional Feature</em>}</li>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances#getOrFeature <em>Or Feature</em>}</li>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances#getComponents <em>Components</em>}</li>
 * </ul>
 *
 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getOptional2ComponentInstances()
 * @model
 * @generated
 */
public interface Optional2ComponentInstances extends VariabilityTraceLink {
	/**
	 * Returns the value of the '<em><b>Optional Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Optional Feature</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Optional Feature</em>' reference.
	 * @see #setOptionalFeature(Optional)
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getOptional2ComponentInstances_OptionalFeature()
	 * @model
	 * @generated
	 */
	Optional getOptionalFeature();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances#getOptionalFeature <em>Optional Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Optional Feature</em>' reference.
	 * @see #getOptionalFeature()
	 * @generated
	 */
	void setOptionalFeature(Optional value);

	/**
	 * Returns the value of the '<em><b>Or Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Or Feature</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Or Feature</em>' reference.
	 * @see #setOrFeature(Or)
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getOptional2ComponentInstances_OrFeature()
	 * @model
	 * @generated
	 */
	Or getOrFeature();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances#getOrFeature <em>Or Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Or Feature</em>' reference.
	 * @see #getOrFeature()
	 * @generated
	 */
	void setOrFeature(Or value);

	/**
	 * Returns the value of the '<em><b>Components</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.app4mc.variability.components.ComponentInstance}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Components</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Components</em>' reference list.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getOptional2ComponentInstances_Components()
	 * @model required="true"
	 * @generated
	 */
	EList<ComponentInstance> getComponents();

} // Optional2ComponentInstances
