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
import org.apache.tuscany.sca.assembly.ComponentReference;
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
public class EndpointReferenceImpl implements EndpointReference {
    private static final long serialVersionUID = 8838066441709300972L;

    protected ExtensionPointRegistry registry;
    protected boolean unresolved = true;
    protected String uri;
    protected Component component;
    protected ComponentReference reference;
    protected Binding binding;
    protected List<PolicySet> policySets = new ArrayList<PolicySet>();
    protected List<Intent> requiredIntents = new ArrayList<Intent>();
    protected InterfaceContract interfaceContract;
    protected boolean remote = false;
    protected int status;

    // the target of the endpoint reference
    protected Endpoint targetEndpoint;

    // callback endpoint that messages across this reference
    // will be directed toward
    protected Endpoint callbackEndpoint;

    protected EndpointReferenceImpl(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        EndpointReference cloned = (EndpointReference)super.clone();

        if (targetEndpoint != null) {
            cloned.setTargetEndpoint((Endpoint)targetEndpoint.clone());
        }

        return cloned;
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

    public ComponentReference getReference() {
        resolve();
        return reference;
    }

    public void setReference(ComponentReference reference) {
        this.reference = reference;
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

    public Endpoint getTargetEndpoint() {
        resolve();
        return targetEndpoint;
    }

    public void setTargetEndpoint(Endpoint targetEndpoint) {
        this.targetEndpoint = targetEndpoint;
        reset();
    }

    public InterfaceContract getInterfaceContract() {
        resolve();
        return interfaceContract;
    }

    public void setInterfaceContract(InterfaceContract interfaceContract) {
        this.interfaceContract = interfaceContract;
        reset();
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
        if (binding instanceof PolicySubject) {
            return ((PolicySubject)binding).getExtensionType();
        }
        return null;
    }

    public void setExtensionType(ExtensionType type) {
        throw new UnsupportedOperationException();
    }

    public Endpoint getCallbackEndpoint() {
        resolve();
        return callbackEndpoint;
    }

    public void setCallbackEndpoint(Endpoint callbackEndpoint) {
        this.callbackEndpoint = callbackEndpoint;
        reset();
    }

    public String toString() {
        String output = "EndpointReference(@" + this.hashCode() + "): ";

        if (getURI() != null) {
            output += " URI = " + getURI();
        }

        switch (status) {
            case 0:
                output += " NOT_CONFIGURED ";
                break;
            case 1:
                output += " RESOLVED_BINDING ";
                break;
            case 2:
                output += " WIRED_TARGET_NOT_FOUND ";
                break;
            case 3:
                output += " WIRED_TARGET_FOUND_READY_FOR_MATCHING ";
                break;
            case 4:
                output += " WIRED_TARGET_FOUND_AND_MATCHED ";
                break;
        }

        if (targetEndpoint != null) {
            output += " Target = " + targetEndpoint.toString();
        }

        return output;
    }

    public String getURI() {
        if (uri == null) {
            if (component != null && reference != null && binding != null) {
                String bindingName = binding.getName();
                if (bindingName == null) {
                    bindingName = reference.getName();
                }
                uri = component.getURI() + "#reference-binding(" + reference.getName() + "/" + bindingName + ")";
            } else if (component != null && reference != null) {
                uri = component.getURI() + "#reference(" + reference.getName() + ")";
            } else if (component != null) {
                uri = component.getURI();
            }
        }
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public boolean isRemote() {
        return remote;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }

    protected void resolve() {
    }

    protected void reset() {
        this.uri = null;
    }

    public void setExtensionPointRegistry(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
