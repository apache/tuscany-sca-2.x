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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.runtime.BaseDomainRegistry;
import org.apache.tuscany.sca.runtime.ContributionDescription;
import org.apache.tuscany.sca.runtime.ContributionListener;
import org.apache.tuscany.sca.runtime.DomainRegistry;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.apache.tuscany.sca.runtime.RuntimeProperties;

/**
 * A DomainRegistry implementation that sees registrations from the same JVM
 */
public class DomainRegistryImpl extends BaseDomainRegistry implements DomainRegistry, LifeCycleListener {
    private final Logger logger = Logger.getLogger(DomainRegistryImpl.class.getName());

    private List<Endpoint> endpoints = new ArrayList<Endpoint>();
    private Map<String, Map<String, Composite>> runningComposites = new HashMap<String, Map<String, Composite>>();
    private Map<String, ContributionDescription> contributionDescriptions = new HashMap<String, ContributionDescription>();
    
    protected boolean quietLogging;

    public DomainRegistryImpl(ExtensionPointRegistry extensionPoints, String endpointRegistryURI, String domainURI) {
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

    public void addRunningComposite(String curi, Composite composite) {
        Map<String, Composite> cs = runningComposites.get(curi);
        if (cs == null) {
            cs = new HashMap<String, Composite>();
            runningComposites.put(curi, cs);
        }
        cs.put(composite.getURI(), composite);
    }

    public void removeRunningComposite(String curi, String compositeURI) {
        Map<String, Composite> cs = runningComposites.get(curi);
        if (cs != null) {
            cs.remove(compositeURI);
        }
    }

    public Composite getRunningComposite(String curi, String compositeURI) {
        Map<String, Composite> cs = runningComposites.get(curi);
        if (cs != null) {
            return cs.get(compositeURI);
        }
        return null;
    }

    public Map<String, List<String>> getRunningCompositeURIs() {
       Map<String, List<String>> compositeURIs = new HashMap<String, List<String>>();
       for (String curi : runningComposites.keySet()) {
           if (runningComposites.get(curi).size() > 0) {
               List<String> uris = new ArrayList<String>();
               compositeURIs.put(curi, uris);
               for (String uri : runningComposites.get(curi).keySet()) {
                   uris.add(uri);
               }
           }
       }
        return compositeURIs;
    }

    public void installContribution(ContributionDescription cd) {
        contributionDescriptions.put(cd.getURI(), cd);
        for (ContributionListener listener : contributionlisteners) {
            listener.contributionInstalled(cd.getURI());
        }
    }

    public void uninstallContribution(String uri) {
        // TUSCANY-4025 - iterate through this list in reverse
        //                in the expectation that a node listener
        //                will appear in the list before and other
        //                listener that appears in the list and which
        //                relies on the node still have the contribution
        //                information. 
        ListIterator<ContributionListener> listenerIterator = contributionlisteners.listIterator(contributionlisteners.size());
        while (listenerIterator.hasPrevious()) { 
            ContributionListener listener = listenerIterator.previous(); 
            listener.contributionRemoved(uri);
        }
        contributionDescriptions.remove(uri);        
    }

    public List<String> getInstalledContributionURIs() {
        return new ArrayList<String>(contributionDescriptions.keySet());
    }

    public ContributionDescription getInstalledContribution(String uri) {
        return contributionDescriptions.get(uri);
    }

    @Override
    public void updateInstalledContribution(ContributionDescription cd) {
        contributionDescriptions.put(cd.getURI(), cd);
        for (ContributionListener listener : contributionlisteners) {
            listener.contributionUpdated(cd.getURI());
        }
    }

    private static final String LOCAL_MEMBER_NAME = "LocalOnly";
    @Override
    public List<String> getNodeNames() {
        return Arrays.asList(new String[]{LOCAL_MEMBER_NAME});
    }

    @Override
    public String getLocalNodeName() {
        return LOCAL_MEMBER_NAME;
    }

    @Override
    public String getRunningNodeName(String contributionURI, String compositeURI) {
        if (getRunningComposite(contributionURI, compositeURI) != null) {
            return LOCAL_MEMBER_NAME;
        }
        return null;
    }

    @Override
    public String remoteCommand(String memberName, Callable<String> command) {
        // TODO or should it just ensure the member name is LocalOnly and the run the command locally?
        throw new IllegalStateException("not supportted for " + LOCAL_MEMBER_NAME);
    }

    @Override
    public String getContainingCompositesContributionURI(String componentName) {
        for (Map<String, Composite> cs : runningComposites.values()) {
            for (Composite c : cs.values()) {
                if (c.getComponent(componentName) != null) {
                    return c.getContributionURI();
                }
            }
        }
        return null;
    }

    @Override
    public boolean isDistributed() {
        return false;
    }
}
