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
package org.apache.tuscany.model.types.java.impl;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.impl.ServiceContractImpl;
import org.apache.tuscany.model.types.java.JavaServiceContract;

/**
 * An implementation of JavaServiceContract.
 */
public class JavaServiceContractImpl extends ServiceContractImpl implements JavaServiceContract {
    
    private String interfaceName;
    private String callbackInterfaceName;

    /**
     * Constructor
     */
    public JavaServiceContractImpl() {
    }
    
    /**
     * @param interfaceName The interfaceName to set.
     */
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
    
    /**
     * @param callbackInterfaceName The callbackInterfaceName to set.
     */
    public void setCallbackInterfaceName(String callbackInterfaceName) {
        this.callbackInterfaceName = callbackInterfaceName;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.impl.ExtensibleImpl#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        if (isInitialized())
            return;
        
        // Load the interface
        if (getInterface()==null && interfaceName!=null) {
            try {
                Class<?> interfaceClass=modelContext.getApplicationResourceLoader().loadClass(interfaceName);
                setInterface(interfaceClass);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }

        // Load the callback interface
        if (getCallbackInterface()==null && callbackInterfaceName!=null) {
            try {
                Class<?> callbackInterfaceClass=modelContext.getApplicationResourceLoader().loadClass(callbackInterfaceName);
                setInterface(callbackInterfaceClass);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        
        super.initialize(modelContext);
    }

}
