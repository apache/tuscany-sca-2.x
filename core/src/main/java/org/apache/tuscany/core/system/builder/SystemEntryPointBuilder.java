/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.system.builder;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.injection.FactoryInitException;
import org.apache.tuscany.core.injection.ReferenceTargetFactory;
import org.apache.tuscany.core.system.assembly.SystemBinding;
import org.apache.tuscany.core.system.config.SystemEntryPointRuntimeConfiguration;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.EntryPoint;

/**
 * Decorates the logical model with entry point context configuration builders
 * 
 * @version $Rev$ $Date$
 */
public class SystemEntryPointBuilder implements RuntimeConfigurationBuilder<AggregateContext> {

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public SystemEntryPointBuilder() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void build(AssemblyModelObject modelObject, AggregateContext context) throws BuilderException {
        if (!(modelObject instanceof EntryPoint)) {
            return;
        }
        EntryPoint entryPoint = (EntryPoint) modelObject;
        if (!(entryPoint.getBindings().get(0) instanceof SystemBinding)
                || entryPoint.getConfiguredReference().getRuntimeConfiguration() != null) {
            return;
        }
        try {
            SystemEntryPointRuntimeConfiguration config = new SystemEntryPointRuntimeConfiguration(entryPoint.getName(),
                    new ReferenceTargetFactory(entryPoint.getConfiguredReference(), context));
            // FIXME this should decorate the entry point
            entryPoint.getConfiguredReference().setRuntimeConfiguration(config);
        } catch (FactoryInitException e) {
            e.addContextName(entryPoint.getName());
            e.addContextName(context.getName());
            throw e;
        }
    }
}
