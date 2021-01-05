/**
 */
package org.eclipse.capra.generic.artifactmodel;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Artifact Wrapper</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getPath <em>Path</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getUri <em>Uri</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getIdentifier <em>Identifier</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getArtifactHandler <em>Artifact Handler</em>}</li>
 * </ul>
 *
 * @see org.eclipse.capra.generic.artifactmodel.ArtifactmodelPackage#getArtifactWrapper()
 * @model
 * @generated
 */
public interface ArtifactWrapper extends EObject {
	/**
	 * Returns the value of the '<em><b>Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Path</em>' attribute.
	 * @see #setPath(String)
	 * @see org.eclipse.capra.generic.artifactmodel.ArtifactmodelPackage#getArtifactWrapper_Path()
	 * @model unique="false"
	 * @generated
	 */
	String getPath();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getPath <em>Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Path</em>' attribute.
	 * @see #getPath()
	 * @generated
	 */
	void setPath(String value);

	/**
	 * Returns the value of the '<em><b>Uri</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Uri</em>' attribute.
	 * @see #setUri(String)
	 * @see org.eclipse.capra.generic.artifactmodel.ArtifactmodelPackage#getArtifactWrapper_Uri()
	 * @model unique="false"
	 * @generated
	 */
	String getUri();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getUri <em>Uri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Uri</em>' attribute.
	 * @see #getUri()
	 * @generated
	 */
	void setUri(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.capra.generic.artifactmodel.ArtifactmodelPackage#getArtifactWrapper_Name()
	 * @model unique="false"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Identifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Identifier</em>' attribute.
	 * @see #setIdentifier(String)
	 * @see org.eclipse.capra.generic.artifactmodel.ArtifactmodelPackage#getArtifactWrapper_Identifier()
	 * @model unique="false"
	 * @generated
	 */
	String getIdentifier();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getIdentifier <em>Identifier</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Identifier</em>' attribute.
	 * @see #getIdentifier()
	 * @generated
	 */
	void setIdentifier(String value);

	/**
	 * Returns the value of the '<em><b>Artifact Handler</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Artifact Handler</em>' attribute.
	 * @see #setArtifactHandler(String)
	 * @see org.eclipse.capra.generic.artifactmodel.ArtifactmodelPackage#getArtifactWrapper_ArtifactHandler()
	 * @model unique="false"
	 * @generated
	 */
	String getArtifactHandler();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getArtifactHandler <em>Artifact Handler</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Artifact Handler</em>' attribute.
	 * @see #getArtifactHandler()
	 * @generated
	 */
	void setArtifactHandler(String value);

} // ArtifactWrapper
