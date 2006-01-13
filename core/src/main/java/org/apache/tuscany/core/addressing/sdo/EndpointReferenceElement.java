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
package org.apache.tuscany.core.addressing.sdo;

import commonj.sdo.Sequence;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Endpoint Reference</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * <p/>
 * The following features are supported:
 * <ul>
 * <li>{@link EndpointReferenceElement#getAddressElement <em>Address Element</em>}</li>
 * <li>{@link EndpointReferenceElement#getReferencePropertiesElement <em>Reference Properties Element</em>}</li>
 * <li>{@link EndpointReferenceElement#getReferenceParametersElement <em>Reference Parameters Element</em>}</li>
 * <li>{@link EndpointReferenceElement#getPortTypeElement <em>Port Type Element</em>}</li>
 * <li>{@link EndpointReferenceElement#getServiceNameElement <em>Service Name Element</em>}</li>
 * <li>{@link EndpointReferenceElement#getAny <em>Any</em>}</li>
 * <li>{@link EndpointReferenceElement#getAnyAttribute <em>Any Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='EndpointReferenceType' kind='elementOnly'"
 * @generated
 * @see AddressingElementPackage#getEndpointReference()
 */
public interface EndpointReferenceElement {
    /**
     * Returns the value of the '<em><b>Address Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Address Element</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Address Element</em>' containment reference.
     * @model containment="true" resolveProxies="false" required="true"
     * extendedMetaData="kind='element' name='Address' namespace='##targetNamespace'"
     * @generated
     * @see #setAddressElement(AttributedURI)
     * @see AddressingElementPackage#getEndpointReference_AddressElement()
     */
    AttributedURI getAddressElement();

    /**
     * Sets the value of the '{@link EndpointReferenceElement#getAddressElement <em>Address Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Address Element</em>' containment reference.
     * @generated
     * @see #getAddressElement()
     */
    void setAddressElement(AttributedURI value);

    /**
     * Returns the value of the '<em><b>Reference Properties Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Reference Properties Element</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Reference Properties Element</em>' containment reference.
     * @model containment="true" resolveProxies="false"
     * extendedMetaData="kind='element' name='ReferenceProperties' namespace='##targetNamespace'"
     * @generated
     * @see #setReferencePropertiesElement(ReferenceProperties)
     * @see AddressingElementPackage#getEndpointReference_ReferencePropertiesElement()
     */
    ReferenceProperties getReferencePropertiesElement();

    /**
     * Sets the value of the '{@link EndpointReferenceElement#getReferencePropertiesElement <em>Reference Properties Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Reference Properties Element</em>' containment reference.
     * @generated
     * @see #getReferencePropertiesElement()
     */
    void setReferencePropertiesElement(ReferenceProperties value);

    /**
     * Returns the value of the '<em><b>Reference Parameters Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Reference Parameters Element</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Reference Parameters Element</em>' containment reference.
     * @model containment="true" resolveProxies="false"
     * extendedMetaData="kind='element' name='ReferenceParameters' namespace='##targetNamespace'"
     * @generated
     * @see #setReferenceParametersElement(ReferenceParameters)
     * @see AddressingElementPackage#getEndpointReference_ReferenceParametersElement()
     */
    ReferenceParameters getReferenceParametersElement();

    /**
     * Sets the value of the '{@link EndpointReferenceElement#getReferenceParametersElement <em>Reference Parameters Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Reference Parameters Element</em>' containment reference.
     * @generated
     * @see #getReferenceParametersElement()
     */
    void setReferenceParametersElement(ReferenceParameters value);

    /**
     * Returns the value of the '<em><b>Port Type Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Port Type Element</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Port Type Element</em>' containment reference.
     * @model containment="true" resolveProxies="false"
     * extendedMetaData="kind='element' name='PortType' namespace='##targetNamespace'"
     * @generated
     * @see #setPortTypeElement(AttributedQName)
     * @see AddressingElementPackage#getEndpointReference_PortTypeElement()
     */
    AttributedQName getPortTypeElement();

    /**
     * Sets the value of the '{@link EndpointReferenceElement#getPortTypeElement <em>Port Type Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Port Type Element</em>' containment reference.
     * @generated
     * @see #getPortTypeElement()
     */
    void setPortTypeElement(AttributedQName value);

    /**
     * Returns the value of the '<em><b>Service Name Element</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p/>
     * If the meaning of the '<em>Service Name Element</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Service Name Element</em>' containment reference.
     * @model containment="true" resolveProxies="false"
     * extendedMetaData="kind='element' name='ServiceName' namespace='##targetNamespace'"
     * @generated
     * @see #setServiceNameElement(ServiceName)
     * @see AddressingElementPackage#getEndpointReference_ServiceNameElement()
     */
    ServiceName getServiceNameElement();

    /**
     * Sets the value of the '{@link EndpointReferenceElement#getServiceNameElement <em>Service Name Element</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Service Name Element</em>' containment reference.
     * @generated
     * @see #getServiceNameElement()
     */
    void setServiceNameElement(ServiceName value);

    /**
     * Returns the value of the '<em><b>Any</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * <p/>
     * If "Policy" elements from namespace "http://schemas.xmlsoap.org/ws/2002/12/policy#policy" are used, they must appear first (before any extensibility elements).
     * <p/>
     * <!-- end-model-doc -->
     *
     * @return the value of the '<em>Any</em>' attribute list.
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     * extendedMetaData="kind='elementWildcard' wildcards='##other' name=':5' processing='lax'"
     * @generated
     * @see AddressingElementPackage#getEndpointReference_Any()
     */
    Sequence getAny();

    /**
     * Returns the value of the '<em><b>Any Attribute</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Any Attribute</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Any Attribute</em>' attribute list.
     * @see org.apache.tuscany.core.addressing.sdo.AddressingElementPackage#getEndpointReference_AnyAttribute()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='attributeWildcard' wildcards='##other' name=':6' processing='lax'"
     * @generated
     */
    Sequence getAnyAttribute();

} // EndpointReference
