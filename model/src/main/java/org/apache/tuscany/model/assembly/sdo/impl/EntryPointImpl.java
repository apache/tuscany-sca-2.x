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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.sdo.impl.EDataObjectImpl;
import org.eclipse.emf.ecore.sdo.util.BasicESequence;
import org.eclipse.emf.ecore.sdo.util.ESequence;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.osoa.sca.model.EntryPoint;
import org.osoa.sca.model.Interface;

import org.apache.tuscany.model.assembly.sdo.AssemblyPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Entry Point</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link EntryPointImpl#getInterfaceGroup <em>Interface Group</em>}</li>
 * <li>{@link EntryPointImpl#getInterface <em>Interface</em>}</li>
 * <li>{@link EntryPointImpl#getBindingGroup <em>Binding Group</em>}</li>
 * <li>{@link EntryPointImpl#getBinding <em>Binding</em>}</li>
 * <li>{@link EntryPointImpl#getReferences <em>References</em>}</li>
 * <li>{@link EntryPointImpl#getAny <em>Any</em>}</li>
 * <li>{@link EntryPointImpl#getMultiplicity <em>Multiplicity</em>}</li>
 * <li>{@link EntryPointImpl#getName <em>Name</em>}</li>
 * <li>{@link EntryPointImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EntryPointImpl extends EDataObjectImpl implements EntryPoint {
    /**
     * The cached value of the '{@link #getInterfaceGroup() <em>Interface Group</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getInterfaceGroup()
     */
    protected ESequence interfaceGroup = null;

    /**
     * The cached value of the '{@link #getBindingGroup() <em>Binding Group</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getBindingGroup()
     */
    protected ESequence bindingGroup = null;

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
     * The default value of the '{@link #getMultiplicity() <em>Multiplicity</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getMultiplicity()
     */
    protected static final String MULTIPLICITY_EDEFAULT = "1..1";

    /**
     * The cached value of the '{@link #getMultiplicity() <em>Multiplicity</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getMultiplicity()
     */
    protected String multiplicity = MULTIPLICITY_EDEFAULT;

    /**
     * This is true if the Multiplicity attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    protected boolean multiplicityESet = false;

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
    protected EntryPointImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return AssemblyPackage.eINSTANCE.getEntryPoint();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getInterfaceGroup() {
        if (interfaceGroup == null) {
            interfaceGroup = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.ENTRY_POINT__INTERFACE_GROUP));
        }
        return interfaceGroup;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Interface getInterface() {
        return (Interface) ((ESequence) getInterfaceGroup()).featureMap().get(AssemblyPackage.eINSTANCE.getEntryPoint_Interface(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetInterface(Interface newInterface, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getInterfaceGroup()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getEntryPoint_Interface(), newInterface, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setInterface(Interface newInterface) {
        ((FeatureMap.Internal) ((ESequence) getInterfaceGroup()).featureMap()).set(AssemblyPackage.eINSTANCE.getEntryPoint_Interface(), newInterface);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getBindingGroup() {
        if (bindingGroup == null) {
            bindingGroup = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.ENTRY_POINT__BINDING_GROUP));
        }
        return bindingGroup;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public List getBindings() {
        return ((ESequence) getBindingGroup()).featureMap().list(AssemblyPackage.eINSTANCE.getEntryPoint_Binding());
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public List getReferences() {
        if (references == null) {
            references = new EObjectContainmentEList(EObject.class, this, AssemblyPackage.ENTRY_POINT__REFERENCES);
        }
        return references;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getAny() {
        if (any == null) {
            any = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.ENTRY_POINT__ANY));
        }
        return any;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getMultiplicity() {
        return multiplicity;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setMultiplicity(String newMultiplicity) {
        String oldMultiplicity = multiplicity;
        multiplicity = newMultiplicity;
        boolean oldMultiplicityESet = multiplicityESet;
        multiplicityESet = true;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AssemblyPackage.ENTRY_POINT__MULTIPLICITY, oldMultiplicity, multiplicity, !oldMultiplicityESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void unsetMultiplicity() {
        String oldMultiplicity = multiplicity;
        boolean oldMultiplicityESet = multiplicityESet;
        multiplicity = MULTIPLICITY_EDEFAULT;
        multiplicityESet = false;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.UNSET, AssemblyPackage.ENTRY_POINT__MULTIPLICITY, oldMultiplicity, MULTIPLICITY_EDEFAULT, oldMultiplicityESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean isSetMultiplicity() {
        return multiplicityESet;
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
            eNotify(new ENotificationImpl(this, Notification.SET, AssemblyPackage.ENTRY_POINT__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getAnyAttribute() {
        if (anyAttribute == null) {
            anyAttribute = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.ENTRY_POINT__ANY_ATTRIBUTE));
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
            case AssemblyPackage.ENTRY_POINT__INTERFACE_GROUP:
                return ((InternalEList) ((ESequence) getInterfaceGroup()).featureMap()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.ENTRY_POINT__INTERFACE:
                return basicSetInterface(null, msgs);
            case AssemblyPackage.ENTRY_POINT__BINDING_GROUP:
                return ((InternalEList) ((ESequence) getBindingGroup()).featureMap()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.ENTRY_POINT__BINDING:
                return ((InternalEList) getBindings()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.ENTRY_POINT__REFERENCES:
                return ((InternalEList) getReferences()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.ENTRY_POINT__ANY:
                return ((InternalEList) ((ESequence) getAny()).featureMap()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.ENTRY_POINT__ANY_ATTRIBUTE:
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
        case AssemblyPackage.ENTRY_POINT__INTERFACE_GROUP:
            return ((ESequence) getInterfaceGroup()).featureMap();
        case AssemblyPackage.ENTRY_POINT__INTERFACE:
            return getInterface();
        case AssemblyPackage.ENTRY_POINT__BINDING_GROUP:
            return ((ESequence) getBindingGroup()).featureMap();
        case AssemblyPackage.ENTRY_POINT__BINDING:
            return getBindings();
        case AssemblyPackage.ENTRY_POINT__REFERENCES:
            return getReferences();
        case AssemblyPackage.ENTRY_POINT__ANY:
            return ((ESequence) getAny()).featureMap();
        case AssemblyPackage.ENTRY_POINT__MULTIPLICITY:
            return getMultiplicity();
        case AssemblyPackage.ENTRY_POINT__NAME:
            return getName();
        case AssemblyPackage.ENTRY_POINT__ANY_ATTRIBUTE:
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
        case AssemblyPackage.ENTRY_POINT__INTERFACE_GROUP:
            ((ESequence) getInterfaceGroup()).featureMap().clear();
            ((ESequence) getInterfaceGroup()).featureMap().addAll((Collection) newValue);
            return;
        case AssemblyPackage.ENTRY_POINT__INTERFACE:
            setInterface((Interface) newValue);
            return;
        case AssemblyPackage.ENTRY_POINT__BINDING_GROUP:
            ((ESequence) getBindingGroup()).featureMap().clear();
            ((ESequence) getBindingGroup()).featureMap().addAll((Collection) newValue);
            return;
        case AssemblyPackage.ENTRY_POINT__BINDING:
            getBindings().clear();
            getBindings().addAll((Collection) newValue);
            return;
        case AssemblyPackage.ENTRY_POINT__REFERENCES:
            getReferences().clear();
            getReferences().addAll((Collection) newValue);
            return;
        case AssemblyPackage.ENTRY_POINT__ANY:
            ((ESequence) getAny()).featureMap().clear();
            ((ESequence) getAny()).featureMap().addAll((Collection) newValue);
            return;
        case AssemblyPackage.ENTRY_POINT__MULTIPLICITY:
            setMultiplicity((String) newValue);
            return;
        case AssemblyPackage.ENTRY_POINT__NAME:
            setName((String) newValue);
            return;
        case AssemblyPackage.ENTRY_POINT__ANY_ATTRIBUTE:
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
        case AssemblyPackage.ENTRY_POINT__INTERFACE_GROUP:
            ((ESequence) getInterfaceGroup()).featureMap().clear();
            return;
        case AssemblyPackage.ENTRY_POINT__INTERFACE:
            setInterface((Interface) null);
            return;
        case AssemblyPackage.ENTRY_POINT__BINDING_GROUP:
            ((ESequence) getBindingGroup()).featureMap().clear();
            return;
        case AssemblyPackage.ENTRY_POINT__BINDING:
            getBindings().clear();
            return;
        case AssemblyPackage.ENTRY_POINT__REFERENCES:
            getReferences().clear();
            return;
        case AssemblyPackage.ENTRY_POINT__ANY:
            ((ESequence) getAny()).featureMap().clear();
            return;
        case AssemblyPackage.ENTRY_POINT__MULTIPLICITY:
            unsetMultiplicity();
            return;
        case AssemblyPackage.ENTRY_POINT__NAME:
            setName(NAME_EDEFAULT);
            return;
        case AssemblyPackage.ENTRY_POINT__ANY_ATTRIBUTE:
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
        case AssemblyPackage.ENTRY_POINT__INTERFACE_GROUP:
            return interfaceGroup != null && !interfaceGroup.featureMap().isEmpty();
        case AssemblyPackage.ENTRY_POINT__INTERFACE:
            return getInterface() != null;
        case AssemblyPackage.ENTRY_POINT__BINDING_GROUP:
            return bindingGroup != null && !bindingGroup.featureMap().isEmpty();
        case AssemblyPackage.ENTRY_POINT__BINDING:
            return !getBindings().isEmpty();
        case AssemblyPackage.ENTRY_POINT__REFERENCES:
            return references != null && !references.isEmpty();
        case AssemblyPackage.ENTRY_POINT__ANY:
            return any != null && !any.featureMap().isEmpty();
        case AssemblyPackage.ENTRY_POINT__MULTIPLICITY:
            return isSetMultiplicity();
        case AssemblyPackage.ENTRY_POINT__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case AssemblyPackage.ENTRY_POINT__ANY_ATTRIBUTE:
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
        result.append(" (interfaceGroup: ");
        result.append(interfaceGroup);
        result.append(", bindingGroup: ");
        result.append(bindingGroup);
        result.append(", any: ");
        result.append(any);
        result.append(", multiplicity: ");
        if (multiplicityESet) result.append(multiplicity);
        else
            result.append("<unset>");
        result.append(", name: ");
        result.append(name);
        result.append(", anyAttribute: ");
        result.append(anyAttribute);
		result.append(')');
		return result.toString();
	}

} //EntryPointImpl
