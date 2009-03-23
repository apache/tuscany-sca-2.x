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

package org.apache.tuscany.sca.implementation.osgi;

import javax.xml.namespace.QName;

/**
 * <tuscany:osgi.property> 
 */
public interface OSGiProperty {
    String NAME = "name";
    QName PROPERTY_QNAME = new QName(OSGiImplementation.SCA11_TUSCANY_NS, "osgi.property");

    /**
     * Standard OSGi property names
     */
    String OSGI_REMOTE = "osgi.remote";
    String SERVICE_INTENTS = "service.intents";
    String OSGI_REMOTE_INTERFACES = "osgi.remote.interfaces";
    String OSGI_REMOTE_INTENTS = "osgi.remote.requires.intents";
    String OSGI_REMOTE_CONFIGURATION_TYPE = "osgi.remote.configuration.type";
    String SCA_BINDINGS = "osgi.remote.configuration.sca.bindings";
    String SCA_REFERENCE = "osgi.remote.configuration.sca.reference";
    String SCA_SERVICE = "osgi.remote.configuration.sca.service";
    String SCA_REFERENCE_BINDING = "osgi.remote.configuration.sca.reference.binding";
    String SCA_SERVICE_BINDING = "osgi.remote.configuration.sca.service.binding";
    String OSGI_REMOTE_CONFIGURATION_TYPE_SCA = "sca";

    String getValue();

    void setValue(String value);

    String getName();

    void setName(String name);
}
