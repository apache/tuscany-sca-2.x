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
package org.apache.tuscany.core.message.sdo.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.sdo.impl.EDataObjectImpl;

import org.apache.tuscany.core.message.sdo.FaultSubCodeElement;
import org.apache.tuscany.core.message.sdo.MessageElementPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Fault Sub Code Element</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link FaultSubCodeElementImpl#getValue <em>Value</em>}</li>
 * <li>{@link FaultSubCodeElementImpl#getSubcode <em>Subcode</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FaultSubCodeElementImpl extends EDataObjectImpl implements FaultSubCodeElement {
    /**
     * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getValue()
     */
    protected static final Object VALUE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getValue()
     */
    protected Object value = VALUE_EDEFAULT;

    /**
     * The cached value of the '{@link #getSubcode() <em>Subcode</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getSubcode()
     */
    protected FaultSubCodeElement subcode = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected FaultSubCodeElementImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return MessageElementPackage.eINSTANCE.getFaultSubCodeElement();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Object getValue() {
        return value;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setValue(Object newValue) {
        Object oldValue = value;
        value = newValue;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MessageElementPackage.FAULT_SUB_CODE_ELEMENT__VALUE, oldValue, value));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FaultSubCodeElement getSubcode() {
        return subcode;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetSubcode(FaultSubCodeElement newSubcode, NotificationChain msgs) {
        FaultSubCodeElement oldSubcode = subcode;
        subcode = newSubcode;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MessageElementPackage.FAULT_SUB_CODE_ELEMENT__SUBCODE, oldSubcode, newSubcode);
            if (msgs == null) msgs = notification;
            else
                msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setSubcode(FaultSubCodeElement newSubcode) {
        if (newSubcode != subcode) {
            NotificationChain msgs = null;
            if (subcode != null)
                msgs = ((InternalEObject) subcode).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MessageElementPackage.FAULT_SUB_CODE_ELEMENT__SUBCODE, null, msgs);
            if (newSubcode != null)
                msgs = ((InternalEObject) newSubcode).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MessageElementPackage.FAULT_SUB_CODE_ELEMENT__SUBCODE, null, msgs);
            msgs = basicSetSubcode(newSubcode, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MessageElementPackage.FAULT_SUB_CODE_ELEMENT__SUBCODE, newSubcode, newSubcode));
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
            case MessageElementPackage.FAULT_SUB_CODE_ELEMENT__SUBCODE:
                return basicSetSubcode(null, msgs);
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
        case MessageElementPackage.FAULT_SUB_CODE_ELEMENT__VALUE:
            return getValue();
        case MessageElementPackage.FAULT_SUB_CODE_ELEMENT__SUBCODE:
            return getSubcode();
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
        case MessageElementPackage.FAULT_SUB_CODE_ELEMENT__VALUE:
            setValue((Object) newValue);
            return;
        case MessageElementPackage.FAULT_SUB_CODE_ELEMENT__SUBCODE:
            setSubcode((FaultSubCodeElement) newValue);
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
        case MessageElementPackage.FAULT_SUB_CODE_ELEMENT__VALUE:
            setValue(VALUE_EDEFAULT);
            return;
        case MessageElementPackage.FAULT_SUB_CODE_ELEMENT__SUBCODE:
            setSubcode((FaultSubCodeElement) null);
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
        case MessageElementPackage.FAULT_SUB_CODE_ELEMENT__VALUE:
            return VALUE_EDEFAULT == null ? value != null : !VALUE_EDEFAULT.equals(value);
        case MessageElementPackage.FAULT_SUB_CODE_ELEMENT__SUBCODE:
            return subcode != null;
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
        result.append(" (value: ");
        result.append(value);
        result.append(')');
        return result.toString();
	}

} //FaultSubCodeElementImpl
