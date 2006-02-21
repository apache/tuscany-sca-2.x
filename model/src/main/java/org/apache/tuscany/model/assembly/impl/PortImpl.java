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
package org.apache.tuscany.model.assembly.impl;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Port;
import org.apache.tuscany.model.assembly.ServiceContract;

/**
 * An implementation of Port.
 */
public abstract class PortImpl extends AssemblyModelObjectImpl implements Port {
    
    private ServiceContract serviceContract;
    private String name;

    /**
     * Constructor
     */
    protected PortImpl() {
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.Port#getName()
     */
    public String getName() {
        return name;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.Port#setName(java.lang.String)
     */
    public void setName(String value) {
        checkNotFrozen();
        name=value;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.Port#getServiceContract()
     */
    public ServiceContract getServiceContract() {
        return serviceContract;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.Port#setServiceContract(org.apache.tuscany.model.assembly.ServiceContract)
     */
    public void setServiceContract(ServiceContract value) {
        checkNotFrozen();
        serviceContract=value;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.impl.AssemblyModelObjectImpl#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);
        
        if (serviceContract!=null)
            serviceContract.initialize(modelContext);
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.impl.AssemblyModelObjectImpl#freeze()
     */
    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();
        
        if (serviceContract!=null)
            serviceContract.freeze();
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.impl.AssemblyModelObjectImpl#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        if (!super.accept(visitor))
            return false;
        
        if (serviceContract!=null) {
            if (!serviceContract.accept(visitor))
                return false;
        }
        
        return true;
    }
}
