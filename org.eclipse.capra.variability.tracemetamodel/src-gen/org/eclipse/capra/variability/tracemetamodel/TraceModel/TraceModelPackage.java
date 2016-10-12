/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/emf/2002/GenModel basePackage='org.eclipse.capra.variability.tracemetamodel'"
 * @generated
 */
public interface TraceModelPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "TraceModel";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "org.eclipse.capra.variability.tracemetamodel.TraceModel";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "TraceModel";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	TraceModelPackage eINSTANCE = org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelImpl <em>Trace Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelImpl
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl#getTraceModel()
	 * @generated
	 */
	int TRACE_MODEL = 0;

	/**
	 * The feature id for the '<em><b>Generic Trace Links</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRACE_MODEL__GENERIC_TRACE_LINKS = 0;

	/**
	 * The feature id for the '<em><b>Variable Trace Links</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRACE_MODEL__VARIABLE_TRACE_LINKS = 1;

	/**
	 * The number of structural features of the '<em>Trace Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRACE_MODEL_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Trace Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRACE_MODEL_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.GenericTraceLinkImpl <em>Generic Trace Link</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.GenericTraceLinkImpl
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl#getGenericTraceLink()
	 * @generated
	 */
	int GENERIC_TRACE_LINK = 1;

	/**
	 * The number of structural features of the '<em>Generic Trace Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_TRACE_LINK_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Generic Trace Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_TRACE_LINK_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.RelatedToImpl <em>Related To</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.RelatedToImpl
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl#getRelatedTo()
	 * @generated
	 */
	int RELATED_TO = 2;

	/**
	 * The feature id for the '<em><b>Item</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATED_TO__ITEM = GENERIC_TRACE_LINK_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Related To</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATED_TO_FEATURE_COUNT = GENERIC_TRACE_LINK_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Related To</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATED_TO_OPERATION_COUNT = GENERIC_TRACE_LINK_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.VariabilityTraceLinkImpl <em>Variability Trace Link</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.VariabilityTraceLinkImpl
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl#getVariabilityTraceLink()
	 * @generated
	 */
	int VARIABILITY_TRACE_LINK = 3;

	/**
	 * The number of structural features of the '<em>Variability Trace Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABILITY_TRACE_LINK_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Variability Trace Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABILITY_TRACE_LINK_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.Mandatory2ComponentInstancesImpl <em>Mandatory2 Component Instances</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.Mandatory2ComponentInstancesImpl
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl#getMandatory2ComponentInstances()
	 * @generated
	 */
	int MANDATORY2_COMPONENT_INSTANCES = 4;

	/**
	 * The feature id for the '<em><b>Mandatory Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANDATORY2_COMPONENT_INSTANCES__MANDATORY_FEATURE = VARIABILITY_TRACE_LINK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Components</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANDATORY2_COMPONENT_INSTANCES__COMPONENTS = VARIABILITY_TRACE_LINK_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Mandatory2 Component Instances</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANDATORY2_COMPONENT_INSTANCES_FEATURE_COUNT = VARIABILITY_TRACE_LINK_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Mandatory2 Component Instances</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANDATORY2_COMPONENT_INSTANCES_OPERATION_COUNT = VARIABILITY_TRACE_LINK_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.Optional2ComponentInstancesImpl <em>Optional2 Component Instances</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.Optional2ComponentInstancesImpl
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl#getOptional2ComponentInstances()
	 * @generated
	 */
	int OPTIONAL2_COMPONENT_INSTANCES = 5;

	/**
	 * The feature id for the '<em><b>Optional Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPTIONAL2_COMPONENT_INSTANCES__OPTIONAL_FEATURE = VARIABILITY_TRACE_LINK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Or Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPTIONAL2_COMPONENT_INSTANCES__OR_FEATURE = VARIABILITY_TRACE_LINK_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Components</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPTIONAL2_COMPONENT_INSTANCES__COMPONENTS = VARIABILITY_TRACE_LINK_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Optional2 Component Instances</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPTIONAL2_COMPONENT_INSTANCES_FEATURE_COUNT = VARIABILITY_TRACE_LINK_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Optional2 Component Instances</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPTIONAL2_COMPONENT_INSTANCES_OPERATION_COUNT = VARIABILITY_TRACE_LINK_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.AlternativeFeature2VariantImpl <em>Alternative Feature2 Variant</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.AlternativeFeature2VariantImpl
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl#getAlternativeFeature2Variant()
	 * @generated
	 */
	int ALTERNATIVE_FEATURE2_VARIANT = 6;

	/**
	 * The feature id for the '<em><b>Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALTERNATIVE_FEATURE2_VARIANT__FEATURE = VARIABILITY_TRACE_LINK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Variant</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALTERNATIVE_FEATURE2_VARIANT__VARIANT = VARIABILITY_TRACE_LINK_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Alternative Feature2 Variant</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALTERNATIVE_FEATURE2_VARIANT_FEATURE_COUNT = VARIABILITY_TRACE_LINK_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Alternative Feature2 Variant</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALTERNATIVE_FEATURE2_VARIANT_OPERATION_COUNT = VARIABILITY_TRACE_LINK_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.AlternativeGroup2VariationPointImpl <em>Alternative Group2 Variation Point</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.AlternativeGroup2VariationPointImpl
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl#getAlternativeGroup2VariationPoint()
	 * @generated
	 */
	int ALTERNATIVE_GROUP2_VARIATION_POINT = 7;

	/**
	 * The feature id for the '<em><b>Alternative Group</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALTERNATIVE_GROUP2_VARIATION_POINT__ALTERNATIVE_GROUP = VARIABILITY_TRACE_LINK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Variation Point</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALTERNATIVE_GROUP2_VARIATION_POINT__VARIATION_POINT = VARIABILITY_TRACE_LINK_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Alternative Group2 Variation Point</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALTERNATIVE_GROUP2_VARIATION_POINT_FEATURE_COUNT = VARIABILITY_TRACE_LINK_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Alternative Group2 Variation Point</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALTERNATIVE_GROUP2_VARIATION_POINT_OPERATION_COUNT = VARIABILITY_TRACE_LINK_OPERATION_COUNT + 0;


	/**
	 * Returns the meta object for class '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModel <em>Trace Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Trace Model</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModel
	 * @generated
	 */
	EClass getTraceModel();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModel#getGenericTraceLinks <em>Generic Trace Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Generic Trace Links</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModel#getGenericTraceLinks()
	 * @see #getTraceModel()
	 * @generated
	 */
	EReference getTraceModel_GenericTraceLinks();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModel#getVariableTraceLinks <em>Variable Trace Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Variable Trace Links</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModel#getVariableTraceLinks()
	 * @see #getTraceModel()
	 * @generated
	 */
	EReference getTraceModel_VariableTraceLinks();

	/**
	 * Returns the meta object for class '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.GenericTraceLink <em>Generic Trace Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Generic Trace Link</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.GenericTraceLink
	 * @generated
	 */
	EClass getGenericTraceLink();

	/**
	 * Returns the meta object for class '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.RelatedTo <em>Related To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Related To</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.RelatedTo
	 * @generated
	 */
	EClass getRelatedTo();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.RelatedTo#getItem <em>Item</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Item</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.RelatedTo#getItem()
	 * @see #getRelatedTo()
	 * @generated
	 */
	EReference getRelatedTo_Item();

	/**
	 * Returns the meta object for class '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.VariabilityTraceLink <em>Variability Trace Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Variability Trace Link</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.VariabilityTraceLink
	 * @generated
	 */
	EClass getVariabilityTraceLink();

	/**
	 * Returns the meta object for class '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Mandatory2ComponentInstances <em>Mandatory2 Component Instances</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Mandatory2 Component Instances</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.Mandatory2ComponentInstances
	 * @generated
	 */
	EClass getMandatory2ComponentInstances();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Mandatory2ComponentInstances#getMandatoryFeature <em>Mandatory Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Mandatory Feature</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.Mandatory2ComponentInstances#getMandatoryFeature()
	 * @see #getMandatory2ComponentInstances()
	 * @generated
	 */
	EReference getMandatory2ComponentInstances_MandatoryFeature();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Mandatory2ComponentInstances#getComponents <em>Components</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Components</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.Mandatory2ComponentInstances#getComponents()
	 * @see #getMandatory2ComponentInstances()
	 * @generated
	 */
	EReference getMandatory2ComponentInstances_Components();

	/**
	 * Returns the meta object for class '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances <em>Optional2 Component Instances</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Optional2 Component Instances</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances
	 * @generated
	 */
	EClass getOptional2ComponentInstances();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances#getOptionalFeature <em>Optional Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Optional Feature</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances#getOptionalFeature()
	 * @see #getOptional2ComponentInstances()
	 * @generated
	 */
	EReference getOptional2ComponentInstances_OptionalFeature();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances#getOrFeature <em>Or Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Or Feature</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances#getOrFeature()
	 * @see #getOptional2ComponentInstances()
	 * @generated
	 */
	EReference getOptional2ComponentInstances_OrFeature();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances#getComponents <em>Components</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Components</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances#getComponents()
	 * @see #getOptional2ComponentInstances()
	 * @generated
	 */
	EReference getOptional2ComponentInstances_Components();

	/**
	 * Returns the meta object for class '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeFeature2Variant <em>Alternative Feature2 Variant</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Alternative Feature2 Variant</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeFeature2Variant
	 * @generated
	 */
	EClass getAlternativeFeature2Variant();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeFeature2Variant#getFeature <em>Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Feature</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeFeature2Variant#getFeature()
	 * @see #getAlternativeFeature2Variant()
	 * @generated
	 */
	EReference getAlternativeFeature2Variant_Feature();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeFeature2Variant#getVariant <em>Variant</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Variant</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeFeature2Variant#getVariant()
	 * @see #getAlternativeFeature2Variant()
	 * @generated
	 */
	EReference getAlternativeFeature2Variant_Variant();

	/**
	 * Returns the meta object for class '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeGroup2VariationPoint <em>Alternative Group2 Variation Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Alternative Group2 Variation Point</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeGroup2VariationPoint
	 * @generated
	 */
	EClass getAlternativeGroup2VariationPoint();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeGroup2VariationPoint#getAlternativeGroup <em>Alternative Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Alternative Group</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeGroup2VariationPoint#getAlternativeGroup()
	 * @see #getAlternativeGroup2VariationPoint()
	 * @generated
	 */
	EReference getAlternativeGroup2VariationPoint_AlternativeGroup();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeGroup2VariationPoint#getVariationPoint <em>Variation Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Variation Point</em>'.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeGroup2VariationPoint#getVariationPoint()
	 * @see #getAlternativeGroup2VariationPoint()
	 * @generated
	 */
	EReference getAlternativeGroup2VariationPoint_VariationPoint();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	TraceModelFactory getTraceModelFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelImpl <em>Trace Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelImpl
		 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl#getTraceModel()
		 * @generated
		 */
		EClass TRACE_MODEL = eINSTANCE.getTraceModel();

		/**
		 * The meta object literal for the '<em><b>Generic Trace Links</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TRACE_MODEL__GENERIC_TRACE_LINKS = eINSTANCE.getTraceModel_GenericTraceLinks();

		/**
		 * The meta object literal for the '<em><b>Variable Trace Links</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TRACE_MODEL__VARIABLE_TRACE_LINKS = eINSTANCE.getTraceModel_VariableTraceLinks();

		/**
		 * The meta object literal for the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.GenericTraceLinkImpl <em>Generic Trace Link</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.GenericTraceLinkImpl
		 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl#getGenericTraceLink()
		 * @generated
		 */
		EClass GENERIC_TRACE_LINK = eINSTANCE.getGenericTraceLink();

		/**
		 * The meta object literal for the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.RelatedToImpl <em>Related To</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.RelatedToImpl
		 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl#getRelatedTo()
		 * @generated
		 */
		EClass RELATED_TO = eINSTANCE.getRelatedTo();

		/**
		 * The meta object literal for the '<em><b>Item</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RELATED_TO__ITEM = eINSTANCE.getRelatedTo_Item();

		/**
		 * The meta object literal for the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.VariabilityTraceLinkImpl <em>Variability Trace Link</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.VariabilityTraceLinkImpl
		 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl#getVariabilityTraceLink()
		 * @generated
		 */
		EClass VARIABILITY_TRACE_LINK = eINSTANCE.getVariabilityTraceLink();

		/**
		 * The meta object literal for the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.Mandatory2ComponentInstancesImpl <em>Mandatory2 Component Instances</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.Mandatory2ComponentInstancesImpl
		 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl#getMandatory2ComponentInstances()
		 * @generated
		 */
		EClass MANDATORY2_COMPONENT_INSTANCES = eINSTANCE.getMandatory2ComponentInstances();

		/**
		 * The meta object literal for the '<em><b>Mandatory Feature</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MANDATORY2_COMPONENT_INSTANCES__MANDATORY_FEATURE = eINSTANCE.getMandatory2ComponentInstances_MandatoryFeature();

		/**
		 * The meta object literal for the '<em><b>Components</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MANDATORY2_COMPONENT_INSTANCES__COMPONENTS = eINSTANCE.getMandatory2ComponentInstances_Components();

		/**
		 * The meta object literal for the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.Optional2ComponentInstancesImpl <em>Optional2 Component Instances</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.Optional2ComponentInstancesImpl
		 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl#getOptional2ComponentInstances()
		 * @generated
		 */
		EClass OPTIONAL2_COMPONENT_INSTANCES = eINSTANCE.getOptional2ComponentInstances();

		/**
		 * The meta object literal for the '<em><b>Optional Feature</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPTIONAL2_COMPONENT_INSTANCES__OPTIONAL_FEATURE = eINSTANCE.getOptional2ComponentInstances_OptionalFeature();

		/**
		 * The meta object literal for the '<em><b>Or Feature</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPTIONAL2_COMPONENT_INSTANCES__OR_FEATURE = eINSTANCE.getOptional2ComponentInstances_OrFeature();

		/**
		 * The meta object literal for the '<em><b>Components</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPTIONAL2_COMPONENT_INSTANCES__COMPONENTS = eINSTANCE.getOptional2ComponentInstances_Components();

		/**
		 * The meta object literal for the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.AlternativeFeature2VariantImpl <em>Alternative Feature2 Variant</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.AlternativeFeature2VariantImpl
		 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl#getAlternativeFeature2Variant()
		 * @generated
		 */
		EClass ALTERNATIVE_FEATURE2_VARIANT = eINSTANCE.getAlternativeFeature2Variant();

		/**
		 * The meta object literal for the '<em><b>Feature</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ALTERNATIVE_FEATURE2_VARIANT__FEATURE = eINSTANCE.getAlternativeFeature2Variant_Feature();

		/**
		 * The meta object literal for the '<em><b>Variant</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ALTERNATIVE_FEATURE2_VARIANT__VARIANT = eINSTANCE.getAlternativeFeature2Variant_Variant();

		/**
		 * The meta object literal for the '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.AlternativeGroup2VariationPointImpl <em>Alternative Group2 Variation Point</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.AlternativeGroup2VariationPointImpl
		 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelPackageImpl#getAlternativeGroup2VariationPoint()
		 * @generated
		 */
		EClass ALTERNATIVE_GROUP2_VARIATION_POINT = eINSTANCE.getAlternativeGroup2VariationPoint();

		/**
		 * The meta object literal for the '<em><b>Alternative Group</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ALTERNATIVE_GROUP2_VARIATION_POINT__ALTERNATIVE_GROUP = eINSTANCE.getAlternativeGroup2VariationPoint_AlternativeGroup();

		/**
		 * The meta object literal for the '<em><b>Variation Point</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ALTERNATIVE_GROUP2_VARIATION_POINT__VARIATION_POINT = eINSTANCE.getAlternativeGroup2VariationPoint_VariationPoint();

	}

} //TraceModelPackage
