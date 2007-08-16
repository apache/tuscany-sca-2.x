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
import org.apache.tuscany.sca.assembly.WireableBinding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.sca.DistributedSCABinding;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * The Distributed SCA binding wrapper for the SCA binding model object
 * 
 * @version $Rev: 564307 $ $Date: 2007-08-09 18:48:29 +0100 (Thu, 09 Aug 2007) $
 */
public class DistributedSCABindingImpl implements DistributedSCABinding {
    
    private SCABinding scaBinding;

    
    public Component getComponent() {
        return null;
    }
    
    public void setComponent(Component component) {
    }

    public String getName() {
        return null;
    }

    public String getURI() {
        return null;
    }

    public void setName(String name) {
    }

    public void setURI(String uri) {
    }

    public List<Object> getExtensions() {
        return null;
    }
    
    public boolean isUnresolved() {
        return false;
    }
    
    public void setUnresolved(boolean unresolved) {
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the targetComponent
     */
    public Component getTargetComponent() {
        return null;
    }

    /**
     * @param targetComponent the targetComponent to set
     */
    public void setTargetComponent(Component targetComponent) {
    }

    /**
     * @return the targetComponentService
     */
    public ComponentService getTargetComponentService() {
        return null;
    }

    /**
     * @param targetComponentService the targetComponentService to set
     */
    public void setTargetComponentService(ComponentService targetComponentService) {
        
    }

    /**
     * @return the targetBinding
     */
    public Binding getTargetBinding() {
        return null;
    }

    /**
     * @param targetBinding the targetBinding to set
     */
    public void setTargetBinding(Binding targetBinding) {
    }
    
    public SCABinding getSCABinding(){
        return scaBinding;
    }
    
    public void setSCABinging(SCABinding scaBinding){
        this.scaBinding = scaBinding;
    }
}
