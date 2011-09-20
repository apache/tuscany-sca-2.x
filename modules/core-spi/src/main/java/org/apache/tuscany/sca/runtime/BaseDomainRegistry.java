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

package org.apache.tuscany.sca.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.LifeCycleListener;

/**
 * A replicated DomainRegistry based on Apache Tomcat Tribes
 * @tuscany.spi.extension.inheritfrom
 */
public abstract class BaseDomainRegistry implements DomainRegistry, LifeCycleListener {
    protected final static Logger logger = Logger.getLogger(BaseDomainRegistry.class.getName());

    protected String domainRegistryURI;
    protected String domainURI;

    protected List<EndpointReference> endpointreferences = new CopyOnWriteArrayList<EndpointReference>();
    protected List<EndpointListener> listeners = new CopyOnWriteArrayList<EndpointListener>();
    protected List<ContributionListener> contributionlisteners = new CopyOnWriteArrayList<ContributionListener>();
    protected ExtensionPointRegistry registry;
    protected Map<String, String> attributes;

    public BaseDomainRegistry(ExtensionPointRegistry registry,
                                Map<String, String> attributes,
                                String domainRegistryURI,
                                String domainURI) {
        this.registry = registry;
        this.domainURI = domainURI;
        this.domainRegistryURI = domainRegistryURI;
        this.attributes = attributes;
    }

    public abstract void addEndpoint(Endpoint endpoint);

    public void addEndpointReference(EndpointReference endpointReference) {
        endpointreferences.add(endpointReference);
        ((RuntimeEndpointReference)endpointReference).bind(registry, this);
        logger.fine("Add endpoint reference - " + endpointReference);
    }

    public void addEndpointListener(EndpointListener listener) {
        listeners.add(listener);
    }

    protected void endpointAdded(Endpoint endpoint) {
        ((RuntimeEndpoint)endpoint).bind(registry, this);
        for (EndpointListener listener : listeners) {
            listener.endpointAdded(endpoint);
        }
    }

    protected void endpointRemoved(Endpoint endpoint) {
        ((RuntimeEndpoint)endpoint).bind(registry, this);
        for (EndpointListener listener : listeners) {
            listener.endpointRemoved(endpoint);
        }
    }

    protected void endpointUpdated(Endpoint oldEp, Endpoint newEp) {
        ((RuntimeEndpoint)newEp).bind(registry, this);
        for (EndpointListener listener : listeners) {
            listener.endpointUpdated(oldEp, newEp);
        }
    }

    public boolean isOutOfDate(EndpointReference endpointReference) {
        return ! findEndpoint(endpointReference).contains(endpointReference.getTargetEndpoint());
    }
    
    public List<Endpoint> findEndpoint(EndpointReference endpointReference) {
        logger.fine("Find endpoint for reference - " + endpointReference);

        if (endpointReference.getReference() != null) {
            Endpoint targetEndpoint = endpointReference.getTargetEndpoint();
            String uri = targetEndpoint.getURI();
            // [rfeng] This is a workaround to deal with the case that the endpoint URI doesn't have the 
            // service name to avoid confusion between structural URIs and service URIs
            if (uri.indexOf('#') == -1) {
                uri = uri + "#service()";
            }
            return findEndpoint(uri);
        }

        return new ArrayList<Endpoint>();
    }

    public abstract List<Endpoint> findEndpoint(String uri);

    public List<EndpointReference> findEndpointReference(Endpoint endpoint) {
        return endpointreferences;
    }
    
    /** 
     * Returns a list of EndpointReferences that have a URI that matches a given URI
     * @param uri - the URI to match
     * @return a List of EndpointReferences that match the supplied URI - if there are none
     * an *empty* list is returned (not null)
     */
    public List<EndpointReference> findEndpointReferences( String uri ) {
    	List<EndpointReference> theRefs = new ArrayList<EndpointReference>();
    	if( uri == null ) return theRefs;
    	
    	for( EndpointReference ref : endpointreferences ) {
    		if( uri.equals(ref.getURI()) ) theRefs.add(ref);
    	} // end for
    	
    	return theRefs;
    } // end method findEndpointReference

    public abstract Endpoint getEndpoint(String uri);

    public List<EndpointReference> getEndpointReferences() {
        return endpointreferences;
    }

    public abstract Collection<Endpoint> getEndpoints();

    public List<EndpointListener> getListeners() {
        return listeners;
    }

    public abstract void removeEndpoint(Endpoint endpoint);

    public void removeEndpointReference(EndpointReference endpointReference) {
        endpointreferences.remove(endpointReference);
        logger.fine("Remove endpoint reference - " + endpointReference);
    }

    public void removeEndpointListener(EndpointListener listener) {
        listeners.remove(listener);
    }

    public String getDomainURI() {
        return domainRegistryURI;
    }

    public String getDomainName() {
        return domainURI;
    }

    public void addContributionListener(ContributionListener listener) {
        contributionlisteners.add(listener);
    }

    public void removeContributionListener(ContributionListener listener) {
        contributionlisteners.remove(listener);
    }

    public Composite getDomainComposite() {
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);
        Composite domainComposite = assemblyFactory.createComposite();
        domainComposite.setName(new QName(Base.SCA11_TUSCANY_NS, getDomainName()));
        domainComposite.setAutowire(false);
        domainComposite.setLocal(false);
        List<Composite> domainIncludes = domainComposite.getIncludes();
        Map<String, List<String>> runningCompositeURIs = getRunningCompositeURIs();
        for (String curi : runningCompositeURIs.keySet()) {
            for (String compositeURI : runningCompositeURIs.get(curi)) {
                domainIncludes.add(getRunningComposite(curi, compositeURI));
            }
        }
        return domainComposite;
    }

    @Override
    public boolean isDistributed() {
        return true;
    }
}
