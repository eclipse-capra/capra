/**
 */
package org.eclipse.capra.generic.artifactmodel.impl;

import org.eclipse.capra.generic.artifactmodel.ArtifactWrapper;
import org.eclipse.capra.generic.artifactmodel.ArtifactmodelPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Artifact Wrapper</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.capra.generic.artifactmodel.impl.ArtifactWrapperImpl#getPath <em>Path</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.artifactmodel.impl.ArtifactWrapperImpl#getUri <em>Uri</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.artifactmodel.impl.ArtifactWrapperImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.artifactmodel.impl.ArtifactWrapperImpl#getIdentifier <em>Identifier</em>}</li>
 *   <li>{@link org.eclipse.capra.generic.artifactmodel.impl.ArtifactWrapperImpl#getArtifactHandler <em>Artifact Handler</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ArtifactWrapperImpl extends MinimalEObjectImpl.Container implements ArtifactWrapper {
	/**
	 * The default value of the '{@link #getPath() <em>Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPath()
	 * @generated
	 * @ordered
	 */
	protected static final String PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPath() <em>Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPath()
	 * @generated
	 * @ordered
	 */
	protected String path = PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #getUri() <em>Uri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUri()
	 * @generated
	 * @ordered
	 */
	protected static final String URI_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUri() <em>Uri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUri()
	 * @generated
	 * @ordered
	 */
	protected String uri = URI_EDEFAULT;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getIdentifier() <em>Identifier</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdentifier()
	 * @generated
	 * @ordered
	 */
	protected static final String IDENTIFIER_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getIdentifier() <em>Identifier</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdentifier()
	 * @generated
	 * @ordered
	 */
	protected String identifier = IDENTIFIER_EDEFAULT;

	/**
	 * The default value of the '{@link #getArtifactHandler() <em>Artifact Handler</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getArtifactHandler()
	 * @generated
	 * @ordered
	 */
	protected static final String ARTIFACT_HANDLER_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getArtifactHandler() <em>Artifact Handler</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getArtifactHandler()
	 * @generated
	 * @ordered
	 */
	protected String artifactHandler = ARTIFACT_HANDLER_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ArtifactWrapperImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ArtifactmodelPackage.Literals.ARTIFACT_WRAPPER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPath() {
		return path;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPath(String newPath) {
		String oldPath = path;
		path = newPath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ArtifactmodelPackage.ARTIFACT_WRAPPER__PATH, oldPath, path));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUri(String newUri) {
		String oldUri = uri;
		uri = newUri;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ArtifactmodelPackage.ARTIFACT_WRAPPER__URI, oldUri, uri));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ArtifactmodelPackage.ARTIFACT_WRAPPER__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIdentifier(String newIdentifier) {
		String oldIdentifier = identifier;
		identifier = newIdentifier;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ArtifactmodelPackage.ARTIFACT_WRAPPER__IDENTIFIER, oldIdentifier, identifier));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getArtifactHandler() {
		return artifactHandler;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setArtifactHandler(String newArtifactHandler) {
		String oldArtifactHandler = artifactHandler;
		artifactHandler = newArtifactHandler;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ArtifactmodelPackage.ARTIFACT_WRAPPER__ARTIFACT_HANDLER, oldArtifactHandler, artifactHandler));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__PATH:
				return getPath();
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__URI:
				return getUri();
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__NAME:
				return getName();
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__IDENTIFIER:
				return getIdentifier();
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__ARTIFACT_HANDLER:
				return getArtifactHandler();
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
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__PATH:
				setPath((String)newValue);
				return;
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__URI:
				setUri((String)newValue);
				return;
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__NAME:
				setName((String)newValue);
				return;
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__IDENTIFIER:
				setIdentifier((String)newValue);
				return;
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__ARTIFACT_HANDLER:
				setArtifactHandler((String)newValue);
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
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__PATH:
				setPath(PATH_EDEFAULT);
				return;
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__URI:
				setUri(URI_EDEFAULT);
				return;
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__IDENTIFIER:
				setIdentifier(IDENTIFIER_EDEFAULT);
				return;
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__ARTIFACT_HANDLER:
				setArtifactHandler(ARTIFACT_HANDLER_EDEFAULT);
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
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__PATH:
				return PATH_EDEFAULT == null ? path != null : !PATH_EDEFAULT.equals(path);
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__URI:
				return URI_EDEFAULT == null ? uri != null : !URI_EDEFAULT.equals(uri);
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__IDENTIFIER:
				return IDENTIFIER_EDEFAULT == null ? identifier != null : !IDENTIFIER_EDEFAULT.equals(identifier);
			case ArtifactmodelPackage.ARTIFACT_WRAPPER__ARTIFACT_HANDLER:
				return ARTIFACT_HANDLER_EDEFAULT == null ? artifactHandler != null : !ARTIFACT_HANDLER_EDEFAULT.equals(artifactHandler);
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
		result.append(" (path: ");
		result.append(path);
		result.append(", uri: ");
		result.append(uri);
		result.append(", name: ");
		result.append(name);
		result.append(", identifier: ");
		result.append(identifier);
		result.append(", ArtifactHandler: ");
		result.append(artifactHandler);
		result.append(')');
		return result.toString();
	}

} //ArtifactWrapperImpl
