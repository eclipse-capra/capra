/**
 */
package org.eclipse.capra.generic.tracemodel070;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.capra.generic.tracemodel070.Tracemodel070Package
 * @generated
 */
public interface Tracemodel070Factory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	Tracemodel070Factory eINSTANCE = org.eclipse.capra.generic.tracemodel070.impl.Tracemodel070FactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Generic Trace Model</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Generic Trace Model</em>'.
	 * @generated
	 */
	GenericTraceModel createGenericTraceModel();

	/**
	 * Returns a new object of class '<em>Related To</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Related To</em>'.
	 * @generated
	 */
	RelatedTo createRelatedTo();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	Tracemodel070Package getTracemodel070Package();

} //Tracemodel070Factory
