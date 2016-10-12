/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel;

import org.eclipse.app4mc.variability.ample.Alternative;

import org.eclipse.app4mc.variability.components.Variant;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Alternative Feature2 Variant</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeFeature2Variant#getFeature <em>Feature</em>}</li>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeFeature2Variant#getVariant <em>Variant</em>}</li>
 * </ul>
 *
 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getAlternativeFeature2Variant()
 * @model
 * @generated
 */
public interface AlternativeFeature2Variant extends VariabilityTraceLink {
	/**
	 * Returns the value of the '<em><b>Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Feature</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Feature</em>' reference.
	 * @see #setFeature(Alternative)
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getAlternativeFeature2Variant_Feature()
	 * @model required="true"
	 * @generated
	 */
	Alternative getFeature();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeFeature2Variant#getFeature <em>Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Feature</em>' reference.
	 * @see #getFeature()
	 * @generated
	 */
	void setFeature(Alternative value);

	/**
	 * Returns the value of the '<em><b>Variant</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Variant</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Variant</em>' reference.
	 * @see #setVariant(Variant)
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#getAlternativeFeature2Variant_Variant()
	 * @model required="true"
	 * @generated
	 */
	Variant getVariant();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeFeature2Variant#getVariant <em>Variant</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Variant</em>' reference.
	 * @see #getVariant()
	 * @generated
	 */
	void setVariant(Variant value);

} // AlternativeFeature2Variant
