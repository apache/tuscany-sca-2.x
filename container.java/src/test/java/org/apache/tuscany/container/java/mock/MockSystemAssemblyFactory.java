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
package org.apache.tuscany.container.java.mock;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.core.system.assembly.pojo.PojoSystemBinding;
import org.apache.tuscany.core.system.assembly.pojo.PojoSystemImplementation;
import org.apache.tuscany.core.system.builder.SystemComponentContextBuilder;
import org.apache.tuscany.core.system.builder.SystemEntryPointBuilder;
import org.apache.tuscany.core.system.builder.SystemExternalServiceBuilder;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ScopeEnum;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.pojo.PojoAggregateComponent;
import org.apache.tuscany.model.assembly.pojo.PojoComponent;
import org.apache.tuscany.model.assembly.pojo.PojoConfiguredReference;
import org.apache.tuscany.model.assembly.pojo.PojoConfiguredService;
import org.apache.tuscany.model.assembly.pojo.PojoEntryPoint;
import org.apache.tuscany.model.assembly.pojo.PojoExternalService;
import org.apache.tuscany.model.assembly.pojo.PojoJavaInterface;
import org.apache.tuscany.model.assembly.pojo.PojoPart;
import org.apache.tuscany.model.assembly.pojo.PojoReference;
import org.apache.tuscany.model.assembly.pojo.PojoService;
import org.apache.tuscany.model.assembly.pojo.PojoSimpleComponent;
import org.apache.tuscany.model.types.java.JavaServiceContract;

/**
 * Creates test artifacts for system types such as runtime configurations and system components
 * 
 * @version $Rev$ $Date$
 */
public class MockSystemAssemblyFactory {

    private MockSystemAssemblyFactory() {
    }

    /**
     * Creates a component decorated with an appropriate runtime configuration
     * 
     * @param name the name of the component
     * @param type the component implementation class name
     * @param scope the scope of the component implementation
     * @param aggregateContext the containing aggregate context
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException 
     * @see RuntimeConfiguration
     */
    public static Component createDecoratedComponent(String name, String type, ScopeEnum scope,
            AggregateContext aggregateContext) throws NoSuchMethodException, ClassNotFoundException {

        Component sc = createComponent(name, type, scope, aggregateContext);
        SystemComponentContextBuilder builder = new SystemComponentContextBuilder();
        builder.build(sc, aggregateContext);
        return sc;
    }

    /**
     * Creates a component
     * 
     * @param name the name of the component
     * @param type the component implementation class name
     * @param scope the scope of the component implementation
     * @param aggregateContext the containing aggregate context
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     * @see RuntimeConfiguration
     */
    public static Component createComponent(String name, String type, ScopeEnum scope, AggregateContext aggregateContext)
            throws NoSuchMethodException, ClassNotFoundException {

        Class claz = JavaIntrospectionHelper.loadClass(type);
        PojoComponent sc = null;
        if (AggregateContext.class.isAssignableFrom(claz)) {
            sc = new PojoAggregateComponent();
        } else {
            sc = new PojoSimpleComponent();
        }
        SystemImplementation impl = new PojoSystemImplementation();
        impl.setImplementationClass(type);
        sc.setComponentImplementation(impl);
        Service s = new PojoService();
        JavaServiceContract ji = new PojoJavaInterface();
        s.setServiceContract(ji);
        ji.setScope(scope);
        impl.getServices().add(s);
        sc.setName(name);
        sc.setComponentImplementation(impl);
        return sc;
    }

    public static EntryPoint createDecoratedEntryPoint(String name, String refName, Component component,
            AggregateContext aggregateContext) throws NoSuchMethodException {

        EntryPoint ep = createEntryPoint(name, refName);
        ep.getConfiguredReference().getTargetConfiguredServices().get(0).setPart(component);
        SystemEntryPointBuilder builder = new SystemEntryPointBuilder();
        builder.build(ep, aggregateContext);
        return ep;
    }

    public static EntryPoint createEntryPoint(String name, String refName) {
        // create entry point
        PojoEntryPoint ep = new PojoEntryPoint();
        ep.setName(name);
        PojoReference ref = new PojoReference();
        ref.setName(refName);
        PojoConfiguredReference configuredReference = new PojoConfiguredReference();
        configuredReference.setReference(ref);
        PojoConfiguredService service = new PojoConfiguredService();
        configuredReference.addConfiguredService(service);
        ep.setConfiguredReference(configuredReference);
        return ep;
    }

    public static ExternalService createDecoratedExternalService(String name, String refName,
            AggregateContext aggregateContext) {
        ExternalService es = createExternalService(name, refName);
        SystemExternalServiceBuilder builder = new SystemExternalServiceBuilder();
        builder.build(es, aggregateContext);
        return es;
    }

    public static ExternalService createExternalService(String name, String refName) {
        PojoExternalService es = new PojoExternalService();
        es.setName(name);
        PojoConfiguredService configuredService = new PojoConfiguredService();

        // PojoService service = new PojoService();
        // service.setName(refName)
        // FIXME No idea if this is correct, I suspect it isn't
        PojoPart part = new PojoPart();
        part.setName(refName);
        configuredService.setPart(part);
        es.setConfiguredService(configuredService);
        es.getBindings().add(new PojoSystemBinding());
        return es;
    }

    public static List<RuntimeConfiguration<InstanceContext>> createConfigurations(Module module,
            AggregateContext moduleContext) {
        SystemComponentContextBuilder componentBuilder = new SystemComponentContextBuilder();
        for (Component component : module.getComponents()) {
            componentBuilder.build(component, moduleContext);
        }

        SystemEntryPointBuilder epBuilder = new SystemEntryPointBuilder();
        for (EntryPoint ep : module.getEntryPoints()) {
            epBuilder.build(ep, moduleContext);
        }
        List<RuntimeConfiguration<InstanceContext>> configs = new ArrayList();
        for (Component component : module.getComponents()) {
            configs
                    .add((RuntimeConfiguration<InstanceContext>) component.getComponentImplementation()
                            .getRuntimeConfiguration());
        }
        for (EntryPoint ep : module.getEntryPoints()) {
            configs.add((RuntimeConfiguration<InstanceContext>) ep.getConfiguredReference().getRuntimeConfiguration());
        }
        return configs;

    }

}
