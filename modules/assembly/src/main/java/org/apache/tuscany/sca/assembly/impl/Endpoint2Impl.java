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
import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;

/**
 * The assembly model object for an endpoint.
 * 
 * @version $Rev$ $Date$
 */
public class Endpoint2Impl implements Endpoint2 {
        
    private Component component;
    private ComponentService service;
    private Binding binding;
    private Binding callbackBinding;   
    private InterfaceContract interfaceContract;
    private String uri;
    
    protected Endpoint2Impl() {
    }
          
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isUnresolved() {
        // TODO Auto-generated method stub
        return false;
    }
    
    public void setUnresolved(boolean unresolved) {
        // TODO Auto-generated method stub
    }
    
    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public ComponentService getService() {
        return service;
    }

    public void setService(ComponentService service) {
        this.service = service;
    }    
    
    public Binding getBinding() {
        return binding;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    } 
    
    public InterfaceContract getInterfaceContract() {
        return interfaceContract;
    }
    
    public void setInterfaceContract(InterfaceContract interfaceContract) {
        this.interfaceContract = interfaceContract;
    }
    
    public String getURI() {
        return uri;
    }
    
    public void setURI(String uri) {
        this.uri = uri;
    }
}
