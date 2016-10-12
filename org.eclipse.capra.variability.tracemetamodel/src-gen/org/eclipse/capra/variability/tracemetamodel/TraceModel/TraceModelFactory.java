/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage
 * @generated
 */
public interface TraceModelFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	TraceModelFactory eINSTANCE = org.eclipse.capra.variability.tracemetamodel.TraceModel.impl.TraceModelFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Trace Model</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Trace Model</em>'.
	 * @generated
	 */
	TraceModel createTraceModel();

	/**
	 * Returns a new object of class '<em>Related To</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Related To</em>'.
	 * @generated
	 */
	RelatedTo createRelatedTo();

	/**
	 * Returns a new object of class '<em>Mandatory2 Component Instances</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Mandatory2 Component Instances</em>'.
	 * @generated
	 */
	Mandatory2ComponentInstances createMandatory2ComponentInstances();

	/**
	 * Returns a new object of class '<em>Optional2 Component Instances</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Optional2 Component Instances</em>'.
	 * @generated
	 */
	Optional2ComponentInstances createOptional2ComponentInstances();

	/**
	 * Returns a new object of class '<em>Alternative Feature2 Variant</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Alternative Feature2 Variant</em>'.
	 * @generated
	 */
	AlternativeFeature2Variant createAlternativeFeature2Variant();

	/**
	 * Returns a new object of class '<em>Alternative Group2 Variation Point</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Alternative Group2 Variation Point</em>'.
	 * @generated
	 */
	AlternativeGroup2VariationPoint createAlternativeGroup2VariationPoint();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	TraceModelPackage getTraceModelPackage();

} //TraceModelFactory
