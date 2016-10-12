/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel.impl;

import java.util.Collection;

import org.eclipse.app4mc.variability.ample.Mandatory;

import org.eclipse.app4mc.variability.components.ComponentInstance;

import org.eclipse.capra.variability.tracemetamodel.TraceModel.Mandatory2ComponentInstances;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mandatory2 Component Instances</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.Mandatory2ComponentInstancesImpl#getMandatoryFeature <em>Mandatory Feature</em>}</li>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.Mandatory2ComponentInstancesImpl#getComponents <em>Components</em>}</li>
 * </ul>
 *
 * @generated
 */
public class Mandatory2ComponentInstancesImpl extends VariabilityTraceLinkImpl implements Mandatory2ComponentInstances {
	/**
	 * The cached value of the '{@link #getMandatoryFeature() <em>Mandatory Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMandatoryFeature()
	 * @generated
	 * @ordered
	 */
	protected Mandatory mandatoryFeature;

	/**
	 * The cached value of the '{@link #getComponents() <em>Components</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponents()
	 * @generated
	 * @ordered
	 */
	protected EList<ComponentInstance> components;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Mandatory2ComponentInstancesImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TraceModelPackage.Literals.MANDATORY2_COMPONENT_INSTANCES;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Mandatory getMandatoryFeature() {
		if (mandatoryFeature != null && mandatoryFeature.eIsProxy()) {
			InternalEObject oldMandatoryFeature = (InternalEObject)mandatoryFeature;
			mandatoryFeature = (Mandatory)eResolveProxy(oldMandatoryFeature);
			if (mandatoryFeature != oldMandatoryFeature) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, TraceModelPackage.MANDATORY2_COMPONENT_INSTANCES__MANDATORY_FEATURE, oldMandatoryFeature, mandatoryFeature));
			}
		}
		return mandatoryFeature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Mandatory basicGetMandatoryFeature() {
		return mandatoryFeature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMandatoryFeature(Mandatory newMandatoryFeature) {
		Mandatory oldMandatoryFeature = mandatoryFeature;
		mandatoryFeature = newMandatoryFeature;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TraceModelPackage.MANDATORY2_COMPONENT_INSTANCES__MANDATORY_FEATURE, oldMandatoryFeature, mandatoryFeature));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ComponentInstance> getComponents() {
		if (components == null) {
			components = new EObjectResolvingEList<ComponentInstance>(ComponentInstance.class, this, TraceModelPackage.MANDATORY2_COMPONENT_INSTANCES__COMPONENTS);
		}
		return components;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case TraceModelPackage.MANDATORY2_COMPONENT_INSTANCES__MANDATORY_FEATURE:
				if (resolve) return getMandatoryFeature();
				return basicGetMandatoryFeature();
			case TraceModelPackage.MANDATORY2_COMPONENT_INSTANCES__COMPONENTS:
				return getComponents();
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
			case TraceModelPackage.MANDATORY2_COMPONENT_INSTANCES__MANDATORY_FEATURE:
				setMandatoryFeature((Mandatory)newValue);
				return;
			case TraceModelPackage.MANDATORY2_COMPONENT_INSTANCES__COMPONENTS:
				getComponents().clear();
				getComponents().addAll((Collection<? extends ComponentInstance>)newValue);
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
			case TraceModelPackage.MANDATORY2_COMPONENT_INSTANCES__MANDATORY_FEATURE:
				setMandatoryFeature((Mandatory)null);
				return;
			case TraceModelPackage.MANDATORY2_COMPONENT_INSTANCES__COMPONENTS:
				getComponents().clear();
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
			case TraceModelPackage.MANDATORY2_COMPONENT_INSTANCES__MANDATORY_FEATURE:
				return mandatoryFeature != null;
			case TraceModelPackage.MANDATORY2_COMPONENT_INSTANCES__COMPONENTS:
				return components != null && !components.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //Mandatory2ComponentInstancesImpl
