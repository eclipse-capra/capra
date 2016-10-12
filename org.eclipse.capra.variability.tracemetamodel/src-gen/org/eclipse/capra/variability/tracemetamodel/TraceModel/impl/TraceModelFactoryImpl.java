/**
 */
package org.eclipse.capra.variability.tracemetamodel.TraceModel.impl;

import org.eclipse.capra.variability.tracemetamodel.TraceModel.*;

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
public class TraceModelFactoryImpl extends EFactoryImpl implements TraceModelFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static TraceModelFactory init() {
		try {
			TraceModelFactory theTraceModelFactory = (TraceModelFactory)EPackage.Registry.INSTANCE.getEFactory(TraceModelPackage.eNS_URI);
			if (theTraceModelFactory != null) {
				return theTraceModelFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new TraceModelFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TraceModelFactoryImpl() {
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
			case TraceModelPackage.TRACE_MODEL: return createTraceModel();
			case TraceModelPackage.RELATED_TO: return createRelatedTo();
			case TraceModelPackage.MANDATORY2_COMPONENT_INSTANCES: return createMandatory2ComponentInstances();
			case TraceModelPackage.OPTIONAL2_COMPONENT_INSTANCES: return createOptional2ComponentInstances();
			case TraceModelPackage.ALTERNATIVE_FEATURE2_VARIANT: return createAlternativeFeature2Variant();
			case TraceModelPackage.ALTERNATIVE_GROUP2_VARIATION_POINT: return createAlternativeGroup2VariationPoint();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TraceModel createTraceModel() {
		TraceModelImpl traceModel = new TraceModelImpl();
		return traceModel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RelatedTo createRelatedTo() {
		RelatedToImpl relatedTo = new RelatedToImpl();
		return relatedTo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Mandatory2ComponentInstances createMandatory2ComponentInstances() {
		Mandatory2ComponentInstancesImpl mandatory2ComponentInstances = new Mandatory2ComponentInstancesImpl();
		return mandatory2ComponentInstances;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Optional2ComponentInstances createOptional2ComponentInstances() {
		Optional2ComponentInstancesImpl optional2ComponentInstances = new Optional2ComponentInstancesImpl();
		return optional2ComponentInstances;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AlternativeFeature2Variant createAlternativeFeature2Variant() {
		AlternativeFeature2VariantImpl alternativeFeature2Variant = new AlternativeFeature2VariantImpl();
		return alternativeFeature2Variant;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AlternativeGroup2VariationPoint createAlternativeGroup2VariationPoint() {
		AlternativeGroup2VariationPointImpl alternativeGroup2VariationPoint = new AlternativeGroup2VariationPointImpl();
		return alternativeGroup2VariationPoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TraceModelPackage getTraceModelPackage() {
		return (TraceModelPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static TraceModelPackage getPackage() {
		return TraceModelPackage.eINSTANCE;
	}

} //TraceModelFactoryImpl
