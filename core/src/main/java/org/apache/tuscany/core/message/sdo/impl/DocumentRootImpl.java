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

import org.apache.tuscany.core.message.sdo.BodyElement;
import org.apache.tuscany.core.message.sdo.DocumentRoot;
import org.apache.tuscany.core.message.sdo.FaultElement;
import org.apache.tuscany.core.message.sdo.HeaderElement;
import org.apache.tuscany.core.message.sdo.MessageElement;
import org.apache.tuscany.core.message.sdo.MessageElementPackage;
import org.apache.tuscany.core.message.sdo.NotUnderstoodType;
import org.apache.tuscany.core.message.sdo.UpgradeType;

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
 * <li>{@link DocumentRootImpl#getBodyElement <em>Body Element</em>}</li>
 * <li>{@link DocumentRootImpl#getEnvelope <em>Envelope</em>}</li>
 * <li>{@link DocumentRootImpl#getFaultElement <em>Fault Element</em>}</li>
 * <li>{@link DocumentRootImpl#getHeaderElement <em>Header Element</em>}</li>
 * <li>{@link DocumentRootImpl#getNotlUnderstoodElement <em>Notl Understood Element</em>}</li>
 * <li>{@link DocumentRootImpl#getUpgradeElement <em>Upgrade Element</em>}</li>
 * <li>{@link DocumentRootImpl#getEncodingStyle <em>Encoding Style</em>}</li>
 * <li>{@link DocumentRootImpl#isMustUnderstand <em>Must Understand</em>}</li>
 * <li>{@link DocumentRootImpl#isRelay <em>Relay</em>}</li>
 * <li>{@link DocumentRootImpl#getRole <em>Role</em>}</li>
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
     * The default value of the '{@link #getEncodingStyle() <em>Encoding Style</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getEncodingStyle()
     */
    protected static final String ENCODING_STYLE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEncodingStyle() <em>Encoding Style</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getEncodingStyle()
     */
    protected String encodingStyle = ENCODING_STYLE_EDEFAULT;

    /**
     * The default value of the '{@link #isMustUnderstand() <em>Must Understand</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #isMustUnderstand()
     */
    protected static final boolean MUST_UNDERSTAND_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isMustUnderstand() <em>Must Understand</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #isMustUnderstand()
     */
    protected boolean mustUnderstand = MUST_UNDERSTAND_EDEFAULT;

    /**
     * This is true if the Must Understand attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    protected boolean mustUnderstandESet = false;

    /**
     * The default value of the '{@link #isRelay() <em>Relay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #isRelay()
     */
    protected static final boolean RELAY_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isRelay() <em>Relay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #isRelay()
     */
    protected boolean relay = RELAY_EDEFAULT;

    /**
     * This is true if the Relay attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    protected boolean relayESet = false;

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
        return MessageElementPackage.eINSTANCE.getDocumentRoot();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getMixed() {
        if (mixed == null) {
            mixed = new BasicESequence(new BasicFeatureMap(this, MessageElementPackage.DOCUMENT_ROOT__MIXED));
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
            xMLNSPrefixMap = new EcoreEMap(EcorePackage.eINSTANCE.getEStringToStringMapEntry(), EStringToStringMapEntryImpl.class, this, MessageElementPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
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
            xSISchemaLocation = new EcoreEMap(EcorePackage.eINSTANCE.getEStringToStringMapEntry(), EStringToStringMapEntryImpl.class, this, MessageElementPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
        }
        return xSISchemaLocation.map();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public BodyElement getBodyElement() {
        return (BodyElement) ((ESequence) getMixed()).featureMap().get(MessageElementPackage.eINSTANCE.getDocumentRoot_BodyElement(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetBodyElement(BodyElement newBodyElement, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(MessageElementPackage.eINSTANCE.getDocumentRoot_BodyElement(), newBodyElement, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setBodyElement(BodyElement newBodyElement) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(MessageElementPackage.eINSTANCE.getDocumentRoot_BodyElement(), newBodyElement);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public MessageElement getEnvelope() {
        return (MessageElement) ((ESequence) getMixed()).featureMap().get(MessageElementPackage.eINSTANCE.getDocumentRoot_Envelope(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetEnvelope(MessageElement newEnvelope, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(MessageElementPackage.eINSTANCE.getDocumentRoot_Envelope(), newEnvelope, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setEnvelope(MessageElement newEnvelope) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(MessageElementPackage.eINSTANCE.getDocumentRoot_Envelope(), newEnvelope);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public FaultElement getFaultElement() {
        return (FaultElement) ((ESequence) getMixed()).featureMap().get(MessageElementPackage.eINSTANCE.getDocumentRoot_FaultElement(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetFaultElement(FaultElement newFaultElement, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(MessageElementPackage.eINSTANCE.getDocumentRoot_FaultElement(), newFaultElement, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setFaultElement(FaultElement newFaultElement) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(MessageElementPackage.eINSTANCE.getDocumentRoot_FaultElement(), newFaultElement);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public HeaderElement getHeaderElement() {
        return (HeaderElement) ((ESequence) getMixed()).featureMap().get(MessageElementPackage.eINSTANCE.getDocumentRoot_HeaderElement(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetHeaderElement(HeaderElement newHeaderElement, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(MessageElementPackage.eINSTANCE.getDocumentRoot_HeaderElement(), newHeaderElement, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setHeaderElement(HeaderElement newHeaderElement) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(MessageElementPackage.eINSTANCE.getDocumentRoot_HeaderElement(), newHeaderElement);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotUnderstoodType getNotlUnderstoodElement() {
        return (NotUnderstoodType) ((ESequence) getMixed()).featureMap().get(MessageElementPackage.eINSTANCE.getDocumentRoot_NotlUnderstoodElement(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetNotlUnderstoodElement(NotUnderstoodType newNotlUnderstoodElement, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(MessageElementPackage.eINSTANCE.getDocumentRoot_NotlUnderstoodElement(), newNotlUnderstoodElement, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setNotlUnderstoodElement(NotUnderstoodType newNotlUnderstoodElement) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(MessageElementPackage.eINSTANCE.getDocumentRoot_NotlUnderstoodElement(), newNotlUnderstoodElement);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public UpgradeType getUpgradeElement() {
        return (UpgradeType) ((ESequence) getMixed()).featureMap().get(MessageElementPackage.eINSTANCE.getDocumentRoot_UpgradeElement(), true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetUpgradeElement(UpgradeType newUpgradeElement, NotificationChain msgs) {
        return ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).basicAdd(MessageElementPackage.eINSTANCE.getDocumentRoot_UpgradeElement(), newUpgradeElement, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setUpgradeElement(UpgradeType newUpgradeElement) {
        ((FeatureMap.Internal) ((ESequence) getMixed()).featureMap()).set(MessageElementPackage.eINSTANCE.getDocumentRoot_UpgradeElement(), newUpgradeElement);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String getEncodingStyle() {
        return encodingStyle;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setEncodingStyle(String newEncodingStyle) {
        String oldEncodingStyle = encodingStyle;
        encodingStyle = newEncodingStyle;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MessageElementPackage.DOCUMENT_ROOT__ENCODING_STYLE, oldEncodingStyle, encodingStyle));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean isMustUnderstand() {
        return mustUnderstand;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setMustUnderstand(boolean newMustUnderstand) {
        boolean oldMustUnderstand = mustUnderstand;
        mustUnderstand = newMustUnderstand;
        boolean oldMustUnderstandESet = mustUnderstandESet;
        mustUnderstandESet = true;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MessageElementPackage.DOCUMENT_ROOT__MUST_UNDERSTAND, oldMustUnderstand, mustUnderstand, !oldMustUnderstandESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void unsetMustUnderstand() {
        boolean oldMustUnderstand = mustUnderstand;
        boolean oldMustUnderstandESet = mustUnderstandESet;
        mustUnderstand = MUST_UNDERSTAND_EDEFAULT;
        mustUnderstandESet = false;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.UNSET, MessageElementPackage.DOCUMENT_ROOT__MUST_UNDERSTAND, oldMustUnderstand, MUST_UNDERSTAND_EDEFAULT, oldMustUnderstandESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean isSetMustUnderstand() {
        return mustUnderstandESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean isRelay() {
        return relay;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void setRelay(boolean newRelay) {
        boolean oldRelay = relay;
        relay = newRelay;
        boolean oldRelayESet = relayESet;
        relayESet = true;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MessageElementPackage.DOCUMENT_ROOT__RELAY, oldRelay, relay, !oldRelayESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void unsetRelay() {
        boolean oldRelay = relay;
        boolean oldRelayESet = relayESet;
        relay = RELAY_EDEFAULT;
        relayESet = false;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.UNSET, MessageElementPackage.DOCUMENT_ROOT__RELAY, oldRelay, RELAY_EDEFAULT, oldRelayESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean isSetRelay() {
        return relayESet;
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
            eNotify(new ENotificationImpl(this, Notification.SET, MessageElementPackage.DOCUMENT_ROOT__ROLE, oldRole, role));
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
            case MessageElementPackage.DOCUMENT_ROOT__MIXED:
                return ((InternalEList) ((ESequence) getMixed()).featureMap()).basicRemove(otherEnd, msgs);
            case MessageElementPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
                return ((InternalEList) ((EMap.InternalMapView) getXMLNSPrefixMap()).eMap()).basicRemove(otherEnd, msgs);
            case MessageElementPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
                return ((InternalEList) ((EMap.InternalMapView) getXSISchemaLocation()).eMap()).basicRemove(otherEnd, msgs);
            case MessageElementPackage.DOCUMENT_ROOT__BODY_ELEMENT:
                return basicSetBodyElement(null, msgs);
            case MessageElementPackage.DOCUMENT_ROOT__ENVELOPE:
                return basicSetEnvelope(null, msgs);
            case MessageElementPackage.DOCUMENT_ROOT__FAULT_ELEMENT:
                return basicSetFaultElement(null, msgs);
            case MessageElementPackage.DOCUMENT_ROOT__HEADER_ELEMENT:
                return basicSetHeaderElement(null, msgs);
            case MessageElementPackage.DOCUMENT_ROOT__NOTL_UNDERSTOOD_ELEMENT:
                return basicSetNotlUnderstoodElement(null, msgs);
            case MessageElementPackage.DOCUMENT_ROOT__UPGRADE_ELEMENT:
                return basicSetUpgradeElement(null, msgs);
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
        case MessageElementPackage.DOCUMENT_ROOT__MIXED:
            return ((ESequence) getMixed()).featureMap();
        case MessageElementPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
            return ((EMap.InternalMapView) getXMLNSPrefixMap()).eMap();
        case MessageElementPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
            return ((EMap.InternalMapView) getXSISchemaLocation()).eMap();
        case MessageElementPackage.DOCUMENT_ROOT__BODY_ELEMENT:
            return getBodyElement();
        case MessageElementPackage.DOCUMENT_ROOT__ENVELOPE:
            return getEnvelope();
        case MessageElementPackage.DOCUMENT_ROOT__FAULT_ELEMENT:
            return getFaultElement();
        case MessageElementPackage.DOCUMENT_ROOT__HEADER_ELEMENT:
            return getHeaderElement();
        case MessageElementPackage.DOCUMENT_ROOT__NOTL_UNDERSTOOD_ELEMENT:
            return getNotlUnderstoodElement();
        case MessageElementPackage.DOCUMENT_ROOT__UPGRADE_ELEMENT:
            return getUpgradeElement();
        case MessageElementPackage.DOCUMENT_ROOT__ENCODING_STYLE:
            return getEncodingStyle();
        case MessageElementPackage.DOCUMENT_ROOT__MUST_UNDERSTAND:
            return isMustUnderstand() ? Boolean.TRUE : Boolean.FALSE;
        case MessageElementPackage.DOCUMENT_ROOT__RELAY:
            return isRelay() ? Boolean.TRUE : Boolean.FALSE;
        case MessageElementPackage.DOCUMENT_ROOT__ROLE:
            return getRole();
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
        case MessageElementPackage.DOCUMENT_ROOT__MIXED:
            ((ESequence) getMixed()).featureMap().clear();
            ((ESequence) getMixed()).featureMap().addAll((Collection) newValue);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
            getXMLNSPrefixMap().clear();
            ((EMap.InternalMapView) getXMLNSPrefixMap()).eMap().addAll((Collection) newValue);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
            getXSISchemaLocation().clear();
            ((EMap.InternalMapView) getXSISchemaLocation()).eMap().addAll((Collection) newValue);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__BODY_ELEMENT:
            setBodyElement((BodyElement) newValue);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__ENVELOPE:
            setEnvelope((MessageElement) newValue);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__FAULT_ELEMENT:
            setFaultElement((FaultElement) newValue);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__HEADER_ELEMENT:
            setHeaderElement((HeaderElement) newValue);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__NOTL_UNDERSTOOD_ELEMENT:
            setNotlUnderstoodElement((NotUnderstoodType) newValue);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__UPGRADE_ELEMENT:
            setUpgradeElement((UpgradeType) newValue);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__ENCODING_STYLE:
            setEncodingStyle((String) newValue);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__MUST_UNDERSTAND:
            setMustUnderstand(((Boolean) newValue).booleanValue());
            return;
        case MessageElementPackage.DOCUMENT_ROOT__RELAY:
            setRelay(((Boolean) newValue).booleanValue());
            return;
        case MessageElementPackage.DOCUMENT_ROOT__ROLE:
            setRole((String) newValue);
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
        case MessageElementPackage.DOCUMENT_ROOT__MIXED:
            ((ESequence) getMixed()).featureMap().clear();
            return;
        case MessageElementPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
            getXMLNSPrefixMap().clear();
            return;
        case MessageElementPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
            getXSISchemaLocation().clear();
            return;
        case MessageElementPackage.DOCUMENT_ROOT__BODY_ELEMENT:
            setBodyElement((BodyElement) null);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__ENVELOPE:
            setEnvelope((MessageElement) null);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__FAULT_ELEMENT:
            setFaultElement((FaultElement) null);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__HEADER_ELEMENT:
            setHeaderElement((HeaderElement) null);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__NOTL_UNDERSTOOD_ELEMENT:
            setNotlUnderstoodElement((NotUnderstoodType) null);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__UPGRADE_ELEMENT:
            setUpgradeElement((UpgradeType) null);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__ENCODING_STYLE:
            setEncodingStyle(ENCODING_STYLE_EDEFAULT);
            return;
        case MessageElementPackage.DOCUMENT_ROOT__MUST_UNDERSTAND:
            unsetMustUnderstand();
            return;
        case MessageElementPackage.DOCUMENT_ROOT__RELAY:
            unsetRelay();
            return;
        case MessageElementPackage.DOCUMENT_ROOT__ROLE:
            setRole(ROLE_EDEFAULT);
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
        case MessageElementPackage.DOCUMENT_ROOT__MIXED:
            return mixed != null && !mixed.featureMap().isEmpty();
        case MessageElementPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
            return xMLNSPrefixMap != null && !xMLNSPrefixMap.isEmpty();
        case MessageElementPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
            return xSISchemaLocation != null && !xSISchemaLocation.isEmpty();
        case MessageElementPackage.DOCUMENT_ROOT__BODY_ELEMENT:
            return getBodyElement() != null;
        case MessageElementPackage.DOCUMENT_ROOT__ENVELOPE:
            return getEnvelope() != null;
        case MessageElementPackage.DOCUMENT_ROOT__FAULT_ELEMENT:
            return getFaultElement() != null;
        case MessageElementPackage.DOCUMENT_ROOT__HEADER_ELEMENT:
            return getHeaderElement() != null;
        case MessageElementPackage.DOCUMENT_ROOT__NOTL_UNDERSTOOD_ELEMENT:
            return getNotlUnderstoodElement() != null;
        case MessageElementPackage.DOCUMENT_ROOT__UPGRADE_ELEMENT:
            return getUpgradeElement() != null;
        case MessageElementPackage.DOCUMENT_ROOT__ENCODING_STYLE:
            return ENCODING_STYLE_EDEFAULT == null ? encodingStyle != null : !ENCODING_STYLE_EDEFAULT.equals(encodingStyle);
        case MessageElementPackage.DOCUMENT_ROOT__MUST_UNDERSTAND:
            return isSetMustUnderstand();
        case MessageElementPackage.DOCUMENT_ROOT__RELAY:
            return isSetRelay();
        case MessageElementPackage.DOCUMENT_ROOT__ROLE:
            return ROLE_EDEFAULT == null ? role != null : !ROLE_EDEFAULT.equals(role);
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
        result.append(", encodingStyle: ");
        result.append(encodingStyle);
        result.append(", mustUnderstand: ");
        if (mustUnderstandESet) result.append(mustUnderstand);
        else
            result.append("<unset>");
        result.append(", relay: ");
        if (relayESet) result.append(relay); else result.append("<unset>");
		result.append(", role: ");
		result.append(role);
		result.append(')');
		return result.toString();
	}

} //DocumentRootImpl
