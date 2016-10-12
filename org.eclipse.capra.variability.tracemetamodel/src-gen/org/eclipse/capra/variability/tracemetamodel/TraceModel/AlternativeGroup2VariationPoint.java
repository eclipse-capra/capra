/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel;

import org.eclipse.app4mc.variability.ample.AlternativeGroup;

import org.eclipse.app4mc.variability.components.VariationPoint;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Alternative Group2 Variation Point</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeGroup2VariationPoint#getAlternativeGroup <em>Alternative Group</em>}</li>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeGroup2VariationPoint#getVariationPoint <em>Variation Point</em>}</li>
 * </ul>
 *
 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getAlternativeGroup2VariationPoint()
 * @model
 * @generated
 */
public interface AlternativeGroup2VariationPoint extends VariabilityTraceLink {
	/**
	 * Returns the value of the '<em><b>Alternative Group</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Alternative Group</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Alternative Group</em>' reference.
	 * @see #setAlternativeGroup(AlternativeGroup)
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getAlternativeGroup2VariationPoint_AlternativeGroup()
	 * @model required="true"
	 * @generated
	 */
	AlternativeGroup getAlternativeGroup();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeGroup2VariationPoint#getAlternativeGroup <em>Alternative Group</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Alternative Group</em>' reference.
	 * @see #getAlternativeGroup()
	 * @generated
	 */
	void setAlternativeGroup(AlternativeGroup value);

	/**
	 * Returns the value of the '<em><b>Variation Point</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Variation Point</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Variation Point</em>' reference.
	 * @see #setVariationPoint(VariationPoint)
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getAlternativeGroup2VariationPoint_VariationPoint()
	 * @model required="true"
	 * @generated
	 */
	VariationPoint getVariationPoint();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeGroup2VariationPoint#getVariationPoint <em>Variation Point</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Variation Point</em>' reference.
	 * @see #getVariationPoint()
	 * @generated
	 */
	void setVariationPoint(VariationPoint value);

} // AlternativeGroup2VariationPoint
