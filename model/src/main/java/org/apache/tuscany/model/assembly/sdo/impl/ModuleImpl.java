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

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.sdo.util.ESequence;
import org.eclipse.emf.ecore.util.InternalEList;
import org.osoa.sca.model.Module;

import org.apache.tuscany.model.assembly.sdo.AssemblyPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Module</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * </p>
 *
 * @generated
 */
public class ModuleImpl extends ModuleFragmentElementImpl implements Module {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected ModuleImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return AssemblyPackage.eINSTANCE.getModule();
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
            case AssemblyPackage.MODULE__ENTRY_POINTS:
                return ((InternalEList) getEntryPoints()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.MODULE__COMPONENTS:
                return ((InternalEList) getComponents()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.MODULE__EXTERNAL_SERVICES:
                return ((InternalEList) getExternalServices()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.MODULE__WIRES:
                return ((InternalEList) getWires()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.MODULE__ANY:
                return ((InternalEList) ((ESequence) getAny()).featureMap()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.MODULE__ANY_ATTRIBUTE:
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
        case AssemblyPackage.MODULE__ENTRY_POINTS:
            return getEntryPoints();
        case AssemblyPackage.MODULE__COMPONENTS:
            return getComponents();
        case AssemblyPackage.MODULE__EXTERNAL_SERVICES:
            return getExternalServices();
        case AssemblyPackage.MODULE__WIRES:
            return getWires();
        case AssemblyPackage.MODULE__ANY:
            return ((ESequence) getAny()).featureMap();
        case AssemblyPackage.MODULE__NAME:
            return getName();
        case AssemblyPackage.MODULE__ANY_ATTRIBUTE:
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
        case AssemblyPackage.MODULE__ENTRY_POINTS:
            getEntryPoints().clear();
            getEntryPoints().addAll((Collection) newValue);
            return;
        case AssemblyPackage.MODULE__COMPONENTS:
            getComponents().clear();
            getComponents().addAll((Collection) newValue);
            return;
        case AssemblyPackage.MODULE__EXTERNAL_SERVICES:
            getExternalServices().clear();
            getExternalServices().addAll((Collection) newValue);
            return;
        case AssemblyPackage.MODULE__WIRES:
            getWires().clear();
            getWires().addAll((Collection) newValue);
            return;
        case AssemblyPackage.MODULE__ANY:
            ((ESequence) getAny()).featureMap().clear();
            ((ESequence) getAny()).featureMap().addAll((Collection) newValue);
            return;
        case AssemblyPackage.MODULE__NAME:
            setName((String) newValue);
            return;
        case AssemblyPackage.MODULE__ANY_ATTRIBUTE:
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
        case AssemblyPackage.MODULE__ENTRY_POINTS:
            getEntryPoints().clear();
            return;
        case AssemblyPackage.MODULE__COMPONENTS:
            getComponents().clear();
            return;
        case AssemblyPackage.MODULE__EXTERNAL_SERVICES:
            getExternalServices().clear();
            return;
        case AssemblyPackage.MODULE__WIRES:
            getWires().clear();
            return;
        case AssemblyPackage.MODULE__ANY:
            ((ESequence) getAny()).featureMap().clear();
            return;
        case AssemblyPackage.MODULE__NAME:
            setName(NAME_EDEFAULT);
            return;
        case AssemblyPackage.MODULE__ANY_ATTRIBUTE:
            ((ESequence) getAnyAttribute()).featureMap().clear();
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
        case AssemblyPackage.MODULE__ENTRY_POINTS:
            return entryPoints != null && !entryPoints.isEmpty();
        case AssemblyPackage.MODULE__COMPONENTS:
            return components != null && !components.isEmpty();
        case AssemblyPackage.MODULE__EXTERNAL_SERVICES:
            return externalServices != null && !externalServices.isEmpty();
        case AssemblyPackage.MODULE__WIRES:
            return wires != null && !wires.isEmpty();
        case AssemblyPackage.MODULE__ANY:
            return any != null && !any.featureMap().isEmpty();
        case AssemblyPackage.MODULE__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case AssemblyPackage.MODULE__ANY_ATTRIBUTE:
            return anyAttribute != null && !anyAttribute.featureMap().isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

} //ModuleImpl
