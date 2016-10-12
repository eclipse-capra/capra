/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel.impl;

import org.eclipse.app4mc.variability.ample.Alternative;

import org.eclipse.app4mc.variability.components.Variant;

import org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeFeature2Variant;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Alternative Feature2 Variant</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.AlternativeFeature2VariantImpl#getFeature <em>Feature</em>}</li>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.AlternativeFeature2VariantImpl#getVariant <em>Variant</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AlternativeFeature2VariantImpl extends VariabilityTraceLinkImpl implements AlternativeFeature2Variant {
	/**
	 * The cached value of the '{@link #getFeature() <em>Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeature()
	 * @generated
	 * @ordered
	 */
	protected Alternative feature;

	/**
	 * The cached value of the '{@link #getVariant() <em>Variant</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVariant()
	 * @generated
	 * @ordered
	 */
	protected Variant variant;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AlternativeFeature2VariantImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TraceModelPackage.Literals.ALTERNATIVE_FEATURE2_VARIANT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Alternative getFeature() {
		if (feature != null && feature.eIsProxy()) {
			InternalEObject oldFeature = (InternalEObject)feature;
			feature = (Alternative)eResolveProxy(oldFeature);
			if (feature != oldFeature) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, TraceModelPackage.ALTERNATIVE_FEATURE2_VARIANT__FEATURE, oldFeature, feature));
			}
		}
		return feature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Alternative basicGetFeature() {
		return feature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFeature(Alternative newFeature) {
		Alternative oldFeature = feature;
		feature = newFeature;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TraceModelPackage.ALTERNATIVE_FEATURE2_VARIANT__FEATURE, oldFeature, feature));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Variant getVariant() {
		if (variant != null && variant.eIsProxy()) {
			InternalEObject oldVariant = (InternalEObject)variant;
			variant = (Variant)eResolveProxy(oldVariant);
			if (variant != oldVariant) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, TraceModelPackage.ALTERNATIVE_FEATURE2_VARIANT__VARIANT, oldVariant, variant));
			}
		}
		return variant;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Variant basicGetVariant() {
		return variant;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVariant(Variant newVariant) {
		Variant oldVariant = variant;
		variant = newVariant;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TraceModelPackage.ALTERNATIVE_FEATURE2_VARIANT__VARIANT, oldVariant, variant));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case TraceModelPackage.ALTERNATIVE_FEATURE2_VARIANT__FEATURE:
				if (resolve) return getFeature();
				return basicGetFeature();
			case TraceModelPackage.ALTERNATIVE_FEATURE2_VARIANT__VARIANT:
				if (resolve) return getVariant();
				return basicGetVariant();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case TraceModelPackage.ALTERNATIVE_FEATURE2_VARIANT__FEATURE:
				setFeature((Alternative)newValue);
				return;
			case TraceModelPackage.ALTERNATIVE_FEATURE2_VARIANT__VARIANT:
				setVariant((Variant)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case TraceModelPackage.ALTERNATIVE_FEATURE2_VARIANT__FEATURE:
				setFeature((Alternative)null);
				return;
			case TraceModelPackage.ALTERNATIVE_FEATURE2_VARIANT__VARIANT:
				setVariant((Variant)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case TraceModelPackage.ALTERNATIVE_FEATURE2_VARIANT__FEATURE:
				return feature != null;
			case TraceModelPackage.ALTERNATIVE_FEATURE2_VARIANT__VARIANT:
				return variant != null;
		}
		return super.eIsSet(featureID);
	}

} //AlternativeFeature2VariantImpl
