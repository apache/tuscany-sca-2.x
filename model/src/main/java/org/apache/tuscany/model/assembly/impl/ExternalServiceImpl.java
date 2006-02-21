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
import java.util.Collections;
import java.util.List;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.OverrideOption;

/**
 * An implementation ExternalService.
 */
public class ExternalServiceImpl extends AggregatePartImpl implements ExternalService {

    private ConfiguredService configuredService;
    private OverrideOption overrideOption;
    private List<Binding> bindings=new ArrayList<Binding>();

    /**
     * Constructor
     */
    protected ExternalServiceImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.ExternalServiceImpl#getOverrideOption()
     */
    public OverrideOption getOverrideOption() {
        return overrideOption;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ExternalService#setOverrideOption(org.apache.tuscany.model.assembly.OverrideOption)
     */
    public void setOverrideOption(OverrideOption newOverridable) {
        checkNotFrozen();
        overrideOption=newOverridable;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ExternalService#getBindings()
     */
    public List<Binding> getBindings() {
        return bindings;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ExternalService#getConfiguredService()
     */
    public ConfiguredService getConfiguredService() {
        return configuredService;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.ExternalService#setConfiguredService(org.apache.tuscany.model.assembly.ConfiguredService)
     */
    public void setConfiguredService(ConfiguredService configuredService) {
        checkNotFrozen();
        this.configuredService=configuredService;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);

        // Initialize the configured service 
        if (configuredService != null) {
            ((ConfiguredPortImpl)configuredService).setAggregatePart(this);
            configuredService.initialize(modelContext);
        }
        
        // Initialize the bindings
        initialize(bindings, modelContext);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#freeze()
     */
    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();

        // Freeze the configured service
        if (configuredService!= null)
            configuredService.freeze();

        // Freeze the bindings
        bindings=Collections.unmodifiableList(bindings);
        freeze(bindings);
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.impl.ExtensibleImpl#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
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
