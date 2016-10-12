/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel.util;

import org.eclipse.capra.variability.tracemetamodel.TraceModel.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage
 * @generated
 */
public class TraceModelSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static TraceModelPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TraceModelSwitch() {
		if (modelPackage == null) {
			modelPackage = TraceModelPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case TraceModelPackage.TRACE_MODEL: {
				TraceModel traceModel = (TraceModel)theEObject;
				T result = caseTraceModel(traceModel);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TraceModelPackage.GENERIC_TRACE_LINK: {
				GenericTraceLink genericTraceLink = (GenericTraceLink)theEObject;
				T result = caseGenericTraceLink(genericTraceLink);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TraceModelPackage.RELATED_TO: {
				RelatedTo relatedTo = (RelatedTo)theEObject;
				T result = caseRelatedTo(relatedTo);
				if (result == null) result = caseGenericTraceLink(relatedTo);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TraceModelPackage.VARIABILITY_TRACE_LINK: {
				VariabilityTraceLink variabilityTraceLink = (VariabilityTraceLink)theEObject;
				T result = caseVariabilityTraceLink(variabilityTraceLink);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TraceModelPackage.MANDATORY2_COMPONENT_INSTANCES: {
				Mandatory2ComponentInstances mandatory2ComponentInstances = (Mandatory2ComponentInstances)theEObject;
				T result = caseMandatory2ComponentInstances(mandatory2ComponentInstances);
				if (result == null) result = caseVariabilityTraceLink(mandatory2ComponentInstances);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES: {
				Optional2ComponentInstances optional2ComponentInstances = (Optional2ComponentInstances)theEObject;
				T result = caseOptional2ComponentInstances(optional2ComponentInstances);
				if (result == null) result = caseVariabilityTraceLink(optional2ComponentInstances);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TraceModelPackage.ALTERNATIVE_FEATURE2_VARIANT: {
				AlternativeFeature2Variant alternativeFeature2Variant = (AlternativeFeature2Variant)theEObject;
				T result = caseAlternativeFeature2Variant(alternativeFeature2Variant);
				if (result == null) result = caseVariabilityTraceLink(alternativeFeature2Variant);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TraceModelPackage.ALTERNATIVE_GROUP2_VARIATION_POINT: {
				AlternativeGroup2VariationPoint alternativeGroup2VariationPoint = (AlternativeGroup2VariationPoint)theEObject;
				T result = caseAlternativeGroup2VariationPoint(alternativeGroup2VariationPoint);
				if (result == null) result = caseVariabilityTraceLink(alternativeGroup2VariationPoint);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Trace Model</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Trace Model</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTraceModel(TraceModel object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Generic Trace Link</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Generic Trace Link</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGenericTraceLink(GenericTraceLink object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Related To</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Related To</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRelatedTo(RelatedTo object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Variability Trace Link</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Variability Trace Link</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseVariabilityTraceLink(VariabilityTraceLink object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Mandatory2 Component Instances</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Mandatory2 Component Instances</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMandatory2ComponentInstances(Mandatory2ComponentInstances object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Optional2 Component Instances</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Optional2 Component Instances</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseOptional2ComponentInstances(Optional2ComponentInstances object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Alternative Feature2 Variant</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Alternative Feature2 Variant</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAlternativeFeature2Variant(AlternativeFeature2Variant object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Alternative Group2 Variation Point</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Alternative Group2 Variation Point</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAlternativeGroup2VariationPoint(AlternativeGroup2VariationPoint object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //TraceModelSwitch
