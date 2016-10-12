/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel.impl;

import org.eclipse.app4mc.variability.ample.AmplePackage;

import org.eclipse.app4mc.variability.components.VacomoPackage;

import org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeFeature2Variant;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeGroup2VariationPoint;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.GenericTraceLink;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.Mandatory2ComponentInstances;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.RelatedTo;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModel;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelFactory;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.VariabilityTraceLink;

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
public class TraceModelPackageImpl extends EPackageImpl implements TraceModelPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass traceModelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass genericTraceLinkEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass relatedToEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass variabilityTraceLinkEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mandatory2ComponentInstancesEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass optional2ComponentInstancesEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass alternativeFeature2VariantEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass alternativeGroup2VariationPointEClass = null;

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
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private TraceModelPackageImpl() {
		super(eNS_URI, TraceModelFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link TraceModelPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static TraceModelPackage init() {
		if (isInited) return (TraceModelPackage)EPackage.Registry.INSTANCE.getEPackage(TraceModelPackage.eNS_URI);

		// Obtain or create and register package
		TraceModelPackageImpl theTraceModelPackage = (TraceModelPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof TraceModelPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new TraceModelPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		AmplePackage.eINSTANCE.eClass();
		VacomoPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theTraceModelPackage.createPackageContents();

		// Initialize created meta-data
		theTraceModelPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theTraceModelPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(TraceModelPackage.eNS_URI, theTraceModelPackage);
		return theTraceModelPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getTraceModel() {
		return traceModelEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getTraceModel_GenericTraceLinks() {
		return (EReference)traceModelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getTraceModel_VariableTraceLinks() {
		return (EReference)traceModelEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getGenericTraceLink() {
		return genericTraceLinkEClass;
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
	public EClass getVariabilityTraceLink() {
		return variabilityTraceLinkEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMandatory2ComponentInstances() {
		return mandatory2ComponentInstancesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMandatory2ComponentInstances_MandatoryFeature() {
		return (EReference)mandatory2ComponentInstancesEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMandatory2ComponentInstances_Components() {
		return (EReference)mandatory2ComponentInstancesEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getOptional2ComponentInstances() {
		return optional2ComponentInstancesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getOptional2ComponentInstances_OptionalFeature() {
		return (EReference)optional2ComponentInstancesEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getOptional2ComponentInstances_OrFeature() {
		return (EReference)optional2ComponentInstancesEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getOptional2ComponentInstances_Components() {
		return (EReference)optional2ComponentInstancesEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAlternativeFeature2Variant() {
		return alternativeFeature2VariantEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAlternativeFeature2Variant_Feature() {
		return (EReference)alternativeFeature2VariantEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAlternativeFeature2Variant_Variant() {
		return (EReference)alternativeFeature2VariantEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAlternativeGroup2VariationPoint() {
		return alternativeGroup2VariationPointEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAlternativeGroup2VariationPoint_AlternativeGroup() {
		return (EReference)alternativeGroup2VariationPointEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAlternativeGroup2VariationPoint_VariationPoint() {
		return (EReference)alternativeGroup2VariationPointEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TraceModelFactory getTraceModelFactory() {
		return (TraceModelFactory)getEFactoryInstance();
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
		traceModelEClass = createEClass(TRACE_MODEL);
		createEReference(traceModelEClass, TRACE_MODEL__GENERIC_TRACE_LINKS);
		createEReference(traceModelEClass, TRACE_MODEL__VARIABLE_TRACE_LINKS);

		genericTraceLinkEClass = createEClass(GENERIC_TRACE_LINK);

		relatedToEClass = createEClass(RELATED_TO);
		createEReference(relatedToEClass, RELATED_TO__ITEM);

		variabilityTraceLinkEClass = createEClass(VARIABILITY_TRACE_LINK);

		mandatory2ComponentInstancesEClass = createEClass(MANDATORY2_COMPONENT_INSTANCES);
		createEReference(mandatory2ComponentInstancesEClass, MANDATORY2_COMPONENT_INSTANCES__MANDATORY_FEATURE);
		createEReference(mandatory2ComponentInstancesEClass, MANDATORY2_COMPONENT_INSTANCES__COMPONENTS);

		optional2ComponentInstancesEClass = createEClass(OPTIONAL2_COMPONENT_INSTANCES);
		createEReference(optional2ComponentInstancesEClass, OPTIONAL2_COMPONENT_INSTANCES__OPTIONAL_FEATURE);
		createEReference(optional2ComponentInstancesEClass, OPTIONAL2_COMPONENT_INSTANCES__OR_FEATURE);
		createEReference(optional2ComponentInstancesEClass, OPTIONAL2_COMPONENT_INSTANCES__COMPONENTS);

		alternativeFeature2VariantEClass = createEClass(ALTERNATIVE_FEATURE2_VARIANT);
		createEReference(alternativeFeature2VariantEClass, ALTERNATIVE_FEATURE2_VARIANT__FEATURE);
		createEReference(alternativeFeature2VariantEClass, ALTERNATIVE_FEATURE2_VARIANT__VARIANT);

		alternativeGroup2VariationPointEClass = createEClass(ALTERNATIVE_GROUP2_VARIATION_POINT);
		createEReference(alternativeGroup2VariationPointEClass, ALTERNATIVE_GROUP2_VARIATION_POINT__ALTERNATIVE_GROUP);
		createEReference(alternativeGroup2VariationPointEClass, ALTERNATIVE_GROUP2_VARIATION_POINT__VARIATION_POINT);
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
		AmplePackage theAmplePackage = (AmplePackage)EPackage.Registry.INSTANCE.getEPackage(AmplePackage.eNS_URI);
		VacomoPackage theVacomoPackage = (VacomoPackage)EPackage.Registry.INSTANCE.getEPackage(VacomoPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		relatedToEClass.getESuperTypes().add(this.getGenericTraceLink());
		mandatory2ComponentInstancesEClass.getESuperTypes().add(this.getVariabilityTraceLink());
		optional2ComponentInstancesEClass.getESuperTypes().add(this.getVariabilityTraceLink());
		alternativeFeature2VariantEClass.getESuperTypes().add(this.getVariabilityTraceLink());
		alternativeGroup2VariationPointEClass.getESuperTypes().add(this.getVariabilityTraceLink());

		// Initialize classes, features, and operations; add parameters
		initEClass(traceModelEClass, TraceModel.class, "TraceModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getTraceModel_GenericTraceLinks(), this.getGenericTraceLink(), null, "genericTraceLinks", null, 0, -1, TraceModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getTraceModel_VariableTraceLinks(), this.getVariabilityTraceLink(), null, "variableTraceLinks", null, 0, -1, TraceModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(genericTraceLinkEClass, GenericTraceLink.class, "GenericTraceLink", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(relatedToEClass, RelatedTo.class, "RelatedTo", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRelatedTo_Item(), theEcorePackage.getEObject(), null, "item", null, 0, -1, RelatedTo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(variabilityTraceLinkEClass, VariabilityTraceLink.class, "VariabilityTraceLink", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(mandatory2ComponentInstancesEClass, Mandatory2ComponentInstances.class, "Mandatory2ComponentInstances", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getMandatory2ComponentInstances_MandatoryFeature(), theAmplePackage.getMandatory(), null, "mandatoryFeature", null, 1, 1, Mandatory2ComponentInstances.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMandatory2ComponentInstances_Components(), theVacomoPackage.getComponentInstance(), null, "components", null, 1, -1, Mandatory2ComponentInstances.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(optional2ComponentInstancesEClass, Optional2ComponentInstances.class, "Optional2ComponentInstances", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getOptional2ComponentInstances_OptionalFeature(), theAmplePackage.getOptional(), null, "optionalFeature", null, 0, 1, Optional2ComponentInstances.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOptional2ComponentInstances_OrFeature(), theAmplePackage.getOr(), null, "orFeature", null, 0, 1, Optional2ComponentInstances.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOptional2ComponentInstances_Components(), theVacomoPackage.getComponentInstance(), null, "components", null, 1, -1, Optional2ComponentInstances.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(alternativeFeature2VariantEClass, AlternativeFeature2Variant.class, "AlternativeFeature2Variant", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getAlternativeFeature2Variant_Feature(), theAmplePackage.getAlternative(), null, "feature", null, 1, 1, AlternativeFeature2Variant.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAlternativeFeature2Variant_Variant(), theVacomoPackage.getVariant(), null, "variant", null, 1, 1, AlternativeFeature2Variant.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(alternativeGroup2VariationPointEClass, AlternativeGroup2VariationPoint.class, "AlternativeGroup2VariationPoint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getAlternativeGroup2VariationPoint_AlternativeGroup(), theAmplePackage.getAlternativeGroup(), null, "alternativeGroup", null, 1, 1, AlternativeGroup2VariationPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAlternativeGroup2VariationPoint_VariationPoint(), theVacomoPackage.getVariationPoint(), null, "variationPoint", null, 1, 1, AlternativeGroup2VariationPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //TraceModelPackageImpl
