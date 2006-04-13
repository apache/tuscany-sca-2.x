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

import org.apache.tuscany.model.assembly.Part;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyVisitor;
import org.apache.tuscany.model.assembly.ConfiguredPort;
import org.apache.tuscany.model.assembly.Port;

/**
 * Implementation of ConfiguredPort.
 */
public abstract class ConfiguredPortImpl<P extends Port> extends AssemblyObjectImpl implements ConfiguredPort<P> {
    private String name;
    private Part part;
    private P port;

    private Object proxyFactory;

    protected ConfiguredPortImpl() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        checkNotFrozen();
        this.name=name;
    }

    public P getPort() {
        return port;
    }
    
    public void setPort(P port) {
        checkNotFrozen();
        this.port = port;
    }
    
    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        checkNotFrozen();
        this.part=part;
    }

    public Object getProxyFactory() {
        return proxyFactory;
    }

    public void setProxyFactory(Object proxyFactory) {
        checkNotFrozen();
        this.proxyFactory = proxyFactory;
    }

    public void initialize(AssemblyContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);
        
        if (port!=null) {
            name=port.getName();
            port.initialize(modelContext);
        }
    }
    
    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();
        
        if (port!=null)
            port.freeze();
    }
    
    public boolean accept(AssemblyVisitor visitor) {
        if (!super.accept(visitor))
            return false;
        
        if (port!=null) {
            if (!port.accept(visitor))
                return false;
        }
        
        return true;
    }
    
}
