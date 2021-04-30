/**
 */
package org.eclipse.capra.generic.metadatamodel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.capra.generic.metadatamodel.MetadatamodelPackage
 * @generated
 */
public interface MetadatamodelFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MetadatamodelFactory eINSTANCE = org.eclipse.capra.generic.metadatamodel.impl.MetadatamodelFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Metadata Container</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Metadata Container</em>'.
	 * @generated
	 */
	MetadataContainer createMetadataContainer();

	/**
	 * Returns a new object of class '<em>Person</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Person</em>'.
	 * @generated
	 */
	Person createPerson();

	/**
	 * Returns a new object of class '<em>Trace Metadata</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Trace Metadata</em>'.
	 * @generated
	 */
	TraceMetadata createTraceMetadata();

	/**
	 * Returns a new object of class '<em>Artifact Metadata</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Artifact Metadata</em>'.
	 * @generated
	 */
	ArtifactMetadata createArtifactMetadata();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	MetadatamodelPackage getMetadatamodelPackage();

} //MetadatamodelFactory
