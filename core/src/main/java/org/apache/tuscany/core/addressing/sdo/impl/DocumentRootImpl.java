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
package org.apache.tuscany.core.addressing.sdo.impl;

import java.util.Collection;
import java.util.Map;

import commonj.sdo.Sequence;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.ecore.sdo.impl.EDataObjectImpl;
import org.eclipse.emf.ecore.sdo.util.BasicESequence;
import org.eclipse.emf.ecore.sdo.util.ESequence;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.apache.tuscany.core.addressing.sdo.AddressingElementPackage;
import org.apache.tuscany.core.addressing.sdo.AttributedURI;
import org.apache.tuscany.core.addressing.sdo.DocumentRoot;
import org.apache.tuscany.core.addressing.sdo.EndpointReferenceElement;
import org.apache.tuscany.core.addressing.sdo.Relationship;
import org.apache.tuscany.core.addressing.sdo.ReplyAfter;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Document Root</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link DocumentRootImpl#getMixed <em>Mixed</em>}</li>
 * <li>{@link DocumentRootImpl#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 * <li>{@link DocumentRootImpl#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 * <li>{@link DocumentRootImpl#getAction <em>Action</em>}</li>
 * <li>{@link DocumentRootImpl#getEndpointReference <em>Endpoint Reference</em>}</li>
 * <li>{@link DocumentRootImpl#getFaultTo <em>Fault To</em>}</li>
 * <li>{@link DocumentRootImpl#getFrom <em>From</em>}</li>
 * <li>{@link DocumentRootImpl#getMessageID <em>Message ID</em>}</li>
 * <li>{@link DocumentRootImpl#getRelatesTo <em>Relates To</em>}</li>
 * <li>{@link DocumentRootImpl#getReplyAfter <em>Reply After</em>}</li>
 * <li>{@link DocumentRootImpl#getReplyTo <em>Reply To</em>}</li>
 * <li>{@link DocumentRootImpl#getTo <em>To</em>}</li>
 * <li>{@link DocumentRootImpl#getAction1 <em>Action1</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DocumentRootImpl extends EDataObjectImpl implements DocumentRoot {
    /**
     * The cached value of the '{@link #getMixed() <em>Mixed</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getMixed()
     */
    protected ESequence mixed = null;

    /**
     * The cached value of the '{@link #getXMLNSPrefixMap() <em>XMLNS Prefix Map</em>}' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getXMLNSPrefixMap()
     */
    protected EMap xMLNSPrefixMap = null;

    /**
     * The cached value of the '{@link #getXSISchemaLocation() <em>XSI Schema Location</em>}' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getXSISchemaLocation()
     */
    protected EMap xSISchemaLocation = null;

    /**
     * The default value of the '{@link #getAction1() <em>Action1</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getAction1()
     */
    protected static final String ACTION1_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getAction1() <em>Action1</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getAction1()
     */
    protected String action1 = ACTION1_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected DocumentRootImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return AddressingElementPackage.eINSTANCE.getDocumentRoot();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getMixed() {
        if (mixed == null) {
            mixed = new BasicESequence(new BasicFeatureMap(this, AddressingElementPackage.DOCUMENT_ROOT__MIXED));
        }
        return mixed;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Map getXMLNSPrefixMap() {
        if (xMLNSPrefixMap == null) {
            xMLNSPrefixMap = new EcoreEMap(EcorePackage.eINSTANCE.getEStringToStringMapEntry(), EStringToStringMapEntryImpl.class, this, AddressingElementPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
        }
        return xMLNSPrefixMap.map();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Map getXSISchemaLocation() {
        if (xSISchemaLocation == null) {
            xSISchemaLocation = new EcoreEMap(EcorePackage.eINSTANCE.getEStringToStringMapEntry(), EStringToStringMapEntryImpl.class, this, AddressingElementPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
        }
        return xSISchemaLocation.map();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public AttributedURI getAction() {
        return (AttributedURI) ((ESequence) getMixed()).featureMap().get(AddressingElementPackage.eINSTANCE.getDocumentRoot_Action(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetAction(AttributedURI newAction, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AddressingElementPackage.eINSTANCE.getDocumentRoot_Action(), newAction, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setAction(AttributedURI newAction) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AddressingElementPackage.eINSTANCE.getDocumentRoot_Action(), newAction);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EndpointReferenceElement getEndpointReference() {
        return (EndpointReferenceElement) ((ESequence) getMixed()).featureMap().get(AddressingElementPackage.eINSTANCE.getDocumentRoot_EndpointReference(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetEndpointReference(EndpointReferenceElement newEndpointReference, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AddressingElementPackage.eINSTANCE.getDocumentRoot_EndpointReference(), newEndpointReference, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setEndpointReference(EndpointReferenceElement newEndpointReference) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AddressingElementPackage.eINSTANCE.getDocumentRoot_EndpointReference(), newEndpointReference);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EndpointReferenceElement getFaultTo() {
        return (EndpointReferenceElement) ((ESequence) getMixed()).featureMap().get(AddressingElementPackage.eINSTANCE.getDocumentRoot_FaultTo(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetFaultTo(EndpointReferenceElement newFaultTo, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AddressingElementPackage.eINSTANCE.getDocumentRoot_FaultTo(), newFaultTo, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setFaultTo(EndpointReferenceElement newFaultTo) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AddressingElementPackage.eINSTANCE.getDocumentRoot_FaultTo(), newFaultTo);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EndpointReferenceElement getFrom() {
        return (EndpointReferenceElement) ((ESequence) getMixed()).featureMap().get(AddressingElementPackage.eINSTANCE.getDocumentRoot_From(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetFrom(EndpointReferenceElement newFrom, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AddressingElementPackage.eINSTANCE.getDocumentRoot_From(), newFrom, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setFrom(EndpointReferenceElement newFrom) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AddressingElementPackage.eINSTANCE.getDocumentRoot_From(), newFrom);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public AttributedURI getMessageID() {
        return (AttributedURI) ((ESequence) getMixed()).featureMap().get(AddressingElementPackage.eINSTANCE.getDocumentRoot_MessageID(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetMessageID(AttributedURI newMessageID, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AddressingElementPackage.eINSTANCE.getDocumentRoot_MessageID(), newMessageID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setMessageID(AttributedURI newMessageID) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AddressingElementPackage.eINSTANCE.getDocumentRoot_MessageID(), newMessageID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Relationship getRelatesTo() {
        return (Relationship) ((ESequence) getMixed()).featureMap().get(AddressingElementPackage.eINSTANCE.getDocumentRoot_RelatesTo(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetRelatesTo(Relationship newRelatesTo, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AddressingElementPackage.eINSTANCE.getDocumentRoot_RelatesTo(), newRelatesTo, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setRelatesTo(Relationship newRelatesTo) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AddressingElementPackage.eINSTANCE.getDocumentRoot_RelatesTo(), newRelatesTo);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ReplyAfter getReplyAfter() {
        return (ReplyAfter) ((ESequence) getMixed()).featureMap().get(AddressingElementPackage.eINSTANCE.getDocumentRoot_ReplyAfter(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetReplyAfter(ReplyAfter newReplyAfter, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AddressingElementPackage.eINSTANCE.getDocumentRoot_ReplyAfter(), newReplyAfter, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setReplyAfter(ReplyAfter newReplyAfter) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AddressingElementPackage.eINSTANCE.getDocumentRoot_ReplyAfter(), newReplyAfter);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EndpointReferenceElement getReplyTo() {
        return (EndpointReferenceElement) ((ESequence) getMixed()).featureMap().get(AddressingElementPackage.eINSTANCE.getDocumentRoot_ReplyTo(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetReplyTo(EndpointReferenceElement newReplyTo, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AddressingElementPackage.eINSTANCE.getDocumentRoot_ReplyTo(), newReplyTo, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setReplyTo(EndpointReferenceElement newReplyTo) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AddressingElementPackage.eINSTANCE.getDocumentRoot_ReplyTo(), newReplyTo);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public AttributedURI getTo() {
        return (AttributedURI) ((ESequence) getMixed()).featureMap().get(AddressingElementPackage.eINSTANCE.getDocumentRoot_To(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetTo(AttributedURI newTo, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(AddressingElementPackage.eINSTANCE.getDocumentRoot_To(), newTo, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setTo(AttributedURI newTo) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(AddressingElementPackage.eINSTANCE.getDocumentRoot_To(), newTo);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getAction1() {
        return action1;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setAction1(String newAction1) {
        String oldAction1 = action1;
        action1 = newAction1;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AddressingElementPackage.DOCUMENT_ROOT__ACTION1, oldAction1, action1));
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
            case AddressingElementPackage.DOCUMENT_ROOT__MIXED:
                return ((InternalEList) ((ESequence) getMixed()).featureMap()).basicRemove(otherEnd, msgs);
            case AddressingElementPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
                return ((InternalEList) ((EMap.InternalMapView) getXMLNSPrefixMap()).eMap()).basicRemove(otherEnd, msgs);
            case AddressingElementPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
                return ((InternalEList) ((EMap.InternalMapView) getXSISchemaLocation()).eMap()).basicRemove(otherEnd, msgs);
            case AddressingElementPackage.DOCUMENT_ROOT__ACTION:
                return basicSetAction(null, msgs);
            case AddressingElementPackage.DOCUMENT_ROOT__ENDPOINT_REFERENCE:
                return basicSetEndpointReference(null, msgs);
            case AddressingElementPackage.DOCUMENT_ROOT__FAULT_TO:
                return basicSetFaultTo(null, msgs);
            case AddressingElementPackage.DOCUMENT_ROOT__FROM:
                return basicSetFrom(null, msgs);
            case AddressingElementPackage.DOCUMENT_ROOT__MESSAGE_ID:
                return basicSetMessageID(null, msgs);
            case AddressingElementPackage.DOCUMENT_ROOT__RELATES_TO:
                return basicSetRelatesTo(null, msgs);
            case AddressingElementPackage.DOCUMENT_ROOT__REPLY_AFTER:
                return basicSetReplyAfter(null, msgs);
            case AddressingElementPackage.DOCUMENT_ROOT__REPLY_TO:
                return basicSetReplyTo(null, msgs);
            case AddressingElementPackage.DOCUMENT_ROOT__TO:
                return basicSetTo(null, msgs);
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
        case AddressingElementPackage.DOCUMENT_ROOT__MIXED:
            return ((ESequence) getMixed()).featureMap();
        case AddressingElementPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
            return ((EMap.InternalMapView) getXMLNSPrefixMap()).eMap();
        case AddressingElementPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
            return ((EMap.InternalMapView) getXSISchemaLocation()).eMap();
        case AddressingElementPackage.DOCUMENT_ROOT__ACTION:
            return getAction();
        case AddressingElementPackage.DOCUMENT_ROOT__ENDPOINT_REFERENCE:
            return getEndpointReference();
        case AddressingElementPackage.DOCUMENT_ROOT__FAULT_TO:
            return getFaultTo();
        case AddressingElementPackage.DOCUMENT_ROOT__FROM:
            return getFrom();
        case AddressingElementPackage.DOCUMENT_ROOT__MESSAGE_ID:
            return getMessageID();
        case AddressingElementPackage.DOCUMENT_ROOT__RELATES_TO:
            return getRelatesTo();
        case AddressingElementPackage.DOCUMENT_ROOT__REPLY_AFTER:
            return getReplyAfter();
        case AddressingElementPackage.DOCUMENT_ROOT__REPLY_TO:
            return getReplyTo();
        case AddressingElementPackage.DOCUMENT_ROOT__TO:
            return getTo();
        case AddressingElementPackage.DOCUMENT_ROOT__ACTION1:
            return getAction1();
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
        case AddressingElementPackage.DOCUMENT_ROOT__MIXED:
            ((ESequence) getMixed()).featureMap().clear();
            ((ESequence) getMixed()).featureMap().addAll((Collection) newValue);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
            getXMLNSPrefixMap().clear();
            ((EMap.InternalMapView) getXMLNSPrefixMap()).eMap().addAll((Collection) newValue);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
            getXSISchemaLocation().clear();
            ((EMap.InternalMapView) getXSISchemaLocation()).eMap().addAll((Collection) newValue);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__ACTION:
            setAction((AttributedURI) newValue);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__ENDPOINT_REFERENCE:
            setEndpointReference((EndpointReferenceElement) newValue);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__FAULT_TO:
            setFaultTo((EndpointReferenceElement) newValue);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__FROM:
            setFrom((EndpointReferenceElement) newValue);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__MESSAGE_ID:
            setMessageID((AttributedURI) newValue);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__RELATES_TO:
            setRelatesTo((Relationship) newValue);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__REPLY_AFTER:
            setReplyAfter((ReplyAfter) newValue);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__REPLY_TO:
            setReplyTo((EndpointReferenceElement) newValue);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__TO:
            setTo((AttributedURI) newValue);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__ACTION1:
            setAction1((String) newValue);
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
        case AddressingElementPackage.DOCUMENT_ROOT__MIXED:
            ((ESequence) getMixed()).featureMap().clear();
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
            getXMLNSPrefixMap().clear();
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
            getXSISchemaLocation().clear();
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__ACTION:
            setAction((AttributedURI) null);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__ENDPOINT_REFERENCE:
            setEndpointReference((EndpointReferenceElement) null);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__FAULT_TO:
            setFaultTo((EndpointReferenceElement) null);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__FROM:
            setFrom((EndpointReferenceElement) null);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__MESSAGE_ID:
            setMessageID((AttributedURI) null);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__RELATES_TO:
            setRelatesTo((Relationship) null);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__REPLY_AFTER:
            setReplyAfter((ReplyAfter) null);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__REPLY_TO:
            setReplyTo((EndpointReferenceElement) null);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__TO:
            setTo((AttributedURI) null);
            return;
        case AddressingElementPackage.DOCUMENT_ROOT__ACTION1:
            setAction1(ACTION1_EDEFAULT);
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
        case AddressingElementPackage.DOCUMENT_ROOT__MIXED:
            return mixed != null && !mixed.featureMap().isEmpty();
        case AddressingElementPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
            return xMLNSPrefixMap != null && !xMLNSPrefixMap.isEmpty();
        case AddressingElementPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
            return xSISchemaLocation != null && !xSISchemaLocation.isEmpty();
        case AddressingElementPackage.DOCUMENT_ROOT__ACTION:
            return getAction() != null;
        case AddressingElementPackage.DOCUMENT_ROOT__ENDPOINT_REFERENCE:
            return getEndpointReference() != null;
        case AddressingElementPackage.DOCUMENT_ROOT__FAULT_TO:
            return getFaultTo() != null;
        case AddressingElementPackage.DOCUMENT_ROOT__FROM:
            return getFrom() != null;
        case AddressingElementPackage.DOCUMENT_ROOT__MESSAGE_ID:
            return getMessageID() != null;
        case AddressingElementPackage.DOCUMENT_ROOT__RELATES_TO:
            return getRelatesTo() != null;
        case AddressingElementPackage.DOCUMENT_ROOT__REPLY_AFTER:
            return getReplyAfter() != null;
        case AddressingElementPackage.DOCUMENT_ROOT__REPLY_TO:
            return getReplyTo() != null;
        case AddressingElementPackage.DOCUMENT_ROOT__TO:
            return getTo() != null;
        case AddressingElementPackage.DOCUMENT_ROOT__ACTION1:
            return ACTION1_EDEFAULT == null ? action1 != null : !ACTION1_EDEFAULT.equals(action1);
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
        result.append(" (mixed: ");
        result.append(mixed);
        result.append(", action1: ");
		result.append(action1);
		result.append(')');
		return result.toString();
	}

} //DocumentRootImpl
