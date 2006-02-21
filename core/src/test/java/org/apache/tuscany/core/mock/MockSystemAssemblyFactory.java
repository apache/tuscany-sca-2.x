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

import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponentImpl;
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
import org.apache.tuscany.model.assembly.AggregatePart;
import org.apache.tuscany.model.assembly.ScopeEnum;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.pojo.PojoAggregateComponent;
import org.apache.tuscany.model.assembly.pojo.PojoComponent;
import org.apache.tuscany.model.assembly.pojo.PojoConfiguredReference;
import org.apache.tuscany.model.assembly.pojo.PojoConfiguredService;
import org.apache.tuscany.model.assembly.pojo.PojoEntryPoint;
import org.apache.tuscany.model.assembly.pojo.PojoExternalService;
import org.apache.tuscany.model.assembly.pojo.PojoInterface;
import org.apache.tuscany.model.assembly.pojo.PojoInterfaceType;
import org.apache.tuscany.model.assembly.pojo.PojoJavaInterface;
import org.apache.tuscany.model.assembly.pojo.PojoModule;
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
    public static Component createComponent(String name, String type, ScopeEnum scope) throws NoSuchMethodException,
            ClassNotFoundException {

        Class claz = JavaIntrospectionHelper.loadClass(type);
        PojoComponent sc = null;
        if (AggregateContext.class.isAssignableFrom(claz)) {
            sc = new PojoAggregateComponent();
        } else {
            sc = new PojoSimpleComponent();
        }
        SystemImplementation impl = new PojoSystemImplementation();
        impl.setClass(type);
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

    /**
     * Creates a basic entry point with no configured reference using the system binding
     * 
     * @param name the name of the entry point
     * @param interfaz the inteface exposed by the entry point
     * @param refName the name of the entry point reference
     */
    public static PojoEntryPoint createEntryPoint(String name, Class interfaz, String refName) {
        return createEntryPoint(name, interfaz, refName, null);
    }

    /**
     * Creates an entry point wired to the given target (e.g. component, external service) using the system binding
     * 
     * @param name the name of the entry point
     * @param interfaz the inteface exposed by the entry point
     * @param refName the name of the entry point reference
     * @param referenceTarget the target of the entry point wire
     */
    public static PojoEntryPoint createEntryPoint(String name, Class interfaz, String refName, AggregatePart referenceTarget) {
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
        ep.getConfiguredReference().getTargetConfiguredServices().get(0).setPart(referenceTarget);
        PojoInterfaceType interfaceType = new PojoInterfaceType();
        interfaceType.setInstanceClass(interfaz);
        PojoInterface inter = new PojoJavaInterface();
        inter.setInterfaceType(interfaceType);
        ep.setServiceContract(inter);
        ep.getBindings().add(new PojoSystemBinding());
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
    public static PojoEntryPoint createEntryPointWithStringRef(String name, Class interfaz, String refName, String componentName) {
        PojoEntryPoint ep = createEntryPoint(name, interfaz, refName, null);
        PojoConfiguredReference cRef = new PojoConfiguredReference();
        PojoReference ref = new PojoReference();
        cRef.setReference(ref);
        PojoService service = new PojoService();
        service.setName(componentName);
        PojoConfiguredService cService = new PojoConfiguredService();
        cService.setService(service);
        cRef.getTargetConfiguredServices().add(cService);
        ep.setConfiguredReference(cRef);
        return ep;
    }

    /**
     * Creates an external service
     */
    public static ExternalService createExternalService(String name, String refName) {
        PojoExternalService es = new PojoExternalService();
        es.setName(name);
        PojoConfiguredService configuredService = new PojoConfiguredService();
        // FIXME No idea if this is correct, I suspect it isn't
        PojoPart part = new PojoPart();
        part.setName(refName);
        configuredService.setPart(part);
        es.setConfiguredService(configuredService);
        es.getBindings().add(new PojoSystemBinding());
        return es;
    }

    /**
     * Creates an external service that specifies an autowire of the given type
     */
    public static ExternalService createAutowirableExternalService(String name, Class type) {
        PojoExternalService es = new PojoExternalService();
        es.setName(name);
        PojoInterface inter = new PojoJavaInterface();
        PojoInterfaceType interType = new PojoInterfaceType();
        interType.setInstanceClass(type);
        inter.setInterfaceType(interType);
        es.setServiceContract(inter);
        es.getBindings().add(new PojoSystemBinding());
        PojoConfiguredService configuredService = new PojoConfiguredService();
        es.setConfiguredService(configuredService);
        return es;
    }

    /**
     * Creates a test system module component with a module-scoped component and entry point
     */
    public static Module createSystemModule() throws Exception {
        PojoModule module = new PojoModule();
        module.setName("system.module");

        // create test component
        PojoSimpleComponent component = new PojoSimpleComponent();
        component.setName("TestService1");
        SystemImplementation impl = new PojoSystemImplementation();
        impl.setClass(ModuleScopeSystemComponentImpl.class.getName());
        component.setComponentImplementation(impl);
        Service s = new PojoService();
        JavaServiceContract ji = new PojoJavaInterface();
        s.setServiceContract(ji);
        ji.setScope(ScopeEnum.MODULE_LITERAL);
        impl.getServices().add(s);
        component.setComponentImplementation(impl);

        // create the entry point
        EntryPoint ep = createEntryPoint("TestService1EP", ModuleScopeSystemComponent.class, "target");
        // wire the entry point to the component
        ep.getConfiguredReference().getTargetConfiguredServices().get(0).setPart(component);

        module.addEntryPoint(ep);
        module.addComponent(component);
        return module;
    }

    /**
     * Creates a test system module component with a module-scoped component and entry point
     */
    public static Module createSystemChildModule() throws Exception {
        PojoModule module = new PojoModule();
        module.setName("system.test.module");

        // create test component
        PojoSimpleComponent component = new PojoSimpleComponent();
        component.setName("TestService2");
        SystemImplementation impl = new PojoSystemImplementation();
        impl.setClass(ModuleScopeSystemComponentImpl.class.getName());
        component.setComponentImplementation(impl);
        Service s = new PojoService();
        JavaServiceContract ji = new PojoJavaInterface();
        s.setServiceContract(ji);
        ji.setScope(ScopeEnum.MODULE_LITERAL);
        impl.getServices().add(s);
        component.setComponentImplementation(impl);

        // create the entry point
        EntryPoint ep = createEntryPoint("TestService2EP", ModuleScopeSystemComponent.class, "target");
        // wire the entry point to the component
        ep.getConfiguredReference().getTargetConfiguredServices().get(0).setPart(component);

        module.addEntryPoint(ep);
        module.addComponent(component);
        return module;
    }

}
