/**
 */
package org.eclipse.capra.GenericTraceMetaModel.impl;

import org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelFactory;
import org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage;
import org.eclipse.capra.GenericTraceMetaModel.GenericTraceModel;
import org.eclipse.capra.GenericTraceMetaModel.RelatedTo;

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
public class GenericTraceMetaModelPackageImpl extends EPackageImpl implements GenericTraceMetaModelPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass genericTraceModelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass relatedToEClass = null;

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
	 * @see org.eclipse.capra.GenericTraceMetaModel.GenericTraceMetaModelPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private GenericTraceMetaModelPackageImpl() {
		super(eNS_URI, GenericTraceMetaModelFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link GenericTraceMetaModelPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static GenericTraceMetaModelPackage init() {
		if (isInited) return (GenericTraceMetaModelPackage)EPackage.Registry.INSTANCE.getEPackage(GenericTraceMetaModelPackage.eNS_URI);

		// Obtain or create and register package
		GenericTraceMetaModelPackageImpl theGenericTraceMetaModelPackage = (GenericTraceMetaModelPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof GenericTraceMetaModelPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new GenericTraceMetaModelPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		EcorePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theGenericTraceMetaModelPackage.createPackageContents();

		// Initialize created meta-data
		theGenericTraceMetaModelPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theGenericTraceMetaModelPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(GenericTraceMetaModelPackage.eNS_URI, theGenericTraceMetaModelPackage);
		return theGenericTraceMetaModelPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getGenericTraceModel() {
		return genericTraceModelEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGenericTraceModel_Traces() {
		return (EReference)genericTraceModelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRelatedTo() {
		return relatedToEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRelatedTo_Item() {
		return (EReference)relatedToEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GenericTraceMetaModelFactory getGenericTraceMetaModelFactory() {
		return (GenericTraceMetaModelFactory)getEFactoryInstance();
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
		genericTraceModelEClass = createEClass(GENERIC_TRACE_MODEL);
		createEReference(genericTraceModelEClass, GENERIC_TRACE_MODEL__TRACES);

		relatedToEClass = createEClass(RELATED_TO);
		createEReference(relatedToEClass, RELATED_TO__ITEM);
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
		initEClass(genericTraceModelEClass, GenericTraceModel.class, "GenericTraceModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getGenericTraceModel_Traces(), this.getRelatedTo(), null, "traces", null, 0, -1, GenericTraceModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(relatedToEClass, RelatedTo.class, "RelatedTo", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRelatedTo_Item(), theEcorePackage.getEObject(), null, "item", null, 0, -1, RelatedTo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //GenericTraceMetaModelPackageImpl
