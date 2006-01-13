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
import java.util.Iterator;
import java.util.Map;

import commonj.sdo.Sequence;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.sdo.impl.EDataObjectImpl;
import org.eclipse.emf.ecore.sdo.util.BasicESequence;
import org.eclipse.emf.ecore.sdo.util.ESequence;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.apache.tuscany.model.util.NotifyingHashMapImpl;
import org.apache.tuscany.core.addressing.sdo.AddressingElementFactory;
import org.apache.tuscany.core.addressing.sdo.AddressingElementPackage;
import org.apache.tuscany.core.addressing.sdo.AttributedQName;
import org.apache.tuscany.core.addressing.sdo.AttributedURI;
import org.apache.tuscany.core.addressing.sdo.EndpointReferenceElement;
import org.apache.tuscany.core.addressing.sdo.ReferenceParameters;
import org.apache.tuscany.core.addressing.sdo.ReferenceProperties;
import org.apache.tuscany.core.addressing.sdo.ServiceName;
import org.apache.tuscany.core.message.sdo.MessageElementPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Endpoint Reference</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are implemented:
 * <ul>
 * <li>{@link EndpointReferenceElementImpl#getAddressElement <em>Address Element</em>}</li>
 * <li>{@link EndpointReferenceElementImpl#getReferencePropertiesElement <em>Reference Properties Element</em>}</li>
 * <li>{@link EndpointReferenceElementImpl#getReferenceParametersElement <em>Reference Parameters Element</em>}</li>
 * <li>{@link EndpointReferenceElementImpl#getPortTypeElement <em>Port Type Element</em>}</li>
 * <li>{@link EndpointReferenceElementImpl#getServiceNameElement <em>Service Name Element</em>}</li>
 * <li>{@link EndpointReferenceElementImpl#getAny <em>Any</em>}</li>
 * <li>{@link EndpointReferenceElementImpl#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EndpointReferenceElementImpl extends EDataObjectImpl implements EndpointReferenceElement {
    /**
     * The cached value of the '{@link #getAddressElement() <em>Address Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getAddressElement()
     */
    protected AttributedURI addressElement = null;

    /**
     * The cached value of the '{@link #getReferencePropertiesElement() <em>Reference Properties Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getReferencePropertiesElement()
     */
    protected ReferenceProperties referencePropertiesElement = null;

    /**
     * The cached value of the '{@link #getReferenceParametersElement() <em>Reference Parameters Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getReferenceParametersElement()
     */
    protected ReferenceParameters referenceParametersElement = null;

    /**
     * The cached value of the '{@link #getPortTypeElement() <em>Port Type Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getPortTypeElement()
     */
    protected AttributedQName portTypeElement = null;

    /**
     * The cached value of the '{@link #getServiceNameElement() <em>Service Name Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getServiceNameElement()
     */
    protected ServiceName serviceNameElement = null;

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
    protected EndpointReferenceElementImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return AddressingElementPackage.eINSTANCE.getEndpointReference();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public AttributedURI getAddressElement() {
        return addressElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetAddressElement(AttributedURI newAddressElement, NotificationChain msgs) {
        AttributedURI oldAddressElement = addressElement;
        addressElement = newAddressElement;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AddressingElementPackage.ENDPOINT_REFERENCE__ADDRESS_ELEMENT, oldAddressElement, newAddressElement);
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
    public void setAddressElement(AttributedURI newAddressElement) {
        if (newAddressElement != addressElement) {
            NotificationChain msgs = null;
            if (addressElement != null)
                msgs = ((InternalEObject) addressElement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AddressingElementPackage.ENDPOINT_REFERENCE__ADDRESS_ELEMENT, null, msgs);
            if (newAddressElement != null)
                msgs = ((InternalEObject) newAddressElement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AddressingElementPackage.ENDPOINT_REFERENCE__ADDRESS_ELEMENT, null, msgs);
            msgs = basicSetAddressElement(newAddressElement, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AddressingElementPackage.ENDPOINT_REFERENCE__ADDRESS_ELEMENT, newAddressElement, newAddressElement));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ReferenceProperties getReferencePropertiesElement() {
        return referencePropertiesElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetReferencePropertiesElement(ReferenceProperties newReferencePropertiesElement, NotificationChain msgs) {
        ReferenceProperties oldReferencePropertiesElement = referencePropertiesElement;
        referencePropertiesElement = newReferencePropertiesElement;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PROPERTIES_ELEMENT, oldReferencePropertiesElement, newReferencePropertiesElement);
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
    public void setReferencePropertiesElement(ReferenceProperties newReferencePropertiesElement) {
        if (newReferencePropertiesElement != referencePropertiesElement) {
            NotificationChain msgs = null;
            if (referencePropertiesElement != null)
                msgs = ((InternalEObject) referencePropertiesElement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PROPERTIES_ELEMENT, null, msgs);
            if (newReferencePropertiesElement != null)
                msgs = ((InternalEObject) newReferencePropertiesElement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PROPERTIES_ELEMENT, null, msgs);
            msgs = basicSetReferencePropertiesElement(newReferencePropertiesElement, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PROPERTIES_ELEMENT, newReferencePropertiesElement, newReferencePropertiesElement));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ReferenceParameters getReferenceParametersElementGen() {
        return referenceParametersElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetReferenceParametersElement(ReferenceParameters newReferenceParametersElement, NotificationChain msgs) {
        ReferenceParameters oldReferenceParametersElement = referenceParametersElement;
        referenceParametersElement = newReferenceParametersElement;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PARAMETERS_ELEMENT, oldReferenceParametersElement, newReferenceParametersElement);
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
    public void setReferenceParametersElementGen(ReferenceParameters newReferenceParametersElement) {
        if (newReferenceParametersElement != referenceParametersElement) {
            NotificationChain msgs = null;
            if (referenceParametersElement != null)
                msgs = ((InternalEObject) referenceParametersElement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PARAMETERS_ELEMENT, null, msgs);
            if (newReferenceParametersElement != null)
                msgs = ((InternalEObject) newReferenceParametersElement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PARAMETERS_ELEMENT, null, msgs);
            msgs = basicSetReferenceParametersElement(newReferenceParametersElement, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PARAMETERS_ELEMENT, newReferenceParametersElement, newReferenceParametersElement));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public AttributedQName getPortTypeElement() {
        return portTypeElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetPortTypeElement(AttributedQName newPortTypeElement, NotificationChain msgs) {
        AttributedQName oldPortTypeElement = portTypeElement;
        portTypeElement = newPortTypeElement;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AddressingElementPackage.ENDPOINT_REFERENCE__PORT_TYPE_ELEMENT, oldPortTypeElement, newPortTypeElement);
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
    public void setPortTypeElement(AttributedQName newPortTypeElement) {
        if (newPortTypeElement != portTypeElement) {
            NotificationChain msgs = null;
            if (portTypeElement != null)
                msgs = ((InternalEObject) portTypeElement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AddressingElementPackage.ENDPOINT_REFERENCE__PORT_TYPE_ELEMENT, null, msgs);
            if (newPortTypeElement != null)
                msgs = ((InternalEObject) newPortTypeElement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AddressingElementPackage.ENDPOINT_REFERENCE__PORT_TYPE_ELEMENT, null, msgs);
            msgs = basicSetPortTypeElement(newPortTypeElement, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AddressingElementPackage.ENDPOINT_REFERENCE__PORT_TYPE_ELEMENT, newPortTypeElement, newPortTypeElement));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ServiceName getServiceNameElement() {
        return serviceNameElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetServiceNameElement(ServiceName newServiceNameElement, NotificationChain msgs) {
        ServiceName oldServiceNameElement = serviceNameElement;
        serviceNameElement = newServiceNameElement;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AddressingElementPackage.ENDPOINT_REFERENCE__SERVICE_NAME_ELEMENT, oldServiceNameElement, newServiceNameElement);
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
    public void setServiceNameElement(ServiceName newServiceNameElement) {
        if (newServiceNameElement != serviceNameElement) {
            NotificationChain msgs = null;
            if (serviceNameElement != null)
                msgs = ((InternalEObject) serviceNameElement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - AddressingElementPackage.ENDPOINT_REFERENCE__SERVICE_NAME_ELEMENT, null, msgs);
            if (newServiceNameElement != null)
                msgs = ((InternalEObject) newServiceNameElement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - AddressingElementPackage.ENDPOINT_REFERENCE__SERVICE_NAME_ELEMENT, null, msgs);
            msgs = basicSetServiceNameElement(newServiceNameElement, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AddressingElementPackage.ENDPOINT_REFERENCE__SERVICE_NAME_ELEMENT, newServiceNameElement, newServiceNameElement));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getAny() {
        if (any == null) {
            any = new BasicESequence(new BasicFeatureMap(this, AddressingElementPackage.ENDPOINT_REFERENCE__ANY));
        }
        return any;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Sequence getAnyAttribute() {
        if (anyAttribute == null) {
            anyAttribute = new BasicESequence(new BasicFeatureMap(this, AddressingElementPackage.ENDPOINT_REFERENCE__ANY_ATTRIBUTE));
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
            case AddressingElementPackage.ENDPOINT_REFERENCE__ADDRESS_ELEMENT:
                return basicSetAddressElement(null, msgs);
            case AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PROPERTIES_ELEMENT:
                return basicSetReferencePropertiesElement(null, msgs);
            case AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PARAMETERS_ELEMENT:
                return basicSetReferenceParametersElement(null, msgs);
            case AddressingElementPackage.ENDPOINT_REFERENCE__PORT_TYPE_ELEMENT:
                return basicSetPortTypeElement(null, msgs);
            case AddressingElementPackage.ENDPOINT_REFERENCE__SERVICE_NAME_ELEMENT:
                return basicSetServiceNameElement(null, msgs);
            case AddressingElementPackage.ENDPOINT_REFERENCE__ANY:
                return ((InternalEList) ((ESequence) getAny()).featureMap()).basicRemove(otherEnd, msgs);
            case AddressingElementPackage.ENDPOINT_REFERENCE__ANY_ATTRIBUTE:
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
        case AddressingElementPackage.ENDPOINT_REFERENCE__ADDRESS_ELEMENT:
            return getAddressElement();
        case AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PROPERTIES_ELEMENT:
            return getReferencePropertiesElement();
        case AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PARAMETERS_ELEMENT:
            return getReferenceParametersElement();
        case AddressingElementPackage.ENDPOINT_REFERENCE__PORT_TYPE_ELEMENT:
            return getPortTypeElement();
        case AddressingElementPackage.ENDPOINT_REFERENCE__SERVICE_NAME_ELEMENT:
            return getServiceNameElement();
        case AddressingElementPackage.ENDPOINT_REFERENCE__ANY:
            return ((ESequence) getAny()).featureMap();
        case AddressingElementPackage.ENDPOINT_REFERENCE__ANY_ATTRIBUTE:
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
        case AddressingElementPackage.ENDPOINT_REFERENCE__ADDRESS_ELEMENT:
            setAddressElement((AttributedURI) newValue);
            return;
        case AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PROPERTIES_ELEMENT:
            setReferencePropertiesElement((ReferenceProperties) newValue);
            return;
        case AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PARAMETERS_ELEMENT:
            setReferenceParametersElement((ReferenceParameters) newValue);
            return;
        case AddressingElementPackage.ENDPOINT_REFERENCE__PORT_TYPE_ELEMENT:
            setPortTypeElement((AttributedQName) newValue);
            return;
        case AddressingElementPackage.ENDPOINT_REFERENCE__SERVICE_NAME_ELEMENT:
            setServiceNameElement((ServiceName) newValue);
            return;
        case AddressingElementPackage.ENDPOINT_REFERENCE__ANY:
            ((ESequence) getAny()).featureMap().clear();
            ((ESequence) getAny()).featureMap().addAll((Collection) newValue);
            return;
        case AddressingElementPackage.ENDPOINT_REFERENCE__ANY_ATTRIBUTE:
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
        case AddressingElementPackage.ENDPOINT_REFERENCE__ADDRESS_ELEMENT:
            setAddressElement((AttributedURI) null);
            return;
        case AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PROPERTIES_ELEMENT:
            setReferencePropertiesElement((ReferenceProperties) null);
            return;
        case AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PARAMETERS_ELEMENT:
            setReferenceParametersElement((ReferenceParameters) null);
            return;
        case AddressingElementPackage.ENDPOINT_REFERENCE__PORT_TYPE_ELEMENT:
            setPortTypeElement((AttributedQName) null);
            return;
        case AddressingElementPackage.ENDPOINT_REFERENCE__SERVICE_NAME_ELEMENT:
            setServiceNameElement((ServiceName) null);
            return;
        case AddressingElementPackage.ENDPOINT_REFERENCE__ANY:
            ((ESequence) getAny()).featureMap().clear();
            return;
        case AddressingElementPackage.ENDPOINT_REFERENCE__ANY_ATTRIBUTE:
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
        case AddressingElementPackage.ENDPOINT_REFERENCE__ADDRESS_ELEMENT:
            return addressElement != null;
        case AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PROPERTIES_ELEMENT:
            return referencePropertiesElement != null;
        case AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PARAMETERS_ELEMENT:
            return referenceParametersElement != null;
        case AddressingElementPackage.ENDPOINT_REFERENCE__PORT_TYPE_ELEMENT:
            return portTypeElement != null;
        case AddressingElementPackage.ENDPOINT_REFERENCE__SERVICE_NAME_ELEMENT:
            return serviceNameElement != null;
        case AddressingElementPackage.ENDPOINT_REFERENCE__ANY:
            return any != null && !any.featureMap().isEmpty();
        case AddressingElementPackage.ENDPOINT_REFERENCE__ANY_ATTRIBUTE:
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
        result.append(" (any: ");
        result.append(any);
        result.append(", anyAttribute: ");
        result.append(anyAttribute);
        result.append(')');
        return result.toString();
    }


    /**
     * Custom code
     */

    private Map parameterMap;
    private boolean parameterMapSet;
    private boolean parameterAnySet;
    private boolean disableParameterAnyNotify;
    private boolean disableParameterMapNotify;

    private ExtendedMetaData extendedMetaData;

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#getAddress()
     */
    public String getAddress() {
        AttributedURI uri = getAddressElement();
        return uri != null ? uri.getValue() : null;
    }

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#setAddress(java.lang.String)
     */
    public void setAddress(String value) {
        if (value != null) {
            AttributedURI uri = AddressingElementFactory.eINSTANCE.createAttributedURI();
            uri.setValue(value);
            setAddressElement(uri);
        } else
            setAddressElement(null);
    }

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#getPortTypeName()
     */
    public String getPortTypeName() {
        AttributedQName aqname = getPortTypeElement();
        org.eclipse.emf.ecore.xml.type.internal.QName qname = aqname != null ? (org.eclipse.emf.ecore.xml.type.internal.QName) aqname.getValue() : null;
        if (qname == null) {
            return null;
        } else {
            return qname.getNamespaceURI() + '#' + qname.getLocalPart();
        }
    }

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#setPortTypeName(java.lang.String)
     */
    public void setPortTypeName(String value) {
        if (value != null) {
            AttributedQName aqname = AddressingElementFactory.eINSTANCE.createAttributedQName();
            int h = value.indexOf('#');
            aqname.setValue(new org.eclipse.emf.ecore.xml.type.internal.QName(value.substring(0, h), value.substring(h + 1), "pt"));
            setPortTypeElement(aqname);
        } else {
            setPortTypeElement(null);
        }
    }

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#getServiceName()
     */
    public String getServiceName() {
        ServiceName sname = getServiceNameElement();
        org.eclipse.emf.ecore.xml.type.internal.QName qname = sname != null ? (org.eclipse.emf.ecore.xml.type.internal.QName) sname.getValue() : null;
        if (qname == null) {
            return null;
        } else {
            return qname.getNamespaceURI() + '#' + qname.getLocalPart();
        }
    }

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#getPortName()
     */
    public String getPortName() {
        ServiceName sname = getServiceNameElement();
        return sname != null ? sname.getPortName() : null;
    }

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#setServiceName(java.lang.String)
     */
    public void setServiceName(String value) {
        if (value != null) {
            int h = value.indexOf('#');
            ServiceName sname = getServiceNameElement();
            if (sname == null) {
                sname = AddressingElementFactory.eINSTANCE.createServiceName();
                sname.setValue(new org.eclipse.emf.ecore.xml.type.internal.QName(value.substring(0, h), value.substring(h + 1), "s"));
                setServiceNameElement(sname);
            } else {
                sname.setValue(new org.eclipse.emf.ecore.xml.type.internal.QName(value.substring(0, h), value.substring(h + 1), "s"));
            }
        } else {
            ServiceName sname = getServiceNameElement();
            if (sname != null) {
                if (sname.getPortName() == null) {
                    setServiceNameElement(null);
                } else {
                    sname.setValue(null);
                }
            }
        }
    }

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#setPortName(java.lang.String)
     */
    public void setPortName(String portName) {
        if (portName != null) {
            ServiceName sname = getServiceNameElement();
            if (sname == null) {
                sname = AddressingElementFactory.eINSTANCE.createServiceName();
                sname.setPortName(portName);
                setServiceNameElement(sname);
            } else {
                sname.setPortName(portName);
            }
        } else {
            ServiceName sname = getServiceNameElement();
            if (sname != null) {
                if (sname.getValue() == null) {
                    setServiceNameElement(null);
                } else {
                    sname.setPortName(null);
                }
            }
        }
    }

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#getReferenceParameters()
     */
    public Map getReferenceParameters() {
        getParameterMap();
        if (!parameterMapSet && parameterAnySet) {
            try {
                disableParameterAnyNotify = true;

                // Refresh the parameters map from the any featureMap if necessary
                parameterMap.clear();
                ESequence any = (ESequence) getReferenceParametersElement().getAny();
                FeatureMap featureMap = any.featureMap();
                for (Iterator i = featureMap.iterator(); i.hasNext();) {
                    FeatureMap.Entry featureMapEntry = (FeatureMap.Entry) i.next();
                    EStructuralFeature feature = featureMapEntry.getEStructuralFeature();
                    String qname = ExtendedMetaData.INSTANCE.getNamespace(feature) + '#' + ExtendedMetaData.INSTANCE.getName(feature);
                    Object value = featureMapEntry.getValue();
                    parameterMap.put(qname, value);
                }
                parameterMapSet = true;
            } finally {
                disableParameterMapNotify = false;
            }
        }
        return parameterMap;
    }

    /**
     * @see org.apache.tuscany.core.addressing.sdo.EndpointReferenceElement#getReferenceParameter(java.lang.String)
     */
    public Object getReferenceParameter(String name) {
        return getReferenceParameters().get(name);
    }

    /**
     * @see org.apache.tuscany.core.addressing.sdo.EndpointReferenceElement#setReferenceParameter(java.lang.String, java.lang.Object)
     */
    public void setReferenceParameter(String name, Object value) {
        getReferenceParameters().put(name, value);
    }

    /**
     * This represents a parameters element.
     */
    private static class ManagedReferenceParametersElementImpl extends ReferenceParametersImpl {

        /**
         * Constructor
         */
        private ManagedReferenceParametersElementImpl() {
            super();
        }

        /**
         * Add to the any featureMap
         */
        private void addToFeatureMap(EndpointReferenceElementImpl endpointReference, FeatureMap featureMap, String qname, Object value) {
            int h = qname.indexOf('#');
            EStructuralFeature feature = endpointReference.demandElement(qname.substring(0, h), qname.substring(h + 1));
            featureMap.add(feature, value);
        }

        /**
         * @see org.apache.tuscany.core.message.sdo.impl.HeaderElementImpl#getAny()
         */
        public Sequence getAny() {
            ESequence any = (ESequence) super.getAny();
            EObject container = eContainer();
            if (container instanceof EndpointReferenceElementImpl) {
                EndpointReferenceElementImpl endpointReference = (EndpointReferenceElementImpl) container;
                if (endpointReference.parameterMapSet && !endpointReference.parameterAnySet) {
                    try {
                        endpointReference.disableParameterAnyNotify = true;

                        // Refresh the any featureMap from the header map and the header fields if necessary
                        FeatureMap featureMap = any.featureMap();
                        if (featureMap.size() != 0)
                            featureMap.clear();
                        if (endpointReference.parameterMapSet) {
                            endpointReference.getParameterMap();
                            for (Iterator i = endpointReference.parameterMap.entrySet().iterator(); i.hasNext();) {
                                Map.Entry e = (Map.Entry) i.next();
                                String qname = (String) e.getKey();
                                addToFeatureMap(endpointReference, featureMap, qname, e.getValue());
                            }
                        }

                        endpointReference.parameterAnySet = true;
                    } finally {
                        endpointReference.disableParameterAnyNotify = false;
                    }
                }
            }
            return any;
        }

        /**
         * @see org.apache.tuscany.core.addressing.sdo.impl.ReferenceParametersImpl#getReferenceParameter(java.lang.String)
         */
        public Object getReferenceParameter(String name) {
            Object container = eContainer();
            if (container instanceof EndpointReferenceElementImpl) {
                return ((EndpointReferenceElementImpl) container).getReferenceParameter(name);
            } else
                return null;
        }

        /**
         * @see org.apache.tuscany.core.addressing.sdo.impl.ReferenceParametersImpl#setReferenceParameter(java.lang.String, java.lang.Object)
         */
        public void setReferenceParameter(String name, Object value) {
            Object container = eContainer();
            if (container instanceof EndpointReferenceElementImpl) {
                ((EndpointReferenceElementImpl) container).setReferenceParameter(name, value);
            } else
                super.setReferenceParameter(name, value);
        }

        /**
         * @see org.apache.tuscany.core.addressing.sdo.impl.ReferenceParametersImpl#getReferenceParameters()
         */
        public Map getReferenceParameters() {
            Object container = eContainer();
            if (container instanceof EndpointReferenceElementImpl) {
                return ((EndpointReferenceElementImpl) container).getReferenceParameters();
            } else
                return null;
        }

        /**
         * @see org.apache.tuscany.core.message.sdo.impl.HeaderElementImpl#eIsSet(org.eclipse.emf.ecore.EStructuralFeature)
         */
        public boolean eIsSet(EStructuralFeature eFeature) {
            if (eDerivedStructuralFeatureID(eFeature) == MessageElementPackage.HEADER_ELEMENT__ANY) {

                // Return true if our body field is set or if the any feature map is not empty
                EObject container = eContainer();
                if (container instanceof EndpointReferenceElementImpl) {
                    EndpointReferenceElementImpl endpointReference = (EndpointReferenceElementImpl) container;
                    if (endpointReference.parameterMapSet) {
                        endpointReference.getParameterMap();
                        return !endpointReference.parameterMap.isEmpty();
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
     * @param nsURI
     * @param partName
     * @return
     */
    private EStructuralFeature demandElement(String nsURI, String partName) {
        if (extendedMetaData == null)
            extendedMetaData = new BasicExtendedMetaData();
        return extendedMetaData.demandFeature(nsURI, partName, true);
    }

    /**
     * This adapter is used to keep the parameters map in sync with the any feature map
     */
    private class ParameterElementAnyAdapterImpl extends AdapterImpl {

        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#isAdapterForType(java.lang.Object)
         */
        public boolean isAdapterForType(Object type) {
            return type == ParameterElementAnyAdapterImpl.class;
        }

        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
         */
        public void notifyChanged(Notification notification) {
            if (disableParameterAnyNotify)
                return;
            if (notification.getFeature() == AddressingElementPackage.eINSTANCE.getReferenceParameters_Any()) {

                // If the any feature map is changed reset the parameters map
                getParameterMap();
                parameterMap.clear();
                parameterMapSet = false;
                parameterAnySet = true;
            }
        }
    }

    /**
     * This adapter is used to keep track of changes in the parameters map
     */
    private class ParameterMapAdapterImpl extends AdapterImpl {

        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#isAdapterForType(java.lang.Object)
         */
        public boolean isAdapterForType(Object type) {
            return type == ParameterMapAdapterImpl.class;
        }

        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
         */
        public void notifyChanged(Notification notification) {
            if (disableParameterMapNotify)
                return;
            parameterMapSet = true;
            parameterAnySet = false;
        }

    }

    /**
     * Returns the parameter map.
     *
     * @return
     */
    private Map getParameterMap() {
        if (parameterMap == null)
            parameterMap = new NotifyingHashMapImpl(new ParameterMapAdapterImpl());
        return parameterMap;
    }

    /**
     * @see org.eclipse.emf.ecore.impl.BasicEObjectImpl#eIsSet(org.eclipse.emf.ecore.EStructuralFeature)
     */
    public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
        case AddressingElementPackage.ENDPOINT_REFERENCE__REFERENCE_PARAMETERS_ELEMENT:
            return true;
        default :
            return eIsSetGen(eFeature);
        }
    }

    /**
     * @see org.apache.tuscany.core.addressing.sdo.EndpointReferenceElement#getReferenceParametersElement()
     */
    public ReferenceParameters getReferenceParametersElement() {
        ReferenceParameters parametersElement = getReferenceParametersElementGen();
        if (parametersElement == null) {
            parametersElement = new ManagedReferenceParametersElementImpl();
            setReferenceParametersElementGen(parametersElement);
            return parametersElement;
        }
        return getReferenceParametersElementGen();
    }

    /**
     * @see org.apache.tuscany.core.addressing.sdo.EndpointReferenceElement#setReferenceParametersElement(org.apache.tuscany.core.addressing.sdo.ReferenceParameters)
     */
    public void setReferenceParametersElement(ReferenceParameters value) {
        setReferenceParametersElementGen(value);

        // Add our adapter to track changes to the any feature map
        if (value != null)
            ((Notifier) value).eAdapters().add(new ParameterElementAnyAdapterImpl());

        // Reset the headers map
        getParameterMap();
        parameterMap.clear();
		parameterMapSet = false;
		parameterAnySet = true;
	}

} //EndpointReferenceImpl
