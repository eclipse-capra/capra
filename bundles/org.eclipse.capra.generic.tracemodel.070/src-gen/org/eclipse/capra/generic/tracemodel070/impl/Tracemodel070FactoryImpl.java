/**
 */
package org.eclipse.capra.generic.tracemodel070.impl;

import org.eclipse.capra.generic.tracemodel070.*;

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
public class Tracemodel070FactoryImpl extends EFactoryImpl implements Tracemodel070Factory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static Tracemodel070Factory init() {
		try {
			Tracemodel070Factory theTracemodel070Factory = (Tracemodel070Factory)EPackage.Registry.INSTANCE.getEFactory(Tracemodel070Package.eNS_URI);
			if (theTracemodel070Factory != null) {
				return theTracemodel070Factory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new Tracemodel070FactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Tracemodel070FactoryImpl() {
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
			case Tracemodel070Package.GENERIC_TRACE_MODEL: return createGenericTraceModel();
			case Tracemodel070Package.RELATED_TO: return createRelatedTo();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GenericTraceModel createGenericTraceModel() {
		GenericTraceModelImpl genericTraceModel = new GenericTraceModelImpl();
		return genericTraceModel;
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
	public Tracemodel070Package getTracemodel070Package() {
		return (Tracemodel070Package)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static Tracemodel070Package getPackage() {
		return Tracemodel070Package.eINSTANCE;
	}

} //Tracemodel070FactoryImpl
