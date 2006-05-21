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

import org.apache.tuscany.core.builder.ContextFactoryBuilder;
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
import org.apache.tuscany.core.system.assembly.SystemModule;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.core.system.builder.SystemContextFactoryBuilder;
import org.apache.tuscany.core.system.builder.SystemEntryPointBuilder;
import org.apache.tuscany.core.system.builder.SystemExternalServiceBuilder;
import org.apache.tuscany.core.client.BootstrapHelper;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.Multiplicity;
import org.apache.tuscany.model.assembly.Part;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.impl.AssemblyContextImpl;
import org.apache.tuscany.model.types.java.JavaServiceContract;
import org.apache.tuscany.core.monitor.impl.NullMonitorFactory;

/**
 * Generates test components, modules, and runtime artifacts
 * 
 * @version $Rev$ $Date$
 */
public class MockFactory {

    private static SystemAssemblyFactory systemFactory = new SystemAssemblyFactoryImpl();

    private static AssemblyContext assemblyContext = new AssemblyContextImpl(systemFactory, null, null);

    private MockFactory() {
    }

    /**
     * Creates an composite component with the given name
     */
    public static ModuleComponent createCompositeComponent(String name) {
        Service s = systemFactory.createService();
        JavaServiceContract ji = systemFactory.createJavaServiceContract();
        s.setServiceContract(ji);
        ji.setScope(Scope.AGGREGATE);

        Module impl = systemFactory.createModule();
        impl.setName(name);
        impl.setComponentInfo(systemFactory.createComponentInfo());
        impl.getComponentInfo().getServices().add(s);

        ModuleComponent sc = systemFactory.createModuleComponent();
        sc.setName(name);
        sc.setImplementation(impl);
        return sc;
    }

    /**
     * Creates an composite component with the given name
     */
    public static ModuleComponent createSystemCompositeComponent(String name) {
        ModuleComponent sc = systemFactory.createModuleComponent();
        SystemModule impl = systemFactory.createSystemModule();
        impl.setName(name);
        //impl.setImplementationClass(SystemCompositeContextImpl.class);
        sc.setImplementation(impl);
        Service s = systemFactory.createService();
        JavaServiceContract ji = systemFactory.createJavaServiceContract();
        s.setServiceContract(ji);
        ji.setScope(Scope.AGGREGATE);
        impl.setComponentInfo(systemFactory.createComponentInfo());
        impl.getComponentInfo().getServices().add(s);
        sc.setName(name);
        sc.setImplementation(impl);
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
    public static EntryPoint createEPSystemBinding(String name, Class interfaz, String refName, Part target) {
        JavaServiceContract contract = systemFactory.createJavaServiceContract();
        contract.setInterface(interfaz);

        EntryPoint ep = systemFactory.createEntryPoint();
        ep.setName(name);

        Reference ref = systemFactory.createReference();
        ref.setName(refName);
        ref.setServiceContract(contract);
        ConfiguredReference configuredReference = systemFactory.createConfiguredReference();
        configuredReference.setPort(ref);
        Service service = systemFactory.createService();
        service.setServiceContract(contract);

        ConfiguredService cService = systemFactory.createConfiguredService();
        cService.setPort(service);
        cService.initialize(assemblyContext);

        configuredReference.getTargetConfiguredServices().add(cService);
        ep.setConfiguredReference(configuredReference);

        Service epService = systemFactory.createService();
        epService.setServiceContract(contract);

        ConfiguredService epCService = systemFactory.createConfiguredService();
        epCService.initialize(assemblyContext);
        epCService.setPort(epService);

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
        cRef.setPort(ref);
        Service service = systemFactory.createService();
        service.setName(componentName);
        ConfiguredService cService = systemFactory.createConfiguredService();
        cService.setPort(service);
        cRef.getTargetConfiguredServices().add(cService);
        cRef.initialize(assemblyContext);
        cService.initialize(assemblyContext);
        JavaServiceContract contract = systemFactory.createJavaServiceContract();
        contract.setInterface(interfaz);
        ref.setServiceContract(contract);
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
        cService.setPort(service);
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
        Component component = systemFactory.createSystemComponent("TestService1", ModuleScopeSystemComponent.class,
                ModuleScopeSystemComponentImpl.class, Scope.MODULE);
        module.getComponents().add(component);

        // create the entry point
        EntryPoint ep = createEPSystemBinding("TestService1EP", ModuleScopeSystemComponent.class, "target", component);
        module.getEntryPoints().add(ep);

        module.initialize(assemblyContext);
        return module;
    }

    public static <T> Component createSystemComponent(String name,  Class<T> service, Class<? extends T> impl,Scope scope ){
       return systemFactory.createSystemComponent(name,service,impl,scope);
    }

    /**
     * Creates a test system module with source and target components wired together.
     * 
     * @see org.apache.tuscany.core.mock.component.Source
     * @see org.apache.tuscany.core.mock.component.Target
     */
    public static Module createSystemModuleWithWiredComponents(String moduleName, Scope sourceScope, Scope targetScope) {

        // create the target component
        Component target = systemFactory.createSystemComponent("target", Target.class, TargetImpl.class, targetScope);
        target.initialize(assemblyContext);

        // create the source componentType
        Component source = systemFactory.createSystemComponent("source", Source.class, SourceImpl.class, sourceScope);
        ComponentInfo sourceComponentType = source.getImplementation().getComponentInfo();
        List<Reference> references = sourceComponentType.getReferences();
        List<ConfiguredReference> configuredReferences = source.getConfiguredReferences();

        // wire source to target
        references.add(systemFactory.createReference("setTarget", Target.class));
        ConfiguredReference configuredReference = systemFactory.createConfiguredReference("setTarget", "target");
        configuredReferences.add(configuredReference);

        // wire multiplicity using a setter
        references.add(systemFactory.createReference("setTargets", Target.class, Multiplicity.ONE_N));
        configuredReference = systemFactory.createConfiguredReference("setTargets", "target");
        configuredReferences.add(configuredReference);

        // wire multiplicity using a field
        references.add(systemFactory.createReference("targetsThroughField", Target.class, Multiplicity.ONE_N));
        configuredReference = systemFactory.createConfiguredReference("targetsThroughField", "target");
        configuredReferences.add(configuredReference);

        // wire multiplicity using a setter
        references.add(systemFactory.createReference("setArrayOfTargets", Target.class, Multiplicity.ONE_N));
        configuredReference = systemFactory.createConfiguredReference("setArrayOfTargets", "target");
        configuredReferences.add(configuredReference);

        source.initialize(assemblyContext);

        Module module = systemFactory.createModule();
        module.setName(moduleName);
        module.getComponents().add(source);
        module.getComponents().add(target);
        module.initialize(assemblyContext);
        return module;
    }

    /**
     * Creates a test system module component with source and target components wired together.
     * 
     * @see org.apache.tuscany.core.mock.component.Source
     * @see org.apache.tuscany.core.mock.component.Target
     */
    public static ModuleComponent createSystemModuleComponentWithWiredComponents(String moduleComponentName, Scope sourceScope,
                                                                                 Scope targetScope) {
        ModuleComponent mc = systemFactory.createModuleComponent();
        mc.setName(moduleComponentName);
        mc.setImplementation(createSystemModuleWithWiredComponents(moduleComponentName+".module", sourceScope, targetScope));
        return mc;
    }

    /**
     * Creates a test system module component with a module-scoped component and entry point
     */
    public static Module createSystemChildModule() {
        Module module = systemFactory.createModule();
        module.setName("system.test.module");

        // create test component
        Component component = systemFactory.createSystemComponent("TestService2", ModuleScopeSystemComponent.class,
                ModuleScopeSystemComponentImpl.class, Scope.MODULE);
        module.getComponents().add(component);

        // create the entry point
        EntryPoint ep = createEPSystemBinding("TestService2EP", ModuleScopeSystemComponent.class, "target", component);
        module.getEntryPoints().add(ep);

        module.initialize(assemblyContext);
        return module;
    }

    /**
     * Returns a collection of bootstrap configuration builders
     */
    public static List<ContextFactoryBuilder> createSystemBuilders() {
        List<ContextFactoryBuilder> builders = new ArrayList<ContextFactoryBuilder>();
        builders.add((new SystemContextFactoryBuilder(null)));
        builders.add(new SystemEntryPointBuilder());
        builders.add(new SystemExternalServiceBuilder());
        return builders;
    }

    /**
     * Creates a default {@link RuntimeContext} configured with support for Java component implementations
     */
    public static RuntimeContext createCoreRuntime() {
        NullMonitorFactory monitorFactory = new NullMonitorFactory();
        RuntimeContext runtime = new RuntimeContextImpl(monitorFactory, BootstrapHelper.bootstrapContextFactoryBuilders(monitorFactory), null);
        runtime.start();
        return runtime;
    }

}
