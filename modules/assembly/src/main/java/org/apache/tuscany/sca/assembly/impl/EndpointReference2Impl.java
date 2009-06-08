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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.assembly.EndpointReference2;
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
public class EndpointReference2Impl implements EndpointReference2, Externalizable {
    private ExtensionPointRegistry registry;
    // this endpoint reference
    private Boolean unresolved = true;
    private Component component;
    private ComponentReference reference;
    private Binding binding;
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private InterfaceContract interfaceContract;

    // the target of the endpoint reference
    private Endpoint2 targetEndpoint;

    // callback endpoint that messages across this reference
    // will be directed toward
    private Endpoint2 callbackEndpoint;

    protected EndpointReference2Impl(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        EndpointReference2 cloned = (EndpointReference2)super.clone();

        if (targetEndpoint != null){
            cloned.setTargetEndpoint((Endpoint2)targetEndpoint.clone());
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
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public ComponentReference getReference() {
        return reference;
    }

    public void setReference(ComponentReference reference) {
        this.reference = reference;
    }

    public Binding getBinding() {
        return binding;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    public Endpoint2 getTargetEndpoint() {
        return targetEndpoint;
    }

    public void setTargetEndpoint(Endpoint2 targetEndpoint) {
        this.targetEndpoint = targetEndpoint;
    }

    public InterfaceContract getInterfaceContract() {
        return interfaceContract;
    }

    public void setInterfaceContract(InterfaceContract interfaceContract) {
        this.interfaceContract = interfaceContract;
    }

    public List<PolicySet> getPolicySets() {
        return policySets;
    }

    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public ExtensionType getType() {
        if (binding instanceof PolicySubject) {
            return ((PolicySubject)binding).getType();
        }
        return null;
    }

    public void setType(ExtensionType type) {
        throw new UnsupportedOperationException();
    }

    public Endpoint2 getCallbackEndpoint() {
        return callbackEndpoint;
    }

    public void setCallbackEndpoint(Endpoint2 callbackEndpoint) {
        this.callbackEndpoint = callbackEndpoint;
    }

    public String toString(){
        String output =  "Endpoint Reference: ";

        if (component != null){
            output += " Component = " + component.getName();
        }

        if (reference != null){
            output += " Reference = " + reference.getName();
        }

        if (binding != null){
            output += " Binding = " + binding.getName() + "/" + binding.getClass().getName() + " ";
        }

        if (unresolved) {
            output += " Unresolved = true ";
        } else {
            output += " Unresolved = false ";
        }

        if (targetEndpoint != null) {
            output += " Target " + targetEndpoint.toString();
        }

        return output;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // TODO: Lookup an endpoint reference serializer utility from the UtilityExtensionPoint
        // Read the EPR from the XML document
        // See javax.xml.ws.wsaddressing.W3CEndpointReference
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // TODO: Lookup an endpoint reference serializer utility from the UtilityExtensionPoint
        // Write the EPR as XML document
        // See javax.xml.ws.wsaddressing.W3CEndpointReference
    }
}
