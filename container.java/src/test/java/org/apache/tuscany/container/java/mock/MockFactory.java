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
package org.apache.tuscany.container.java.mock;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.container.java.assembly.JavaAssemblyFactory;
import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.container.java.assembly.impl.JavaAssemblyFactoryImpl;
import org.apache.tuscany.container.java.assembly.mock.HelloWorldImpl;
import org.apache.tuscany.container.java.assembly.mock.HelloWorldService;
import org.apache.tuscany.container.java.mock.binding.foo.FooBinding;
import org.apache.tuscany.container.java.mock.components.GenericComponent;
import org.apache.tuscany.container.java.mock.components.HelloWorldClient;
import org.apache.tuscany.container.java.mock.components.ModuleScopeComponentImpl;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AggregateContext;
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
 * Generates test components, modules, and runtime artifacts
 * 
 * @version $Rev$ $Date$
 */
public class MockFactory {

    private static JavaAssemblyFactory factory = new JavaAssemblyFactoryImpl();

    private static SystemAssemblyFactory systemFactory = new SystemAssemblyFactoryImpl();

    private static AssemblyModelContext assemblyContext = new AssemblyModelContextImpl(null, null, null);

    public static SimpleComponent createComponent(String name, Class type, Scope scope) throws NoSuchMethodException {
        SimpleComponent sc = factory.createSimpleComponent();
        JavaImplementation impl = factory.createJavaImplementation();
        impl.setComponentType(factory.createComponentType());
        impl.setImplementationClass(type);
        sc.setComponentImplementation(impl);
        Service s = factory.createService();
        JavaServiceContract ji = factory.createJavaServiceContract();
        s.setServiceContract(ji);
        ji.setScope(scope);
        impl.getComponentType().getServices().add(s);
        sc.setName(name);
        sc.setComponentImplementation(impl);
        return sc;
    }

    public static Component createSystemComponent(String name, String type, Scope scope) throws NoSuchMethodException,
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

    /**
     * Creates an external service configured with the 'Foo' test binding
     */
    public static ExternalService createFooBindingExternalService(String name, Class interfaz) throws NoSuchMethodException,
            ClassNotFoundException {
        ExternalService es = factory.createExternalService();
        es.setName(name);
        Service s = factory.createService();
        JavaServiceContract ji = factory.createJavaServiceContract();
        ji.setScope(Scope.MODULE);
        ji.setInterface(interfaz);
        s.setServiceContract(ji);
        ConfiguredService configuredService = factory.createConfiguredService();
        es.setConfiguredService(configuredService);

        FooBinding binding = new FooBinding();
        es.getBindings().add(binding);
        return es;
    }

    public static EntryPoint createFooBindingEntryPoint(String name, Class interfaz) throws NoSuchMethodException,
            ClassNotFoundException {
        EntryPoint ep = factory.createEntryPoint();
        ep.setName(name);
        Service s = factory.createService();
        JavaServiceContract ji = factory.createJavaServiceContract();
        ji.setScope(Scope.MODULE);
        ji.setInterface(interfaz);
        s.setServiceContract(ji);
        ConfiguredService configuredService = factory.createConfiguredService();
        configuredService.setService(s);
        ep.setConfiguredService(configuredService);
        FooBinding binding = new FooBinding();
        ep.getBindings().add(binding);
        return ep;
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
    
        Component sc = createSystemComponent(name, type, scope);
        SystemComponentContextBuilder builder = new SystemComponentContextBuilder();
        builder.build(sc, aggregateContext);
        return sc;
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
        cService.initialize(MockFactory.assemblyContext);
    
        configuredReference.getTargetConfiguredServices().add(cService);
        ep.setConfiguredReference(configuredReference);
    
        ///
        Service epService = systemFactory.createService();
        epService.setServiceContract(contract);
    
        ConfiguredService epCService = systemFactory.createConfiguredService();
        epCService.initialize(MockFactory.assemblyContext);
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
            target.initialize(MockFactory.assemblyContext);
        }
        ep.initialize(null);
        return ep;
    }

    /**
     * Creates an external service
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

    public static Module createModule() throws Exception {
        Component sourceComponent = createComponent("source", ModuleScopeComponentImpl.class,Scope.MODULE);
        Component targetComponent = createComponent("target", ModuleScopeComponentImpl.class,Scope.MODULE);
    
        Service targetService = factory.createService();
        JavaServiceContract targetContract = factory.createJavaServiceContract();
        targetContract.setInterface(GenericComponent.class);
        targetService.setServiceContract(targetContract);
        targetService.setName("GenericComponent");
        ConfiguredService cTargetService = factory.createConfiguredService();
        cTargetService.setService(targetService);
        cTargetService.initialize(assemblyContext);
        targetComponent.getConfiguredServices().add(cTargetService);
        targetComponent.initialize(assemblyContext);
        
        Reference ref = factory.createReference();
        ConfiguredReference cref = factory.createConfiguredReference();
        ref.setName("setGenericComponent");
        JavaServiceContract inter = factory.createJavaServiceContract();
        inter.setInterface(GenericComponent.class);
        ref.setServiceContract(inter);
        cref.setReference(ref);
        cref.getTargetConfiguredServices().add(cTargetService);
        cref.initialize(assemblyContext);
        sourceComponent.getConfiguredReferences().add(cref);
        sourceComponent.initialize(assemblyContext);
    
        Module module = factory.createModule();
        module.setName("test.module");
        module.getComponents().add(sourceComponent);
        module.getComponents().add(targetComponent);
        module.initialize(assemblyContext);
        return module;
    }

    public static Module createModuleWithExternalService() throws Exception {
        Component sourceComponent = createComponent("source", HelloWorldClient.class,Scope.MODULE);
        ExternalService targetES = createFooBindingExternalService("target",HelloWorldService.class);
    
        Service targetService = factory.createService();
        JavaServiceContract targetContract = factory.createJavaServiceContract();
        targetContract.setInterface(HelloWorldService.class);
        targetService.setServiceContract(targetContract);
        targetService.setName("HelloWorld");
        ConfiguredService cTargetService = factory.createConfiguredService();
        cTargetService.setService(targetService);
        targetES.setConfiguredService(cTargetService);
        targetES.initialize(assemblyContext);
        
        Reference ref = factory.createReference();
        ConfiguredReference cref = factory.createConfiguredReference();
        ref.setName("setHelloWorldService");
        JavaServiceContract inter = factory.createJavaServiceContract();
        inter.setInterface(HelloWorldService.class);
        ref.setServiceContract(inter);
        cref.setReference(ref);
        cref.getTargetConfiguredServices().add(cTargetService);
        cref.initialize(assemblyContext);
        sourceComponent.getConfiguredReferences().add(cref);
        sourceComponent.initialize(assemblyContext);
    
        Module module = factory.createModule();
        module.setName("test.module");
        module.getComponents().add(sourceComponent);
        module.getExternalServices().add(targetES);
        module.initialize(assemblyContext);
        return module;
    }

    public static Module createModuleWithEntryPoint() throws Exception {
        EntryPoint sourceEP = createFooBindingEntryPoint("source", HelloWorldService.class);
        Component targetComponent = createComponent("target",HelloWorldImpl.class,Scope.MODULE);
    
        Service targetService = factory.createService();
        JavaServiceContract targetContract = factory.createJavaServiceContract();
        targetContract.setInterface(HelloWorldService.class);
        targetService.setServiceContract(targetContract);
        targetService.setName("HelloWorldService");
        ConfiguredService cTargetService = factory.createConfiguredService();
        cTargetService.setService(targetService);
        targetComponent.getConfiguredServices().add(cTargetService);
        targetComponent.initialize(assemblyContext);
        
        Reference ref = factory.createReference();
        ConfiguredReference cref = factory.createConfiguredReference();
        ref.setName("setHelloWorldService");
        JavaServiceContract inter = factory.createJavaServiceContract();
        inter.setInterface(HelloWorldService.class);
        ref.setServiceContract(inter);
        cref.setReference(ref);
        cref.getTargetConfiguredServices().add(cTargetService);
        cref.initialize(assemblyContext);
        sourceEP.setConfiguredReference(cref);
        sourceEP.getConfiguredService().getService().setName("HelloWorldService");
        sourceEP.initialize(assemblyContext);
        
    
        Module module = factory.createModule();
        module.setName("test.module");
        module.getEntryPoints().add(sourceEP);
        module.getComponents().add(targetComponent);
        module.initialize(assemblyContext);
        return module;
    }

    public static List<RuntimeConfigurationBuilder> createBuilders() {
        List<RuntimeConfigurationBuilder> builders = new ArrayList();
        builders.add((new SystemComponentContextBuilder()));
        builders.add(new SystemEntryPointBuilder());
        builders.add(new SystemExternalServiceBuilder());
        return builders;
    }
    
    
}
