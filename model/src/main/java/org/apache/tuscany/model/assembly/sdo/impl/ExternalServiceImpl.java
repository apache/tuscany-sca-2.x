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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.sdo.impl.EDataObjectImpl;
import org.eclipse.emf.ecore.sdo.util.BasicESequence;
import org.eclipse.emf.ecore.sdo.util.ESequence;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.osoa.sca.model.ExternalService;
import org.osoa.sca.model.Interface;

import org.apache.tuscany.model.assembly.sdo.AssemblyPackage;
import org.apache.tuscany.model.assembly.sdo.OverrideOptions;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>External Service</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link ExternalServiceImpl#getInterfaceGroup <em>Interface Group</em>}</li>
 * <li>{@link ExternalServiceImpl#getInterface <em>Interface</em>}</li>
 * <li>{@link ExternalServiceImpl#getBindingsGroup <em>Bindings Group</em>}</li>
 * <li>{@link ExternalServiceImpl#getBindings <em>Bindings</em>}</li>
 * <li>{@link ExternalServiceImpl#getAny <em>Any</em>}</li>
 * <li>{@link ExternalServiceImpl#getName <em>Name</em>}</li>
 * <li>{@link ExternalServiceImpl#getOverridable <em>Overridable</em>}</li>
 * <li>{@link ExternalServiceImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ExternalServiceImpl extends EDataObjectImpl implements ExternalService {
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
     * The cached value of the '{@link #getBindingsGroup() <em>Bindings Group</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getBindingsGroup()
     */
    protected ESequence bindingsGroup = null;

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
     * The default value of the '{@link #getOverridable() <em>Overridable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getOverridable()
     */
    protected static final OverrideOptions OVERRIDABLE_EDEFAULT = OverrideOptions.MAY_LITERAL;

    /**
     * The cached value of the '{@link #getOverridable() <em>Overridable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getOverridable()
     */
    protected OverrideOptions overridable = OVERRIDABLE_EDEFAULT;

    /**
     * This is true if the Overridable attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    protected boolean overridableESet = false;

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
    protected ExternalServiceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return AssemblyPackage.eINSTANCE.getExternalService();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getInterfaceGroup() {
        if (interfaceGroup == null) {
            interfaceGroup = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.EXTERNAL_SERVICE__INTERFACE_GROUP));
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
        return (Interface) ((ESequence) getInterfaceGroup()).featureMap().get(AssemblyPackage.eINSTANCE.getExternalService_Interface(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetInterface(Interface newInterface, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getInterfaceGroup()).featureMap()).basicAdd(AssemblyPackage.eINSTANCE.getExternalService_Interface(), newInterface, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setInterface(Interface newInterface) {
        ((FeatureMap.Internal) ((ESequence) getInterfaceGroup()).featureMap()).set(AssemblyPackage.eINSTANCE.getExternalService_Interface(), newInterface);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getBindingsGroup() {
        if (bindingsGroup == null) {
            bindingsGroup = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.EXTERNAL_SERVICE__BINDINGS_GROUP));
        }
        return bindingsGroup;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public List getBindings() {
        return ((ESequence) getBindingsGroup()).featureMap().list(AssemblyPackage.eINSTANCE.getExternalService_Bindings());
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getAny() {
        if (any == null) {
            any = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.EXTERNAL_SERVICE__ANY));
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
            eNotify(new ENotificationImpl(this, Notification.SET, AssemblyPackage.EXTERNAL_SERVICE__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public OverrideOptions getOverridable() {
        return overridable;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setOverridable(OverrideOptions newOverridable) {
        OverrideOptions oldOverridable = overridable;
        overridable = newOverridable == null ? OVERRIDABLE_EDEFAULT : newOverridable;
        boolean oldOverridableESet = overridableESet;
        overridableESet = true;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AssemblyPackage.EXTERNAL_SERVICE__OVERRIDABLE, oldOverridable, overridable, !oldOverridableESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void unsetOverridable() {
        OverrideOptions oldOverridable = overridable;
        boolean oldOverridableESet = overridableESet;
        overridable = OVERRIDABLE_EDEFAULT;
        overridableESet = false;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.UNSET, AssemblyPackage.EXTERNAL_SERVICE__OVERRIDABLE, oldOverridable, OVERRIDABLE_EDEFAULT, oldOverridableESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean isSetOverridable() {
        return overridableESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getAnyAttribute() {
        if (anyAttribute == null) {
            anyAttribute = new BasicESequence(new BasicFeatureMap(this, AssemblyPackage.EXTERNAL_SERVICE__ANY_ATTRIBUTE));
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
            case AssemblyPackage.EXTERNAL_SERVICE__INTERFACE_GROUP:
                return ((InternalEList) ((ESequence) getInterfaceGroup()).featureMap()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.EXTERNAL_SERVICE__INTERFACE:
                return basicSetInterface(null, msgs);
            case AssemblyPackage.EXTERNAL_SERVICE__BINDINGS_GROUP:
                return ((InternalEList) ((ESequence) getBindingsGroup()).featureMap()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.EXTERNAL_SERVICE__BINDINGS:
                return ((InternalEList) getBindings()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.EXTERNAL_SERVICE__ANY:
                return ((InternalEList) ((ESequence) getAny()).featureMap()).basicRemove(otherEnd, msgs);
            case AssemblyPackage.EXTERNAL_SERVICE__ANY_ATTRIBUTE:
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
        case AssemblyPackage.EXTERNAL_SERVICE__INTERFACE_GROUP:
            return ((ESequence) getInterfaceGroup()).featureMap();
        case AssemblyPackage.EXTERNAL_SERVICE__INTERFACE:
            return getInterface();
        case AssemblyPackage.EXTERNAL_SERVICE__BINDINGS_GROUP:
            return ((ESequence) getBindingsGroup()).featureMap();
        case AssemblyPackage.EXTERNAL_SERVICE__BINDINGS:
            return getBindings();
        case AssemblyPackage.EXTERNAL_SERVICE__ANY:
            return ((ESequence) getAny()).featureMap();
        case AssemblyPackage.EXTERNAL_SERVICE__NAME:
            return getName();
        case AssemblyPackage.EXTERNAL_SERVICE__OVERRIDABLE:
            return getOverridable();
        case AssemblyPackage.EXTERNAL_SERVICE__ANY_ATTRIBUTE:
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
        case AssemblyPackage.EXTERNAL_SERVICE__INTERFACE_GROUP:
            ((ESequence) getInterfaceGroup()).featureMap().clear();
            ((ESequence) getInterfaceGroup()).featureMap().addAll((Collection) newValue);
            return;
        case AssemblyPackage.EXTERNAL_SERVICE__INTERFACE:
            setInterface((Interface) newValue);
            return;
        case AssemblyPackage.EXTERNAL_SERVICE__BINDINGS_GROUP:
            ((ESequence) getBindingsGroup()).featureMap().clear();
            ((ESequence) getBindingsGroup()).featureMap().addAll((Collection) newValue);
            return;
        case AssemblyPackage.EXTERNAL_SERVICE__BINDINGS:
            getBindings().clear();
            getBindings().addAll((Collection) newValue);
            return;
        case AssemblyPackage.EXTERNAL_SERVICE__ANY:
            ((ESequence) getAny()).featureMap().clear();
            ((ESequence) getAny()).featureMap().addAll((Collection) newValue);
            return;
        case AssemblyPackage.EXTERNAL_SERVICE__NAME:
            setName((String) newValue);
            return;
        case AssemblyPackage.EXTERNAL_SERVICE__OVERRIDABLE:
            setOverridable((OverrideOptions) newValue);
            return;
        case AssemblyPackage.EXTERNAL_SERVICE__ANY_ATTRIBUTE:
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
        case AssemblyPackage.EXTERNAL_SERVICE__INTERFACE_GROUP:
            ((ESequence) getInterfaceGroup()).featureMap().clear();
            return;
        case AssemblyPackage.EXTERNAL_SERVICE__INTERFACE:
            setInterface((Interface) null);
            return;
        case AssemblyPackage.EXTERNAL_SERVICE__BINDINGS_GROUP:
            ((ESequence) getBindingsGroup()).featureMap().clear();
            return;
        case AssemblyPackage.EXTERNAL_SERVICE__BINDINGS:
            getBindings().clear();
            return;
        case AssemblyPackage.EXTERNAL_SERVICE__ANY:
            ((ESequence) getAny()).featureMap().clear();
            return;
        case AssemblyPackage.EXTERNAL_SERVICE__NAME:
            setName(NAME_EDEFAULT);
            return;
        case AssemblyPackage.EXTERNAL_SERVICE__OVERRIDABLE:
            unsetOverridable();
            return;
        case AssemblyPackage.EXTERNAL_SERVICE__ANY_ATTRIBUTE:
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
        case AssemblyPackage.EXTERNAL_SERVICE__INTERFACE_GROUP:
            return interfaceGroup != null && !interfaceGroup.featureMap().isEmpty();
        case AssemblyPackage.EXTERNAL_SERVICE__INTERFACE:
            return getInterface() != null;
        case AssemblyPackage.EXTERNAL_SERVICE__BINDINGS_GROUP:
            return bindingsGroup != null && !bindingsGroup.featureMap().isEmpty();
        case AssemblyPackage.EXTERNAL_SERVICE__BINDINGS:
            return !getBindings().isEmpty();
        case AssemblyPackage.EXTERNAL_SERVICE__ANY:
            return any != null && !any.featureMap().isEmpty();
        case AssemblyPackage.EXTERNAL_SERVICE__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case AssemblyPackage.EXTERNAL_SERVICE__OVERRIDABLE:
            return isSetOverridable();
        case AssemblyPackage.EXTERNAL_SERVICE__ANY_ATTRIBUTE:
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
        result.append(", bindingsGroup: ");
        result.append(bindingsGroup);
        result.append(", any: ");
        result.append(any);
        result.append(", name: ");
        result.append(name);
        result.append(", overridable: ");
        if (overridableESet) result.append(overridable);
        else
            result.append("<unset>");
        result.append(", anyAttribute: ");
        result.append(anyAttribute);
		result.append(')');
		return result.toString();
	}

} //ExternalServiceImpl
