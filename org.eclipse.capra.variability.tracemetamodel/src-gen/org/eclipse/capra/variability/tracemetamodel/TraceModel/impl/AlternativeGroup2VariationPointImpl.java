/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel.impl;

import org.eclipse.app4mc.variability.ample.AlternativeGroup;

import org.eclipse.app4mc.variability.components.VariationPoint;

import org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeGroup2VariationPoint;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Alternative Group2 Variation Point</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.AlternativeGroup2VariationPointImpl#getAlternativeGroup <em>Alternative Group</em>}</li>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.AlternativeGroup2VariationPointImpl#getVariationPoint <em>Variation Point</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AlternativeGroup2VariationPointImpl extends VariabilityTraceLinkImpl implements AlternativeGroup2VariationPoint {
	/**
	 * The cached value of the '{@link #getAlternativeGroup() <em>Alternative Group</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAlternativeGroup()
	 * @generated
	 * @ordered
	 */
	protected AlternativeGroup alternativeGroup;

	/**
	 * The cached value of the '{@link #getVariationPoint() <em>Variation Point</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVariationPoint()
	 * @generated
	 * @ordered
	 */
	protected VariationPoint variationPoint;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AlternativeGroup2VariationPointImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TraceModelPackage.Literals.ALTERNATIVE_GROUP2_VARIATION_POINT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AlternativeGroup getAlternativeGroup() {
		if (alternativeGroup != null && alternativeGroup.eIsProxy()) {
			InternalEObject oldAlternativeGroup = (InternalEObject)alternativeGroup;
			alternativeGroup = (AlternativeGroup)eResolveProxy(oldAlternativeGroup);
			if (alternativeGroup != oldAlternativeGroup) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, TraceModelPackage.ALTERNATIVE_GROUP2_VARIATION_POINT__ALTERNATIVE_GROUP, oldAlternativeGroup, alternativeGroup));
			}
		}
		return alternativeGroup;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AlternativeGroup basicGetAlternativeGroup() {
		return alternativeGroup;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAlternativeGroup(AlternativeGroup newAlternativeGroup) {
		AlternativeGroup oldAlternativeGroup = alternativeGroup;
		alternativeGroup = newAlternativeGroup;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TraceModelPackage.ALTERNATIVE_GROUP2_VARIATION_POINT__ALTERNATIVE_GROUP, oldAlternativeGroup, alternativeGroup));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public VariationPoint getVariationPoint() {
		if (variationPoint != null && variationPoint.eIsProxy()) {
			InternalEObject oldVariationPoint = (InternalEObject)variationPoint;
			variationPoint = (VariationPoint)eResolveProxy(oldVariationPoint);
			if (variationPoint != oldVariationPoint) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, TraceModelPackage.ALTERNATIVE_GROUP2_VARIATION_POINT__VARIATION_POINT, oldVariationPoint, variationPoint));
			}
		}
		return variationPoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public VariationPoint basicGetVariationPoint() {
		return variationPoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVariationPoint(VariationPoint newVariationPoint) {
		VariationPoint oldVariationPoint = variationPoint;
		variationPoint = newVariationPoint;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TraceModelPackage.ALTERNATIVE_GROUP2_VARIATION_POINT__VARIATION_POINT, oldVariationPoint, variationPoint));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case TraceModelPackage.ALTERNATIVE_GROUP2_VARIATION_POINT__ALTERNATIVE_GROUP:
				if (resolve) return getAlternativeGroup();
				return basicGetAlternativeGroup();
			case TraceModelPackage.ALTERNATIVE_GROUP2_VARIATION_POINT__VARIATION_POINT:
				if (resolve) return getVariationPoint();
				return basicGetVariationPoint();
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
			case TraceModelPackage.ALTERNATIVE_GROUP2_VARIATION_POINT__ALTERNATIVE_GROUP:
				setAlternativeGroup((AlternativeGroup)newValue);
				return;
			case TraceModelPackage.ALTERNATIVE_GROUP2_VARIATION_POINT__VARIATION_POINT:
				setVariationPoint((VariationPoint)newValue);
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
			case TraceModelPackage.ALTERNATIVE_GROUP2_VARIATION_POINT__ALTERNATIVE_GROUP:
				setAlternativeGroup((AlternativeGroup)null);
				return;
			case TraceModelPackage.ALTERNATIVE_GROUP2_VARIATION_POINT__VARIATION_POINT:
				setVariationPoint((VariationPoint)null);
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
			case TraceModelPackage.ALTERNATIVE_GROUP2_VARIATION_POINT__ALTERNATIVE_GROUP:
				return alternativeGroup != null;
			case TraceModelPackage.ALTERNATIVE_GROUP2_VARIATION_POINT__VARIATION_POINT:
				return variationPoint != null;
		}
		return super.eIsSet(featureID);
	}

} //AlternativeGroup2VariationPointImpl
