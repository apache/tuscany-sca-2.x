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
import org.apache.tuscany.sca.assembly.ComponentService;
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
public class Endpoint2Impl implements Endpoint2, Externalizable {
    private ExtensionPointRegistry registry;
    private Boolean unresolved;
    private String componentName;
    private Component component;
    private String serviceName;
    private ComponentService service;
    private String bindingName;
    private Binding binding;
    private InterfaceContract interfaceContract;
    private List<EndpointReference2> callbackEndpointReferences = new ArrayList<EndpointReference2>();
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();

    protected Endpoint2Impl(ExtensionPointRegistry registry) {
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

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
        this.componentName = component.getURI();
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public ComponentService getService() {
        return service;
    }

    public void setService(ComponentService service) {
        this.service = service;
        this.serviceName = service.getName();
    }

    public String getBindingName() {
        return bindingName;
    }

    public void setBindingName(String bindingName) {
        this.bindingName = bindingName;
    }

    public Binding getBinding() {
        return binding;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
        this.bindingName = binding.getName();
    }

    public InterfaceContract getInterfaceContract() {
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
    public List<EndpointReference2> getCallbackEndpointReferences(){
        return callbackEndpointReferences;
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

    public String toString(){
        String output =  "Endpoint: ";

        if (componentName != null){
            output += " Component = " + componentName;
        }

        if (serviceName != null){
            output += " Service = " + serviceName;
        }

        if (bindingName != null){
            output += " Binding = " + bindingName + "/" + binding.getClass().getName() + " ";
        }

        if (unresolved) {
            output += " Unresolved = true";
        } else {
            output += " Unresolved = false";
        }

        return output;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // TODO: Lookup an endpoint serializer utility from the UtilityExtensionPoint
        // Read the EP from the XML document
        // See javax.xml.ws.wsaddressing.W3CEndpointReference
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // TODO: Lookup an endpoint serializer utility from the UtilityExtensionPoint
        // Write the EP as XML document
        // See javax.xml.ws.wsaddressing.W3CEndpointReference
    }

}
