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

import org.apache.tuscany.core.message.sdo.FaultCodeElement;
import org.apache.tuscany.core.message.sdo.FaultDetailElement;
import org.apache.tuscany.core.message.sdo.FaultElement;
import org.apache.tuscany.core.message.sdo.FaultReasonElement;
import org.apache.tuscany.core.message.sdo.MessageElementPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Fault Element</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link FaultElementImpl#getCode <em>Code</em>}</li>
 * <li>{@link FaultElementImpl#getReason <em>Reason</em>}</li>
 * <li>{@link FaultElementImpl#getNode <em>Node</em>}</li>
 * <li>{@link FaultElementImpl#getRole <em>Role</em>}</li>
 * <li>{@link FaultElementImpl#getDetail <em>Detail</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FaultElementImpl extends EDataObjectImpl implements FaultElement {
    /**
     * The cached value of the '{@link #getCode() <em>Code</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getCode()
     */
    protected FaultCodeElement code = null;

    /**
     * The cached value of the '{@link #getReason() <em>Reason</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getReason()
     */
    protected FaultReasonElement reason = null;

    /**
     * The default value of the '{@link #getNode() <em>Node</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getNode()
     */
    protected static final String NODE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getNode() <em>Node</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getNode()
     */
    protected String node = NODE_EDEFAULT;

    /**
     * The default value of the '{@link #getRole() <em>Role</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getRole()
     */
    protected static final String ROLE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getRole() <em>Role</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getRole()
     */
    protected String role = ROLE_EDEFAULT;

    /**
     * The cached value of the '{@link #getDetail() <em>Detail</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getDetail()
     */
    protected FaultDetailElement detail = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected FaultElementImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return MessageElementPackage.eINSTANCE.getFaultElement();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FaultCodeElement getCode() {
        return code;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetCode(FaultCodeElement newCode, NotificationChain msgs) {
        FaultCodeElement oldCode = code;
        code = newCode;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MessageElementPackage.FAULT_ELEMENT__CODE, oldCode, newCode);
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
    public void setCode(FaultCodeElement newCode) {
        if (newCode != code) {
            NotificationChain msgs = null;
            if (code != null)
                msgs = ((InternalEObject) code).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MessageElementPackage.FAULT_ELEMENT__CODE, null, msgs);
            if (newCode != null)
                msgs = ((InternalEObject) newCode).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MessageElementPackage.FAULT_ELEMENT__CODE, null, msgs);
            msgs = basicSetCode(newCode, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MessageElementPackage.FAULT_ELEMENT__CODE, newCode, newCode));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FaultReasonElement getReason() {
        return reason;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetReason(FaultReasonElement newReason, NotificationChain msgs) {
        FaultReasonElement oldReason = reason;
        reason = newReason;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MessageElementPackage.FAULT_ELEMENT__REASON, oldReason, newReason);
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
    public void setReason(FaultReasonElement newReason) {
        if (newReason != reason) {
            NotificationChain msgs = null;
            if (reason != null)
                msgs = ((InternalEObject) reason).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MessageElementPackage.FAULT_ELEMENT__REASON, null, msgs);
            if (newReason != null)
                msgs = ((InternalEObject) newReason).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MessageElementPackage.FAULT_ELEMENT__REASON, null, msgs);
            msgs = basicSetReason(newReason, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MessageElementPackage.FAULT_ELEMENT__REASON, newReason, newReason));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getNode() {
        return node;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setNode(String newNode) {
        String oldNode = node;
        node = newNode;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MessageElementPackage.FAULT_ELEMENT__NODE, oldNode, node));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getRole() {
        return role;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setRole(String newRole) {
        String oldRole = role;
        role = newRole;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MessageElementPackage.FAULT_ELEMENT__ROLE, oldRole, role));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FaultDetailElement getDetail() {
        return detail;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetDetail(FaultDetailElement newDetail, NotificationChain msgs) {
        FaultDetailElement oldDetail = detail;
        detail = newDetail;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MessageElementPackage.FAULT_ELEMENT__DETAIL, oldDetail, newDetail);
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
    public void setDetail(FaultDetailElement newDetail) {
        if (newDetail != detail) {
            NotificationChain msgs = null;
            if (detail != null)
                msgs = ((InternalEObject) detail).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MessageElementPackage.FAULT_ELEMENT__DETAIL, null, msgs);
            if (newDetail != null)
                msgs = ((InternalEObject) newDetail).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MessageElementPackage.FAULT_ELEMENT__DETAIL, null, msgs);
            msgs = basicSetDetail(newDetail, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MessageElementPackage.FAULT_ELEMENT__DETAIL, newDetail, newDetail));
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
            case MessageElementPackage.FAULT_ELEMENT__CODE:
                return basicSetCode(null, msgs);
            case MessageElementPackage.FAULT_ELEMENT__REASON:
                return basicSetReason(null, msgs);
            case MessageElementPackage.FAULT_ELEMENT__DETAIL:
                return basicSetDetail(null, msgs);
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
        case MessageElementPackage.FAULT_ELEMENT__CODE:
            return getCode();
        case MessageElementPackage.FAULT_ELEMENT__REASON:
            return getReason();
        case MessageElementPackage.FAULT_ELEMENT__NODE:
            return getNode();
        case MessageElementPackage.FAULT_ELEMENT__ROLE:
            return getRole();
        case MessageElementPackage.FAULT_ELEMENT__DETAIL:
            return getDetail();
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
        case MessageElementPackage.FAULT_ELEMENT__CODE:
            setCode((FaultCodeElement) newValue);
            return;
        case MessageElementPackage.FAULT_ELEMENT__REASON:
            setReason((FaultReasonElement) newValue);
            return;
        case MessageElementPackage.FAULT_ELEMENT__NODE:
            setNode((String) newValue);
            return;
        case MessageElementPackage.FAULT_ELEMENT__ROLE:
            setRole((String) newValue);
            return;
        case MessageElementPackage.FAULT_ELEMENT__DETAIL:
            setDetail((FaultDetailElement) newValue);
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
        case MessageElementPackage.FAULT_ELEMENT__CODE:
            setCode((FaultCodeElement) null);
            return;
        case MessageElementPackage.FAULT_ELEMENT__REASON:
            setReason((FaultReasonElement) null);
            return;
        case MessageElementPackage.FAULT_ELEMENT__NODE:
            setNode(NODE_EDEFAULT);
            return;
        case MessageElementPackage.FAULT_ELEMENT__ROLE:
            setRole(ROLE_EDEFAULT);
            return;
        case MessageElementPackage.FAULT_ELEMENT__DETAIL:
            setDetail((FaultDetailElement) null);
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
        case MessageElementPackage.FAULT_ELEMENT__CODE:
            return code != null;
        case MessageElementPackage.FAULT_ELEMENT__REASON:
            return reason != null;
        case MessageElementPackage.FAULT_ELEMENT__NODE:
            return NODE_EDEFAULT == null ? node != null : !NODE_EDEFAULT.equals(node);
        case MessageElementPackage.FAULT_ELEMENT__ROLE:
            return ROLE_EDEFAULT == null ? role != null : !ROLE_EDEFAULT.equals(role);
        case MessageElementPackage.FAULT_ELEMENT__DETAIL:
            return detail != null;
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
        result.append(" (node: ");
        result.append(node);
        result.append(", role: ");
        result.append(role);
		result.append(')');
		return result.toString();
	}

} //FaultElementImpl
