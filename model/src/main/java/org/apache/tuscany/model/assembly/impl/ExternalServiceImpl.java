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

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyVisitor;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.OverrideOption;

/**
 * An implementation ExternalService.
 */
public class ExternalServiceImpl extends PartImpl implements ExternalService {

    private ConfiguredService configuredService;
    private OverrideOption overrideOption;
    private List<Binding> bindings=new ArrayList<Binding>();
    
    private Object contextFactory;

    protected ExternalServiceImpl() {
    }

    public OverrideOption getOverrideOption() {
        return overrideOption;
    }

    public void setOverrideOption(OverrideOption newOverridable) {
        checkNotFrozen();
        overrideOption=newOverridable;
    }

    public List<Binding> getBindings() {
        return bindings;
    }

    public ConfiguredService getConfiguredService() {
        return configuredService;
    }
    
    public void setConfiguredService(ConfiguredService configuredService) {
        checkNotFrozen();
        configuredService.setPart(this);
        this.configuredService=configuredService;
    }
    
    public Object getContextFactory() {
        return contextFactory;
    }
    
    public void setContextFactory(Object contextFactory) {
        this.contextFactory=contextFactory;
    }

    public void initialize(AssemblyContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);

        // Initialize the configured service 
        if (configuredService != null) {
            configuredService.initialize(modelContext);
        }
        
        // Initialize the bindings
        initialize(bindings, modelContext);
    }

    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();

        // Freeze the configured service
        if (configuredService!= null)
            configuredService.freeze();

        // Freeze the bindings
        bindings=freeze(bindings);
    }
    
    public boolean accept(AssemblyVisitor visitor) {
        if (!super.accept(visitor))
            return false;
        
        if (configuredService!=null) {
            if (!configuredService.accept(visitor))
                return false;
        }
        
        if (!accept(bindings, visitor))
            return false;
        
        return true;
    }

}
