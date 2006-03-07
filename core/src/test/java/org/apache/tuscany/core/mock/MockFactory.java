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
package org.apache.tuscany.core.mock;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.impl.AggregateContextImpl;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponentImpl;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.runtime.RuntimeContextImpl;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.SystemBinding;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.core.system.builder.SystemComponentContextBuilder;
import org.apache.tuscany.core.system.builder.SystemEntryPointBuilder;
import org.apache.tuscany.core.system.builder.SystemExternalServiceBuilder;
import org.apache.tuscany.core.system.context.SystemAggregateContextImpl;
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
 * Generates test components, modules, and runtime artifacts
 * 
 * @version $Rev$ $Date$
 */
public class MockFactory {

    private static SystemAssemblyFactory systemFactory = new SystemAssemblyFactoryImpl();

    private static AssemblyModelContext assemblyContext = new AssemblyModelContextImpl(systemFactory, null, null);

    private MockFactory() {
    }

    /**
     * Creates a system component of the given type with the given name and scope
     */
    public static Component createSystemComponent(String name, Class type, Scope scope) {

        Component sc = null;
        if (AggregateContext.class.isAssignableFrom(type)) {
            sc = systemFactory.createModuleComponent();
        } else {
            sc = systemFactory.createSimpleComponent();
        }
        SystemImplementation impl = systemFactory.createSystemImplementation();
        impl.setImplementationClass(type);
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

    /**
     * Creates an aggregate component with the given name
     */
    public static Component createAggregateComponent(String name) {
        Component sc = sc = systemFactory.createModuleComponent();
        SystemImplementation impl = systemFactory.createSystemImplementation();
        impl.setImplementationClass(AggregateContextImpl.class);
        sc.setComponentImplementation(impl);
        Service s = systemFactory.createService();
        JavaServiceContract ji = systemFactory.createJavaServiceContract();
        s.setServiceContract(ji);
        ji.setScope(Scope.AGGREGATE);
        impl.setComponentType(systemFactory.createComponentType());
        impl.getComponentType().getServices().add(s);
        sc.setName(name);
        sc.setComponentImplementation(impl);
        return sc;
    }

    /**
     * Creates an aggregate component with the given name
     */
    public static Component createSystemAggregateComponent(String name) {
        Component sc = sc = systemFactory.createModuleComponent();
        SystemImplementation impl = systemFactory.createSystemImplementation();
        impl.setImplementationClass(SystemAggregateContextImpl.class);
        sc.setComponentImplementation(impl);
        Service s = systemFactory.createService();
        JavaServiceContract ji = systemFactory.createJavaServiceContract();
        s.setServiceContract(ji);
        ji.setScope(Scope.AGGREGATE);
        impl.setComponentType(systemFactory.createComponentType());
        impl.getComponentType().getServices().add(s);
        sc.setName(name);
        sc.setComponentImplementation(impl);
        return sc;
    }

    /**
     * Creates and initializes a system component of the given type with the given name and scope
     */
    public static Component createSystemInitializedComponent(String name, Class type, Scope scope) {
        Component sc = createSystemComponent(name, type, scope);
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
    public static EntryPoint createEPSystemBinding(String name, Class interfaz, String refName) {
        return createEPSystemBinding(name, interfaz, refName, null);
    }

    /**
     * Creates an entry point wired to the given target (e.g. component, external service) using the system binding
     * 
     * @param name the name of the entry point
     * @param interfaz the inteface exposed by the entry point
     * @param refName the name of the entry point reference
     * @param target the target the entry point is wired to
     */
    public static EntryPoint createEPSystemBinding(String name, Class interfaz, String refName, AggregatePart target) {
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

        Service epService = systemFactory.createService();
        epService.setServiceContract(contract);

        ConfiguredService epCService = systemFactory.createConfiguredService();
        epCService.initialize(assemblyContext);
        epCService.setService(epService);

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
        EntryPoint ep = createEPSystemBinding(name, interfaz, refName, null);
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
     * Creates an external service configured with a {@link SystemBinding}
     */
    public static ExternalService createESSystemBinding(String name, String refName) {
        ExternalService es = systemFactory.createExternalService();
        es.setName(name);
        ConfiguredService configuredService = systemFactory.createConfiguredService();
        es.setConfiguredService(configuredService);
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
        return es;
    }

    /**
     * Creates a test system module with a module-scoped component and entry point
     */
    public static Module createSystemModule() {
        Module module = systemFactory.createModule();
        module.setName("system.module");

        // create test component
        SimpleComponent component = systemFactory.createSimpleComponent();
        component.setName("TestService1");
        SystemImplementation impl = systemFactory.createSystemImplementation();
        impl.setComponentType(systemFactory.createComponentType());
        impl.setImplementationClass(ModuleScopeSystemComponentImpl.class);
        component.setComponentImplementation(impl);
        Service s = systemFactory.createService();
        JavaServiceContract contract = systemFactory.createJavaServiceContract();
        s.setServiceContract(contract);
        contract.setScope(Scope.MODULE);
        impl.getComponentType().getServices().add(s);
        component.setComponentImplementation(impl);

        // create the entry point
        EntryPoint ep = createEPSystemBinding("TestService1EP", ModuleScopeSystemComponent.class, "target", component);

        module.getEntryPoints().add(ep);
        module.getComponents().add(component);
        module.initialize(assemblyContext);
        return module;
    }

    /**
     * Creates a test system module with source and target components wired together.
     * 
     * @see org.apache.tuscany.core.mock.component.Source
     * @see org.apache.tuscany.core.mock.component.Target
     */
    public static Module createSystemModuleWithWiredComponents(Scope sourceScope, Scope targetScope) {

        // create the target component
        SimpleComponent target = systemFactory.createSimpleComponent();
        target.setName("target");
        SystemImplementation targetImpl = systemFactory.createSystemImplementation();
        targetImpl.setComponentType(systemFactory.createComponentType());
        targetImpl.setImplementationClass(TargetImpl.class);
        target.setComponentImplementation(targetImpl);
        Service targetService = systemFactory.createService();
        JavaServiceContract targetContract = systemFactory.createJavaServiceContract();
        targetContract.setInterface(Target.class);
        targetService.setServiceContract(targetContract);
        targetService.setName("Target");
        targetImpl.getComponentType().getServices().add(targetService);
        targetContract.setScope(targetScope);
        ConfiguredService cTargetService = systemFactory.createConfiguredService();
        cTargetService.setService(targetService);
        cTargetService.initialize(assemblyContext);
        target.getConfiguredServices().add(cTargetService);
        target.initialize(assemblyContext);

        // create the source component
        SimpleComponent source = systemFactory.createSimpleComponent();
        source.setName("source");
        SystemImplementation impl = systemFactory.createSystemImplementation();
        impl.setComponentType(systemFactory.createComponentType());
        impl.setImplementationClass(SourceImpl.class);
        source.setComponentImplementation(impl);
        Service s = systemFactory.createService();
        JavaServiceContract contract = systemFactory.createJavaServiceContract();
        contract.setInterface(Source.class);
        s.setServiceContract(contract);
        contract.setScope(sourceScope);
        impl.getComponentType().getServices().add(s);
        source.setComponentImplementation(impl);

        // wire source to target
        JavaServiceContract refContract = systemFactory.createJavaServiceContract();
        refContract.setInterface(Target.class);
        Reference reference = systemFactory.createReference();
        reference.setName("setTarget");
        reference.setServiceContract(refContract);
        ConfiguredReference cReference = systemFactory.createConfiguredReference();
        cReference.setReference(reference);
        cReference.getTargetConfiguredServices().add(cTargetService);
        cReference.initialize(assemblyContext);
        source.getConfiguredReferences().add(cReference);
        source.initialize(assemblyContext);
        
        Module module = systemFactory.createModule();
        module.setName("system.module");

        module.getComponents().add(source);
        module.getComponents().add(target);
        module.initialize(assemblyContext);
        return module;
    }

    /**
     * Creates a test system module component with a module-scoped component and entry point
     */
    public static Module createSystemChildModule() {
        Module module = systemFactory.createModule();
        module.setName("system.test.module");

        // create test component
        SimpleComponent component = systemFactory.createSimpleComponent();
        component.setName("TestService2");
        SystemImplementation impl = systemFactory.createSystemImplementation();
        impl.setImplementationClass(ModuleScopeSystemComponentImpl.class);
        component.setComponentImplementation(impl);
        Service s = systemFactory.createService();
        JavaServiceContract ji = systemFactory.createJavaServiceContract();
        s.setServiceContract(ji);
        ji.setScope(Scope.MODULE);
        impl.setComponentType(systemFactory.createComponentType());
        impl.getComponentType().getServices().add(s);
        component.setComponentImplementation(impl);

        // create the entry point
        EntryPoint ep = createEPSystemBinding("TestService2EP", ModuleScopeSystemComponent.class, "target", component);

        module.getEntryPoints().add(ep);
        module.getComponents().add(component);
        module.initialize(assemblyContext);
        return module;
    }

    /**
     * Returns a collection of bootstrap configuration builders
     */
    public static List<RuntimeConfigurationBuilder> createSystemBuilders() {
        List<RuntimeConfigurationBuilder> builders = new ArrayList();
        builders.add((new SystemComponentContextBuilder()));
        builders.add(new SystemEntryPointBuilder());
        builders.add(new SystemExternalServiceBuilder());
        return builders;
    }
    
    /**
     * Creates a default {@link RuntimeContext} configured with support for Java component implementations
     * 
     * @throws ConfigurationException
     */
    public static RuntimeContext createCoreRuntime() throws ConfigurationException {
        RuntimeContext runtime = new RuntimeContextImpl(null, null, createSystemBuilders(), null);
        runtime.start();
        return runtime;
    }

}
