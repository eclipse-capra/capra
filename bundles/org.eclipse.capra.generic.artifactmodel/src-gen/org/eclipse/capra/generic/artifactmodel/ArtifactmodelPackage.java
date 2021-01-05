/**
 */
package org.eclipse.capra.generic.artifactmodel;

import org.eclipse.emf.ecore.EAttribute;
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
 * @see org.eclipse.capra.generic.artifactmodel.ArtifactmodelFactory
 * @model kind="package"
 * @generated
 */
public interface ArtifactmodelPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "artifactmodel";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "org.eclipse.capra.generic.artifactmodel";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "artifactmodel";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ArtifactmodelPackage eINSTANCE = org.eclipse.capra.generic.artifactmodel.impl.ArtifactmodelPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.capra.generic.artifactmodel.impl.ArtifactWrapperContainerImpl <em>Artifact Wrapper Container</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.capra.generic.artifactmodel.impl.ArtifactWrapperContainerImpl
	 * @see org.eclipse.capra.generic.artifactmodel.impl.ArtifactmodelPackageImpl#getArtifactWrapperContainer()
	 * @generated
	 */
	int ARTIFACT_WRAPPER_CONTAINER = 0;

	/**
	 * The feature id for the '<em><b>Artifacts</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT_WRAPPER_CONTAINER__ARTIFACTS = 0;

	/**
	 * The number of structural features of the '<em>Artifact Wrapper Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT_WRAPPER_CONTAINER_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Artifact Wrapper Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT_WRAPPER_CONTAINER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.capra.generic.artifactmodel.impl.ArtifactWrapperImpl <em>Artifact Wrapper</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.capra.generic.artifactmodel.impl.ArtifactWrapperImpl
	 * @see org.eclipse.capra.generic.artifactmodel.impl.ArtifactmodelPackageImpl#getArtifactWrapper()
	 * @generated
	 */
	int ARTIFACT_WRAPPER = 1;

	/**
	 * The feature id for the '<em><b>Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT_WRAPPER__PATH = 0;

	/**
	 * The feature id for the '<em><b>Uri</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT_WRAPPER__URI = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT_WRAPPER__NAME = 2;

	/**
	 * The feature id for the '<em><b>Identifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT_WRAPPER__IDENTIFIER = 3;

	/**
	 * The feature id for the '<em><b>Artifact Handler</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT_WRAPPER__ARTIFACT_HANDLER = 4;

	/**
	 * The number of structural features of the '<em>Artifact Wrapper</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT_WRAPPER_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Artifact Wrapper</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT_WRAPPER_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapperContainer <em>Artifact Wrapper Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Artifact Wrapper Container</em>'.
	 * @see org.eclipse.capra.generic.artifactmodel.ArtifactWrapperContainer
	 * @generated
	 */
	EClass getArtifactWrapperContainer();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapperContainer#getArtifacts <em>Artifacts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Artifacts</em>'.
	 * @see org.eclipse.capra.generic.artifactmodel.ArtifactWrapperContainer#getArtifacts()
	 * @see #getArtifactWrapperContainer()
	 * @generated
	 */
	EReference getArtifactWrapperContainer_Artifacts();

	/**
	 * Returns the meta object for class '{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper <em>Artifact Wrapper</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Artifact Wrapper</em>'.
	 * @see org.eclipse.capra.generic.artifactmodel.ArtifactWrapper
	 * @generated
	 */
	EClass getArtifactWrapper();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getPath <em>Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Path</em>'.
	 * @see org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getPath()
	 * @see #getArtifactWrapper()
	 * @generated
	 */
	EAttribute getArtifactWrapper_Path();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getUri <em>Uri</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Uri</em>'.
	 * @see org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getUri()
	 * @see #getArtifactWrapper()
	 * @generated
	 */
	EAttribute getArtifactWrapper_Uri();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getName()
	 * @see #getArtifactWrapper()
	 * @generated
	 */
	EAttribute getArtifactWrapper_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getIdentifier <em>Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Identifier</em>'.
	 * @see org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getIdentifier()
	 * @see #getArtifactWrapper()
	 * @generated
	 */
	EAttribute getArtifactWrapper_Identifier();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getArtifactHandler <em>Artifact Handler</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Artifact Handler</em>'.
	 * @see org.eclipse.capra.generic.artifactmodel.ArtifactWrapper#getArtifactHandler()
	 * @see #getArtifactWrapper()
	 * @generated
	 */
	EAttribute getArtifactWrapper_ArtifactHandler();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ArtifactmodelFactory getArtifactmodelFactory();

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
		 * The meta object literal for the '{@link org.eclipse.capra.generic.artifactmodel.impl.ArtifactWrapperContainerImpl <em>Artifact Wrapper Container</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.capra.generic.artifactmodel.impl.ArtifactWrapperContainerImpl
		 * @see org.eclipse.capra.generic.artifactmodel.impl.ArtifactmodelPackageImpl#getArtifactWrapperContainer()
		 * @generated
		 */
		EClass ARTIFACT_WRAPPER_CONTAINER = eINSTANCE.getArtifactWrapperContainer();

		/**
		 * The meta object literal for the '<em><b>Artifacts</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ARTIFACT_WRAPPER_CONTAINER__ARTIFACTS = eINSTANCE.getArtifactWrapperContainer_Artifacts();

		/**
		 * The meta object literal for the '{@link org.eclipse.capra.generic.artifactmodel.impl.ArtifactWrapperImpl <em>Artifact Wrapper</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.capra.generic.artifactmodel.impl.ArtifactWrapperImpl
		 * @see org.eclipse.capra.generic.artifactmodel.impl.ArtifactmodelPackageImpl#getArtifactWrapper()
		 * @generated
		 */
		EClass ARTIFACT_WRAPPER = eINSTANCE.getArtifactWrapper();

		/**
		 * The meta object literal for the '<em><b>Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ARTIFACT_WRAPPER__PATH = eINSTANCE.getArtifactWrapper_Path();

		/**
		 * The meta object literal for the '<em><b>Uri</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ARTIFACT_WRAPPER__URI = eINSTANCE.getArtifactWrapper_Uri();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ARTIFACT_WRAPPER__NAME = eINSTANCE.getArtifactWrapper_Name();

		/**
		 * The meta object literal for the '<em><b>Identifier</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ARTIFACT_WRAPPER__IDENTIFIER = eINSTANCE.getArtifactWrapper_Identifier();

		/**
		 * The meta object literal for the '<em><b>Artifact Handler</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ARTIFACT_WRAPPER__ARTIFACT_HANDLER = eINSTANCE.getArtifactWrapper_ArtifactHandler();

	}

} //ArtifactmodelPackage
