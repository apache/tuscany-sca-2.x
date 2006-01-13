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

import org.apache.tuscany.model.config.DynamicPackage;
import org.apache.tuscany.model.config.ModelConfigurationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Dynamic Package</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link DynamicPackageImpl#getAny <em>Any</em>}</li>
 * <li>{@link DynamicPackageImpl#getLoaderClassName <em>Loader Class Name</em>}</li>
 * <li>{@link DynamicPackageImpl#getLocation <em>Location</em>}</li>
 * <li>{@link DynamicPackageImpl#isPreLoad <em>Pre Load</em>}</li>
 * <li>{@link DynamicPackageImpl#getUri <em>Uri</em>}</li>
 * <li>{@link DynamicPackageImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DynamicPackageImpl extends EObjectImpl implements DynamicPackage {
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
     * The default value of the '{@link #getLoaderClassName() <em>Loader Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getLoaderClassName()
     */
    protected static final String LOADER_CLASS_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLoaderClassName() <em>Loader Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getLoaderClassName()
     */
    protected String loaderClassName = LOADER_CLASS_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getLocation() <em>Location</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getLocation()
     */
    protected static final String LOCATION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLocation() <em>Location</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getLocation()
     */
    protected String location = LOCATION_EDEFAULT;

    /**
     * The default value of the '{@link #isPreLoad() <em>Pre Load</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #isPreLoad()
     */
    protected static final boolean PRE_LOAD_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isPreLoad() <em>Pre Load</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #isPreLoad()
     */
    protected boolean preLoad = PRE_LOAD_EDEFAULT;

    /**
     * This is true if the Pre Load attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    protected boolean preLoadESet = false;

    /**
     * The default value of the '{@link #getUri() <em>Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getUri()
     */
    protected static final String URI_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUri() <em>Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getUri()
     */
    protected String uri = URI_EDEFAULT;

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
    protected DynamicPackageImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return ModelConfigurationPackage.eINSTANCE.getDynamicPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FeatureMap getAny() {
        if (any == null) {
            any = new BasicFeatureMap(this, ModelConfigurationPackage.DYNAMIC_PACKAGE__ANY);
        }
        return any;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getLoaderClassName() {
        return loaderClassName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setLoaderClassName(String newLoaderClassName) {
        String oldLoaderClassName = loaderClassName;
        loaderClassName = newLoaderClassName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelConfigurationPackage.DYNAMIC_PACKAGE__LOADER_CLASS_NAME, oldLoaderClassName, loaderClassName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getLocation() {
        return location;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setLocation(String newLocation) {
        String oldLocation = location;
        location = newLocation;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelConfigurationPackage.DYNAMIC_PACKAGE__LOCATION, oldLocation, location));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean isPreLoad() {
        return preLoad;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setPreLoad(boolean newPreLoad) {
        boolean oldPreLoad = preLoad;
        preLoad = newPreLoad;
        boolean oldPreLoadESet = preLoadESet;
        preLoadESet = true;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelConfigurationPackage.DYNAMIC_PACKAGE__PRE_LOAD, oldPreLoad, preLoad, !oldPreLoadESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void unsetPreLoad() {
        boolean oldPreLoad = preLoad;
        boolean oldPreLoadESet = preLoadESet;
        preLoad = PRE_LOAD_EDEFAULT;
        preLoadESet = false;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.UNSET, ModelConfigurationPackage.DYNAMIC_PACKAGE__PRE_LOAD, oldPreLoad, PRE_LOAD_EDEFAULT, oldPreLoadESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean isSetPreLoad() {
        return preLoadESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getUri() {
        return uri;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setUri(String newUri) {
        String oldUri = uri;
        uri = newUri;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelConfigurationPackage.DYNAMIC_PACKAGE__URI, oldUri, uri));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FeatureMap getAnyAttribute() {
        if (anyAttribute == null) {
            anyAttribute = new BasicFeatureMap(this, ModelConfigurationPackage.DYNAMIC_PACKAGE__ANY_ATTRIBUTE);
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
            case ModelConfigurationPackage.DYNAMIC_PACKAGE__ANY:
                return ((InternalEList) getAny()).basicRemove(otherEnd, msgs);
            case ModelConfigurationPackage.DYNAMIC_PACKAGE__ANY_ATTRIBUTE:
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
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__ANY:
            return getAny();
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__LOADER_CLASS_NAME:
            return getLoaderClassName();
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__LOCATION:
            return getLocation();
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__PRE_LOAD:
            return isPreLoad() ? Boolean.TRUE : Boolean.FALSE;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__URI:
            return getUri();
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__ANY_ATTRIBUTE:
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
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__ANY:
            getAny().clear();
            getAny().addAll((Collection) newValue);
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__LOADER_CLASS_NAME:
            setLoaderClassName((String) newValue);
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__LOCATION:
            setLocation((String) newValue);
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__PRE_LOAD:
            setPreLoad(((Boolean) newValue).booleanValue());
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__URI:
            setUri((String) newValue);
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__ANY_ATTRIBUTE:
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
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__ANY:
            getAny().clear();
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__LOADER_CLASS_NAME:
            setLoaderClassName(LOADER_CLASS_NAME_EDEFAULT);
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__LOCATION:
            setLocation(LOCATION_EDEFAULT);
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__PRE_LOAD:
            unsetPreLoad();
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__URI:
            setUri(URI_EDEFAULT);
            return;
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__ANY_ATTRIBUTE:
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
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__ANY:
            return any != null && !any.isEmpty();
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__LOADER_CLASS_NAME:
            return LOADER_CLASS_NAME_EDEFAULT == null ? loaderClassName != null : !LOADER_CLASS_NAME_EDEFAULT.equals(loaderClassName);
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__LOCATION:
            return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__PRE_LOAD:
            return isSetPreLoad();
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__URI:
            return URI_EDEFAULT == null ? uri != null : !URI_EDEFAULT.equals(uri);
        case ModelConfigurationPackage.DYNAMIC_PACKAGE__ANY_ATTRIBUTE:
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
        result.append(", loaderClassName: ");
        result.append(loaderClassName);
        result.append(", location: ");
        result.append(location);
        result.append(", preLoad: ");
        if (preLoadESet) result.append(preLoad);
        else
            result.append("<unset>");
        result.append(", uri: ");
        result.append(uri);
        result.append(", anyAttribute: ");
        result.append(anyAttribute);
		result.append(')');
		return result.toString();
	}

} //DynamicPackageImpl
