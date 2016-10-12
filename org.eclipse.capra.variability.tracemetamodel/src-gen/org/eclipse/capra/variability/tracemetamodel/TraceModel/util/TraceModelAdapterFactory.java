/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel.util;

import org.eclipse.capra.variability.tracemetamodel.TraceModel.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage
 * @generated
 */
public class TraceModelAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static TraceModelPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TraceModelAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = TraceModelPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TraceModelSwitch<Adapter> modelSwitch =
		new TraceModelSwitch<Adapter>() {
			@Override
			public Adapter caseTraceModel(TraceModel object) {
				return createTraceModelAdapter();
			}
			@Override
			public Adapter caseGenericTraceLink(GenericTraceLink object) {
				return createGenericTraceLinkAdapter();
			}
			@Override
			public Adapter caseRelatedTo(RelatedTo object) {
				return createRelatedToAdapter();
			}
			@Override
			public Adapter caseVariabilityTraceLink(VariabilityTraceLink object) {
				return createVariabilityTraceLinkAdapter();
			}
			@Override
			public Adapter caseMandatory2ComponentInstances(Mandatory2ComponentInstances object) {
				return createMandatory2ComponentInstancesAdapter();
			}
			@Override
			public Adapter caseOptional2ComponentInstances(Optional2ComponentInstances object) {
				return createOptional2ComponentInstancesAdapter();
			}
			@Override
			public Adapter caseAlternativeFeature2Variant(AlternativeFeature2Variant object) {
				return createAlternativeFeature2VariantAdapter();
			}
			@Override
			public Adapter caseAlternativeGroup2VariationPoint(AlternativeGroup2VariationPoint object) {
				return createAlternativeGroup2VariationPointAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModel <em>Trace Model</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModel
	 * @generated
	 */
	public Adapter createTraceModelAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.GenericTraceLink <em>Generic Trace Link</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.GenericTraceLink
	 * @generated
	 */
	public Adapter createGenericTraceLinkAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.RelatedTo <em>Related To</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.RelatedTo
	 * @generated
	 */
	public Adapter createRelatedToAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.VariabilityTraceLink <em>Variability Trace Link</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.VariabilityTraceLink
	 * @generated
	 */
	public Adapter createVariabilityTraceLinkAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Mandatory2ComponentInstances <em>Mandatory2 Component Instances</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.Mandatory2ComponentInstances
	 * @generated
	 */
	public Adapter createMandatory2ComponentInstancesAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances <em>Optional2 Component Instances</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances
	 * @generated
	 */
	public Adapter createOptional2ComponentInstancesAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeFeature2Variant <em>Alternative Feature2 Variant</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeFeature2Variant
	 * @generated
	 */
	public Adapter createAlternativeFeature2VariantAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeGroup2VariationPoint <em>Alternative Group2 Variation Point</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeGroup2VariationPoint
	 * @generated
	 */
	public Adapter createAlternativeGroup2VariationPointAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //TraceModelAdapterFactory
