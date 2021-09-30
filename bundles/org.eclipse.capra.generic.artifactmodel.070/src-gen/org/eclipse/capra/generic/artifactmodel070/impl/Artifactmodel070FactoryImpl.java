/**
 */
package org.eclipse.capra.generic.artifactmodel070.impl;

import org.eclipse.capra.generic.artifactmodel070.*;

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
public class Artifactmodel070FactoryImpl extends EFactoryImpl implements Artifactmodel070Factory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static Artifactmodel070Factory init() {
		try {
			Artifactmodel070Factory theArtifactmodel070Factory = (Artifactmodel070Factory)EPackage.Registry.INSTANCE.getEFactory(Artifactmodel070Package.eNS_URI);
			if (theArtifactmodel070Factory != null) {
				return theArtifactmodel070Factory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new Artifactmodel070FactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Artifactmodel070FactoryImpl() {
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
			case Artifactmodel070Package.ARTIFACT_WRAPPER_CONTAINER: return createArtifactWrapperContainer();
			case Artifactmodel070Package.ARTIFACT_WRAPPER: return createArtifactWrapper();
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
	public Artifactmodel070Package getArtifactmodel070Package() {
		return (Artifactmodel070Package)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static Artifactmodel070Package getPackage() {
		return Artifactmodel070Package.eINSTANCE;
	}

} //Artifactmodel070FactoryImpl
