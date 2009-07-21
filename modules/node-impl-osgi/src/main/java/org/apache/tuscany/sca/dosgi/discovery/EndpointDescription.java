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

import static org.osgi.service.discovery.ServicePublication.ENDPOINT_ID;
import static org.osgi.service.discovery.ServicePublication.ENDPOINT_LOCATION;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;

/**
 *
 */
public class EndpointDescription extends ServiceEndpointDescriptionImpl {
    public EndpointDescription(Endpoint endpoint) {
        super(Collections.singleton(getInterfaceName(endpoint)), getServiceProperties(endpoint));
    }

    static String getInterfaceName(Endpoint endpoint) {
        ComponentService service = endpoint.getService();
        if (service == null) {
            return null;
        }
        InterfaceContract contract = service.getInterfaceContract();
        if (contract == null) {
            return null;
        }
        Interface intf = contract.getInterface();
        if (intf instanceof JavaInterface) {
            JavaInterface javaInterface = (JavaInterface)intf;
            return javaInterface.getName();
        }

        return null;
    }

    static Map<String, Object> getServiceProperties(Endpoint endpoint) {
        Map<String, Object> serviceProps = new HashMap<String, Object>();
        serviceProps.put(ENDPOINT_ID, endpoint.getURI());
        serviceProps.put(ENDPOINT_LOCATION, URI.create(endpoint.getBinding().getURI()));
        // TODO: Populate the properties from the Endpoint object
        return serviceProps;
    }
}
