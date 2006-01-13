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
package org.apache.tuscany.core.system.assembly.sdo.impl;

import java.util.Collection;

import commonj.sdo.Sequence;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.sdo.util.BasicESequence;
import org.eclipse.emf.ecore.sdo.util.ESequence;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.apache.tuscany.core.system.assembly.sdo.SystemAssemblyPackage;
import org.apache.tuscany.core.system.assembly.sdo.SystemImplementation;
import org.apache.tuscany.model.assembly.sdo.impl.ImplementationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Extension Implementation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.tuscany.core.system.assembly.sdo.impl.SystemImplementationImpl#getAny <em>Any</em>}</li>
 *   <li>{@link org.apache.tuscany.core.system.assembly.sdo.impl.SystemImplementationImpl#getClass_ <em>Class</em>}</li>
 *   <li>{@link org.apache.tuscany.core.system.assembly.sdo.impl.SystemImplementationImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SystemImplementationImpl extends ImplementationImpl implements SystemImplementation {
    /**
     * The cached value of the '{@link #getAny() <em>Any</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAny()
     * @generated
     * @ordered
     */
    protected ESequence any = null;

    /**
     * The default value of the '{@link #getClass_() <em>Class</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getClass_()
     * @generated
     * @ordered
     */
    protected static final String CLASS_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getClass_() <em>Class</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getClass_()
     * @generated
     * @ordered
     */
    protected String class_ = CLASS_EDEFAULT;

    /**
     * The cached value of the '{@link #getAnyAttribute() <em>Any Attribute</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAnyAttribute()
     * @generated
     * @ordered
     */
    protected ESequence anyAttribute = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SystemImplementationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return SystemAssemblyPackage.eINSTANCE.getSystemImplementation();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Sequence getAny() {
        if (any == null) {
            any = new BasicESequence(new BasicFeatureMap(this, SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__ANY));
        }
        return any;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getClass_() {
        return class_;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setClass(String newClass) {
        String oldClass = class_;
        class_ = newClass;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__CLASS, oldClass, class_));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Sequence getAnyAttribute() {
        if (anyAttribute == null) {
            anyAttribute = new BasicESequence(new BasicFeatureMap(this, SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__ANY_ATTRIBUTE));
        }
        return anyAttribute;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__ANY:
                return ((InternalEList)((ESequence)getAny()).featureMap()).basicRemove(otherEnd, msgs);
                case SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__ANY_ATTRIBUTE:
                return ((InternalEList)((ESequence)getAnyAttribute()).featureMap()).basicRemove(otherEnd, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__ANY:
                return ((ESequence)getAny()).featureMap();
            case SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__CLASS:
                return getClass_();
            case SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__ANY_ATTRIBUTE:
                return ((ESequence)getAnyAttribute()).featureMap();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void eSet(EStructuralFeature eFeature, Object newValue) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__ANY:
                ((ESequence)getAny()).featureMap().clear();
                ((ESequence)getAny()).featureMap().addAll((Collection)newValue);
                return;
            case SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__CLASS:
                setClass((String)newValue);
                return;
            case SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__ANY_ATTRIBUTE:
                ((ESequence)getAnyAttribute()).featureMap().clear();
                ((ESequence)getAnyAttribute()).featureMap().addAll((Collection)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void eUnset(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__ANY:
                ((ESequence)getAny()).featureMap().clear();
                return;
            case SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__CLASS:
                setClass(CLASS_EDEFAULT);
                return;
            case SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__ANY_ATTRIBUTE:
                ((ESequence)getAnyAttribute()).featureMap().clear();
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__ANY:
                return any != null && !any.featureMap().isEmpty();
            case SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__CLASS:
                return CLASS_EDEFAULT == null ? class_ != null : !CLASS_EDEFAULT.equals(class_);
            case SystemAssemblyPackage.SYSTEM_IMPLEMENTATION__ANY_ATTRIBUTE:
                return anyAttribute != null && !anyAttribute.featureMap().isEmpty();
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
        result.append(", class: ");
        result.append(class_);
        result.append(", anyAttribute: ");
        result.append(anyAttribute);
        result.append(')');
        return result.toString();
    }

} //ExtensionImplementationImpl
