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

package org.apache.tuscany.sca.binding.http.impl;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.binding.http.HTTPBinding;


/**
 * Implementation of the HTTP binding model.
 * 
 * @version $Rev$ $Date$
 */
class HTTPBindingImpl implements HTTPBinding, OptimizableBinding {
    
    private String name;
    private String uri;

    public String getName() {
        return name;
    }

    public String getURI() {
        return uri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    
    public boolean isUnresolved() {
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // The sample binding is always resolved
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }     

    //FIXME Temporary to get access to the target binding information
    // To be removed when the distributed domain supports wiring of other
    // bindings than the SCA binding
    private Binding targetBinding; 
    private Component targetComponent; 
    private ComponentService targetComponentService; 
    
    public Binding getTargetBinding() {
        return targetBinding;
    }
    
    public void setTargetBinding(Binding binding) {
        this.targetBinding = binding;
    }
    
    public Component getTargetComponent() {
        return targetComponent;
    }
    
    public void setTargetComponent(Component component) {
        this.targetComponent = component;
    }
    
    public ComponentService getTargetComponentService() {
        return targetComponentService;
    }
    
    public void setTargetComponentService(ComponentService service) {
        this.targetComponentService = service; 
    }

}
