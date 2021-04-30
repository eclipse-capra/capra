/**
 */
package org.eclipse.capra.generic.metadatamodel.impl;

import org.eclipse.capra.generic.metadatamodel.ArtifactMetadata;
import org.eclipse.capra.generic.metadatamodel.MetadatamodelPackage;
import org.eclipse.capra.generic.metadatamodel.Person;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Artifact Metadata</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.generic.metadatamodel.impl.ArtifactMetadataImpl#getResponsibleUser <em>Responsible User</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.metadatamodel.impl.ArtifactMetadataImpl#getArtifact <em>Artifact</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ArtifactMetadataImpl extends MinimalEObjectImpl.Container implements ArtifactMetadata {
	/**
	 * The cached value of the '{@link #getResponsibleUser() <em>Responsible User</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResponsibleUser()
	 * @generated
	 * @ordered
	 */
	protected Person responsibleUser;

	/**
	 * The cached value of the '{@link #getArtifact() <em>Artifact</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getArtifact()
	 * @generated
	 * @ordered
	 */
	protected EObject artifact;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ArtifactMetadataImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MetadatamodelPackage.Literals.ARTIFACT_METADATA;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Person getResponsibleUser() {
		return responsibleUser;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetResponsibleUser(Person newResponsibleUser, NotificationChain msgs) {
		Person oldResponsibleUser = responsibleUser;
		responsibleUser = newResponsibleUser;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MetadatamodelPackage.ARTIFACT_METADATA__RESPONSIBLE_USER, oldResponsibleUser, newResponsibleUser);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setResponsibleUser(Person newResponsibleUser) {
		if (newResponsibleUser != responsibleUser) {
			NotificationChain msgs = null;
			if (responsibleUser != null)
				msgs = ((InternalEObject)responsibleUser).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MetadatamodelPackage.ARTIFACT_METADATA__RESPONSIBLE_USER, null, msgs);
			if (newResponsibleUser != null)
				msgs = ((InternalEObject)newResponsibleUser).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MetadatamodelPackage.ARTIFACT_METADATA__RESPONSIBLE_USER, null, msgs);
			msgs = basicSetResponsibleUser(newResponsibleUser, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadatamodelPackage.ARTIFACT_METADATA__RESPONSIBLE_USER, newResponsibleUser, newResponsibleUser));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EObject getArtifact() {
		if (artifact != null && artifact.eIsProxy()) {
			InternalEObject oldArtifact = (InternalEObject)artifact;
			artifact = eResolveProxy(oldArtifact);
			if (artifact != oldArtifact) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MetadatamodelPackage.ARTIFACT_METADATA__ARTIFACT, oldArtifact, artifact));
			}
		}
		return artifact;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EObject basicGetArtifact() {
		return artifact;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setArtifact(EObject newArtifact) {
		EObject oldArtifact = artifact;
		artifact = newArtifact;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadatamodelPackage.ARTIFACT_METADATA__ARTIFACT, oldArtifact, artifact));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MetadatamodelPackage.ARTIFACT_METADATA__RESPONSIBLE_USER:
				return basicSetResponsibleUser(null, msgs);
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
			case MetadatamodelPackage.ARTIFACT_METADATA__RESPONSIBLE_USER:
				return getResponsibleUser();
			case MetadatamodelPackage.ARTIFACT_METADATA__ARTIFACT:
				if (resolve) return getArtifact();
				return basicGetArtifact();
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
			case MetadatamodelPackage.ARTIFACT_METADATA__RESPONSIBLE_USER:
				setResponsibleUser((Person)newValue);
				return;
			case MetadatamodelPackage.ARTIFACT_METADATA__ARTIFACT:
				setArtifact((EObject)newValue);
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
			case MetadatamodelPackage.ARTIFACT_METADATA__RESPONSIBLE_USER:
				setResponsibleUser((Person)null);
				return;
			case MetadatamodelPackage.ARTIFACT_METADATA__ARTIFACT:
				setArtifact((EObject)null);
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
			case MetadatamodelPackage.ARTIFACT_METADATA__RESPONSIBLE_USER:
				return responsibleUser != null;
			case MetadatamodelPackage.ARTIFACT_METADATA__ARTIFACT:
				return artifact != null;
		}
		return super.eIsSet(featureID);
	}

} //ArtifactMetadataImpl
