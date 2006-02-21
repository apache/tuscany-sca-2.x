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

import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.injection.ReferenceTargetFactory;
import org.apache.tuscany.core.system.assembly.SystemBinding;
import org.apache.tuscany.core.system.config.SystemExternalServiceRuntimeConfiguration;
import org.apache.tuscany.core.system.injection.AutowireFactory;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.ExternalService;

/**
 * Creates runtime configurations for system type external services
 * 
 * @version $Rev$ $Date$
 */
public class SystemExternalServiceBuilder implements RuntimeConfigurationBuilder<AggregateContext> {
    // ----------------------------------
    // Constructors
    // ----------------------------------

    public SystemExternalServiceBuilder() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void build(AssemblyModelObject modelObject, AggregateContext context) throws BuilderException {
        if (!(modelObject instanceof ExternalService)) {
            return;
        }
        ExternalService externalService = (ExternalService) modelObject;
        if (externalService.getConfiguredService() != null
                && externalService.getConfiguredService().getRuntimeConfiguration() != null) {
            return;
        } else if (externalService.getBindings() == null || externalService.getBindings().size() < 1
                || !(externalService.getBindings().get(0) instanceof SystemBinding)) {
            return;
        }

        if (externalService.getConfiguredService() != null && externalService.getConfiguredService().getAggregatePart() != null) {
            SystemExternalServiceRuntimeConfiguration config = new SystemExternalServiceRuntimeConfiguration(externalService
                    .getName(), new ReferenceTargetFactory(externalService.getConfiguredService(), context));
            externalService.getConfiguredService().setRuntimeConfiguration(config);
        } else if (externalService.getConfiguredService().getService().getServiceContract().getInterface() != null) {
            // autowire
            Class<?> claz = externalService.getConfiguredService().getService().getServiceContract().getInterface();
            if (claz == null) {
                BuilderException e = new BuilderConfigException("Interface type not specified");
                e.setIdentifier(externalService.getName());
                e.addContextName(externalService.getName());
                e.addContextName(context.getName());
                throw e;
            }
            SystemExternalServiceRuntimeConfiguration config = new SystemExternalServiceRuntimeConfiguration(externalService
                    .getName(), new AutowireFactory(claz, context));
            externalService.getConfiguredService().setRuntimeConfiguration(config);
        }
    }
}
