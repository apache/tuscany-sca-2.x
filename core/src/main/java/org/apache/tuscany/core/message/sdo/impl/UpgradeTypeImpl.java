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

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.sdo.impl.EDataObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.apache.tuscany.core.message.sdo.MessageElementPackage;
import org.apache.tuscany.core.message.sdo.SupportedEnvElement;
import org.apache.tuscany.core.message.sdo.UpgradeType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Upgrade Type</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link UpgradeTypeImpl#getSupportedEnvelope <em>Supported Envelope</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class UpgradeTypeImpl extends EDataObjectImpl implements UpgradeType {
    /**
     * The cached value of the '{@link #getSupportedEnvelope() <em>Supported Envelope</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getSupportedEnvelope()
     */
    protected EList supportedEnvelope = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected UpgradeTypeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return MessageElementPackage.eINSTANCE.getUpgradeType();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public List getSupportedEnvelope() {
        if (supportedEnvelope == null) {
            supportedEnvelope = new EObjectContainmentEList(SupportedEnvElement.class, this, MessageElementPackage.UPGRADE_TYPE__SUPPORTED_ENVELOPE);
        }
        return supportedEnvelope;
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
            case MessageElementPackage.UPGRADE_TYPE__SUPPORTED_ENVELOPE:
                return ((InternalEList) getSupportedEnvelope()).basicRemove(otherEnd, msgs);
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
        case MessageElementPackage.UPGRADE_TYPE__SUPPORTED_ENVELOPE:
            return getSupportedEnvelope();
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
        case MessageElementPackage.UPGRADE_TYPE__SUPPORTED_ENVELOPE:
            getSupportedEnvelope().clear();
            getSupportedEnvelope().addAll((Collection) newValue);
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
        case MessageElementPackage.UPGRADE_TYPE__SUPPORTED_ENVELOPE:
            getSupportedEnvelope().clear();
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
        case MessageElementPackage.UPGRADE_TYPE__SUPPORTED_ENVELOPE:
            return supportedEnvelope != null && !supportedEnvelope.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

} //UpgradeTypeImpl
