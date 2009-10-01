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

package org.apache.tuscany.sca.endpoint.wrapper;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.management.ConfigAttributes;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.apache.tuscany.sca.runtime.EndpointRegistry;

/**
 * A EndpointRegistry implementation that sees registrations from the same JVM
 */
public class EndpointRegistryWrapper implements EndpointRegistry, LifeCycleListener {
    private final Logger logger = Logger.getLogger(EndpointRegistryWrapper.class.getName());

    private ExtensionPointRegistry extensionPoints;
    private EndpointRegistry delegateEndpointRegistry;

    public EndpointRegistryWrapper(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;
    }

    public void start() {
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        ConfigAttributes domainConfig = utilities.getUtility(ConfigAttributes.class);
        String scheme = null;
        if (domainConfig != null) {
            scheme = domainConfig.getAttributes().get("domainScheme");
        }
        if (scheme == null) {
            scheme = "vm";
        }
        try {
            Collection<ServiceDeclaration> sds =
                ServiceDiscovery.getInstance().getServiceDeclarations(EndpointRegistry.class);
            for (ServiceDeclaration sd : sds) {
                if (scheme.equals(sd.getAttributes().get("scheme"))) {
                    logger.info("EndpointRegistry found: " + sd.getClassName());
                    Class<?> implClass = sd.loadClass();
                    Constructor<?> constructor = null;
                    try {
                        constructor = implClass.getConstructor(ExtensionPointRegistry.class);
                        delegateEndpointRegistry = (EndpointRegistry)constructor.newInstance(extensionPoints);
                    } catch (NoSuchMethodException e) {
                        constructor = implClass.getConstructor(ExtensionPointRegistry.class, Map.class);
                        delegateEndpointRegistry =
                            (EndpointRegistry)constructor.newInstance(extensionPoints, sd.getAttributes());
                    }
                    break;
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        if (delegateEndpointRegistry instanceof LifeCycleListener) {
            ((LifeCycleListener)delegateEndpointRegistry).start();
        }
    }

    public void stop() {
        if (delegateEndpointRegistry instanceof LifeCycleListener) {
            ((LifeCycleListener)delegateEndpointRegistry).stop();
        }
    }

    public void addEndpoint(Endpoint endpoint) {
        delegateEndpointRegistry.addEndpoint(endpoint);
    }

    public void addEndpointReference(EndpointReference endpointReference) {
        delegateEndpointRegistry.addEndpointReference(endpointReference);
    }

    public void addListener(EndpointListener listener) {
        delegateEndpointRegistry.addListener(listener);
    }

    public List<Endpoint> findEndpoint(EndpointReference endpointReference) {
        return delegateEndpointRegistry.findEndpoint(endpointReference);
    }

    public List<EndpointReference> findEndpointReference(Endpoint endpoint) {
        return delegateEndpointRegistry.findEndpointReference(endpoint);
    }

    public Endpoint getEndpoint(String uri) {
        return delegateEndpointRegistry.getEndpoint(uri);
    }

    public List<EndpointReference> getEndpointRefereneces() {
        return delegateEndpointRegistry.getEndpointRefereneces();
    }

    public List<Endpoint> getEndpoints() {
        return delegateEndpointRegistry.getEndpoints();
    }

    public List<EndpointListener> getListeners() {
        return delegateEndpointRegistry.getListeners();
    }

    public void removeEndpoint(Endpoint endpoint) {
        delegateEndpointRegistry.removeEndpoint(endpoint);
    }

    public void removeEndpointReference(EndpointReference endpointReference) {
        delegateEndpointRegistry.removeEndpointReference(endpointReference);
    }

    public void removeListener(EndpointListener listener) {
        delegateEndpointRegistry.removeListener(listener);
    }

    public void updateEndpoint(String uri, Endpoint endpoint) {
        delegateEndpointRegistry.updateEndpoint(uri, endpoint);
    }

}