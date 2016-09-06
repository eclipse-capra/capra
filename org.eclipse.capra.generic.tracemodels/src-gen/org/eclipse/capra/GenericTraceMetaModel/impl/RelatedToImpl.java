/**
 */
package org.eclipse.capra.GenericTraceMetaModel.impl;

import java.util.Collection;

import org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage;
import org.eclipse.capra.GenericTraceMetaModel.RelatedTo;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Related To</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.GenericTraceMetaModel.impl.RelatedToImpl#getItem <em>Item</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RelatedToImpl extends MinimalEObjectImpl.Container implements RelatedTo {
	/**
	 * The cached value of the '{@link #getItem() <em>Item</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getItem()
	 * @generated
	 * @ordered
	 */
	protected EList<EObject> item;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RelatedToImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GenericTraceMetaModelPackage.Literals.RELATED_TO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EObject> getItem() {
		if (item == null) {
			item = new EObjectResolvingEList<EObject>(EObject.class, this, GenericTraceMetaModelPackage.RELATED_TO__ITEM);
		}
		return item;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GenericTraceMetaModelPackage.RELATED_TO__ITEM:
				return getItem();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case GenericTraceMetaModelPackage.RELATED_TO__ITEM:
				getItem().clear();
				getItem().addAll((Collection<? extends EObject>)newValue);
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
			case GenericTraceMetaModelPackage.RELATED_TO__ITEM:
				getItem().clear();
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
			case GenericTraceMetaModelPackage.RELATED_TO__ITEM:
				return item != null && !item.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //RelatedToImpl
