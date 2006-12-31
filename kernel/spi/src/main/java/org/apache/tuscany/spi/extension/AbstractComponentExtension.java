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
package org.apache.tuscany.spi.extension;

import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Implementation;

/**
 * 
 * @version Provides support for property accessors.
 *
 */
public abstract class AbstractComponentExtension extends AbstractSCAObject implements Component {

    /** Component Definition */
    private ComponentDefinition<Implementation<?>> componentDefinition;
    
    /**
     * Initializes component name and parent.
     * 
     * @param name Name of the component.
     * @param parent Parent of the component.
     * @param componentDefinition Definition of this component.
     */
    public AbstractComponentExtension(String name, CompositeComponent parent, ComponentDefinition<Implementation<?>> componentDefinition) {
        super(name, parent);
        this.componentDefinition = componentDefinition;
    }
    
    /**
     * Initializes component name and parent.
     * 
     * @param name Name of the component.
     * @param parent Parent of the component.
     * @param componentDefinition Definition of this component.
     * @deprecated Use <code>AbstractComponentExtension(String name, CompositeComponent parent, ComponentDefinition<Implementation<?>> componentDefinition)</code>. 
     */
    public AbstractComponentExtension(String name, CompositeComponent parent) {
        super(name, parent);
    }

    /**
     * @see org.apache.tuscany.spi.component.Component#getComponentDefinition()
     */
    public ComponentDefinition<Implementation<?>> getComponentDefinition() {
        return componentDefinition;
    }

    /**
     * @see org.apache.tuscany.spi.component.Component#setComponentDefinition(org.apache.tuscany.spi.model.ComponentDefinition)
     */
    public void setComponentDefinition(ComponentDefinition<Implementation<?>> componentDefinition) {
        this.componentDefinition = componentDefinition;
    }

}
