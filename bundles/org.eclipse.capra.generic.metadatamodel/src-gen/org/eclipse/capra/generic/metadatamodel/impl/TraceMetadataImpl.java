/**
 */
package org.eclipse.capra.generic.metadatamodel.impl;

import java.util.Date;

import org.eclipse.capra.generic.metadatamodel.MetadatamodelPackage;
import org.eclipse.capra.generic.metadatamodel.Person;
import org.eclipse.capra.generic.metadatamodel.TraceMetadata;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Trace Metadata</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.generic.metadatamodel.impl.TraceMetadataImpl#getCreationDate <em>Creation Date</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.metadatamodel.impl.TraceMetadataImpl#getComment <em>Comment</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.metadatamodel.impl.TraceMetadataImpl#getCreationUser <em>Creation User</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.metadatamodel.impl.TraceMetadataImpl#getTrace <em>Trace</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TraceMetadataImpl extends MinimalEObjectImpl.Container implements TraceMetadata {
	/**
	 * The default value of the '{@link #getCreationDate() <em>Creation Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCreationDate()
	 * @generated
	 * @ordered
	 */
	protected static final Date CREATION_DATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCreationDate() <em>Creation Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCreationDate()
	 * @generated
	 * @ordered
	 */
	protected Date creationDate = CREATION_DATE_EDEFAULT;

	/**
	 * The default value of the '{@link #getComment() <em>Comment</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComment()
	 * @generated
	 * @ordered
	 */
	protected static final String COMMENT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getComment() <em>Comment</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComment()
	 * @generated
	 * @ordered
	 */
	protected String comment = COMMENT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getCreationUser() <em>Creation User</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCreationUser()
	 * @generated
	 * @ordered
	 */
	protected Person creationUser;

	/**
	 * The cached value of the '{@link #getTrace() <em>Trace</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTrace()
	 * @generated
	 * @ordered
	 */
	protected EObject trace;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TraceMetadataImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MetadatamodelPackage.Literals.TRACE_METADATA;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCreationDate(Date newCreationDate) {
		Date oldCreationDate = creationDate;
		creationDate = newCreationDate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadatamodelPackage.TRACE_METADATA__CREATION_DATE, oldCreationDate, creationDate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setComment(String newComment) {
		String oldComment = comment;
		comment = newComment;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadatamodelPackage.TRACE_METADATA__COMMENT, oldComment, comment));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Person getCreationUser() {
		return creationUser;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCreationUser(Person newCreationUser, NotificationChain msgs) {
		Person oldCreationUser = creationUser;
		creationUser = newCreationUser;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MetadatamodelPackage.TRACE_METADATA__CREATION_USER, oldCreationUser, newCreationUser);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCreationUser(Person newCreationUser) {
		if (newCreationUser != creationUser) {
			NotificationChain msgs = null;
			if (creationUser != null)
				msgs = ((InternalEObject)creationUser).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MetadatamodelPackage.TRACE_METADATA__CREATION_USER, null, msgs);
			if (newCreationUser != null)
				msgs = ((InternalEObject)newCreationUser).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MetadatamodelPackage.TRACE_METADATA__CREATION_USER, null, msgs);
			msgs = basicSetCreationUser(newCreationUser, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadatamodelPackage.TRACE_METADATA__CREATION_USER, newCreationUser, newCreationUser));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EObject getTrace() {
		if (trace != null && trace.eIsProxy()) {
			InternalEObject oldTrace = (InternalEObject)trace;
			trace = eResolveProxy(oldTrace);
			if (trace != oldTrace) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MetadatamodelPackage.TRACE_METADATA__TRACE, oldTrace, trace));
			}
		}
		return trace;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EObject basicGetTrace() {
		return trace;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTrace(EObject newTrace) {
		EObject oldTrace = trace;
		trace = newTrace;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadatamodelPackage.TRACE_METADATA__TRACE, oldTrace, trace));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MetadatamodelPackage.TRACE_METADATA__CREATION_USER:
				return basicSetCreationUser(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MetadatamodelPackage.TRACE_METADATA__CREATION_DATE:
				return getCreationDate();
			case MetadatamodelPackage.TRACE_METADATA__COMMENT:
				return getComment();
			case MetadatamodelPackage.TRACE_METADATA__CREATION_USER:
				return getCreationUser();
			case MetadatamodelPackage.TRACE_METADATA__TRACE:
				if (resolve) return getTrace();
				return basicGetTrace();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MetadatamodelPackage.TRACE_METADATA__CREATION_DATE:
				setCreationDate((Date)newValue);
				return;
			case MetadatamodelPackage.TRACE_METADATA__COMMENT:
				setComment((String)newValue);
				return;
			case MetadatamodelPackage.TRACE_METADATA__CREATION_USER:
				setCreationUser((Person)newValue);
				return;
			case MetadatamodelPackage.TRACE_METADATA__TRACE:
				setTrace((EObject)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case MetadatamodelPackage.TRACE_METADATA__CREATION_DATE:
				setCreationDate(CREATION_DATE_EDEFAULT);
				return;
			case MetadatamodelPackage.TRACE_METADATA__COMMENT:
				setComment(COMMENT_EDEFAULT);
				return;
			case MetadatamodelPackage.TRACE_METADATA__CREATION_USER:
				setCreationUser((Person)null);
				return;
			case MetadatamodelPackage.TRACE_METADATA__TRACE:
				setTrace((EObject)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case MetadatamodelPackage.TRACE_METADATA__CREATION_DATE:
				return CREATION_DATE_EDEFAULT == null ? creationDate != null : !CREATION_DATE_EDEFAULT.equals(creationDate);
			case MetadatamodelPackage.TRACE_METADATA__COMMENT:
				return COMMENT_EDEFAULT == null ? comment != null : !COMMENT_EDEFAULT.equals(comment);
			case MetadatamodelPackage.TRACE_METADATA__CREATION_USER:
				return creationUser != null;
			case MetadatamodelPackage.TRACE_METADATA__TRACE:
				return trace != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (creationDate: ");
		result.append(creationDate);
		result.append(", comment: ");
		result.append(comment);
		result.append(')');
		return result.toString();
	}

} //TraceMetadataImpl
