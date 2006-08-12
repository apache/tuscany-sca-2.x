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
package org.apache.servicemix.sca.tuscany;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.ModuleComponentConfigurationLoader;
import org.apache.tuscany.core.config.impl.ModuleComponentConfigurationLoaderImpl;
import org.apache.tuscany.core.config.impl.StAXModuleComponentConfigurationLoaderImpl;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.SystemAggregateContext;
import org.apache.tuscany.core.loader.StAXLoaderRegistry;
import org.apache.tuscany.core.loader.StAXUtil;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.core.system.builder.SystemContextFactoryBuilder;
import org.apache.tuscany.core.system.builder.SystemEntryPointBuilder;
import org.apache.tuscany.core.system.builder.SystemExternalServiceBuilder;
import org.apache.tuscany.core.system.loader.SystemSCDLModelLoader;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.apache.tuscany.model.scdl.loader.impl.SCDLAssemblyModelLoaderImpl;

public class BootstrapHelper {
    
    /**
     * Returns a default AssemblyModelContext.
     *
     * @param classLoader the classloader to use for application artifacts
     * @return a default AssemblyModelContext
     */
    public static AssemblyModelContext getModelContext(ClassLoader classLoader) {
        // Create an assembly model factory
        AssemblyFactory modelFactory = new SystemAssemblyFactoryImpl();

        // Create a default assembly model loader
        List<SCDLModelLoader> scdlLoaders = new ArrayList<SCDLModelLoader>();
        scdlLoaders.add(new SystemSCDLModelLoader());
        AssemblyModelLoader modelLoader = new SCDLAssemblyModelLoaderImpl(scdlLoaders);

        // Create a resource loader from the supplied classloader
        ResourceLoader resourceLoader = new ResourceLoaderImpl(classLoader);

        // Create an assembly model context
        return new AssemblyModelContextImpl(modelFactory, modelLoader, resourceLoader);
    }

    /**
     * Returns a default list of configuration builders.
     *
     * @return a default list of configuration builders
     */
    public static List<ContextFactoryBuilder> getBuilders() {
        List<ContextFactoryBuilder> configBuilders = new ArrayList<ContextFactoryBuilder>();
        configBuilders.add((new SystemContextFactoryBuilder()));
        configBuilders.add(new SystemEntryPointBuilder());
        configBuilders.add(new SystemExternalServiceBuilder());
        return configBuilders;
    }

    private static final boolean useStax = true;
    private static final String SYSTEM_LOADER_COMPONENT = "tuscany.loader";

    /**
     * Returns the default module configuration loader.
     *
     * @param systemContext the runtime's system context
     * @param modelContext  the model context the loader will use
     * @return the default module configuration loader
     */
    public static ModuleComponentConfigurationLoader getConfigurationLoader(SystemAggregateContext systemContext, AssemblyModelContext modelContext) throws ConfigurationException {
        if (useStax) {
            // Bootstrap the StAX loader module
            bootstrapStaxLoader(systemContext, modelContext);
            return new StAXModuleComponentConfigurationLoaderImpl(modelContext, XMLInputFactory.newInstance(), systemContext.resolveInstance(StAXLoaderRegistry.class));
        } else {
            return new ModuleComponentConfigurationLoaderImpl(modelContext);
        }
    }

    private static AggregateContext bootstrapStaxLoader(SystemAggregateContext systemContext, AssemblyModelContext modelContext) throws ConfigurationException {
        AggregateContext loaderContext = (AggregateContext) systemContext.getContext(SYSTEM_LOADER_COMPONENT);
        if (loaderContext == null) {
            ModuleComponent loaderComponent = StAXUtil.bootstrapLoader(SYSTEM_LOADER_COMPONENT, modelContext);
            loaderContext = registerModule(systemContext, loaderComponent);
            loaderContext.fireEvent(EventContext.MODULE_START, null);
        }
        return loaderContext;
    }

    public static AggregateContext registerModule(AggregateContext parent, ModuleComponent component) throws ConfigurationException {
        // register the component
        parent.registerModelObject(component);

        // Get the aggregate context representing the component
        return (AggregateContext) parent.getContext(component.getName());
    }
}
