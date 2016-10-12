/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel.impl;

import java.util.Collection;

import org.eclipse.app4mc.variability.ample.Optional;
import org.eclipse.app4mc.variability.ample.Or;

import org.eclipse.app4mc.variability.components.ComponentInstance;

import org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Optional2 Component Instances</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.Optional2ComponentInstancesImpl#getOptionalFeature <em>Optional Feature</em>}</li>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.Optional2ComponentInstancesImpl#getOrFeature <em>Or Feature</em>}</li>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.Optional2ComponentInstancesImpl#getComponents <em>Components</em>}</li>
 * </ul>
 *
 * @generated
 */
public class Optional2ComponentInstancesImpl extends VariabilityTraceLinkImpl implements Optional2ComponentInstances {
	/**
	 * The cached value of the '{@link #getOptionalFeature() <em>Optional Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOptionalFeature()
	 * @generated
	 * @ordered
	 */
	protected Optional optionalFeature;

	/**
	 * The cached value of the '{@link #getOrFeature() <em>Or Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOrFeature()
	 * @generated
	 * @ordered
	 */
	protected Or orFeature;

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
	protected Optional2ComponentInstancesImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TraceModelPackage.Literals.OPTIONAL2_COMPONENT_INSTANCES;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Optional getOptionalFeature() {
		if (optionalFeature != null && optionalFeature.eIsProxy()) {
			InternalEObject oldOptionalFeature = (InternalEObject)optionalFeature;
			optionalFeature = (Optional)eResolveProxy(oldOptionalFeature);
			if (optionalFeature != oldOptionalFeature) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__OPTIONAL_FEATURE, oldOptionalFeature, optionalFeature));
			}
		}
		return optionalFeature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Optional basicGetOptionalFeature() {
		return optionalFeature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOptionalFeature(Optional newOptionalFeature) {
		Optional oldOptionalFeature = optionalFeature;
		optionalFeature = newOptionalFeature;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__OPTIONAL_FEATURE, oldOptionalFeature, optionalFeature));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Or getOrFeature() {
		if (orFeature != null && orFeature.eIsProxy()) {
			InternalEObject oldOrFeature = (InternalEObject)orFeature;
			orFeature = (Or)eResolveProxy(oldOrFeature);
			if (orFeature != oldOrFeature) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__OR_FEATURE, oldOrFeature, orFeature));
			}
		}
		return orFeature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Or basicGetOrFeature() {
		return orFeature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOrFeature(Or newOrFeature) {
		Or oldOrFeature = orFeature;
		orFeature = newOrFeature;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__OR_FEATURE, oldOrFeature, orFeature));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ComponentInstance> getComponents() {
		if (components == null) {
			components = new EObjectResolvingEList<ComponentInstance>(ComponentInstance.class, this, TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__COMPONENTS);
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
			case TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__OPTIONAL_FEATURE:
				if (resolve) return getOptionalFeature();
				return basicGetOptionalFeature();
			case TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__OR_FEATURE:
				if (resolve) return getOrFeature();
				return basicGetOrFeature();
			case TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__COMPONENTS:
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
			case TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__OPTIONAL_FEATURE:
				setOptionalFeature((Optional)newValue);
				return;
			case TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__OR_FEATURE:
				setOrFeature((Or)newValue);
				return;
			case TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__COMPONENTS:
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
			case TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__OPTIONAL_FEATURE:
				setOptionalFeature((Optional)null);
				return;
			case TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__OR_FEATURE:
				setOrFeature((Or)null);
				return;
			case TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__COMPONENTS:
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
			case TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__OPTIONAL_FEATURE:
				return optionalFeature != null;
			case TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__OR_FEATURE:
				return orFeature != null;
			case TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES__COMPONENTS:
				return components != null && !components.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //Optional2ComponentInstancesImpl
