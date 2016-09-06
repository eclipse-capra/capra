/**
 */
package org.eclipse.capra.GenericArtifactMetaModel.impl;

import org.eclipse.capra.GenericArtifactMetaModel.*;

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
public class GenericArtifactMetaModelFactoryImpl extends EFactoryImpl implements GenericArtifactMetaModelFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static GenericArtifactMetaModelFactory init() {
		try {
			GenericArtifactMetaModelFactory theGenericArtifactMetaModelFactory = (GenericArtifactMetaModelFactory)EPackage.Registry.INSTANCE.getEFactory(GenericArtifactMetaModelPackage.eNS_URI);
			if (theGenericArtifactMetaModelFactory != null) {
				return theGenericArtifactMetaModelFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new GenericArtifactMetaModelFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GenericArtifactMetaModelFactoryImpl() {
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
			case GenericArtifactMetaModelPackage.ARTIFACT_WRAPPER_CONTAINER: return createArtifactWrapperContainer();
			case GenericArtifactMetaModelPackage.ARTIFACT_WRAPPER: return createArtifactWrapper();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ArtifactWrapperContainer createArtifactWrapperContainer() {
		ArtifactWrapperContainerImpl artifactWrapperContainer = new ArtifactWrapperContainerImpl();
		return artifactWrapperContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ArtifactWrapper createArtifactWrapper() {
		ArtifactWrapperImpl artifactWrapper = new ArtifactWrapperImpl();
		return artifactWrapper;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GenericArtifactMetaModelPackage getGenericArtifactMetaModelPackage() {
		return (GenericArtifactMetaModelPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static GenericArtifactMetaModelPackage getPackage() {
		return GenericArtifactMetaModelPackage.eINSTANCE;
	}

} //GenericArtifactMetaModelFactoryImpl
