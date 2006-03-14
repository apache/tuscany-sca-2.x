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
import org.apache.tuscany.model.assembly.ComponentImplementation;
import org.apache.tuscany.model.assembly.ComponentType;

/**
 * An implementation of ComponentImplementation.
 */
public class ComponentImplementationImpl extends ExtensibleImpl implements ComponentImplementation {
    
    private ComponentType componentType;
    private Object contextFactory;

    /**
     * Constructor
     */
    protected ComponentImplementationImpl() {
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.ComponentImplementation#getComponentType()
     */
    public ComponentType getComponentType() {
        return componentType;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.ComponentImplementation#setComponentType(org.apache.tuscany.model.assembly.ComponentType)
     */
    public void setComponentType(ComponentType componentType) {
        checkNotFrozen();
        this.componentType=componentType;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);

        if (componentType!=null)
            componentType.initialize(modelContext);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#freeze()
     */
    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();
        
        // Freeze component type
        if (componentType!=null)
            componentType.freeze();
    }

    /**
     * @see org.apache.tuscany.model.assembly.ContextFactoryHolder#getContextFactory()
     */
    public Object getContextFactory() {
        return contextFactory;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ContextFactoryHolder#setContextFactory(java.lang.Object)
     */
    public void setContextFactory(Object configuration) {
        checkNotFrozen();
        this.contextFactory = configuration;
    }

    /**
     * @see org.apache.tuscany.model.assembly.impl.AggregateImpl#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        if (!super.accept(visitor))
            return false;
        
        if (componentType!=null) {
            if (!componentType.accept(visitor))
                return false;
        }
        
        return true;
    }
    
}
