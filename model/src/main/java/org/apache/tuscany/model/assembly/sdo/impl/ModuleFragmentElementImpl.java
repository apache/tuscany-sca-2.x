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
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.sdo.impl.EDataObjectImpl;
import org.eclipse.emf.ecore.sdo.util.BasicESequence;
import org.eclipse.emf.ecore.sdo.util.ESequence;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.osoa.sca.model.Component;
import org.osoa.sca.model.EntryPoint;
import org.osoa.sca.model.ExternalService;
import org.osoa.sca.model.ModuleFragment;
import org.osoa.sca.model.ModuleWire;

import org.apache.tuscany.model.assembly.sdo.AssemblyPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Module Fragment</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link ModuleFragmentElementImpl#getEntryPoints <em>Entry Points</em>}</li>
 * <li>{@link ModuleFragmentElementImpl#getComponents <em>Components</em>}</li>
 * <li>{@link ModuleFragmentElementImpl#getExternalServices <em>External Services</em>}</li>
 * <li>{@link ModuleFragmentElementImpl#getWires <em>Wires</em>}</li>
 * <li>{@link ModuleFragmentElementImpl#getAny <em>Any</em>}</li>
 * <li>{@link ModuleFragmentElementImpl#getName <em>Name</em>}</li>
 * <li>{@link ModuleFragmentElementImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ModuleFragmentElementImpl extends EDataObjectImpl implements ModuleFragment {
    /**
     * The cached value of the '{@link #getEntryPoints() <em>Entry Points</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getEntryPoints()
     */
    protected EList entryPoints = null;

    /**
     * The cached value of the '{@link #getComponents() <em>Components</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getComponents()
     */
    protected EList components = null;

    /**
     * The cached value of the '{@link #getExternalServices() <em>External Services</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getExternalServices()
     */
    protected EList externalServices = null;

    /**
     * The cached value of the '{@link #getWires() <em>Wires</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getWires()
     */
    protected EList wires = null;

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
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getName()
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getName()
     */
    protected String name = NAME_EDEFAULT;

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
    protected ModuleFragmentElementImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return AssemblyPackage.eINSTANCE.getModuleFragment();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public List getEntryPoints() {
        if (entryPoints == null) {
            entryPoints = new EObjectContainmentEList(EntryPoint.class, this, AssemblyPackage.MODULE_FRAGMENT__ENTRY_POINTS);
        }
        return entryPoints;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public List getComponents() {
        if (components == null) {
            components = new EObjectContainmentEList(Component.class, this, AssemblyPackage.MODULE_FRAGMENT__COMPONENTS);
        }
        return components;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public List getExternalServices() {
        if (externalServices == null) {
            externalServices = new EObjectContainmentEList(ExternalService.class, this, AssemblyPackage.MODULE_FRAGMENT__EXTERNAL_SERVICES);
        }
        return externalServices;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public List getWires() {
        if (wires == null) {
            wires = new EObjectContainmentEList(ModuleWire.class, this, AssemblyPackage.MODULE_FRAGMENT__WIRES);
        }
        return wires;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getAny() {
        if (any == null) {
            any = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.MODULE_FRAGMENT__ANY));
        }
        return any;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AssemblyPackage.MODULE_FRAGMENT__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getAnyAttribute() {
        if (anyAttribute == null) {
            anyAttribute = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.MODULE_FRAGMENT__ANY_ATTRIBUTE));
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
            case AssemblyPackage.MODULE_FRAGMENT__ENTRY_POINTS:
                return ((InternalEList) getEntryPoints()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.MODULE_FRAGMENT__COMPONENTS:
                return ((InternalEList) getComponents()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.MODULE_FRAGMENT__EXTERNAL_SERVICES:
                return ((InternalEList) getExternalServices()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.MODULE_FRAGMENT__WIRES:
                return ((InternalEList) getWires()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.MODULE_FRAGMENT__ANY:
                return ((InternalEList) ((ESequence) getAny()).featureMap()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.MODULE_FRAGMENT__ANY_ATTRIBUTE:
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
        case AssemblyPackage.MODULE_FRAGMENT__ENTRY_POINTS:
            return getEntryPoints();
        case AssemblyPackage.MODULE_FRAGMENT__COMPONENTS:
            return getComponents();
        case AssemblyPackage.MODULE_FRAGMENT__EXTERNAL_SERVICES:
            return getExternalServices();
        case AssemblyPackage.MODULE_FRAGMENT__WIRES:
            return getWires();
        case AssemblyPackage.MODULE_FRAGMENT__ANY:
            return ((ESequence) getAny()).featureMap();
        case AssemblyPackage.MODULE_FRAGMENT__NAME:
            return getName();
        case AssemblyPackage.MODULE_FRAGMENT__ANY_ATTRIBUTE:
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
        case AssemblyPackage.MODULE_FRAGMENT__ENTRY_POINTS:
            getEntryPoints().clear();
            getEntryPoints().addAll((Collection) newValue);
            return;
        case AssemblyPackage.MODULE_FRAGMENT__COMPONENTS:
            getComponents().clear();
            getComponents().addAll((Collection) newValue);
            return;
        case AssemblyPackage.MODULE_FRAGMENT__EXTERNAL_SERVICES:
            getExternalServices().clear();
            getExternalServices().addAll((Collection) newValue);
            return;
        case AssemblyPackage.MODULE_FRAGMENT__WIRES:
            getWires().clear();
            getWires().addAll((Collection) newValue);
            return;
        case AssemblyPackage.MODULE_FRAGMENT__ANY:
            ((ESequence) getAny()).featureMap().clear();
            ((ESequence) getAny()).featureMap().addAll((Collection) newValue);
            return;
        case AssemblyPackage.MODULE_FRAGMENT__NAME:
            setName((String) newValue);
            return;
        case AssemblyPackage.MODULE_FRAGMENT__ANY_ATTRIBUTE:
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
        case AssemblyPackage.MODULE_FRAGMENT__ENTRY_POINTS:
            getEntryPoints().clear();
            return;
        case AssemblyPackage.MODULE_FRAGMENT__COMPONENTS:
            getComponents().clear();
            return;
        case AssemblyPackage.MODULE_FRAGMENT__EXTERNAL_SERVICES:
            getExternalServices().clear();
            return;
        case AssemblyPackage.MODULE_FRAGMENT__WIRES:
            getWires().clear();
            return;
        case AssemblyPackage.MODULE_FRAGMENT__ANY:
            ((ESequence) getAny()).featureMap().clear();
            return;
        case AssemblyPackage.MODULE_FRAGMENT__NAME:
            setName(NAME_EDEFAULT);
            return;
        case AssemblyPackage.MODULE_FRAGMENT__ANY_ATTRIBUTE:
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
        case AssemblyPackage.MODULE_FRAGMENT__ENTRY_POINTS:
            return entryPoints != null && !entryPoints.isEmpty();
        case AssemblyPackage.MODULE_FRAGMENT__COMPONENTS:
            return components != null && !components.isEmpty();
        case AssemblyPackage.MODULE_FRAGMENT__EXTERNAL_SERVICES:
            return externalServices != null && !externalServices.isEmpty();
        case AssemblyPackage.MODULE_FRAGMENT__WIRES:
            return wires != null && !wires.isEmpty();
        case AssemblyPackage.MODULE_FRAGMENT__ANY:
            return any != null && !any.featureMap().isEmpty();
        case AssemblyPackage.MODULE_FRAGMENT__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case AssemblyPackage.MODULE_FRAGMENT__ANY_ATTRIBUTE:
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
        result.append(", name: ");
        result.append(name);
        result.append(", anyAttribute: ");
        result.append(anyAttribute);
        result.append(')');
		return result.toString();
	}

} //ModuleFragmentImpl
