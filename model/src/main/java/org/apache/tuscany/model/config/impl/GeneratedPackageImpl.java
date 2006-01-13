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

import org.apache.tuscany.model.config.GeneratedPackage;
import org.apache.tuscany.model.config.ModelConfigurationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Generated Package</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link GeneratedPackageImpl#getAny <em>Any</em>}</li>
 * <li>{@link GeneratedPackageImpl#getPackageClassName <em>Package Class Name</em>}</li>
 * <li>{@link GeneratedPackageImpl#isPreLoad <em>Pre Load</em>}</li>
 * <li>{@link GeneratedPackageImpl#getUri <em>Uri</em>}</li>
 * <li>{@link GeneratedPackageImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GeneratedPackageImpl extends EObjectImpl implements GeneratedPackage {
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
     * The default value of the '{@link #getPackageClassName() <em>Package Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getPackageClassName()
     */
    protected static final String PACKAGE_CLASS_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPackageClassName() <em>Package Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getPackageClassName()
     */
    protected String packageClassName = PACKAGE_CLASS_NAME_EDEFAULT;

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
    protected GeneratedPackageImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return ModelConfigurationPackage.eINSTANCE.getGeneratedPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FeatureMap getAny() {
        if (any == null) {
            any = new BasicFeatureMap(this, ModelConfigurationPackage.GENERATED_PACKAGE__ANY);
        }
        return any;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getPackageClassName() {
        return packageClassName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setPackageClassName(String newPackageClassName) {
        String oldPackageClassName = packageClassName;
        packageClassName = newPackageClassName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelConfigurationPackage.GENERATED_PACKAGE__PACKAGE_CLASS_NAME, oldPackageClassName, packageClassName));
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
            eNotify(new ENotificationImpl(this, Notification.SET, ModelConfigurationPackage.GENERATED_PACKAGE__PRE_LOAD, oldPreLoad, preLoad, !oldPreLoadESet));
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
            eNotify(new ENotificationImpl(this, Notification.UNSET, ModelConfigurationPackage.GENERATED_PACKAGE__PRE_LOAD, oldPreLoad, PRE_LOAD_EDEFAULT, oldPreLoadESet));
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
            eNotify(new ENotificationImpl(this, Notification.SET, ModelConfigurationPackage.GENERATED_PACKAGE__URI, oldUri, uri));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FeatureMap getAnyAttribute() {
        if (anyAttribute == null) {
            anyAttribute = new BasicFeatureMap(this, ModelConfigurationPackage.GENERATED_PACKAGE__ANY_ATTRIBUTE);
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
            case ModelConfigurationPackage.GENERATED_PACKAGE__ANY:
                return ((InternalEList) getAny()).basicRemove(otherEnd, msgs);
            case ModelConfigurationPackage.GENERATED_PACKAGE__ANY_ATTRIBUTE:
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
        case ModelConfigurationPackage.GENERATED_PACKAGE__ANY:
            return getAny();
        case ModelConfigurationPackage.GENERATED_PACKAGE__PACKAGE_CLASS_NAME:
            return getPackageClassName();
        case ModelConfigurationPackage.GENERATED_PACKAGE__PRE_LOAD:
            return isPreLoad() ? Boolean.TRUE : Boolean.FALSE;
        case ModelConfigurationPackage.GENERATED_PACKAGE__URI:
            return getUri();
        case ModelConfigurationPackage.GENERATED_PACKAGE__ANY_ATTRIBUTE:
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
        case ModelConfigurationPackage.GENERATED_PACKAGE__ANY:
            getAny().clear();
            getAny().addAll((Collection) newValue);
            return;
        case ModelConfigurationPackage.GENERATED_PACKAGE__PACKAGE_CLASS_NAME:
            setPackageClassName((String) newValue);
            return;
        case ModelConfigurationPackage.GENERATED_PACKAGE__PRE_LOAD:
            setPreLoad(((Boolean) newValue).booleanValue());
            return;
        case ModelConfigurationPackage.GENERATED_PACKAGE__URI:
            setUri((String) newValue);
            return;
        case ModelConfigurationPackage.GENERATED_PACKAGE__ANY_ATTRIBUTE:
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
        case ModelConfigurationPackage.GENERATED_PACKAGE__ANY:
            getAny().clear();
            return;
        case ModelConfigurationPackage.GENERATED_PACKAGE__PACKAGE_CLASS_NAME:
            setPackageClassName(PACKAGE_CLASS_NAME_EDEFAULT);
            return;
        case ModelConfigurationPackage.GENERATED_PACKAGE__PRE_LOAD:
            unsetPreLoad();
            return;
        case ModelConfigurationPackage.GENERATED_PACKAGE__URI:
            setUri(URI_EDEFAULT);
            return;
        case ModelConfigurationPackage.GENERATED_PACKAGE__ANY_ATTRIBUTE:
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
        case ModelConfigurationPackage.GENERATED_PACKAGE__ANY:
            return any != null && !any.isEmpty();
        case ModelConfigurationPackage.GENERATED_PACKAGE__PACKAGE_CLASS_NAME:
            return PACKAGE_CLASS_NAME_EDEFAULT == null ? packageClassName != null : !PACKAGE_CLASS_NAME_EDEFAULT.equals(packageClassName);
        case ModelConfigurationPackage.GENERATED_PACKAGE__PRE_LOAD:
            return isSetPreLoad();
        case ModelConfigurationPackage.GENERATED_PACKAGE__URI:
            return URI_EDEFAULT == null ? uri != null : !URI_EDEFAULT.equals(uri);
        case ModelConfigurationPackage.GENERATED_PACKAGE__ANY_ATTRIBUTE:
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
        result.append(", packageClassName: ");
        result.append(packageClassName);
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

} //GeneratedPackageImpl
