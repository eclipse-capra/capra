/**
 */
package org.eclipse.capra.generic.metadatamodel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Metadata Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.generic.metadatamodel.MetadataContainer#getTraceMetadata <em>Trace Metadata</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.metadatamodel.MetadataContainer#getArtifactMetadata <em>Artifact Metadata</em>}</li>
 * </ul>
 *
 * @see org.eclipse.capra.generic.metadatamodel.MetadatamodelPackage#getMetadataContainer()
 * @model
 * @generated
 */
public interface MetadataContainer extends EObject {
	/**
	 * Returns the value of the '<em><b>Trace Metadata</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.capra.generic.metadatamodel.TraceMetadata}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Trace Metadata</em>' containment reference list.
	 * @see org.eclipse.capra.generic.metadatamodel.MetadatamodelPackage#getMetadataContainer_TraceMetadata()
	 * @model containment="true"
	 * @generated
	 */
	EList<TraceMetadata> getTraceMetadata();

	/**
	 * Returns the value of the '<em><b>Artifact Metadata</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.capra.generic.metadatamodel.ArtifactMetadata}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Artifact Metadata</em>' containment reference list.
	 * @see org.eclipse.capra.generic.metadatamodel.MetadatamodelPackage#getMetadataContainer_ArtifactMetadata()
	 * @model containment="true"
	 * @generated
	 */
	EList<ArtifactMetadata> getArtifactMetadata();

} // MetadataContainer
