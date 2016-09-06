/**
 */
package org.eclipse.capra.GenericArtifactMetaModel.impl;

import java.util.Collection;

import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapper;
import org.eclipse.capra.GenericArtifactMetaModel.ArtifactWrapperContainer;
import org.eclipse.capra.GenericArtifactMetaModel.GenericArtifactMetaModelPackage;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Artifact Wrapper Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.GenericArtifactMetaModel.impl.ArtifactWrapperContainerImpl#getArtifacts <em>Artifacts</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ArtifactWrapperContainerImpl extends MinimalEObjectImpl.Container implements ArtifactWrapperContainer {
	/**
	 * The cached value of the '{@link #getArtifacts() <em>Artifacts</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getArtifacts()
	 * @generated
	 * @ordered
	 */
	protected EList<ArtifactWrapper> artifacts;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ArtifactWrapperContainerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GenericArtifactMetaModelPackage.Literals.ARTIFACT_WRAPPER_CONTAINER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ArtifactWrapper> getArtifacts() {
		if (artifacts == null) {
			artifacts = new EObjectContainmentEList<ArtifactWrapper>(ArtifactWrapper.class, this, GenericArtifactMetaModelPackage.ARTIFACT_WRAPPER_CONTAINER__ARTIFACTS);
		}
		return artifacts;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case GenericArtifactMetaModelPackage.ARTIFACT_WRAPPER_CONTAINER__ARTIFACTS:
				return ((InternalEList<?>)getArtifacts()).basicRemove(otherEnd, msgs);
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
			case GenericArtifactMetaModelPackage.ARTIFACT_WRAPPER_CONTAINER__ARTIFACTS:
				return getArtifacts();
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
			case GenericArtifactMetaModelPackage.ARTIFACT_WRAPPER_CONTAINER__ARTIFACTS:
				getArtifacts().clear();
				getArtifacts().addAll((Collection<? extends ArtifactWrapper>)newValue);
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
			case GenericArtifactMetaModelPackage.ARTIFACT_WRAPPER_CONTAINER__ARTIFACTS:
				getArtifacts().clear();
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
			case GenericArtifactMetaModelPackage.ARTIFACT_WRAPPER_CONTAINER__ARTIFACTS:
				return artifacts != null && !artifacts.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ArtifactWrapperContainerImpl
