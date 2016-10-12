/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel;

import org.eclipse.app4mc.variability.ample.Mandatory;

import org.eclipse.app4mc.variability.components.ComponentInstance;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mandatory2 Component Instances</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Mandatory2ComponentInstances#getMandatoryFeature <em>Mandatory Feature</em>}</li>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Mandatory2ComponentInstances#getComponents <em>Components</em>}</li>
 * </ul>
 *
 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getMandatory2ComponentInstances()
 * @model
 * @generated
 */
public interface Mandatory2ComponentInstances extends VariabilityTraceLink {
	/**
	 * Returns the value of the '<em><b>Mandatory Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mandatory Feature</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mandatory Feature</em>' reference.
	 * @see #setMandatoryFeature(Mandatory)
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getMandatory2ComponentInstances_MandatoryFeature()
	 * @model required="true"
	 * @generated
	 */
	Mandatory getMandatoryFeature();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Mandatory2ComponentInstances#getMandatoryFeature <em>Mandatory Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mandatory Feature</em>' reference.
	 * @see #getMandatoryFeature()
	 * @generated
	 */
	void setMandatoryFeature(Mandatory value);

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
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getMandatory2ComponentInstances_Components()
	 * @model required="true"
	 * @generated
	 */
	EList<ComponentInstance> getComponents();

} // Mandatory2ComponentInstances
