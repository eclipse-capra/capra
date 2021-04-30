/**
 */
package org.eclipse.capra.generic.metadatamodel;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Artifact Metadata</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Default metadata for an artifact.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.generic.metadatamodel.ArtifactMetadata#getResponsibleUser <em>Responsible User</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.metadatamodel.ArtifactMetadata#getArtifact <em>Artifact</em>}</li>
 * </ul>
 *
 * @see org.eclipse.capra.generic.metadatamodel.MetadatamodelPackage#getArtifactMetadata()
 * @model
 * @generated
 */
public interface ArtifactMetadata extends EObject {
	/**
	 * Returns the value of the '<em><b>Responsible User</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Responsible User</em>' containment reference.
	 * @see #setResponsibleUser(Person)
	 * @see org.eclipse.capra.generic.metadatamodel.MetadatamodelPackage#getArtifactMetadata_ResponsibleUser()
	 * @model containment="true"
	 * @generated
	 */
	Person getResponsibleUser();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.generic.metadatamodel.ArtifactMetadata#getResponsibleUser <em>Responsible User</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Responsible User</em>' containment reference.
	 * @see #getResponsibleUser()
	 * @generated
	 */
	void setResponsibleUser(Person value);

	/**
	 * Returns the value of the '<em><b>Artifact</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Artifact</em>' reference.
	 * @see #setArtifact(EObject)
	 * @see org.eclipse.capra.generic.metadatamodel.MetadatamodelPackage#getArtifactMetadata_Artifact()
	 * @model
	 * @generated
	 */
	EObject getArtifact();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.generic.metadatamodel.ArtifactMetadata#getArtifact <em>Artifact</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Artifact</em>' reference.
	 * @see #getArtifact()
	 * @generated
	 */
	void setArtifact(EObject value);

} // ArtifactMetadata
