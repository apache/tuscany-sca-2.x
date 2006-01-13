/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.config.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.apache.tuscany.model.config.DynamicPackageLoader;
import org.apache.tuscany.model.config.ModelConfigurationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Dynamic Package Loader</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link DynamicPackageLoaderImpl#getAny <em>Any</em>}</li>
 * <li>{@link DynamicPackageLoaderImpl#getClassName <em>Class Name</em>}</li>
 * <li>{@link DynamicPackageLoaderImpl#getExtension <em>Extension</em>}</li>
 * <li>{@link DynamicPackageLoaderImpl#getProtocol <em>Protocol</em>}</li>
 * <li>{@link DynamicPackageLoaderImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DynamicPackageLoaderImpl extends EObjectImpl implements DynamicPackageLoader {
    /**
     * The cached value of the '{@link #getAny() <em>Any</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getAny()
     */
    protected FeatureMap any = null;

    /**
     * The default value of the '{@link #getClassName() <em>Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getClassName()
     */
    protected static final String CLASS_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getClassName() <em>Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getClassName()
     */
    protected String className = CLASS_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getExtension() <em>Extension</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getExtension()
     */
    protected static final String EXTENSION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getExtension() <em>Extension</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getExtension()
     */
    protected String extension = EXTENSION_EDEFAULT;

    /**
     * The default value of the '{@link #getProtocol() <em>Protocol</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getProtocol()
     */
    protected static final String PROTOCOL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getProtocol() <em>Protocol</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getProtocol()
     */
    protected String protocol = PROTOCOL_EDEFAULT;

    /**
     * The cached value of the '{@link #getAnyAttribute() <em>Any Attribute</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getAnyAttribute()
     */
    protected FeatureMap anyAttribute = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected DynamicPackageLoaderImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return ModelConfigurationPackage.eINSTANCE.getDynamicPackageLoader();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FeatureMap getAny() {
        if (any == null) {
            any = new BasicFeatureMap(this, ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__ANY);
        }
        return any;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getClassName() {
        return className;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setClassName(String newClassName) {
        String oldClassName = className;
        className = newClassName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__CLASS_NAME, oldClassName, className));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getExtension() {
        return extension;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setExtension(String newExtension) {
        String oldExtension = extension;
        extension = newExtension;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__EXTENSION, oldExtension, extension));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setProtocol(String newProtocol) {
        String oldProtocol = protocol;
        protocol = newProtocol;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__PROTOCOL, oldProtocol, protocol));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FeatureMap getAnyAttribute() {
        if (anyAttribute == null) {
            anyAttribute = new BasicFeatureMap(this, ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__ANY_ATTRIBUTE);
        }
        return anyAttribute;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
            case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__ANY:
                return ((InternalEList) getAny()).basicRemove(otherEnd, msgs);
            case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__ANY_ATTRIBUTE:
                return ((InternalEList) getAnyAttribute()).basicRemove(otherEnd, msgs);
            default:
                return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__ANY:
            return getAny();
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__CLASS_NAME:
            return getClassName();
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__EXTENSION:
            return getExtension();
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__PROTOCOL:
            return getProtocol();
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__ANY_ATTRIBUTE:
            return getAnyAttribute();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void eSet(EStructuralFeature eFeature, Object newValue) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__ANY:
            getAny().clear();
            getAny().addAll((Collection) newValue);
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__CLASS_NAME:
            setClassName((String) newValue);
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__EXTENSION:
            setExtension((String) newValue);
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__PROTOCOL:
            setProtocol((String) newValue);
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__ANY_ATTRIBUTE:
            getAnyAttribute().clear();
            getAnyAttribute().addAll((Collection) newValue);
            return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void eUnset(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__ANY:
            getAny().clear();
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__CLASS_NAME:
            setClassName(CLASS_NAME_EDEFAULT);
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__EXTENSION:
            setExtension(EXTENSION_EDEFAULT);
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__PROTOCOL:
            setProtocol(PROTOCOL_EDEFAULT);
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__ANY_ATTRIBUTE:
            getAnyAttribute().clear();
            return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__ANY:
            return any != null && !any.isEmpty();
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__CLASS_NAME:
            return CLASS_NAME_EDEFAULT == null ? className != null : !CLASS_NAME_EDEFAULT.equals(className);
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__EXTENSION:
            return EXTENSION_EDEFAULT == null ? extension != null : !EXTENSION_EDEFAULT.equals(extension);
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__PROTOCOL:
            return PROTOCOL_EDEFAULT == null ? protocol != null : !PROTOCOL_EDEFAULT.equals(protocol);
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER__ANY_ATTRIBUTE:
            return anyAttribute != null && !anyAttribute.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (any: ");
        result.append(any);
        result.append(", className: ");
        result.append(className);
        result.append(", extension: ");
        result.append(extension);
        result.append(", protocol: ");
        result.append(protocol);
        result.append(", anyAttribute: ");
        result.append(anyAttribute);
        result.append(')');
		return result.toString();
	}

} //DynamicPackageLoaderImpl
