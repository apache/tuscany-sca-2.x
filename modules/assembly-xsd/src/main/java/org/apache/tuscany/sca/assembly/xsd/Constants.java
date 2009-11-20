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

package org.apache.tuscany.sca.assembly.xsd;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Constants used in SCA assembly XML files.
 *
 * @version $Rev$ $Date$
 */
public interface Constants {
    String SCA11_NS = "http://docs.oasis-open.org/ns/opencsa/sca/200903";
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
    String TARGET_NAMESPACE = "targetNamespace";
    String LOCAL = "local";
    String AUTOWIRE = "autowire";
    String NONOVERRIDABLE = "nonOverridable";
    String REPLACE = "replace";
    String REQUIRES = "requires";
    String POLICY_SETS = "policySets"; 
    String APPLICABLE_POLICY_SETS = "applicablePolicySets";
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
    
    /**
     * Cache for public XSDs and DTDs
     */
    Map<String, URL> CACHED_XSDS = XSDCache.cache();

    static class XSDCache {
        static Map<String, URL> cache() {
            Map<String, URL> cachedXSDs = new HashMap<String, URL>();
            cachedXSDs.put(Constants.SCA11_NS, Constants.class.getResource("/sca-1.1-cd04.xsd"));
            cachedXSDs
                .put("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
                     Constants.class
                         .getResource("/org/apache/tuscany/sca/assembly/xsd/oasis-200401-wss-wssecurity-secext-1.0.xsd"));
            cachedXSDs
                .put("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
                     Constants.class
                         .getResource("/org/apache/tuscany/sca/assembly/xsd/oasis-200401-wss-wssecurity-utility-1.0.xsd"));
            cachedXSDs.put("http://www.w3.org/2005/08/addressing", Constants.class
                .getResource("/org/apache/tuscany/sca/assembly/xsd/ws-addr.xsd"));
            cachedXSDs.put("http://www.w3.org/ns/ws-policy", Constants.class
                .getResource("/org/apache/tuscany/sca/assembly/xsd/ws-policy.xsd"));
            cachedXSDs.put("http://www.w3.org/ns/wsdl-instance", Constants.class
                .getResource("/org/apache/tuscany/sca/assembly/xsd/wsdli.xsd"));
            cachedXSDs.put("http://www.w3.org/XML/1998/namespace", Constants.class
                .getResource("/org/apache/tuscany/sca/assembly/xsd/xml.xsd"));
            cachedXSDs.put("http://www.w3.org/2000/09/xmldsig#", Constants.class
                .getResource("/org/apache/tuscany/sca/assembly/xsd/xmldsig-core-schema.xsd"));

            cachedXSDs.put("-//W3C//DTD XMLSCHEMA 200102//EN", Constants.class
                .getResource("/org/apache/tuscany/sca/assembly/xsd/XMLSchema.dtd"));
            cachedXSDs.put("datatypes", Constants.class
                .getResource("/org/apache/tuscany/sca/assembly/xsd/datatypes.dtd"));
            return cachedXSDs;
        }
    };
}
