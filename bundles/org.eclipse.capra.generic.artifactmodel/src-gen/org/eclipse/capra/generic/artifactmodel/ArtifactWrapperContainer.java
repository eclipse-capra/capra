/**
 */
package org.eclipse.capra.generic.artifactmodel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Artifact Wrapper Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapperContainer#getArtifacts <em>Artifacts</em>}</li>
 * </ul>
 *
 * @see org.eclipse.capra.generic.artifactmodel.ArtifactmodelPackage#getArtifactWrapperContainer()
 * @model
 * @generated
 */
public interface ArtifactWrapperContainer extends EObject {
	/**
	 * Returns the value of the '<em><b>Artifacts</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Artifacts</em>' containment reference list.
	 * @see org.eclipse.capra.generic.artifactmodel.ArtifactmodelPackage#getArtifactWrapperContainer_Artifacts()
	 * @model containment="true"
	 * @generated
	 */
	EList<ArtifactWrapper> getArtifacts();

} // ArtifactWrapperContainer
