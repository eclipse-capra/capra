/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel.impl;

import java.util.Collection;

import org.eclipse.capra.variability.tracemetamodel.TraceModel.GenericTraceLink;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModel;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.VariabilityTraceLink;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Trace Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelImpl#getGenericTraceLinks <em>Generic Trace Links</em>}</li>
 *   <li>{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelImpl#getVariableTraceLinks <em>Variable Trace Links</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TraceModelImpl extends MinimalEObjectImpl.Container implements TraceModel {
	/**
	 * The cached value of the '{@link #getGenericTraceLinks() <em>Generic Trace Links</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGenericTraceLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<GenericTraceLink> genericTraceLinks;

	/**
	 * The cached value of the '{@link #getVariableTraceLinks() <em>Variable Trace Links</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVariableTraceLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<VariabilityTraceLink> variableTraceLinks;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TraceModelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TraceModelPackage.Literals.TRACE_MODEL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<GenericTraceLink> getGenericTraceLinks() {
		if (genericTraceLinks == null) {
			genericTraceLinks = new EObjectContainmentEList<GenericTraceLink>(GenericTraceLink.class, this, TraceModelPackage.TRACE_MODEL__GENERIC_TRACE_LINKS);
		}
		return genericTraceLinks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<VariabilityTraceLink> getVariableTraceLinks() {
		if (variableTraceLinks == null) {
			variableTraceLinks = new EObjectContainmentEList<VariabilityTraceLink>(VariabilityTraceLink.class, this, TraceModelPackage.TRACE_MODEL__VARIABLE_TRACE_LINKS);
		}
		return variableTraceLinks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case TraceModelPackage.TRACE_MODEL__GENERIC_TRACE_LINKS:
				return ((InternalEList<?>)getGenericTraceLinks()).basicRemove(otherEnd, msgs);
			case TraceModelPackage.TRACE_MODEL__VARIABLE_TRACE_LINKS:
				return ((InternalEList<?>)getVariableTraceLinks()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case TraceModelPackage.TRACE_MODEL__GENERIC_TRACE_LINKS:
				return getGenericTraceLinks();
			case TraceModelPackage.TRACE_MODEL__VARIABLE_TRACE_LINKS:
				return getVariableTraceLinks();
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
			case TraceModelPackage.TRACE_MODEL__GENERIC_TRACE_LINKS:
				getGenericTraceLinks().clear();
				getGenericTraceLinks().addAll((Collection<? extends GenericTraceLink>)newValue);
				return;
			case TraceModelPackage.TRACE_MODEL__VARIABLE_TRACE_LINKS:
				getVariableTraceLinks().clear();
				getVariableTraceLinks().addAll((Collection<? extends VariabilityTraceLink>)newValue);
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
			case TraceModelPackage.TRACE_MODEL__GENERIC_TRACE_LINKS:
				getGenericTraceLinks().clear();
				return;
			case TraceModelPackage.TRACE_MODEL__VARIABLE_TRACE_LINKS:
				getVariableTraceLinks().clear();
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
			case TraceModelPackage.TRACE_MODEL__GENERIC_TRACE_LINKS:
				return genericTraceLinks != null && !genericTraceLinks.isEmpty();
			case TraceModelPackage.TRACE_MODEL__VARIABLE_TRACE_LINKS:
				return variableTraceLinks != null && !variableTraceLinks.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //TraceModelImpl
