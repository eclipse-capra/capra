/**
 */
package org.eclipse.capra.GenericTraceMetaModel.impl;

import java.util.Collection;

import org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage;
import org.eclipse.capra.GenericTraceMetaModel.RelatedTo;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Related To</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.GenericTraceMetaModel.impl.RelatedToImpl#getID <em>ID</em>}</li>
 *   <li>{@link org.eclipse.capra.GenericTraceMetaModel.impl.RelatedToImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.capra.GenericTraceMetaModel.impl.RelatedToImpl#getItem <em>Item</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RelatedToImpl extends MinimalEObjectImpl.Container implements RelatedTo {
	/**
	 * The default value of the '{@link #getID() <em>ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getID()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

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
	public String getID() {
		return EcoreUtil.generateUUID();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GenericTraceMetaModelPackage.RELATED_TO__NAME, oldName, name));
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
			case GenericTraceMetaModelPackage.RELATED_TO__ID:
				return getID();
			case GenericTraceMetaModelPackage.RELATED_TO__NAME:
				return getName();
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
			case GenericTraceMetaModelPackage.RELATED_TO__NAME:
				setName((String)newValue);
				return;
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
			case GenericTraceMetaModelPackage.RELATED_TO__NAME:
				setName(NAME_EDEFAULT);
				return;
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
			case GenericTraceMetaModelPackage.RELATED_TO__ID:
				return ID_EDEFAULT == null ? getID() != null : !ID_EDEFAULT.equals(getID());
			case GenericTraceMetaModelPackage.RELATED_TO__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case GenericTraceMetaModelPackage.RELATED_TO__ITEM:
				return item != null && !item.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //RelatedToImpl
