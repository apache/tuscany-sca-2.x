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

package org.apache.tuscany.sca.core.assembly.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.runtime.BaseEndpointRegistry;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.RuntimeProperties;

/**
 * A EndpointRegistry implementation that sees registrations from the same JVM
 */
public class EndpointRegistryImpl extends BaseEndpointRegistry implements EndpointRegistry, LifeCycleListener {
    private final Logger logger = Logger.getLogger(EndpointRegistryImpl.class.getName());

    private List<Endpoint> endpoints = new ArrayList<Endpoint>();
    private Map<QName, Composite> runningComposites = new HashMap<QName, Composite>();
    private Map<String, String> installedContributions = new HashMap<String, String>();
    private Map<String, List<QName>> installedContributionsDeployables = new HashMap<String, List<QName>>();
    private Map<String, List<Export>> installedContributionsExports = new HashMap<String, List<Export>>();
    
    protected boolean quietLogging;

    public EndpointRegistryImpl(ExtensionPointRegistry extensionPoints, String endpointRegistryURI, String domainURI) {
        super(extensionPoints, null, endpointRegistryURI, domainURI);
        Properties runtimeProps = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class).getUtility(RuntimeProperties.class).getProperties();
        quietLogging = Boolean.parseBoolean(runtimeProps.getProperty(RuntimeProperties.QUIET_LOGGING));
    }

    public synchronized void addEndpoint(Endpoint endpoint) {
        endpoints.add(endpoint);
        for (EndpointListener listener : listeners) {
            listener.endpointAdded(endpoint);
        }
        if (logger.isLoggable(quietLogging ? Level.FINE : Level.INFO)) {
            String uri = null;
            Binding b = endpoint.getBinding();
            if (b != null) {
                uri = b.getURI();
                if (uri != null && uri.startsWith("/")) {
                    uri = uri.substring(1);
                }
            }
            String msg = "Add endpoint - " + (uri == null ? endpoint.getURI() : b.getType().getLocalPart()+" - " + uri);
            if (quietLogging) {
                logger.fine(msg);
            } else {
                logger.info(msg);
            }
        }
    }

    public List<Endpoint> findEndpoint(String uri) {
        List<Endpoint> foundEndpoints = new ArrayList<Endpoint>();
        for (Endpoint endpoint : endpoints) {
            if (endpoint.matches(uri)) {
                foundEndpoints.add(endpoint);
                logger.fine("Found endpoint with matching service  - " + endpoint);
            }
            // else the service name doesn't match
        }
        return foundEndpoints;
    }
    
    public synchronized void removeEndpoint(Endpoint endpoint) {
        endpoints.remove(endpoint);
        endpointRemoved(endpoint);
        if (logger.isLoggable(quietLogging ? Level.FINE : Level.INFO)) {
            String uri = null;
            Binding b = endpoint.getBinding();
            if (b != null) {
                uri = b.getURI();
                if (uri != null && uri.startsWith("/")) {
                    uri = uri.substring(1);
                }
            }
            String msg = "Remove endpoint - " + (uri == null ? endpoint.getURI() : b.getType().getLocalPart()+" - "+uri);
            if (quietLogging) {
                logger.fine(msg);
            } else {
                logger.info(msg);
            }
        }
    }

    public synchronized List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public synchronized Endpoint getEndpoint(String uri) {
        for (Endpoint ep : endpoints) {
            String epURI =
                ep.getComponent().getURI() + "#" + ep.getService().getName() + "/" + ep.getBinding().getName();
            if (epURI.equals(uri)) {
                return ep;
            }
            if (ep.getBinding().getName() == null || ep.getBinding().getName().equals(ep.getService().getName())) {
                epURI = ep.getComponent().getURI() + "#" + ep.getService().getName();
                if (epURI.equals(uri)) {
                    return ep;
                }
            }
        }
        return null;

    }

    public synchronized void updateEndpoint(String uri, Endpoint endpoint) {
        Endpoint oldEndpoint = getEndpoint(uri);
        if (oldEndpoint == null) {
            throw new IllegalArgumentException("Endpoint is not found: " + uri);
        }
        endpoints.remove(oldEndpoint);
        endpoints.add(endpoint);
        for (EndpointListener listener : listeners) {
            listener.endpointUpdated(oldEndpoint, endpoint);
        }
    }

    public synchronized void start() {
    }

    public synchronized void stop() {
        for (Iterator<Endpoint> i = endpoints.iterator(); i.hasNext();) {
            Endpoint ep = i.next();
            i.remove();
            endpointRemoved(ep);
        }
        endpointreferences.clear();
        listeners.clear();
    }

    public void addRunningComposite(Composite composite) {
        runningComposites.put(composite.getName(), composite);
    }

    public void removeRunningComposite(QName name) {
        runningComposites.remove(name);
    }

    public Composite getRunningComposite(QName name) {
        return runningComposites.get(name);
    }

    public List<QName> getRunningCompositeNames() {
        List<QName> compositeNames = new ArrayList<QName>();
        for (Composite composite : runningComposites.values()) {
            compositeNames.add(composite.getName());
        }
        return compositeNames;
    }

    public void installContribution(String uri, String url, List<QName> deployables, List<Export> exports) {
        installedContributions.put(uri, url);
        installedContributionsDeployables.put(uri, deployables);
        installedContributionsExports.put(uri, exports);
    }

    public List<String> getInstalledContributionURIs() {
        return new ArrayList<String>(installedContributions.keySet());
    }

    public String getInstalledContributionURL(String uri) {
        return installedContributions.get(uri);
    }

    public List<QName> getInstalledContributionDeployables(String uri) {
        return installedContributionsDeployables.get(uri);
    }

    public List<Export> getInstalledContributionExports(String uri) {
        return installedContributionsExports.get(uri);
    }

    public void uninstallContribution(String uri) {
        installedContributions.remove(uri);
        installedContributionsDeployables.remove(uri);
        installedContributionsExports.remove(uri);
    }
}
