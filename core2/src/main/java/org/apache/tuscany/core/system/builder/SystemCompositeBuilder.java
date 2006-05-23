/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.core.system.builder;

import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.system.context.SystemCompositeContext;
import org.apache.tuscany.core.system.context.SystemCompositeContextImpl;
import org.apache.tuscany.core.system.model.SystemCompositeImplementation;
import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.CompositeContext;

/**
 * @version $Rev$ $Date$
 */
public class SystemCompositeBuilder implements ComponentBuilder<SystemCompositeImplementation> {
    public ComponentContext build(CompositeContext parent, Component<SystemCompositeImplementation> component) throws BuilderConfigException {
        SystemCompositeContext<?> context = new SystemCompositeContextImpl(component.getName(), parent, getAutowireContext(parent));
        return context;
    }

    /**
     * Return the autowire context for the supplied parent
     *
     * @param parent the parent for a new context
     * @return the autowire context for the parent or null if it does not support autowire
     */
    protected AutowireContext getAutowireContext(CompositeContext<?> parent) {
        if (parent instanceof AutowireContext) {
            return (AutowireContext) parent;
        } else {
            return null;
        }
    }
}
