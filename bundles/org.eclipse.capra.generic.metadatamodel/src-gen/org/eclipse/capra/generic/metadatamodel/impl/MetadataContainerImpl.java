/**
 */
package org.eclipse.capra.generic.metadatamodel.impl;

import java.util.Collection;

import org.eclipse.capra.generic.metadatamodel.ArtifactMetadata;
import org.eclipse.capra.generic.metadatamodel.MetadataContainer;
import org.eclipse.capra.generic.metadatamodel.MetadatamodelPackage;
import org.eclipse.capra.generic.metadatamodel.TraceMetadata;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Metadata Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.generic.metadatamodel.impl.MetadataContainerImpl#getTraceMetadata <em>Trace Metadata</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.metadatamodel.impl.MetadataContainerImpl#getArtifactMetadata <em>Artifact Metadata</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MetadataContainerImpl extends MinimalEObjectImpl.Container implements MetadataContainer {
	/**
	 * The cached value of the '{@link #getTraceMetadata() <em>Trace Metadata</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTraceMetadata()
	 * @generated
	 * @ordered
	 */
	protected EList<TraceMetadata> traceMetadata;

	/**
	 * The cached value of the '{@link #getArtifactMetadata() <em>Artifact Metadata</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getArtifactMetadata()
	 * @generated
	 * @ordered
	 */
	protected EList<ArtifactMetadata> artifactMetadata;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MetadataContainerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MetadatamodelPackage.Literals.METADATA_CONTAINER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<TraceMetadata> getTraceMetadata() {
		if (traceMetadata == null) {
			traceMetadata = new EObjectContainmentEList<TraceMetadata>(TraceMetadata.class, this, MetadatamodelPackage.METADATA_CONTAINER__TRACE_METADATA);
		}
		return traceMetadata;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ArtifactMetadata> getArtifactMetadata() {
		if (artifactMetadata == null) {
			artifactMetadata = new EObjectContainmentEList<ArtifactMetadata>(ArtifactMetadata.class, this, MetadatamodelPackage.METADATA_CONTAINER__ARTIFACT_METADATA);
		}
		return artifactMetadata;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MetadatamodelPackage.METADATA_CONTAINER__TRACE_METADATA:
				return ((InternalEList<?>)getTraceMetadata()).basicRemove(otherEnd, msgs);
			case MetadatamodelPackage.METADATA_CONTAINER__ARTIFACT_METADATA:
				return ((InternalEList<?>)getArtifactMetadata()).basicRemove(otherEnd, msgs);
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
			case MetadatamodelPackage.METADATA_CONTAINER__TRACE_METADATA:
				return getTraceMetadata();
			case MetadatamodelPackage.METADATA_CONTAINER__ARTIFACT_METADATA:
				return getArtifactMetadata();
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
			case MetadatamodelPackage.METADATA_CONTAINER__TRACE_METADATA:
				getTraceMetadata().clear();
				getTraceMetadata().addAll((Collection<? extends TraceMetadata>)newValue);
				return;
			case MetadatamodelPackage.METADATA_CONTAINER__ARTIFACT_METADATA:
				getArtifactMetadata().clear();
				getArtifactMetadata().addAll((Collection<? extends ArtifactMetadata>)newValue);
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
			case MetadatamodelPackage.METADATA_CONTAINER__TRACE_METADATA:
				getTraceMetadata().clear();
				return;
			case MetadatamodelPackage.METADATA_CONTAINER__ARTIFACT_METADATA:
				getArtifactMetadata().clear();
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
			case MetadatamodelPackage.METADATA_CONTAINER__TRACE_METADATA:
				return traceMetadata != null && !traceMetadata.isEmpty();
			case MetadatamodelPackage.METADATA_CONTAINER__ARTIFACT_METADATA:
				return artifactMetadata != null && !artifactMetadata.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //MetadataContainerImpl
