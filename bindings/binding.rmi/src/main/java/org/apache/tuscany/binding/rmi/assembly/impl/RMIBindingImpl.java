/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.rmi.assembly.impl;

import org.apache.tuscany.binding.rmi.assembly.RMIBinding;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.impl.BindingImpl;

import commonj.sdo.helper.TypeHelper;

/**
 * An implementation of WebServiceBinding.
 */
public class RMIBindingImpl extends BindingImpl implements RMIBinding {

    private String rmiHostName;
    
    private String rmiPort;
    
    private String rmiServerName;
    
    private TypeHelper typeHelper;

    private ResourceLoader resourceLoader;

    
    /**
     * Constructor
     */
    protected RMIBindingImpl() {
    }

    public TypeHelper getTypeHelper() {
        return typeHelper;
    }

    public void setTypeHelper(TypeHelper typeHelper) {
        this.typeHelper = typeHelper;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     */
    @SuppressWarnings("unchecked")
    public void initialize(AssemblyContext modelContext) {
        if (isInitialized()) {
            return;
        }
        super.initialize(modelContext);
        
    }

    public String getRMIHostName() {
        
        return rmiHostName;
    }

    public String getRMIPort() {
       
        return rmiPort;
    }

    public String getRMIServerName() {
        
        return rmiServerName;
    }

    public void setRMIHostName(String hostName) {
        checkNotFrozen();
        rmiHostName = hostName;
        
    }

    public void setRMIPort(String port) {
        checkNotFrozen();
        rmiPort = port;
        
    }

    public void setRMIServerName(String serverName) {
       checkNotFrozen();
       rmiServerName = serverName;
        
    }

}
