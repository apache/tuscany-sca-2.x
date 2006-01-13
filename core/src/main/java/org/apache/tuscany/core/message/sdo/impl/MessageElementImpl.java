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
import java.util.Iterator;
import java.util.Map;

import commonj.sdo.DataObject;
import commonj.sdo.Sequence;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.sdo.impl.EDataObjectImpl;
import org.eclipse.emf.ecore.sdo.util.BasicESequence;
import org.eclipse.emf.ecore.sdo.util.ESequence;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.xml.type.SimpleAnyType;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import org.apache.tuscany.model.util.NotifyingHashMapImpl;
import org.apache.tuscany.core.addressing.AddressingConstants;
import org.apache.tuscany.core.addressing.EndpointReference;
import org.apache.tuscany.core.addressing.sdo.AddressingElementFactory;
import org.apache.tuscany.core.addressing.sdo.AttributedURI;
import org.apache.tuscany.core.addressing.sdo.EndpointReferenceElement;
import org.apache.tuscany.core.message.sdo.AnyObject;
import org.apache.tuscany.core.message.sdo.AnyObjectFactory;
import org.apache.tuscany.core.message.sdo.BodyElement;
import org.apache.tuscany.core.message.sdo.HeaderElement;
import org.apache.tuscany.core.message.sdo.MessageElement;
import org.apache.tuscany.core.message.sdo.MessageElementPackage;
import org.apache.tuscany.model.types.java.impl.JavaTypeHelperImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Message</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link MessageElementImpl#getHeaderElement <em>Header Element</em>}</li>
 * <li>{@link MessageElementImpl#getBodyElement <em>Body Element</em>}</li>
 * <li>{@link MessageElementImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MessageElementImpl extends EDataObjectImpl implements MessageElement {
    /**
     * The cached value of the '{@link #getHeaderElement() <em>Header Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getHeaderElement()
     */
    protected HeaderElement headerElement = null;

    /**
     * The cached value of the '{@link #getBodyElement() <em>Body Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getBodyElement()
     */
    protected BodyElement bodyElement = null;

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
    protected MessageElementImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return MessageElementPackage.eINSTANCE.getMessage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public HeaderElement getHeaderElementGen() {
        return headerElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetHeaderElement(HeaderElement newHeaderElement, NotificationChain msgs) {
        HeaderElement oldHeaderElement = headerElement;
        headerElement = newHeaderElement;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MessageElementPackage.MESSAGE__HEADER_ELEMENT, oldHeaderElement, newHeaderElement);
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
    public void setHeaderElementGen(HeaderElement newHeaderElement) {
        if (newHeaderElement != headerElement) {
            NotificationChain msgs = null;
            if (headerElement != null)
                msgs = ((InternalEObject) headerElement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MessageElementPackage.MESSAGE__HEADER_ELEMENT, null, msgs);
            if (newHeaderElement != null)
                msgs = ((InternalEObject) newHeaderElement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MessageElementPackage.MESSAGE__HEADER_ELEMENT, null, msgs);
            msgs = basicSetHeaderElement(newHeaderElement, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MessageElementPackage.MESSAGE__HEADER_ELEMENT, newHeaderElement, newHeaderElement));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public BodyElement getBodyElementGen() {
        return bodyElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetBodyElement(BodyElement newBodyElement, NotificationChain msgs) {
        BodyElement oldBodyElement = bodyElement;
        bodyElement = newBodyElement;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MessageElementPackage.MESSAGE__BODY_ELEMENT, oldBodyElement, newBodyElement);
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
    public void setBodyElementGen(BodyElement newBodyElement) {
        if (newBodyElement != bodyElement) {
            NotificationChain msgs = null;
            if (bodyElement != null)
                msgs = ((InternalEObject) bodyElement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MessageElementPackage.MESSAGE__BODY_ELEMENT, null, msgs);
            if (newBodyElement != null)
                msgs = ((InternalEObject) newBodyElement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MessageElementPackage.MESSAGE__BODY_ELEMENT, null, msgs);
            msgs = basicSetBodyElement(newBodyElement, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MessageElementPackage.MESSAGE__BODY_ELEMENT, newBodyElement, newBodyElement));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getAnyAttribute() {
        if (anyAttribute == null) {
            anyAttribute = new BasicESequence(new BasicFeatureMap(this, MessageElementPackage.MESSAGE__ANY_ATTRIBUTE));
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
            case MessageElementPackage.MESSAGE__HEADER_ELEMENT:
                return basicSetHeaderElement(null, msgs);
            case MessageElementPackage.MESSAGE__BODY_ELEMENT:
                return basicSetBodyElement(null, msgs);
            case MessageElementPackage.MESSAGE__ANY_ATTRIBUTE:
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
        case MessageElementPackage.MESSAGE__HEADER_ELEMENT:
            return getHeaderElement();
        case MessageElementPackage.MESSAGE__BODY_ELEMENT:
            return getBodyElement();
        case MessageElementPackage.MESSAGE__ANY_ATTRIBUTE:
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
        case MessageElementPackage.MESSAGE__HEADER_ELEMENT:
            setHeaderElement((HeaderElement) newValue);
            return;
        case MessageElementPackage.MESSAGE__BODY_ELEMENT:
            setBodyElement((BodyElement) newValue);
            return;
        case MessageElementPackage.MESSAGE__ANY_ATTRIBUTE:
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
        case MessageElementPackage.MESSAGE__HEADER_ELEMENT:
            setHeaderElement((HeaderElement) null);
            return;
        case MessageElementPackage.MESSAGE__BODY_ELEMENT:
            setBodyElement((BodyElement) null);
            return;
        case MessageElementPackage.MESSAGE__ANY_ATTRIBUTE:
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
    public boolean eIsSetGen(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
        case MessageElementPackage.MESSAGE__HEADER_ELEMENT:
            return headerElement != null;
        case MessageElementPackage.MESSAGE__BODY_ELEMENT:
            return bodyElement != null;
        case MessageElementPackage.MESSAGE__ANY_ATTRIBUTE:
            return anyAttribute != null && !anyAttribute.featureMap().isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (anyAttribute: ");
        result.append(anyAttribute);
        result.append(')');
        return result.toString();
    }

    /**
     * Custom code
     */

    private boolean disableBodyAnyNotify;
    private boolean bodySet;
    private boolean bodyAnySet;
    private String operationName;
    private Object body;

    private Map headerMap;
    private boolean headerMapSet;
    private boolean headerAnySet;
    private boolean disableHeaderAnyNotify;
    private boolean disableHeaderMapNotify;

    private ExtendedMetaData extendedMetaData;

    /**
     * This class is used to store the most common fields
     */
    protected class HeaderFields {
        private String action;
        private EndpointReferenceElement endpointReference;
        private EndpointReferenceElement faultTo;
        private EndpointReferenceElement from;
        private String messageID;
        private String relatesTo;
        private EndpointReferenceElement replyTo;
        private EndpointReferenceElement to;

        /**
         * @return Returns the action.
         */
        public String getAction() {
            return action;
        }

        /**
         * @param action The action to set.
         */
        public void setAction(String action) {
            headerFieldsSet = true;
            headerAnySet = false;
            this.action = action;
        }

        /**
         * @return Returns the endpointReference.
         */
        public EndpointReferenceElement getEndpointReference() {
            return endpointReference;
        }

        /**
         * @param endpointReference The endpointReference to set.
         */
        public void setEndpointReference(EndpointReferenceElement endpointReference) {
            headerFieldsSet = true;
            headerAnySet = false;
            this.endpointReference = endpointReference;
        }

        /**
         * @return Returns the faultTo.
         */
        public EndpointReferenceElement getFaultTo() {
            return faultTo;
        }

        /**
         * @param faultTo The faultTo to set.
         */
        public void setFaultTo(EndpointReferenceElement faultTo) {
            headerFieldsSet = true;
            headerAnySet = false;
            this.faultTo = faultTo;
        }

        /**
         * @return Returns the from.
         */
        public EndpointReferenceElement getFrom() {
            return from;
        }

        /**
         * @param from The from to set.
         */
        public void setFrom(EndpointReferenceElement from) {
            headerFieldsSet = true;
            headerAnySet = false;
            this.from = from;
        }

        /**
         * @return Returns the messageID.
         */
        public String getMessageID() {
            return messageID;
        }

        /**
         * @param messageID The messageID to set.
         */
        public void setMessageID(String messageID) {
            headerFieldsSet = true;
            headerAnySet = false;
            this.messageID = messageID;
        }

        /**
         * @return Returns the relatesTo.
         */
        public String getRelatesTo() {
            return relatesTo;
        }

        /**
         * @param relatesTo The relatesTo to set.
         */
        public void setRelatesTo(String relatesTo) {
            headerFieldsSet = true;
            headerAnySet = false;
            this.relatesTo = relatesTo;
        }

        /**
         * @return Returns the replyTo.
         */
        public EndpointReferenceElement getReplyTo() {
            return replyTo;
        }

        /**
         * @param replyTo The replyTo to set.
         */
        public void setReplyTo(EndpointReferenceElement replyTo) {
            headerFieldsSet = true;
            headerAnySet = false;
            this.replyTo = replyTo;
        }

        /**
         * @return Returns the to.
         */
        public EndpointReferenceElement getTo() {
            return to;
        }

        /**
         * @param to The to to set.
         */
        public void setTo(EndpointReferenceElement to) {
            headerFieldsSet = true;
            headerAnySet = false;
            this.to = to;
        }
    }

    private HeaderFields headerFields;
    private boolean headerFieldsSet;

    /**
     * @return Returns the headerFields.
     */
    protected HeaderFields getHeaderFields() {
        if (headerFields == null)
            headerFields = new HeaderFields();
        if (!headerFieldsSet && headerAnySet) {

            // Refresh the header fields from the any featureMap if necessary
            ESequence any = (ESequence) getHeaderElement().getAny();
            FeatureMap featureMap = any.featureMap();
            for (Iterator i = featureMap.iterator(); i.hasNext();) {
                FeatureMap.Entry featureMapEntry = (FeatureMap.Entry) i.next();
                EStructuralFeature feature = featureMapEntry.getEStructuralFeature();
                String qname = ExtendedMetaData.INSTANCE.getNamespace(feature) + '#' + ExtendedMetaData.INSTANCE.getName(feature);

                if (AddressingConstants.ACTION_HEADER_NAME.equals(qname)) {
                    AttributedURI actionURI = (AttributedURI) featureMapEntry.getValue();
                    if (actionURI != null) {
                        String value = actionURI.getValue();
                        headerFields.setAction(value);
                    }
                } else if (AddressingConstants.ENDPOINT_REFERENCE_HEADER_NAME.equals(qname)) {
                    headerFields.setEndpointReference((EndpointReferenceElement) featureMapEntry.getValue());

                } else if (AddressingConstants.FAULT_TO_HEADER_NAME.equals(qname)) {
                    headerFields.setFaultTo((EndpointReferenceElement) featureMapEntry.getValue());

                } else if (AddressingConstants.FROM_HEADER_NAME.equals(qname)) {
                    headerFields.setFrom((EndpointReferenceElement) featureMapEntry.getValue());

                } else if (AddressingConstants.MESSAGE_ID_HEADER_NAME.equals(qname)) {
                    SimpleAnyType value = (SimpleAnyType) featureMapEntry.getValue();
                    headerFields.setMessageID((String) value.getValue());

                } else if (AddressingConstants.RELATES_TO_HEADER_NAME.equals(qname)) {
                    SimpleAnyType value = (SimpleAnyType) featureMapEntry.getValue();
                    headerFields.setRelatesTo((String) value.getValue());

                } else if (AddressingConstants.REPLY_TO_HEADER_NAME.equals(qname)) {
                    headerFields.setReplyTo((EndpointReferenceElement) featureMapEntry.getValue());

                } else if (AddressingConstants.TO_HEADER_NAME.equals(qname)) {
                    AttributedURI address = (AttributedURI) featureMapEntry.getValue();
                    EndpointReferenceElement to = AddressingElementFactory.eINSTANCE.createEndpointReferenceElement();
                    ((EndpointReference) to).setAddress(address.getValue());
                    headerFields.setTo(to);
                }
            }
            headerFieldsSet = true;
            headerAnySet = true;
        }

        return headerFields;
    }


    /**
     * This adapter is used to keep the body field in sync with the any feature map
     */
    private class BodyElementAnyAdapterImpl extends AdapterImpl {

        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#isAdapterForType(java.lang.Object)
         */
        public boolean isAdapterForType(Object type) {
            return type == BodyElementAnyAdapterImpl.class;
        }

        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
         */
        public void notifyChanged(Notification notification) {
            if (disableBodyAnyNotify)
                return;
            if (notification.getFeature() == MessageElementPackage.eINSTANCE.getBodyElement_Any()) {

                // If the any feature map is changed reset the body field
                body = null;
                operationName = null;
                bodySet = false;
                bodyAnySet = true;
            }
        }
    }

    /**
     * This adapter is used to keep the headers map in sync with the any feature map
     */
    private class HeaderElementAnyAdapterImpl extends AdapterImpl {

        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#isAdapterForType(java.lang.Object)
         */
        public boolean isAdapterForType(Object type) {
            return type == HeaderElementAnyAdapterImpl.class;
        }

        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
         */
        public void notifyChanged(Notification notification) {
            if (disableHeaderAnyNotify)
                return;
            if (notification.getFeature() == MessageElementPackage.eINSTANCE.getHeaderElement_Any()) {

                // If the any feature map is changed reset the headers map
                getHeaderMap();
                headerMap.clear();
                headerMapSet = false;
                headerAnySet = true;
                headerFields = null;
                headerFieldsSet = false;
            }
        }
    }

    /**
     * This adapter is used to keep track of changes in the headers map
     */
    private class HeaderMapAdapterImpl extends AdapterImpl {

        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#isAdapterForType(java.lang.Object)
         */
        public boolean isAdapterForType(Object type) {
            return type == HeaderMapAdapterImpl.class;
        }

        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
         */
        public void notifyChanged(Notification notification) {
            if (disableHeaderMapNotify)
                return;
            headerMapSet = true;
            headerAnySet = false;
        }

    }

    /**
     * This represents a message body element.
     */
    private class ManagedBodyElementImpl extends BodyElementImpl {

        /**
         * Constructor
         */
        private ManagedBodyElementImpl() {
            super();
        }

        /**
         * @see org.apache.tuscany.core.message.sdo.impl.BodyElementImpl#getAny()
         */
        public Sequence getAny() {
            Sequence bodyAny = super.getAny();
            EObject container = eContainer();
            if (container instanceof MessageElementImpl) {
                MessageElementImpl message = (MessageElementImpl) container;
                if (message.bodySet && !message.bodyAnySet) {
                    try {
                        message.disableBodyAnyNotify = true;

                        // Clear the feature map
                        FeatureMap bodyFeatureMap = ((ESequence) super.getAny()).featureMap();
                        if (bodyFeatureMap.size() != 0)
                            bodyFeatureMap.clear();

                        Object body = message.body;
                        if (body != null) {
                            EStructuralFeature bodyFeature = null;

                            String partName = message.operationName;
                            if (partName == null)
                                partName = "unknown";

                            // Store a DataObject into a demand feature
                            if (body instanceof EObject) {

                                // First make a copy of the DataObject
                                DataObject bodyCopy = (DataObject) EcoreUtil.copy((EObject) body);

                                // Then store the copy into the demand feature
                                bodyFeature = message.demandElement(null, partName);

                                // Add the demand feature
                                bodyFeatureMap.add(bodyFeature, bodyCopy);

                            } else if (body == null) {

                                // Create a vanilla demand feature
                                bodyFeature = message.demandElement(null, partName);

                                // Add the demand feature
                                bodyFeatureMap.add(bodyFeature, body);

                            } else {

                                // Store a simple type into a demand feature
                                //FIXME pass a ModelContext
                                EDataType dataType = new JavaTypeHelperImpl(null).getBuiltinDataType(body.getClass());
                                if (dataType != null) {
                                    bodyFeature = message.demandElement(null, partName);
                                    SimpleAnyType simpleAnyType = XMLTypeFactory.eINSTANCE.createSimpleAnyType();
                                    simpleAnyType.setInstanceType(dataType);
                                    simpleAnyType.setValue(body);
                                    body = simpleAnyType;
                                } else {

                                    // Store a Java object into a demand feature
                                    bodyFeature = message.demandElement(null, partName);
                                    AnyObject anyObject = AnyObjectFactory.eINSTANCE.createAnyObject();
                                    anyObject.setObject(body);
                                    body = anyObject;
                                }

                                // Add the demand feature
                                bodyFeatureMap.add(bodyFeature, body);
                            }
                        }

                        message.bodyAnySet = true;
                        message.bodySet = true;
                    } finally {
                        message.disableBodyAnyNotify = false;
                    }
                }
            }
            return bodyAny;
        }

        /**
         * @see org.apache.tuscany.core.message.sdo.impl.BodyElementImpl#eIsSet(org.eclipse.emf.ecore.EStructuralFeature)
         */
        public boolean eIsSet(EStructuralFeature eFeature) {
            if (eDerivedStructuralFeatureID(eFeature) == MessageElementPackage.BODY_ELEMENT__ANY) {

                // Return true if our body field is set or if the any feature map is not empty
                EObject container = eContainer();
                if (container instanceof MessageElementImpl) {
                    MessageElementImpl message = (MessageElementImpl) container;
                    if (message.bodySet)
                        return message.getBody() != null;
                }
                return any != null && !any.featureMap().isEmpty();
            }
            return super.eIsSet(eFeature);
        }

    }

    /**
     * This represents a message header element.
     */
    private static class ManagedHeaderElementImpl extends HeaderElementImpl {

        private MessageElementImpl message;

        /**
         * Constructor
         */
        private ManagedHeaderElementImpl(MessageElementImpl message) {
            super();
            this.message = message;
        }

        /**
         * Add to the any featureMap
         */
        private void addToFeatureMap(MessageElementImpl message, FeatureMap featureMap, String qname, Object value) {
            int h = qname.indexOf('#');
            EStructuralFeature feature = message.demandElement(qname.substring(0, h), qname.substring(h + 1));
            featureMap.add(feature, value);
        }

        /**
         * @see org.apache.tuscany.core.message.sdo.impl.HeaderElementImpl#getAny()
         */
        public Sequence getAny() {
            ESequence any = (ESequence) super.getAny();
            if (message != null) {
                if ((message.headerFieldsSet || message.headerMapSet) && !message.headerAnySet) {
                    try {
                        message.disableHeaderAnyNotify = true;

                        // Refresh the any featureMap from the header map and the header fields if necessary
                        FeatureMap featureMap = any.featureMap();
                        if (featureMap.size() != 0)
                            featureMap.clear();
                        if (message.headerMapSet) {
                            message.getHeaderMap();
                            for (Iterator i = message.headerMap.entrySet().iterator(); i.hasNext();) {
                                Map.Entry e = (Map.Entry) i.next();
                                String qname = (String) e.getKey();
                                addToFeatureMap(message, featureMap, qname, e.getValue());
                            }
                        }
                        if (message.headerFieldsSet) {
                            HeaderFields fields = message.getHeaderFields();
                            if (fields.action != null) {
                                AttributedURI actionURI = AddressingElementFactory.eINSTANCE.createAttributedURI();
                                actionURI.setValue(fields.action);
                                addToFeatureMap(message, featureMap, AddressingConstants.ACTION_HEADER_NAME, EcoreUtil.copy((EObject) actionURI));
                            }
                            if (fields.endpointReference != null) {
                                addToFeatureMap(message, featureMap, AddressingConstants.ENDPOINT_REFERENCE_HEADER_NAME, EcoreUtil.copy((EObject) fields.endpointReference));
                            }
                            if (fields.faultTo != null) {
                                addToFeatureMap(message, featureMap, AddressingConstants.FAULT_TO_HEADER_NAME, EcoreUtil.copy((EObject) fields.faultTo));
                            }
                            if (fields.from != null) {
                                addToFeatureMap(message, featureMap, AddressingConstants.FROM_HEADER_NAME, EcoreUtil.copy((EObject) fields.from));
                            }
                            if (fields.messageID != null) {
                                SimpleAnyType value = XMLTypeFactory.eINSTANCE.createSimpleAnyType();
                                value.setInstanceType(XMLTypePackage.eINSTANCE.getString());
                                value.setValue(fields.messageID);
                                addToFeatureMap(message, featureMap, AddressingConstants.MESSAGE_ID_HEADER_NAME, value);
                            }
                            if (fields.relatesTo != null) {
                                SimpleAnyType value = XMLTypeFactory.eINSTANCE.createSimpleAnyType();
                                value.setInstanceType(XMLTypePackage.eINSTANCE.getString());
                                value.setValue(fields.relatesTo);
                                addToFeatureMap(message, featureMap, AddressingConstants.RELATES_TO_HEADER_NAME, value);
                            }
                            if (fields.replyTo != null) {
                                addToFeatureMap(message, featureMap, AddressingConstants.REPLY_TO_HEADER_NAME, EcoreUtil.copy((EObject) fields.replyTo));
                            }
                            if (fields.to != null) {
                                AttributedURI addressURI = AddressingElementFactory.eINSTANCE.createAttributedURI();
                                addressURI.setValue(((EndpointReference) fields.to).getAddress());
                                addToFeatureMap(message, featureMap, AddressingConstants.TO_HEADER_NAME, EcoreUtil.copy((EObject) addressURI));
                            }

                        }

                        message.headerAnySet = true;
                    } finally {
                        message.disableHeaderAnyNotify = false;
                    }
                }
            }
            return any;
        }

        /**
         * @see org.apache.tuscany.core.message.sdo.impl.HeaderElementImpl#eIsSet(org.eclipse.emf.ecore.EStructuralFeature)
         */
        public boolean eIsSet(EStructuralFeature eFeature) {
            if (eDerivedStructuralFeatureID(eFeature) == MessageElementPackage.HEADER_ELEMENT__ANY) {

                // Return true if our body field is set or if the any feature map is not empty
                if (message != null) {
                    if (message.headerMapSet || message.headerFieldsSet) {
                        message.getHeaderMap();
                        return message.headerFields != null || !message.headerMap.isEmpty();
                    }
                }
                return any != null && !any.featureMap().isEmpty();
            }
            return super.eIsSet(eFeature);
        }

    }

    /**
     * Create a demandFeature for the named element.
     *
     */
    private EStructuralFeature demandElement(String nsURI, String partName) {
        if (extendedMetaData == null)
            extendedMetaData = new BasicExtendedMetaData();
        return extendedMetaData.demandFeature(nsURI, partName, true);
    }

    /**
     * Returns the header map.
     *
     */
    private Map getHeaderMap() {
        if (headerMap == null)
            headerMap = new NotifyingHashMapImpl(new HeaderMapAdapterImpl());
        return headerMap;
    }

    protected Map getHeaders() {
        getHeaderMap();
        if (!headerMapSet && headerAnySet) {
            try {
                disableHeaderMapNotify = true;

                // Refresh the headers map from the any featureMap if necessary
                headerMap.clear();
                ESequence any = (ESequence) getHeaderElement().getAny();
                FeatureMap featureMap = any.featureMap();
                for (Iterator i = featureMap.iterator(); i.hasNext();) {
                    FeatureMap.Entry featureMapEntry = (FeatureMap.Entry) i.next();
                    EStructuralFeature feature = featureMapEntry.getEStructuralFeature();
                    String qname = ExtendedMetaData.INSTANCE.getNamespace(feature) + '#' + ExtendedMetaData.INSTANCE.getName(feature);
                    Object value = featureMapEntry.getValue();
                    headerMap.put(qname, value);
                }
                headerMapSet = true;
            } finally {
                disableHeaderMapNotify = false;
            }
        }
        return headerMap;
    }

    protected void setBody(Object body) {
        this.body = body;
        bodySet = true;
        bodyAnySet = false;
    }

    protected Object getBody() {
        // Refresh the body field from the any featureMap if necessary
        if (!bodySet && bodyAnySet) {
            Sequence bodyAny = getBodyElement().getAny();
            if (bodyAny.size() != 0) {

                // Set the operation name field
                operationName = bodyAny.getProperty(0).getName();

                Object value = bodyAny.getValue(0);
                if (value instanceof SimpleAnyType) {

                    // Simple type
                    value = ((SimpleAnyType) value).getValue();

                } else if (value instanceof AnyObject) {

                    // Java object
                    value = ((AnyObject) value).getObject();
                }

                if (value instanceof EObject) {

                    // Detach the EObject from its container
                    EObject eObject = (EObject) value;
                    if (eObject.eContainer() != null)
                        eObject.eContainer().eUnset(eObject.eContainingFeature());
                }

                // Set the body field
                body = value;

            } else
                body = null;

            // Move the body to the java field
            bodySet = true;
            bodyAnySet = false; // Defect 264920
        }

        return body;
    }

    /**
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementImpl#getBodyElement()
     */
    public BodyElement getBodyElement() {
        BodyElement bodyElement = getBodyElementGen();
        if (bodyElement == null) {
            bodyElement = new ManagedBodyElementImpl();
            setBodyElementGen(bodyElement);
            ((Notifier) bodyElement).eAdapters().add(new BodyElementAnyAdapterImpl());
            return bodyElement;
        }
        return getBodyElementGen();
    }

    /**
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementImpl#setBodyElement(org.apache.tuscany.core.message.sdo.BodyElement)
     */
    public void setBodyElement(BodyElement newBodyElement) {
        setBodyElementGen(newBodyElement);

        // Add our adapter to track changes to the any feature map
        if (newBodyElement != null) {
            ((Notifier) newBodyElement).eAdapters().add(new BodyElementAnyAdapterImpl());
        }

        // Reset our body field
        body = null;
        bodySet = false;
        bodyAnySet = true;
    }

    /**
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementImpl#getHeaderElement()
     */
    public HeaderElement getHeaderElement() {
        HeaderElement headerElement = getHeaderElementGen();
        if (headerElement == null) {
            headerElement = new ManagedHeaderElementImpl(this);
            setHeaderElementGen(headerElement);
            return headerElement;
        }
        return getHeaderElementGen();
    }

    /**
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementImpl#setHeaderElement(org.apache.tuscany.core.message.sdo.HeaderElement)
     */
    public void setHeaderElement(HeaderElement newHeaderElement) {
        setHeaderElementGen(newHeaderElement);

        // Add our adapter to track changes to the any feature map
        if (newHeaderElement != null)
            ((Notifier) newHeaderElement).eAdapters().add(new HeaderElementAnyAdapterImpl());

        // Reset the headers map
        getHeaderMap();
        headerMap.clear();
        headerMapSet = false;
        headerFieldsSet = false;
        headerFields = null;
        headerAnySet = true;
    }

    /**
     * @see org.apache.tuscany.core.message.sdo.impl.MessageElementImpl#eIsSet(org.eclipse.emf.ecore.EStructuralFeature)
     */
    public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
        case MessageElementPackage.MESSAGE__HEADER_ELEMENT :
            return true;
        case MessageElementPackage.MESSAGE__BODY_ELEMENT :
            return true;
        default :
            return eIsSetGen(eFeature);
        }
    }

    protected void setOperationName(String operationName) {
        this.operationName = operationName;
        bodySet = true;
        bodyAnySet = false;
    }

    public String getOperationName() {
        // Refresh the body and operationName fields the from the any featureMap if necessary
        if (!bodySet && bodyAnySet) {
            getBody();
        }
		return operationName;
	}

} //MessageImpl
