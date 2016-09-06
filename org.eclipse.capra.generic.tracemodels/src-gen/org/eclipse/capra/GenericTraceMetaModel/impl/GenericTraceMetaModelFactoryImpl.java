/**
 */
package org.eclipse.capra.GenericTraceMetaModel.impl;

import org.eclipse.capra.GenericTraceMetaModel.*;

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
public class GenericTraceMetaModelFactoryImpl extends EFactoryImpl implements GenericTraceMetaModelFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static GenericTraceMetaModelFactory init() {
		try {
			GenericTraceMetaModelFactory theGenericTraceMetaModelFactory = (GenericTraceMetaModelFactory)EPackage.Registry.INSTANCE.getEFactory(GenericTraceMetaModelPackage.eNS_URI);
			if (theGenericTraceMetaModelFactory != null) {
				return theGenericTraceMetaModelFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new GenericTraceMetaModelFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GenericTraceMetaModelFactoryImpl() {
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
			case GenericTraceMetaModelPackage.GENERIC_TRACE_MODEL: return createGenericTraceModel();
			case GenericTraceMetaModelPackage.RELATED_TO: return createRelatedTo();
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
	public GenericTraceMetaModelPackage getGenericTraceMetaModelPackage() {
		return (GenericTraceMetaModelPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static GenericTraceMetaModelPackage getPackage() {
		return GenericTraceMetaModelPackage.eINSTANCE;
	}

} //GenericTraceMetaModelFactoryImpl
