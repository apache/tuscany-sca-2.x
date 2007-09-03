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
package org.apache.tuscany.sca.binding.sca.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Extensible;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.WireableBinding;
import org.apache.tuscany.sca.distributed.domain.DistributedSCADomain;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

/**
 * he assemly mode object for an SCA binding.
 * 
 * @version $Rev$ $Date$
 */
public class SCABindingImpl implements SCABinding, Extensible, PolicySetAttachPoint, WireableBinding {
    private String name;
    private String uri;
    private List<Object> extensions = new ArrayList<Object>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private IntentAttachPointType intentAttachPointType;
    
    private Component targetComponent;
    private ComponentService targetComponentService;
    private Binding targetBinding;
    
    private DistributedSCADomain distributedDomain;

    /**
     * Constructs a new SCA binding.
     */
    protected SCABindingImpl() {
    }
    
    // SCA Binding operations
    
    /**
     * Setters for the binding name. Defaults to the
     * name of the service or reference with which the binding is
     * associated
     * 
     * @return the biniding name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Setter for the binding name
     * 
     * @param name the binding name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getters for the binding URI. The computed URI for the 
     * service that the reference is targetting or which the service represents
     * depending on whether the biding is associated with a reference or
     * service
     * 
     * @return the binding uri
     */
    public String getURI() {
        return uri;
    }

    /**
     * Setter for the binding uri
     * 
     * @para uri the binding uri
     */
    public void setURI(String uri) {
        this.uri = uri;
    }
    
    /**
     * Returns a list of extension objects contained in this model object.
     * 
     * @return a list of extension objects container in this model object
     */
    public List<Object> getExtensions() {
        return extensions;
    }
    
    /**
     * Returns true if the model element is unresolved.
     * 
     * @return true if the model element is unresolved.
     */
    public boolean isUnresolved() {
        return false;
    }
    
    /**
     * Sets whether the model element is unresolved.
     * 
     * @param unresolved whether the model element is unresolved
     */    
    public void setUnresolved(boolean unresolved) {
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public List<PolicySet> getPolicySets() {
        return policySets;
    }
    
    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public IntentAttachPointType getType() {
        return intentAttachPointType;
    }
    
    public void setType(IntentAttachPointType intentAttachPointType) {
        this.intentAttachPointType = intentAttachPointType;
    }
    
    // Wireable binding operations

    /**
     * @return the targetComponent
     */
    public Component getTargetComponent() {
        return targetComponent;
    }

    /**
     * @param targetComponent the targetComponent to set
     */
    public void setTargetComponent(Component targetComponent) {
        this.targetComponent = targetComponent;
    }

    /**
     * @return the targetComponentService
     */
    public ComponentService getTargetComponentService() {
        return targetComponentService;
    }

    /**
     * @param targetComponentService the targetComponentService to set
     */
    public void setTargetComponentService(ComponentService targetComponentService) {
        this.targetComponentService = targetComponentService;
    }

    /**
     * @return the targetBinding
     */
    public Binding getTargetBinding() {
        return targetBinding;
    }

    /**
     * @param targetBinding the targetBinding to set
     */
    public void setTargetBinding(Binding targetBinding) {
        this.targetBinding = targetBinding;
    }
    
    // Operations needed by the distributed SCA binding
    // TODO - these are really runtime reference so need a better place for them 
    
    public void setDistributedDomain(DistributedSCADomain distributedDomain){
        this.distributedDomain = distributedDomain;
    }
    
    public DistributedSCADomain getDistributedDomain(){
        return distributedDomain;
    }

}
