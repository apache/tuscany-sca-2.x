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
import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.assembly.EndpointReference2;
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
public class EndpointReference2Impl implements EndpointReference2 {
    
    // this endpoint reference
    private Boolean unresolved = true;
    private Component component;
    private ComponentReference reference;
    private Binding binding;
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    
    // the target of the endpoint reference 
    private String targetName;
    private Endpoint2 targetEndpoint;
    private InterfaceContract interfaceContract;
//  private String uri;
    
    // callback that messages across this reference 
    // will be directed toward
    private Endpoint2 callbackEndpoint;
   
    protected EndpointReference2Impl() {
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
    
/*    
    public Binding getCallbackBinding() {
        return callbackBinding;
    }
    
    public void setCallbackBinding(Binding callbackBinding) {
        this.callbackBinding = callbackBinding;
    }
*/
    
    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
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
    
    /*
    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }
    */

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
}
