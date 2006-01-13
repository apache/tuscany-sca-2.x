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
package org.apache.tuscany.model.assembly.sdo.impl;

import java.util.Collection;
import java.util.List;

import commonj.sdo.Sequence;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.sdo.impl.EDataObjectImpl;
import org.eclipse.emf.ecore.sdo.util.BasicESequence;
import org.eclipse.emf.ecore.sdo.util.ESequence;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.osoa.sca.model.ComponentType;
import org.osoa.sca.model.Property;
import org.osoa.sca.model.Reference;
import org.osoa.sca.model.Service;

import org.apache.tuscany.model.assembly.sdo.AssemblyPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Component Type</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link ComponentTypeImpl#getServices <em>Services</em>}</li>
 * <li>{@link ComponentTypeImpl#getReferences <em>References</em>}</li>
 * <li>{@link ComponentTypeImpl#getProperties <em>Properties</em>}</li>
 * <li>{@link ComponentTypeImpl#getAny <em>Any</em>}</li>
 * <li>{@link ComponentTypeImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ComponentTypeImpl extends EDataObjectImpl implements ComponentType {
    /**
     * The cached value of the '{@link #getServices() <em>Services</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getServices()
     */
    protected EList services = null;

    /**
     * The cached value of the '{@link #getReferences() <em>References</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getReferences()
     */
    protected EList references = null;

    /**
     * The cached value of the '{@link #getProperties() <em>Properties</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getProperties()
     */
    protected EList properties = null;

    /**
     * The cached value of the '{@link #getAny() <em>Any</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getAny()
     */
    protected ESequence any = null;

    /**
     * The cached value of the '{@link #getAnyAttribute() <em>Any Attribute</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getAnyAttribute()
     */
    protected ESequence anyAttribute = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected ComponentTypeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return AssemblyPackage.eINSTANCE.getComponentType();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public List getServices() {
        if (services == null) {
            services = new EObjectContainmentEList(Service.class, this, AssemblyPackage.COMPONENT_TYPE__SERVICES);
        }
        return services;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public List getReferences() {
        if (references == null) {
            references = new EObjectContainmentEList(Reference.class, this, AssemblyPackage.COMPONENT_TYPE__REFERENCES);
        }
        return references;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public List getProperties() {
        if (properties == null) {
            properties = new EObjectContainmentEList(Property.class, this, AssemblyPackage.COMPONENT_TYPE__PROPERTIES);
        }
        return properties;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getAny() {
        if (any == null) {
            any = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.COMPONENT_TYPE__ANY));
        }
        return any;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getAnyAttribute() {
        if (anyAttribute == null) {
            anyAttribute = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.COMPONENT_TYPE__ANY_ATTRIBUTE));
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
            case AssemblyPackage.COMPONENT_TYPE__SERVICES:
                return ((InternalEList) getServices()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.COMPONENT_TYPE__REFERENCES:
                return ((InternalEList) getReferences()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.COMPONENT_TYPE__PROPERTIES:
                return ((InternalEList) getProperties()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.COMPONENT_TYPE__ANY:
                return ((InternalEList) ((ESequence) getAny()).featureMap()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.COMPONENT_TYPE__ANY_ATTRIBUTE:
                return ((InternalEList) ((ESequence) getAnyAttribute()).featureMap()).basicRemove(otherEnd, msgs);
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
        case AssemblyPackage.COMPONENT_TYPE__SERVICES:
            return getServices();
        case AssemblyPackage.COMPONENT_TYPE__REFERENCES:
            return getReferences();
        case AssemblyPackage.COMPONENT_TYPE__PROPERTIES:
            return getProperties();
        case AssemblyPackage.COMPONENT_TYPE__ANY:
            return ((ESequence) getAny()).featureMap();
        case AssemblyPackage.COMPONENT_TYPE__ANY_ATTRIBUTE:
            return ((ESequence) getAnyAttribute()).featureMap();
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
        case AssemblyPackage.COMPONENT_TYPE__SERVICES:
            getServices().clear();
            getServices().addAll((Collection) newValue);
            return;
        case AssemblyPackage.COMPONENT_TYPE__REFERENCES:
            getReferences().clear();
            getReferences().addAll((Collection) newValue);
            return;
        case AssemblyPackage.COMPONENT_TYPE__PROPERTIES:
            getProperties().clear();
            getProperties().addAll((Collection) newValue);
            return;
        case AssemblyPackage.COMPONENT_TYPE__ANY:
            ((ESequence) getAny()).featureMap().clear();
            ((ESequence) getAny()).featureMap().addAll((Collection) newValue);
            return;
        case AssemblyPackage.COMPONENT_TYPE__ANY_ATTRIBUTE:
            ((ESequence) getAnyAttribute()).featureMap().clear();
            ((ESequence) getAnyAttribute()).featureMap().addAll((Collection) newValue);
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
        case AssemblyPackage.COMPONENT_TYPE__SERVICES:
            getServices().clear();
            return;
        case AssemblyPackage.COMPONENT_TYPE__REFERENCES:
            getReferences().clear();
            return;
        case AssemblyPackage.COMPONENT_TYPE__PROPERTIES:
            getProperties().clear();
            return;
        case AssemblyPackage.COMPONENT_TYPE__ANY:
            ((ESequence) getAny()).featureMap().clear();
            return;
        case AssemblyPackage.COMPONENT_TYPE__ANY_ATTRIBUTE:
            ((ESequence) getAnyAttribute()).featureMap().clear();
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
        case AssemblyPackage.COMPONENT_TYPE__SERVICES:
            return services != null && !services.isEmpty();
        case AssemblyPackage.COMPONENT_TYPE__REFERENCES:
            return references != null && !references.isEmpty();
        case AssemblyPackage.COMPONENT_TYPE__PROPERTIES:
            return properties != null && !properties.isEmpty();
        case AssemblyPackage.COMPONENT_TYPE__ANY:
            return any != null && !any.featureMap().isEmpty();
        case AssemblyPackage.COMPONENT_TYPE__ANY_ATTRIBUTE:
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
        result.append(", anyAttribute: ");
        result.append(anyAttribute);
        result.append(')');
        return result.toString();
	}

} //ComponentTypeImpl
