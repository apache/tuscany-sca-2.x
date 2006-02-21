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

import org.apache.tuscany.model.assembly.AggregatePart;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.ConfiguredPort;
import org.apache.tuscany.model.assembly.Port;

/**
 * Implementation of ConfiguredPort.
 */
public abstract class ConfiguredPortImpl extends AssemblyModelObjectImpl implements ConfiguredPort {
    private AggregatePart aggregatePart;
    private Port port;

    private Object runtimeConfiguration;
    private Object proxyFactory;

    /**
     * Constructor
     */
    protected ConfiguredPortImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredPort#getPort()
     */
    public Port getPort() {
        return port;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredPort#setPort(org.apache.tuscany.model.assembly.Port)
     */
    public void setPort(Port port) {
        checkNotFrozen();
        this.port = port;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredPort#getAggregatePart()
     */
    public AggregatePart getAggregatePart() {
        checkInitialized();
        return aggregatePart;
    }
    
    /**
     * Sets the aggregate part containing this configured port.
     * @param aggregatePart
     */
    protected void setAggregatePart(AggregatePart aggregatePart) {
        checkNotFrozen();
        this.aggregatePart=aggregatePart;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredPort#getProxyFactory()
     */
    public Object getProxyFactory() {
        return proxyFactory;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredPort#setProxyFactory(java.lang.Object)
     */
    public void setProxyFactory(Object proxyFactory) {
        checkNotFrozen();
        this.proxyFactory = proxyFactory;
    }

    /**
     * @see org.apache.tuscany.model.assembly.RuntimeConfigurationHolder#getRuntimeConfiguration()
     */
    public Object getRuntimeConfiguration() {
        return runtimeConfiguration;
    }

    /**
     * @see org.apache.tuscany.model.assembly.RuntimeConfigurationHolder#setRuntimeConfiguration(java.lang.Object)
     */
    public void setRuntimeConfiguration(Object configuration) {
        checkNotFrozen();
        runtimeConfiguration = configuration;
    }

    /**
     * @see org.apache.tuscany.model.assembly.impl.AssemblyModelObjectImpl#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);
        
        if (port!=null)
            port.initialize(modelContext);
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.impl.AssemblyModelObjectImpl#freeze()
     */
    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();
        
        if (port!=null)
            port.freeze();
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.impl.AssemblyModelObjectImpl#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        if (!super.accept(visitor))
            return false;
        
        if (port!=null) {
            if (!port.accept(visitor))
                return false;
        }
        
        return true;
    }
    
}
