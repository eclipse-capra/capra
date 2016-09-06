/**
 */
package org.eclipse.capra.GenericArtifactMetaModel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.capra.GenericArtifactMetaModel.GenericArtifactMetaModelPackage
 * @generated
 */
public interface GenericArtifactMetaModelFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	GenericArtifactMetaModelFactory eINSTANCE = org.eclipse.capra.GenericArtifactMetaModel.impl.GenericArtifactMetaModelFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Artifact Wrapper Container</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Artifact Wrapper Container</em>'.
	 * @generated
	 */
	ArtifactWrapperContainer createArtifactWrapperContainer();

	/**
	 * Returns a new object of class '<em>Artifact Wrapper</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Artifact Wrapper</em>'.
	 * @generated
	 */
	ArtifactWrapper createArtifactWrapper();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	GenericArtifactMetaModelPackage getGenericArtifactMetaModelPackage();

} //GenericArtifactMetaModelFactory
