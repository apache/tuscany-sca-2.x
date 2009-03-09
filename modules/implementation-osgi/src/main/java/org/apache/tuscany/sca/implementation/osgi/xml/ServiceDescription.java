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

package org.apache.tuscany.sca.implementation.osgi.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * The OSGi RFC 119 description of a remote OSGi service
 */
public class ServiceDescription {
    public final static String REMOTE_SERVICE_FOLDER = "OSGI-INF/remote-service";
    public final static String SD_NS = "http://www.osgi.org/xmlns/sd/v1.0.0";
    public final static QName SERVICE_DESCRIPTIONS_QNAME = new QName(SD_NS, "service-descriptions");
    public final static QName SERVICE_DESCRIPTION_QNAME = new QName(SD_NS, "service-description");
    public final static String REMOTE_SERVICE_HEADER = "Remote-Service";
    public final static String PROP_SERVICE_INTENTS = "service.intents";
    public final static String PROP_REQUIRES_INTENTS = "osgi.remote.requires.intents";
    public final static String PROP_CONFIGURATION_TYPE = "osgi.remote.configuration.type";
    public final static String CONFIGURATION_TYPE_SCA = "sca";
    public final static String PROP_CONFIGURATION_SCA_BINDINGS = "osgi.remote.configuration.sca.bindings";

    private List<String> interfaces = new ArrayList<String>();
    private Map<String, Object> properties = new HashMap<String, Object>();

    public List<String> getInterfaces() {
        return interfaces;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public String toString() {
        return "service-description: interfaces=" + interfaces + "properties=" + properties;
    }
}