/**
 */
package org.eclipse.capra.generic.metadatamodel;

import java.util.Date;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Trace Metadata</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Default metadata for a traceability link.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.generic.metadatamodel.TraceMetadata#getCreationDate <em>Creation Date</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.metadatamodel.TraceMetadata#getComment <em>Comment</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.metadatamodel.TraceMetadata#getCreationUser <em>Creation User</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.metadatamodel.TraceMetadata#getTrace <em>Trace</em>}</li>
 * </ul>
 *
 * @see org.eclipse.capra.generic.metadatamodel.MetadatamodelPackage#getTraceMetadata()
 * @model
 * @generated
 */
public interface TraceMetadata extends EObject {
	/**
	 * Returns the value of the '<em><b>Creation Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Creation Date</em>' attribute.
	 * @see #setCreationDate(Date)
	 * @see org.eclipse.capra.generic.metadatamodel.MetadatamodelPackage#getTraceMetadata_CreationDate()
	 * @model unique="false"
	 * @generated
	 */
	Date getCreationDate();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.generic.metadatamodel.TraceMetadata#getCreationDate <em>Creation Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Creation Date</em>' attribute.
	 * @see #getCreationDate()
	 * @generated
	 */
	void setCreationDate(Date value);

	/**
	 * Returns the value of the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Comment</em>' attribute.
	 * @see #setComment(String)
	 * @see org.eclipse.capra.generic.metadatamodel.MetadatamodelPackage#getTraceMetadata_Comment()
	 * @model unique="false"
	 * @generated
	 */
	String getComment();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.generic.metadatamodel.TraceMetadata#getComment <em>Comment</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Comment</em>' attribute.
	 * @see #getComment()
	 * @generated
	 */
	void setComment(String value);

	/**
	 * Returns the value of the '<em><b>Creation User</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Creation User</em>' containment reference.
	 * @see #setCreationUser(Person)
	 * @see org.eclipse.capra.generic.metadatamodel.MetadatamodelPackage#getTraceMetadata_CreationUser()
	 * @model containment="true"
	 * @generated
	 */
	Person getCreationUser();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.generic.metadatamodel.TraceMetadata#getCreationUser <em>Creation User</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Creation User</em>' containment reference.
	 * @see #getCreationUser()
	 * @generated
	 */
	void setCreationUser(Person value);

	/**
	 * Returns the value of the '<em><b>Trace</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Trace</em>' reference.
	 * @see #setTrace(EObject)
	 * @see org.eclipse.capra.generic.metadatamodel.MetadatamodelPackage#getTraceMetadata_Trace()
	 * @model
	 * @generated
	 */
	EObject getTrace();

	/**
	 * Sets the value of the '{@link org.eclipse.capra.generic.metadatamodel.TraceMetadata#getTrace <em>Trace</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Trace</em>' reference.
	 * @see #getTrace()
	 * @generated
	 */
	void setTrace(EObject value);

} // TraceMetadata
