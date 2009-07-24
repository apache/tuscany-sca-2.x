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

package org.apache.tuscany.sca.osgi.service.remoteadmin.impl;

import static org.apache.tuscany.sca.osgi.service.remoteadmin.RemoteConstants.SERVICE_EXPORTED_INTERFACES;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.osgi.service.remoteadmin.EndpointDescription;
import org.apache.tuscany.sca.osgi.service.remoteadmin.RemoteConstants;
import org.apache.tuscany.sca.policy.Intent;
import org.osgi.framework.Version;

/**
 * Implementation of {@link EndpointDescription}
 */
public class EndpointDescriptionImpl implements EndpointDescription {
    private Endpoint endpoint;

    private static final Logger logger = Logger.getLogger(EndpointDescriptionImpl.class.getName());

    private Collection<String> interfaces;
    private Map<String, Object> properties;

    public EndpointDescriptionImpl(Collection<String> interfaceNames) {
        this(interfaceNames, Collections.<String, Object> singletonMap(SERVICE_EXPORTED_INTERFACES, interfaceNames));
    }

    public EndpointDescriptionImpl(Collection<String> interfaceNames, Map<String, Object> remoteProperties) {
        this.interfaces = new HashSet<String>(interfaceNames);
        this.properties =
            remoteProperties == null ? new HashMap<String, Object>() : new HashMap<String, Object>(remoteProperties);
        this.properties.put(RemoteConstants.SERVICE_EXPORTED_INTERFACES, interfaceNames);
        this.endpoint = (Endpoint) properties.get(Endpoint.class.getName());
    }

    public EndpointDescriptionImpl(String interfaceName) {
        this(Collections.singleton(interfaceName));
    }

    public EndpointDescriptionImpl(Endpoint endpoint) {
        this(getInterfaces(endpoint), getProperties(endpoint));
        this.endpoint = endpoint;
    }

    /**
     * @see org.apache.tuscany.sca.osgi.service.remoteadmin.EndpointDescription#getConfigurationTypes()
     */
    public List<String> getConfigurationTypes() {
        return Collections.singletonList("sca");
    }

    /**
     * @see org.apache.tuscany.sca.osgi.service.remoteadmin.EndpointDescription#getIntents()
     */
    public List<String> getIntents() {
        List<String> intents = new ArrayList<String>();
        for (Intent intent : endpoint.getRequiredIntents()) {
            intents.add(intent.getName().toString());
        }
        return intents;
    }

    /**
     * @see org.apache.tuscany.sca.osgi.service.remoteadmin.EndpointDescription#getInterfaceVersion(java.lang.String)
     */
    public Version getInterfaceVersion(String name) {
        return Version.emptyVersion;
    }

    /**
     * @see org.apache.tuscany.sca.osgi.service.remoteadmin.EndpointDescription#getInterfaces()
     */
    public List<String> getInterfaces() {
        return new ArrayList<String>(interfaces);
    }

    private static List<String> getInterfaces(Endpoint endpoint) {
        Interface intf = endpoint.getInterfaceContract().getInterface();
        JavaInterface javaInterface = (JavaInterface)intf;
        return Collections.singletonList(javaInterface.getName());
    }

    /**
     * @see org.apache.tuscany.sca.osgi.service.remoteadmin.EndpointDescription#getProperties()
     */
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties); // endpoint.getService().getExtensions();
    }

    private static Map<String, Object> getProperties(Endpoint endpoint) {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(RemoteConstants.ENDPOINT_URI, endpoint.getURI());
        props.put(RemoteConstants.SERVICE_EXPORTED_CONFIGS, new String[] {"sca"});
        props.put(Endpoint.class.getName(), endpoint);
        return props;
    }

    /**
     * @see org.apache.tuscany.sca.osgi.service.remoteadmin.EndpointDescription#getRemoteServiceID()
     */
    public String getRemoteServiceID() {
        return null; // endpoint.getService().getExtensions();
    }

    /**
     * @see org.apache.tuscany.sca.osgi.service.remoteadmin.EndpointDescription#getURI()
     */
    public URI getURI() {
        if (endpoint != null) {
            return URI.create(endpoint.getURI());
        } else {
            return URI.create("urn:" + UUID.randomUUID());
        }
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

}
