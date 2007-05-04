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

package org.apache.tuscany.implementation.spi;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.ConstrainingType;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.Reference;
import org.apache.tuscany.assembly.Service;
import org.apache.tuscany.core.ImplementationActivator;
import org.apache.tuscany.core.ImplementationProvider;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeComponentReference;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.core.invocation.JDKProxyService;
import org.apache.tuscany.interfacedef.Interface;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.policy.Intent;
import org.apache.tuscany.policy.PolicySet;
import org.apache.tuscany.spi.component.WorkContextTunnel;

/**
 * TODO: couldn't something like this class be provided by the runtime?
 */
public abstract class AbstractImplementation implements Implementation, ImplementationProvider, ImplementationActivator {

    private List<Service> services = new ArrayList<Service>();
    private List<Reference> references = new ArrayList<Reference>();
    private List<Property> properties = new ArrayList<Property>();
    private ConstrainingType constrainingType;
    private String uri;
    private boolean unresolved;
    private List<Intent> intents;
    private List<PolicySet> policySets;
    
    public List<Property> getProperties() {
        return properties;
    }

    public List<Reference> getReferences() {
        return references;
    }

    public List<Service> getServices() {
        return services;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public ConstrainingType getConstrainingType() {
        return constrainingType;
    }

    public void setConstrainingType(ConstrainingType constrainingType) {
        this.constrainingType = constrainingType;
    }

    public List<Intent> getRequiredIntents() {
        return intents;
    }

    public List<PolicySet> getPolicySets() {
        return policySets;
    }

    public List<Object> getExtensions() {
        // TODO what is this for?
        return null;
    }

    public boolean isUnresolved() {
        // TODO what is this for?
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        // TODO what is this for?
        this.unresolved = unresolved;
    }

    public void configure(RuntimeComponent component) {
        // TODO what is this for?
    }

    public InterfaceContract getImplementationInterfaceContract(ComponentService service) {
        // TODO what is this for?
        return null;
    }

    public void start(RuntimeComponent component) {
    }

    public void stop(RuntimeComponent component) {
    }

    /**
     * TODO: yuk yuk yuk
     * Maybe RuntimeComponentReference could have a createProxy method?
     */
    protected Object createReferenceProxy(String name, RuntimeComponent component) {
        for (ComponentReference reference : component.getReferences()) {
            if (reference.getName().equals(name)) {
                List<RuntimeWire> wireList = ((RuntimeComponentReference)reference).getRuntimeWires();
                RuntimeWire wire = wireList.get(0);
                JDKProxyService ps = new JDKProxyService(WorkContextTunnel.getThreadWorkContext(), null);
                Interface iface = reference.getInterfaceContract().getInterface();
                return ps.createProxy(((JavaInterface)iface).getJavaClass(), wire);
            }
        }
        throw new IllegalStateException("reference " + name + " not found on component: " + component);
    }

}
