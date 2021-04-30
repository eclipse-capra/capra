/**
 */
package org.eclipse.capra.generic.metadatamodel.impl;

import org.eclipse.capra.generic.metadatamodel.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MetadatamodelFactoryImpl extends EFactoryImpl implements MetadatamodelFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static MetadatamodelFactory init() {
		try {
			MetadatamodelFactory theMetadatamodelFactory = (MetadatamodelFactory)EPackage.Registry.INSTANCE.getEFactory(MetadatamodelPackage.eNS_URI);
			if (theMetadatamodelFactory != null) {
				return theMetadatamodelFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new MetadatamodelFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MetadatamodelFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case MetadatamodelPackage.METADATA_CONTAINER: return createMetadataContainer();
			case MetadatamodelPackage.PERSON: return createPerson();
			case MetadatamodelPackage.TRACE_METADATA: return createTraceMetadata();
			case MetadatamodelPackage.ARTIFACT_METADATA: return createArtifactMetadata();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MetadataContainer createMetadataContainer() {
		MetadataContainerImpl metadataContainer = new MetadataContainerImpl();
		return metadataContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Person createPerson() {
		PersonImpl person = new PersonImpl();
		return person;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TraceMetadata createTraceMetadata() {
		TraceMetadataImpl traceMetadata = new TraceMetadataImpl();
		return traceMetadata;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ArtifactMetadata createArtifactMetadata() {
		ArtifactMetadataImpl artifactMetadata = new ArtifactMetadataImpl();
		return artifactMetadata;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MetadatamodelPackage getMetadatamodelPackage() {
		return (MetadatamodelPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static MetadatamodelPackage getPackage() {
		return MetadatamodelPackage.eINSTANCE;
	}

} //MetadatamodelFactoryImpl
