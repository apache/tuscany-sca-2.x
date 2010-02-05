/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.assembly.xml;

import javax.xml.namespace.QName;

/**
 * Constants used in SCA assembly XML files.
 *
 * @version $Rev$ $Date$
 */
public interface Constants {
    String SCA11_NS = "http://docs.oasis-open.org/ns/opencsa/sca/200912";
    String SCA11_TUSCANY_NS = "http://tuscany.apache.org/xmlns/sca/1.1";
    
    String COMPONENT_TYPE = "componentType";
    QName COMPONENT_TYPE_QNAME = new QName(SCA11_NS, COMPONENT_TYPE);
    
    String SERVICE = "service";
    QName SERVICE_QNAME = new QName(SCA11_NS, SERVICE);
    
    String REFERENCE = "reference";
    QName REFERENCE_QNAME = new QName(SCA11_NS, REFERENCE);
    
    String PROPERTY = "property";
    QName PROPERTY_QNAME = new QName(SCA11_NS, PROPERTY);
    
    String COMPOSITE = "composite";
    QName COMPOSITE_QNAME = new QName(SCA11_NS, COMPOSITE);
    
    String INCLUDE = "include";
    QName INCLUDE_QNAME = new QName(SCA11_NS, INCLUDE); 
    
    String COMPONENT = "component";
    QName COMPONENT_QNAME = new QName(SCA11_NS, COMPONENT);
    
    String WIRE = "wire";
    QName WIRE_QNAME = new QName(SCA11_NS, WIRE);

    String OPERATION = "operation";
    QName OPERATION_QNAME = new QName(SCA11_NS, OPERATION);
    
    String CALLBACK = "callback";
    QName CALLBACK_QNAME = new QName(SCA11_NS, CALLBACK);

    String IMPLEMENTATION_COMPOSITE = "implementation.composite";
    QName IMPLEMENTATION_COMPOSITE_QNAME = new QName(SCA11_NS, IMPLEMENTATION_COMPOSITE);
    
    String IMPLEMENTATION = "implementation";
    QName IMPLEMENTATION_QNAME = new QName(SCA11_NS, IMPLEMENTATION);
    
    String BINDING_SCA = "binding.sca";
    QName BINDING_SCA_QNAME = new QName(Constants.SCA11_NS, BINDING_SCA);
    
    String NAME = "name";
    String VALUE = "value";
    QName VALUE_QNAME = new QName(SCA11_NS, VALUE);
    
    String POLICY_SET_ATTACHMENT = "policySetAttachment";
    QName POLICY_SET_ATTACHMENT_QNAME = new QName(SCA11_NS, POLICY_SET_ATTACHMENT);
    
    String TARGET_NAMESPACE = "targetNamespace";
    String LOCAL = "local";
    String AUTOWIRE = "autowire";
    String NONOVERRIDABLE = "nonOverridable";
    String REPLACE = "replace";
    String REQUIRES = "requires";
    QName REQUIRES_QNAME = new QName(SCA11_NS, REQUIRES);
    String INTENTS = "intents";
    
    String POLICY_SETS = "policySets"; 
    String PROMOTE = "promote";
    String TARGET = "target";
    String WIRED_BY_IMPL = "wiredByImpl";
    String MULTIPLICITY = "multiplicity";
    String TYPE = "type";
    String ELEMENT = "element";
    String MANY = "many";
    String MUST_SUPPLY = "mustSupply";
    String SOURCE = "source";
    String FILE = "file";
    String URI = "uri";
    String ZERO_ONE = "0..1";
    String ZERO_N = "0..n";
    String ONE_ONE = "1..1";
    String ONE_N = "1..n";
    
    String SERVER_AUTHENTICATION = "serverAuthentication";
    QName SERVER_AUTHENTICATION_INTENT = new QName(SCA11_NS, SERVER_AUTHENTICATION);
    String SERVER_AUTHENTICATION_TRANSPORT = "serverAuthentication.transport";
    QName SERVER_AUTHENTICATION_TRANSPORT_INTENT = new QName(SCA11_NS, SERVER_AUTHENTICATION_TRANSPORT);
    String SERVER_AUTHENTICATION_MESSAGE = "serverAuthentication.message";
    QName SERVER_AUTHENTICATION_MESSAGE_INTENT = new QName(SCA11_NS, SERVER_AUTHENTICATION_MESSAGE);

    String CLIENT_AUTHENTICATION = "clientAuthentication";
    QName CLIENT_AUTHENTICATION_INTENT = new QName(SCA11_NS, CLIENT_AUTHENTICATION);
    String CLIENT_AUTHENTICATION_TRANSPORT = "clientAuthentication.transport";
    QName CLIENT_AUTHENTICATION_TRANSPORT_INTENT = new QName(SCA11_NS, CLIENT_AUTHENTICATION_TRANSPORT);
    String CLIENT_AUTHENTICATION_MESSAGE = "clientAuthentication.message";
    QName CLIENT_AUTHENTICATION_MESSAGE_INTENT = new QName(SCA11_NS, CLIENT_AUTHENTICATION_MESSAGE);
    
    String AUTHENTICATION = "authentication";
    QName AUTHENTICATION_INTENT = new QName(SCA11_NS, AUTHENTICATION);
    
    String MUTUAL_AUTHENTICATION = "mutualAuthentication";
    QName MUTUAL_AUTHENTICATION_INTENT = new QName(SCA11_NS, MUTUAL_AUTHENTICATION);    
    
    String CONFIDENTIALITY = "confidentiality";
    QName CONFIDENTIALITY_INTENT = new QName(SCA11_NS, CONFIDENTIALITY);
    String CONFIDENTIALITY_TRANSPORT = "confidentiality.transport";
    QName CONFIDENTIALITY_TRANSPORT_INTENT = new QName(SCA11_NS, CONFIDENTIALITY_TRANSPORT);
    String CONFIDENTIALITY_MESSAGE = "confidentiality.message";
    QName CONFIDENTIALITY_MESSAGE_INTENT = new QName(SCA11_NS, CONFIDENTIALITY_MESSAGE);    
    
    String INTEGRITY = "integrity";
    QName INTEGRITY_INTENT = new QName(SCA11_NS, "INTEGRITY");
    String INTEGRITY_TRANSPORT = "integrity.transport";
    QName INTEGRITY_TRANSPORT_INTENT = new QName(SCA11_NS, INTEGRITY_TRANSPORT);
    String INTEGRITY_MESSAGE = "integrity.message";
    QName INTEGRITY_MESSAGE_INTENT = new QName(SCA11_NS, INTEGRITY_MESSAGE); 
    
    String AUTHORIZATION = "authorization";
    QName AUTHORIZATION_INTENT = new QName(SCA11_NS, "INTEGRITY");
    String AUTHORIZATION_FINE_GRAIN = "authorization.fineGrain";
    QName AUTHORIZATION_FINE_GRAIN_INTENT = new QName(SCA11_NS, AUTHORIZATION_FINE_GRAIN);  
    
    // TODO - add transaction intent constants
    
    String SOAP = "SOAP";
    QName SOAP_INTENT = new QName(SCA11_NS, SOAP);
    String SOAP11 = "SOAP.v1_1";
    QName SOAP11_INTENT = new QName(SCA11_NS, SOAP11);
    String SOAP12 = "SOAP.v1_2";
    QName SOAP12_INTENT = new QName(SCA11_NS, SOAP12);
}
