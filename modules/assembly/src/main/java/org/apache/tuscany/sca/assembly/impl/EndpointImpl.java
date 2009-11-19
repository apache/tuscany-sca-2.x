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
package org.apache.tuscany.sca.assembly.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * The assembly model object for an endpoint.
 *
 * @version $Rev$ $Date$
 */
public class EndpointImpl implements Endpoint {
    private static final long serialVersionUID = 7344399683703812593L;

    protected transient ExtensionPointRegistry registry;
    protected boolean unresolved;
    protected String uri;
    protected Component component;
    protected ComponentService service;
    protected Binding binding;
    protected InterfaceContract interfaceContract;
    protected List<EndpointReference> callbackEndpointReferences = new ArrayList<EndpointReference>();
    protected List<PolicySet> policySets = new ArrayList<PolicySet>();
    protected List<Intent> requiredIntents = new ArrayList<Intent>();
    protected boolean remote = false;

    protected EndpointImpl(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

    public Component getComponent() {
        resolve();
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
        reset();
    }

    public ComponentService getService() {
        resolve();
        return service;
    }

    public void setService(ComponentService service) {
        this.service = service;
        reset();
    }

    public Binding getBinding() {
        resolve();
        return binding;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
        reset();
    }

    public InterfaceContract getComponentServiceInterfaceContract() {
        resolve();
        if (interfaceContract == null && service != null) {
            interfaceContract = service.getInterfaceContract();
        }
        return interfaceContract;
    }

    public void setInterfaceContract(InterfaceContract interfaceContract) {
        this.interfaceContract = interfaceContract;
    }

    /**
     * Get the services callbacl enpoint references that
     * represent endpoint references from which callbacks
     * originate
     *
     * @return callbackEndpoint the reference callback endpoint
     */
    public List<EndpointReference> getCallbackEndpointReferences() {
        resolve();
        return callbackEndpointReferences;
    }

    public List<PolicySet> getPolicySets() {
        resolve();
        return policySets;
    }

    public List<Intent> getRequiredIntents() {
        resolve();
        return requiredIntents;
    }

    public ExtensionType getExtensionType() {
        getBinding();
        if (binding instanceof PolicySubject) {
            return ((PolicySubject)binding).getExtensionType();
        }
        return null;
    }

    public void setExtensionType(ExtensionType type) {
        throw new UnsupportedOperationException();
    }
    
    public String toStringWithoutHash() {
        String output = "Endpoint: ";
        
        if (getURI() != null) {
            output += " URI = " + getURI();
        }

        if (unresolved) {
            output += " [Unresolved]";
        }

        return output;
    }

    public String toString() {
        return "(@" + this.hashCode() + ")" + toStringWithoutHash();
    }

    public String getURI() {
        if (uri == null) {
            if (component != null && service != null && binding != null) {
                String bindingName = binding.getName();
                if (bindingName == null) {
                    bindingName = service.getName();
                }
                uri = component.getURI() + "#service-binding(" + service.getName() + "/" + bindingName + ")";
            } else if (component != null && service != null) {
                uri = component.getURI() + "#service(" + service.getName() + ")";
            } else if (component != null) {
                uri = component.getURI();
            }
        }
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    protected void resolve() {
    }

    protected void reset() {
        this.uri = null;
    }

    protected void setExtensionPointRegistry(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    public boolean isRemote() {
        return remote;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }
}
