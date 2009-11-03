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

package org.apache.tuscany.sca.osgi.remoteserviceadmin.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.implementation.osgi.OSGiProperty;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.osgi.remoteserviceadmin.EndpointDescription;
import org.apache.tuscany.sca.osgi.remoteserviceadmin.RemoteConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * Implementation of {@link EndpointDescription}
 */
public class EndpointHelper {
    private EndpointHelper() {
    }

    public static EndpointDescription createEndpointDescription(BundleContext bundleContext, Endpoint endpoint) {
        return new EndpointDescription(getProperties(bundleContext, endpoint));
    }

    private static List<String> getInterfaces(Endpoint endpoint) {
        Interface intf = endpoint.getInterfaceContract().getInterface();
        JavaInterface javaInterface = (JavaInterface)intf;
        return Collections.singletonList(javaInterface.getName());
    }

    private static Map<String, Object> getProperties(BundleContext bundleContext, Endpoint endpoint) {
        Map<String, Object> props = new HashMap<String, Object>();
        
        if (!endpoint.isRemote()) {
            String uuid = OSGiHelper.getFrameworkUUID(bundleContext);
            props.put(RemoteConstants.SERVICE_REMOTE_FRAMEWORK_UUID, uuid);
        }
        
        for (Object ext : endpoint.getService().getExtensions()) {
            if (ext instanceof OSGiProperty) {
                OSGiProperty prop = (OSGiProperty)ext;
                props.put(prop.getName(), prop.getStringValue());
            }
        }
        
        props.put(RemoteConstants.SERVICE_REMOTE_ID, props.get(Constants.SERVICE_ID));
        props.put(RemoteConstants.SERVICE_REMOTE_URI, endpoint.getURI());
        // FIXME: [rfeng] How to pass in the remote service id from the endpoint XML
        props.put(RemoteConstants.SERVICE_EXPORTED_CONFIGS, new String[] {"org.osgi.sca"});
        props.put(Endpoint.class.getName(), endpoint);
        List<String> interfaces = getInterfaces(endpoint);
        props.put(Constants.OBJECTCLASS, interfaces.toArray(new String[interfaces.size()]));
        return props;
    }

    public static Endpoint getEndpoint(EndpointDescription endpointDescription) {
        return (Endpoint)endpointDescription.getProperties().get(Endpoint.class.getName());
    }

}
