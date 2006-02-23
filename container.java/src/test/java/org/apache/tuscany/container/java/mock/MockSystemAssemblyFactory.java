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

import org.apache.tuscany.container.java.mock.components.ModuleScopeComponent;
import org.apache.tuscany.container.java.mock.components.ModuleScopeComponentImpl;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.SystemBinding;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.core.system.builder.SystemComponentContextBuilder;
import org.apache.tuscany.core.system.builder.SystemEntryPointBuilder;
import org.apache.tuscany.core.system.builder.SystemExternalServiceBuilder;
import org.apache.tuscany.model.assembly.AggregatePart;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.SimpleComponent;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.types.java.JavaServiceContract;

/**
 * Creates test artifacts for system types such as runtime configurations and system components
 * 
 * @version $Rev$ $Date$
 */
public class MockSystemAssemblyFactory {

    private static SystemAssemblyFactory systemFactory = new SystemAssemblyFactoryImpl();

    private static AssemblyModelContext assemblyContext = new AssemblyModelContextImpl(null, null, null);

    private MockSystemAssemblyFactory() {
    }

    public static List<RuntimeConfigurationBuilder> createBuilders() {
        List<RuntimeConfigurationBuilder> builders = new ArrayList();
        builders.add((new SystemComponentContextBuilder()));
        builders.add(new SystemEntryPointBuilder());
        builders.add(new SystemExternalServiceBuilder());
        return builders;
    }

    /**
     * Creates a component
     * 
     * @param name the name of the component
     * @param type the component implementation class name
     * @param scope the scope of the component implementation
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     * @see RuntimeConfiguration
     */
    public static Component createComponent(String name, String type, Scope scope) throws NoSuchMethodException,
            ClassNotFoundException {

        Class claz = JavaIntrospectionHelper.loadClass(type);
        Component sc = null;
        if (AggregateContext.class.isAssignableFrom(claz)) {
            sc = systemFactory.createModuleComponent();
        } else {
            sc = systemFactory.createSimpleComponent();
        }
        SystemImplementation impl = systemFactory.createSystemImplementation();
        impl.setImplementationClass(claz);
        sc.setComponentImplementation(impl);
        Service s = systemFactory.createService();
        JavaServiceContract ji = systemFactory.createJavaServiceContract();
        s.setServiceContract(ji);
        ji.setScope(scope);
        impl.setComponentType(systemFactory.createComponentType());
        impl.getComponentType().getServices().add(s);
        sc.setName(name);
        sc.setComponentImplementation(impl);
        return sc;
    }

    public static Component createInitializedComponent(String name, String type, Scope scope) throws NoSuchMethodException,
            ClassNotFoundException {

        Class claz = JavaIntrospectionHelper.loadClass(type);
        Component sc = null;
        if (AggregateContext.class.isAssignableFrom(claz)) {
            sc = systemFactory.createModuleComponent();
        } else {
            sc = systemFactory.createSimpleComponent();
        }
        SystemImplementation impl = systemFactory.createSystemImplementation();
        impl.setImplementationClass(claz);
        sc.setComponentImplementation(impl);
        Service s = systemFactory.createService();
        JavaServiceContract ji = systemFactory.createJavaServiceContract();
        s.setServiceContract(ji);
        ji.setScope(scope);
        impl.setComponentType(systemFactory.createComponentType());
        impl.getComponentType().getServices().add(s);
        sc.setName(name);
        sc.setComponentImplementation(impl);
        sc.initialize(assemblyContext);
        return sc;
    }

    /**
     * Creates a basic entry point with no configured reference using the system binding
     * 
     * @param name the name of the entry point
     * @param interfaz the inteface exposed by the entry point
     * @param refName the name of the entry point reference
     */
    public static EntryPoint createEntryPoint(String name, Class interfaz, String refName) {
        return createEntryPoint(name, interfaz, refName, null);
    }

    /**
     * Creates an entry point wired to the given target (e.g. component, external service) using the system binding
     * 
     * @param name the name of the entry point
     * @param interfaz the inteface exposed by the entry point
     * @param refName the name of the entry point reference
     * @param target the target the entry point is wired to
     */
    public static EntryPoint createEntryPoint(String name, Class interfaz, String refName, AggregatePart target) {
        JavaServiceContract contract = systemFactory.createJavaServiceContract();
        contract.setInterface(interfaz);

        EntryPoint ep = systemFactory.createEntryPoint();
        ep.setName(name);

        Reference ref = systemFactory.createReference();
        ref.setName(refName);
        ref.setServiceContract(contract);
        ConfiguredReference configuredReference = systemFactory.createConfiguredReference();
        configuredReference.setReference(ref);
        Service service = systemFactory.createService();
        service.setServiceContract(contract);

        ConfiguredService cService = systemFactory.createConfiguredService();
        cService.setService(service);
        cService.initialize(assemblyContext);

        configuredReference.getTargetConfiguredServices().add(cService);
        ep.setConfiguredReference(configuredReference);

        ///
        Service epService = systemFactory.createService();
        epService.setServiceContract(contract);

        ConfiguredService epCService = systemFactory.createConfiguredService();
        epCService.initialize(assemblyContext);
        epCService.setService(epService);

        //
        
        ep.setConfiguredService(epCService);
        SystemBinding binding = systemFactory.createSystemBinding();
        ep.getBindings().add(binding);
        if (target != null) {
            if (target instanceof Component) {
                ((Component) target).getConfiguredServices().add(cService);
                // cService.
            } else if (target instanceof ExternalService) {
                ((ExternalService) target).setConfiguredService(cService);
            }
            target.initialize(assemblyContext);
        }
        ep.initialize(null);
        return ep;
    }

    /**
     * Creates an entry point that should be wired to the given target (e.g. component, external service) using the
     * system binding. The system assembly process should resolve the target name to an actual target configuration.
     * 
     * @param name the name of the entry point
     * @param interfaz the inteface exposed by the entry point
     * @param refName the name of the entry point reference
     * @param componentName the name of the target to resolve
     */
    public static EntryPoint createEntryPointWithStringRef(String name, Class interfaz, String refName, String componentName) {
        EntryPoint ep = createEntryPoint(name, interfaz, refName, null);
        ConfiguredReference cRef = systemFactory.createConfiguredReference();
        Reference ref = systemFactory.createReference();
        cRef.setReference(ref);
        Service service = systemFactory.createService();
        service.setName(componentName);
        ConfiguredService cService = systemFactory.createConfiguredService();
        cService.setService(service);
        cRef.getTargetConfiguredServices().add(cService);
        cRef.initialize(assemblyContext);
        cService.initialize(assemblyContext);
        ep.setConfiguredReference(cRef);
        ep.initialize(assemblyContext);
        return ep;
    }

    /**
     * Creates an external service
     */
    public static ExternalService createExternalService(String name, String refName) {
        ExternalService es = systemFactory.createExternalService();
        es.setName(name);
        ConfiguredService configuredService = systemFactory.createConfiguredService();
        // FIXME model hack to get external service to work
        //AggregatePart part = systemFactory.createSimpleComponent();
        //part.setName(refName);
        // FIXME set name on system binding xcv
        // configuredService.setPart(part);
        es.setConfiguredService(configuredService);
        
        //ssss
        SystemBinding binding = systemFactory.createSystemBinding();
        binding.setTargetName(refName);
        es.getBindings().add(binding);
        es.initialize(null);
        return es;
    }

    /**
     * Creates an external service that specifies an autowire of the given type
     */
    public static ExternalService createAutowirableExternalService(String name, Class type) {
        ExternalService es = systemFactory.createExternalService();
        es.setName(name);
        JavaServiceContract inter = systemFactory.createJavaServiceContract();
        inter.setInterface(type);
        Service service = systemFactory.createService();
        service.setServiceContract(inter);
        ConfiguredService cService = systemFactory.createConfiguredService();
        cService.setService(service);
        cService.initialize(assemblyContext);
        es.setConfiguredService(cService);
        es.getBindings().add(systemFactory.createSystemBinding());
        es.initialize(null);
        //externalService.getConfiguredService().getService().getServiceContract().getInterface() != null
        return es;
    }

    /**
     * Creates a test system module component with a module-scoped component and entry point
     */
    public static Module createSystemModule() throws Exception {
        Module module = systemFactory.createModule();
        module.setName("system.module");

        // create test component
        SimpleComponent component = systemFactory.createSimpleComponent();
        component.setName("TestService1");
        SystemImplementation impl = systemFactory.createSystemImplementation();
        impl.setComponentType(systemFactory.createComponentType());
        impl.setImplementationClass(ModuleScopeComponentImpl.class);
        component.setComponentImplementation(impl);
        Service s = systemFactory.createService();
        JavaServiceContract contract = systemFactory.createJavaServiceContract();
        s.setServiceContract(contract);
        contract.setScope(Scope.MODULE);
        impl.getComponentType().getServices().add(s);
        component.setComponentImplementation(impl);

        // create the entry point
        EntryPoint ep = createEntryPoint("TestService1EP", ModuleScopeComponent.class, "target", component);
        // wire the entry point to the component
        // ep.getConfiguredReference().getTargetConfiguredServices().get(0).setPart(component);

        module.getEntryPoints().add(ep);
        module.getComponents().add(component);
        module.initialize(assemblyContext);
        return module;
    }

    /**
     * Creates a test system module component with a module-scoped component and entry point
     */
    public static Module createSystemChildModule() throws Exception {
        Module module = systemFactory.createModule();
        module.setName("system.test.module");

        // create test component
        SimpleComponent component = systemFactory.createSimpleComponent();
        component.setName("TestService2");
        SystemImplementation impl = systemFactory.createSystemImplementation();
        impl.setImplementationClass(ModuleScopeComponentImpl.class);
        component.setComponentImplementation(impl);
        Service s = systemFactory.createService();
        JavaServiceContract ji = systemFactory.createJavaServiceContract();
        s.setServiceContract(ji);
        ji.setScope(Scope.MODULE);
        impl.setComponentType(systemFactory.createComponentType());
        impl.getComponentType().getServices().add(s);
        component.setComponentImplementation(impl);

        // create the entry point
        EntryPoint ep = createEntryPoint("TestService2EP", ModuleScopeComponent.class, "target", component);
        // wire the entry point to the component
        // ep.getConfiguredReference().getTargetConfiguredServices().get(0).setPart(component);

        module.getEntryPoints().add(ep);
        module.getComponents().add(component);
        module.initialize(assemblyContext);
        return module;
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
    public static Component createDecoratedComponent(String name, String type, Scope scope,
            AggregateContext aggregateContext) throws NoSuchMethodException, ClassNotFoundException {

        Component sc = createComponent(name, type, scope);
        SystemComponentContextBuilder builder = new SystemComponentContextBuilder();
        builder.build(sc, aggregateContext);
        return sc;
    }



//    public static EntryPoint createDecoratedEntryPoint(String name, String refName, Component component,
//            AggregateContext aggregateContext) throws NoSuchMethodException {
//
//        EntryPoint ep = createEntryPoint(name, refName);
//        //ep.getConfiguredReference().getTargetConfiguredServices().get(0).setPart(component);
//        SystemEntryPointBuilder builder = new SystemEntryPointBuilder();
//        builder.build(ep, aggregateContext);
//        return ep;
//    }

    public static ExternalService createDecoratedExternalService(String name, String refName,
            AggregateContext aggregateContext) {
        ExternalService es = createExternalService(name, refName);
        SystemExternalServiceBuilder builder = new SystemExternalServiceBuilder();
        builder.build(es, aggregateContext);
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
