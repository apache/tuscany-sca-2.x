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

package org.apache.tuscany.sca.dosgi.discovery;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.osgi.framework.ServiceReference;
import org.osgi.service.discovery.ServicePublication;

/**
 * Publication of an SCA endpoint
 */
public class EndpointPublication implements ServicePublication {
    private Endpoint endpoint;
    private ServiceReference reference;

    /**
     * Create a publication for the endpoint
     * @param reference The OSGi service reference for the given endpoint. The SCA endpoint
     * is pointing to a local service in the OSGi service registry
     */
    public EndpointPublication(ServiceReference reference, Endpoint endpoint) {
        super();
        this.reference = reference;
        this.endpoint = endpoint;
    }

    public ServiceReference getReference() {
        return reference;
    }

    public Dictionary<String, Object> getProperties() {
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        Map<String, Object> serviceProps = new HashMap<String, Object>();
        serviceProps.put(ENDPOINT_LOCATION, endpoint.getURI());
        props.put(SERVICE_PROPERTIES, serviceProps);
        // TODO: Populate the properties from the Endpoint object
        return props;
    }

}
