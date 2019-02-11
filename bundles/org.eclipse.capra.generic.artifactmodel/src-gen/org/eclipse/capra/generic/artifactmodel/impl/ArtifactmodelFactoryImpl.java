/**
 */
package org.eclipse.capra.generic.artifactmodel.impl;

import org.eclipse.capra.generic.artifactmodel.*;

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
public class ArtifactmodelFactoryImpl extends EFactoryImpl implements ArtifactmodelFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ArtifactmodelFactory init() {
		try {
			ArtifactmodelFactory theArtifactmodelFactory = (ArtifactmodelFactory)EPackage.Registry.INSTANCE.getEFactory(ArtifactmodelPackage.eNS_URI);
			if (theArtifactmodelFactory != null) {
				return theArtifactmodelFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new ArtifactmodelFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ArtifactmodelFactoryImpl() {
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
			case ArtifactmodelPackage.ARTIFACT_WRAPPER_CONTAINER: return createArtifactWrapperContainer();
			case ArtifactmodelPackage.ARTIFACT_WRAPPER: return createArtifactWrapper();
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
	public ArtifactmodelPackage getArtifactmodelPackage() {
		return (ArtifactmodelPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static ArtifactmodelPackage getPackage() {
		return ArtifactmodelPackage.eINSTANCE;
	}

} //ArtifactmodelFactoryImpl
