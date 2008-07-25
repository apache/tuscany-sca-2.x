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
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;

/**
 * The assembly model object for an endpoint.
 * 
 * @version $Rev$ $Date$
 */
public class EndpointImpl implements Endpoint {
   
    private String targetName;
        
    private Component sourceComponent;
    private ComponentReference sourceComponentReference;
    private Binding resolvedBinding;
    private Binding resolvedCallbackBinding;
    
    private List<Binding> candidateBindings = new ArrayList<Binding>();
    
    private Component targetComponent;
    private ComponentService targetComponentService;
    private Binding targetBinding;
    private Binding targetCallbackBinding;
    
    private InterfaceContract interfaceContract;
    
    protected EndpointImpl() {
    }
          
    public boolean isUnresolved() {
        return resolvedBinding == null;
    }
    
    public void setUnresolved(boolean unresolved) {
        // do nothing as the status is determined by having
        // a resolved source binding
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public String getTargetName(){
        return targetName;
    }
    
    public void setTargetName(String targetName){
        this.targetName = targetName;
    }

    public Component getSourceComponent() {
        return sourceComponent;
    }

    public void setSourceComponent(Component sourceComponent) {
        this.sourceComponent = sourceComponent;
    }

    public ComponentReference getSourceComponentReference() {
        return sourceComponentReference;
    }

    public void setSourceComponentReference(ComponentReference sourceComponentReference) {
        this.sourceComponentReference = sourceComponentReference;
    }    
    
    public Binding getSourceBinding() {
        return resolvedBinding;
    }

    public void setSourceBinding(Binding resolvedBinding) {
        this.resolvedBinding = resolvedBinding;
    }
    
    public Binding getSourceCallbackBinding(){
        return resolvedCallbackBinding;
    }

    public void setSourceCallbackBinding(Binding resolvedCallbackBinding){
        this.resolvedCallbackBinding = resolvedCallbackBinding;   
    }    

    public List<Binding> getCandidateBindings() {
        return candidateBindings;
    }
    
    public Component getTargetComponent() {
        return targetComponent;
    }

    public void setTargetComponent(Component targetComponent) {
        this.targetComponent = targetComponent;
    }

    public ComponentService getTargetComponentService() {
        return targetComponentService;
    }

    public void setTargetComponentService(ComponentService targetComponentService) {
        this.targetComponentService = targetComponentService;
    }
    
    public Binding getTargetBinding() {
        return targetBinding;
    }

    public void setTargetBinding(Binding targetBinding) {
        this.targetBinding = targetBinding;
    }
    
    public Binding getTargetCallbackBinding(){
        return targetCallbackBinding;
    }
    
    public void setTargetCallbackBinding(Binding targetCallbackBinding){
        this.targetCallbackBinding = targetCallbackBinding;   
    } 
    
    public InterfaceContract getInterfaceContract() {
        return interfaceContract;
    }
    
    public void setInterfaceContract(InterfaceContract interfaceContract) {
        this.interfaceContract = interfaceContract;
    }
}
