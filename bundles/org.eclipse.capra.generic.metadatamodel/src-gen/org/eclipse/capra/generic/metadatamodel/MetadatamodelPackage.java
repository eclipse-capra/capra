/**
 */
package org.eclipse.capra.generic.metadatamodel;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
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
 * <!-- begin-model-doc -->
 * ******************************************************************************
 * Copyright (c) 2016-2023 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *      Chalmers | University of Gothenburg - additional features, updated API
 * 		Jan-Philipp Steghöfer - additional features, updated API
 * *****************************************************************************
 * <!-- end-model-doc -->
 * @see org.eclipse.capra.generic.metadatamodel.MetadatamodelFactory
 * @model kind="package"
 * @generated
 */
public interface MetadatamodelPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "metadatamodel";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.eclipse.org/capra/metadata/0.9.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "metadatamodel";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MetadatamodelPackage eINSTANCE = org.eclipse.capra.generic.metadatamodel.impl.MetadatamodelPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.capra.generic.metadatamodel.impl.MetadataContainerImpl <em>Metadata Container</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.capra.generic.metadatamodel.impl.MetadataContainerImpl
	 * @see org.eclipse.capra.generic.metadatamodel.impl.MetadatamodelPackageImpl#getMetadataContainer()
	 * @generated
	 */
	int METADATA_CONTAINER = 0;

	/**
	 * The feature id for the '<em><b>Trace Metadata</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_CONTAINER__TRACE_METADATA = 0;

	/**
	 * The feature id for the '<em><b>Artifact Metadata</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_CONTAINER__ARTIFACT_METADATA = 1;

	/**
	 * The number of structural features of the '<em>Metadata Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_CONTAINER_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Metadata Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_CONTAINER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.capra.generic.metadatamodel.impl.PersonImpl <em>Person</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.capra.generic.metadatamodel.impl.PersonImpl
	 * @see org.eclipse.capra.generic.metadatamodel.impl.MetadatamodelPackageImpl#getPerson()
	 * @generated
	 */
	int PERSON = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSON__NAME = 0;

	/**
	 * The feature id for the '<em><b>Email</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSON__EMAIL = 1;

	/**
	 * The number of structural features of the '<em>Person</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSON_FEATURE_COUNT = 2;

	/**
	 * The operation id for the '<em>To String</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSON___TO_STRING = 0;

	/**
	 * The number of operations of the '<em>Person</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSON_OPERATION_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.capra.generic.metadatamodel.impl.TraceMetadataImpl <em>Trace Metadata</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.capra.generic.metadatamodel.impl.TraceMetadataImpl
	 * @see org.eclipse.capra.generic.metadatamodel.impl.MetadatamodelPackageImpl#getTraceMetadata()
	 * @generated
	 */
	int TRACE_METADATA = 2;

	/**
	 * The feature id for the '<em><b>Creation Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRACE_METADATA__CREATION_DATE = 0;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRACE_METADATA__COMMENT = 1;

	/**
	 * The feature id for the '<em><b>Creation User</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRACE_METADATA__CREATION_USER = 2;

	/**
	 * The feature id for the '<em><b>Trace</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRACE_METADATA__TRACE = 3;

	/**
	 * The number of structural features of the '<em>Trace Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRACE_METADATA_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Trace Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRACE_METADATA_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.capra.generic.metadatamodel.impl.ArtifactMetadataImpl <em>Artifact Metadata</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.capra.generic.metadatamodel.impl.ArtifactMetadataImpl
	 * @see org.eclipse.capra.generic.metadatamodel.impl.MetadatamodelPackageImpl#getArtifactMetadata()
	 * @generated
	 */
	int ARTIFACT_METADATA = 3;

	/**
	 * The feature id for the '<em><b>Responsible User</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT_METADATA__RESPONSIBLE_USER = 0;

	/**
	 * The feature id for the '<em><b>Artifact</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT_METADATA__ARTIFACT = 1;

	/**
	 * The number of structural features of the '<em>Artifact Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT_METADATA_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Artifact Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT_METADATA_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link org.eclipse.capra.generic.metadatamodel.MetadataContainer <em>Metadata Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Metadata Container</em>'.
	 * @see org.eclipse.capra.generic.metadatamodel.MetadataContainer
	 * @generated
	 */
	EClass getMetadataContainer();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.capra.generic.metadatamodel.MetadataContainer#getTraceMetadata <em>Trace Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Trace Metadata</em>'.
	 * @see org.eclipse.capra.generic.metadatamodel.MetadataContainer#getTraceMetadata()
	 * @see #getMetadataContainer()
	 * @generated
	 */
	EReference getMetadataContainer_TraceMetadata();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.capra.generic.metadatamodel.MetadataContainer#getArtifactMetadata <em>Artifact Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Artifact Metadata</em>'.
	 * @see org.eclipse.capra.generic.metadatamodel.MetadataContainer#getArtifactMetadata()
	 * @see #getMetadataContainer()
	 * @generated
	 */
	EReference getMetadataContainer_ArtifactMetadata();

	/**
	 * Returns the meta object for class '{@link org.eclipse.capra.generic.metadatamodel.Person <em>Person</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Person</em>'.
	 * @see org.eclipse.capra.generic.metadatamodel.Person
	 * @generated
	 */
	EClass getPerson();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.capra.generic.metadatamodel.Person#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.capra.generic.metadatamodel.Person#getName()
	 * @see #getPerson()
	 * @generated
	 */
	EAttribute getPerson_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.capra.generic.metadatamodel.Person#getEmail <em>Email</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Email</em>'.
	 * @see org.eclipse.capra.generic.metadatamodel.Person#getEmail()
	 * @see #getPerson()
	 * @generated
	 */
	EAttribute getPerson_Email();

	/**
	 * Returns the meta object for the '{@link org.eclipse.capra.generic.metadatamodel.Person#toString() <em>To String</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>To String</em>' operation.
	 * @see org.eclipse.capra.generic.metadatamodel.Person#toString()
	 * @generated
	 */
	EOperation getPerson__ToString();

	/**
	 * Returns the meta object for class '{@link org.eclipse.capra.generic.metadatamodel.TraceMetadata <em>Trace Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Trace Metadata</em>'.
	 * @see org.eclipse.capra.generic.metadatamodel.TraceMetadata
	 * @generated
	 */
	EClass getTraceMetadata();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.capra.generic.metadatamodel.TraceMetadata#getCreationDate <em>Creation Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Creation Date</em>'.
	 * @see org.eclipse.capra.generic.metadatamodel.TraceMetadata#getCreationDate()
	 * @see #getTraceMetadata()
	 * @generated
	 */
	EAttribute getTraceMetadata_CreationDate();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.capra.generic.metadatamodel.TraceMetadata#getComment <em>Comment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Comment</em>'.
	 * @see org.eclipse.capra.generic.metadatamodel.TraceMetadata#getComment()
	 * @see #getTraceMetadata()
	 * @generated
	 */
	EAttribute getTraceMetadata_Comment();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.capra.generic.metadatamodel.TraceMetadata#getCreationUser <em>Creation User</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Creation User</em>'.
	 * @see org.eclipse.capra.generic.metadatamodel.TraceMetadata#getCreationUser()
	 * @see #getTraceMetadata()
	 * @generated
	 */
	EReference getTraceMetadata_CreationUser();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.capra.generic.metadatamodel.TraceMetadata#getTrace <em>Trace</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Trace</em>'.
	 * @see org.eclipse.capra.generic.metadatamodel.TraceMetadata#getTrace()
	 * @see #getTraceMetadata()
	 * @generated
	 */
	EReference getTraceMetadata_Trace();

	/**
	 * Returns the meta object for class '{@link org.eclipse.capra.generic.metadatamodel.ArtifactMetadata <em>Artifact Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Artifact Metadata</em>'.
	 * @see org.eclipse.capra.generic.metadatamodel.ArtifactMetadata
	 * @generated
	 */
	EClass getArtifactMetadata();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.capra.generic.metadatamodel.ArtifactMetadata#getResponsibleUser <em>Responsible User</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Responsible User</em>'.
	 * @see org.eclipse.capra.generic.metadatamodel.ArtifactMetadata#getResponsibleUser()
	 * @see #getArtifactMetadata()
	 * @generated
	 */
	EReference getArtifactMetadata_ResponsibleUser();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.capra.generic.metadatamodel.ArtifactMetadata#getArtifact <em>Artifact</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Artifact</em>'.
	 * @see org.eclipse.capra.generic.metadatamodel.ArtifactMetadata#getArtifact()
	 * @see #getArtifactMetadata()
	 * @generated
	 */
	EReference getArtifactMetadata_Artifact();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	MetadatamodelFactory getMetadatamodelFactory();

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
		 * The meta object literal for the '{@link org.eclipse.capra.generic.metadatamodel.impl.MetadataContainerImpl <em>Metadata Container</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.capra.generic.metadatamodel.impl.MetadataContainerImpl
		 * @see org.eclipse.capra.generic.metadatamodel.impl.MetadatamodelPackageImpl#getMetadataContainer()
		 * @generated
		 */
		EClass METADATA_CONTAINER = eINSTANCE.getMetadataContainer();

		/**
		 * The meta object literal for the '<em><b>Trace Metadata</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference METADATA_CONTAINER__TRACE_METADATA = eINSTANCE.getMetadataContainer_TraceMetadata();

		/**
		 * The meta object literal for the '<em><b>Artifact Metadata</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference METADATA_CONTAINER__ARTIFACT_METADATA = eINSTANCE.getMetadataContainer_ArtifactMetadata();

		/**
		 * The meta object literal for the '{@link org.eclipse.capra.generic.metadatamodel.impl.PersonImpl <em>Person</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.capra.generic.metadatamodel.impl.PersonImpl
		 * @see org.eclipse.capra.generic.metadatamodel.impl.MetadatamodelPackageImpl#getPerson()
		 * @generated
		 */
		EClass PERSON = eINSTANCE.getPerson();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSON__NAME = eINSTANCE.getPerson_Name();

		/**
		 * The meta object literal for the '<em><b>Email</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSON__EMAIL = eINSTANCE.getPerson_Email();

		/**
		 * The meta object literal for the '<em><b>To String</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation PERSON___TO_STRING = eINSTANCE.getPerson__ToString();

		/**
		 * The meta object literal for the '{@link org.eclipse.capra.generic.metadatamodel.impl.TraceMetadataImpl <em>Trace Metadata</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.capra.generic.metadatamodel.impl.TraceMetadataImpl
		 * @see org.eclipse.capra.generic.metadatamodel.impl.MetadatamodelPackageImpl#getTraceMetadata()
		 * @generated
		 */
		EClass TRACE_METADATA = eINSTANCE.getTraceMetadata();

		/**
		 * The meta object literal for the '<em><b>Creation Date</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRACE_METADATA__CREATION_DATE = eINSTANCE.getTraceMetadata_CreationDate();

		/**
		 * The meta object literal for the '<em><b>Comment</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRACE_METADATA__COMMENT = eINSTANCE.getTraceMetadata_Comment();

		/**
		 * The meta object literal for the '<em><b>Creation User</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TRACE_METADATA__CREATION_USER = eINSTANCE.getTraceMetadata_CreationUser();

		/**
		 * The meta object literal for the '<em><b>Trace</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TRACE_METADATA__TRACE = eINSTANCE.getTraceMetadata_Trace();

		/**
		 * The meta object literal for the '{@link org.eclipse.capra.generic.metadatamodel.impl.ArtifactMetadataImpl <em>Artifact Metadata</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.capra.generic.metadatamodel.impl.ArtifactMetadataImpl
		 * @see org.eclipse.capra.generic.metadatamodel.impl.MetadatamodelPackageImpl#getArtifactMetadata()
		 * @generated
		 */
		EClass ARTIFACT_METADATA = eINSTANCE.getArtifactMetadata();

		/**
		 * The meta object literal for the '<em><b>Responsible User</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ARTIFACT_METADATA__RESPONSIBLE_USER = eINSTANCE.getArtifactMetadata_ResponsibleUser();

		/**
		 * The meta object literal for the '<em><b>Artifact</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ARTIFACT_METADATA__ARTIFACT = eINSTANCE.getArtifactMetadata_Artifact();

	}

} //MetadatamodelPackage
