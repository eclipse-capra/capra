/**
 */
package org.eclipse.capra.generic.artifactmodel.impl;

import org.eclipse.capra.generic.artifactmodel.ArtifactWrapper;
import org.eclipse.capra.generic.artifactmodel.ArtifactWrapperContainer;
import org.eclipse.capra.generic.artifactmodel.ArtifactmodelFactory;
import org.eclipse.capra.generic.artifactmodel.ArtifactmodelPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ArtifactmodelPackageImpl extends EPackageImpl implements ArtifactmodelPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass artifactWrapperContainerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass artifactWrapperEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.capra.generic.artifactmodel.ArtifactmodelPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ArtifactmodelPackageImpl() {
		super(eNS_URI, ArtifactmodelFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link ArtifactmodelPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ArtifactmodelPackage init() {
		if (isInited) return (ArtifactmodelPackage)EPackage.Registry.INSTANCE.getEPackage(ArtifactmodelPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredArtifactmodelPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		ArtifactmodelPackageImpl theArtifactmodelPackage = registeredArtifactmodelPackage instanceof ArtifactmodelPackageImpl ? (ArtifactmodelPackageImpl)registeredArtifactmodelPackage : new ArtifactmodelPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		EcorePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theArtifactmodelPackage.createPackageContents();

		// Initialize created meta-data
		theArtifactmodelPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theArtifactmodelPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(ArtifactmodelPackage.eNS_URI, theArtifactmodelPackage);
		return theArtifactmodelPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getArtifactWrapperContainer() {
		return artifactWrapperContainerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getArtifactWrapperContainer_Artifacts() {
		return (EReference)artifactWrapperContainerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getArtifactWrapper() {
		return artifactWrapperEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getArtifactWrapper_Path() {
		return (EAttribute)artifactWrapperEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getArtifactWrapper_Uri() {
		return (EAttribute)artifactWrapperEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getArtifactWrapper_Name() {
		return (EAttribute)artifactWrapperEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getArtifactWrapper_Identifier() {
		return (EAttribute)artifactWrapperEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getArtifactWrapper_ArtifactHandler() {
		return (EAttribute)artifactWrapperEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ArtifactmodelFactory getArtifactmodelFactory() {
		return (ArtifactmodelFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		artifactWrapperContainerEClass = createEClass(ARTIFACT_WRAPPER_CONTAINER);
		createEReference(artifactWrapperContainerEClass, ARTIFACT_WRAPPER_CONTAINER__ARTIFACTS);

		artifactWrapperEClass = createEClass(ARTIFACT_WRAPPER);
		createEAttribute(artifactWrapperEClass, ARTIFACT_WRAPPER__PATH);
		createEAttribute(artifactWrapperEClass, ARTIFACT_WRAPPER__URI);
		createEAttribute(artifactWrapperEClass, ARTIFACT_WRAPPER__NAME);
		createEAttribute(artifactWrapperEClass, ARTIFACT_WRAPPER__IDENTIFIER);
		createEAttribute(artifactWrapperEClass, ARTIFACT_WRAPPER__ARTIFACT_HANDLER);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(artifactWrapperContainerEClass, ArtifactWrapperContainer.class, "ArtifactWrapperContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getArtifactWrapperContainer_Artifacts(), this.getArtifactWrapper(), null, "artifacts", null, 0, -1, ArtifactWrapperContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(artifactWrapperEClass, ArtifactWrapper.class, "ArtifactWrapper", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getArtifactWrapper_Path(), theEcorePackage.getEString(), "path", null, 0, 1, ArtifactWrapper.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getArtifactWrapper_Uri(), theEcorePackage.getEString(), "uri", null, 0, 1, ArtifactWrapper.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getArtifactWrapper_Name(), theEcorePackage.getEString(), "name", null, 0, 1, ArtifactWrapper.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getArtifactWrapper_Identifier(), theEcorePackage.getEString(), "identifier", null, 0, 1, ArtifactWrapper.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getArtifactWrapper_ArtifactHandler(), theEcorePackage.getEString(), "ArtifactHandler", null, 0, 1, ArtifactWrapper.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //ArtifactmodelPackageImpl
