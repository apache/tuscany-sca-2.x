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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

import org.apache.tuscany.container.java.assembly.JavaAssemblyFactory;
import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.container.java.assembly.impl.JavaAssemblyFactoryImpl;
import org.apache.tuscany.container.java.assembly.mock.HelloWorldImpl;
import org.apache.tuscany.container.java.assembly.mock.HelloWorldService;
import org.apache.tuscany.container.java.builder.JavaComponentContextBuilder;
import org.apache.tuscany.container.java.builder.JavaTargetWireBuilder;
import org.apache.tuscany.container.java.context.JavaComponentContext;
import org.apache.tuscany.container.java.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.container.java.mock.binding.foo.FooBinding;
import org.apache.tuscany.container.java.mock.binding.foo.FooBindingBuilder;
import org.apache.tuscany.container.java.mock.binding.foo.FooBindingWireBuilder;
import org.apache.tuscany.container.java.mock.components.GenericComponent;
import org.apache.tuscany.container.java.mock.components.HelloWorldClient;
import org.apache.tuscany.container.java.mock.components.ModuleScopeComponentImpl;
import org.apache.tuscany.container.java.mock.components.Source;
import org.apache.tuscany.container.java.mock.components.SourceImpl;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.container.java.mock.components.TargetImpl;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.SystemAggregateContext;
import org.apache.tuscany.core.context.impl.AggregateContextImpl;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
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
import org.apache.tuscany.model.assembly.ComponentType;
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

    public static final String JAVA_BUILDER = "java.runtime.builder";

    public static final String JAVA_WIRE_BUILDER = "java.wire.builder";

    public static final String FOO_BUILDER = "foo.binding.builder";

    public static final String FOO_WIRE_BUILDER = "foo.binding.wire.builder";

    public static final String SYSTEM_CHILD = "tuscany.system.child";

    private static JavaAssemblyFactory factory = new JavaAssemblyFactoryImpl();

    private static SystemAssemblyFactory systemFactory = new SystemAssemblyFactoryImpl();

    private static AssemblyModelContext assemblyContext = new AssemblyModelContextImpl(null, null);

    /**
     * Creates an initialized simple component
     *
     * @param name  the component name
     * @param type  the implementation type
     * @param scope the component scope
     */
    public static SimpleComponent createComponent(String name, Class type, Scope scope) {
        SimpleComponent sc = factory.createSimpleComponent();
        JavaImplementation impl = factory.createJavaImplementation();
        impl.setComponentType(factory.createComponentType());
        impl.setImplementationClass(type);
        sc.setComponentImplementation(impl);
        Service s = factory.createService();
        JavaServiceContract ji = factory.createJavaServiceContract();
        ji.setInterface(type);
        s.setServiceContract(ji);
        ji.setScope(scope);
        impl.getComponentType().getServices().add(s);
        sc.setName(name);
        sc.setComponentImplementation(impl);
        return sc;
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
     * Creates a system aggregate component with the given name
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
     * Creates an external service configured with the 'Foo' test binding
     */
    public static ExternalService createFooBindingExternalService(String name, Class interfaz) {
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

    /**
     * Creates an entry point with the given name configured with the given interface and the {@link FooBinding}
     */
    public static EntryPoint createFooBindingEntryPoint(String name, Class interfaz) {
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
     * Creates an entry point wired to the given target (e.g. component, external service) using the system binding
     *
     * @param name     the name of the entry point
     * @param interfaz the inteface exposed by the entry point
     * @param refName  the name of the entry point reference
     * @param target   the target the entry point is wired to
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

        Service epService = systemFactory.createService();
        epService.setServiceContract(contract);

        ConfiguredService epCService = systemFactory.createConfiguredService();
        epCService.initialize(MockFactory.assemblyContext);
        epCService.setService(epService);

        ep.setConfiguredService(epCService);
        SystemBinding binding = systemFactory.createSystemBinding();
        ep.getBindings().add(binding);
        if (target != null) {
            if (target instanceof Component) {
                ((Component) target).getConfiguredServices().add(cService);
            } else if (target instanceof ExternalService) {
                ((ExternalService) target).setConfiguredService(cService);
            }
            target.initialize(MockFactory.assemblyContext);
        }
        ep.initialize(null);
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
     * Creates a module with a Java-based "target" module-scoped component wired to a module-scoped "source"
     */
    public static Module createModule() {
        return createModule(Scope.MODULE, Scope.MODULE);
    }

    /**
     * Creates a module with a Java-based "target" component wired to a "source"
     */
    public static Module createModule(Scope sourceScope, Scope targetScope) {
        Component sourceComponent = createComponent("source", ModuleScopeComponentImpl.class, sourceScope);
        Component targetComponent = createComponent("target", ModuleScopeComponentImpl.class, targetScope);

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
        ref.setName("setGenericComponent");
        JavaServiceContract inter = factory.createJavaServiceContract();
        inter.setInterface(GenericComponent.class);
        ref.setServiceContract(inter);
        sourceComponent.getComponentImplementation().getComponentType().getReferences().add(ref);

        ConfiguredReference cref = factory.createConfiguredReference();
        cref.setName("setGenericComponent");
        cref.setTarget("target");
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

    /**
     * Creates a module with a Java-based source component wired to a "target" external service configured with the
     * {@link FooBinding}
     */
    public static Module createModuleWithExternalService() {
        Component sourceComponent = createComponent("source", HelloWorldClient.class, Scope.MODULE);
        ExternalService targetES = createFooBindingExternalService("target", HelloWorldService.class);

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
        ref.setName("setHelloWorldService");
        JavaServiceContract inter = factory.createJavaServiceContract();
        inter.setInterface(HelloWorldService.class);
        ref.setServiceContract(inter);
        sourceComponent.getComponentImplementation().getComponentType().getReferences().add(ref);

        ConfiguredReference cref = factory.createConfiguredReference();
        cref.setName(ref.getName());
        cref.setTarget("target");
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

    /**
     * Creates a module with an entry point named "source" configured with the {@link FooBinding} wired to a service
     * offered by a Java-based component named "target"
     *
     * @param scope the scope of the target service
     */
    public static Module createModuleWithEntryPoint(Scope scope) {
        EntryPoint sourceEP = createFooBindingEntryPoint("source", HelloWorldService.class);
        Component targetComponent = createComponent("target", HelloWorldImpl.class, scope);

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


    /**
     * Creates a test system module with source and target components wired together.
     *
     * @see org.apache.tuscany.core.mock.component.Source
     * @see org.apache.tuscany.core.mock.component.Target
     */
    public static Module createModuleWithWiredComponents(Scope sourceScope, Scope targetScope) {

        // create the target component
        SimpleComponent target = factory.createSimpleComponent();
        target.setName("target");
        JavaImplementation targetImpl = factory.createJavaImplementation();
        targetImpl.setComponentType(factory.createComponentType());
        targetImpl.setImplementationClass(TargetImpl.class);
        target.setComponentImplementation(targetImpl);
        Service targetService = factory.createService();
        JavaServiceContract targetContract = factory.createJavaServiceContract();
        targetContract.setInterface(Target.class);
        targetService.setServiceContract(targetContract);
        targetService.setName("Target");
        targetImpl.getComponentType().getServices().add(targetService);
        targetContract.setScope(targetScope);
        ConfiguredService cTargetService = factory.createConfiguredService();
        cTargetService.setService(targetService);
        cTargetService.initialize(assemblyContext);
        target.getConfiguredServices().add(cTargetService);
        target.initialize(assemblyContext);

        // create the source component
        SimpleComponent source = factory.createSimpleComponent();
        ComponentType componentType = factory.createComponentType();
        source.setName("source");
        JavaImplementation impl = factory.createJavaImplementation();
        impl.setComponentType(componentType);
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
        componentType.getReferences().add(reference);
        ConfiguredReference cReference = systemFactory.createConfiguredReference();
        cReference.setName(reference.getName());
        cReference.setTarget("target");
        cReference.initialize(assemblyContext);
        source.getConfiguredReferences().add(cReference);

        // wire multiplicity using a setter
        JavaServiceContract refContract2 = systemFactory.createJavaServiceContract();
        refContract2.setInterface(List.class);
        Reference reference2 = systemFactory.createReference();
        reference2.setName("setTargets");
        reference2.setServiceContract(refContract2);
        componentType.getReferences().add(reference2);
        ConfiguredReference cReference2 = systemFactory.createConfiguredReference();
        cReference2.setName(reference2.getName());
        cReference2.setTarget("target");
        cReference2.initialize(assemblyContext);
        source.getConfiguredReferences().add(cReference2);

        // wire multiplicity using a field
        JavaServiceContract refContract3 = systemFactory.createJavaServiceContract();
        refContract3.setInterface(List.class);
        Reference reference3 = systemFactory.createReference();
        reference3.setName("targetsThroughField");
        reference3.setServiceContract(refContract3);
        componentType.getReferences().add(reference3);
        ConfiguredReference cReference3 = systemFactory.createConfiguredReference();
        cReference3.setName(reference3.getName());
        cReference3.setTarget("target");
        cReference3.initialize(assemblyContext);
        source.getConfiguredReferences().add(cReference3);
        source.initialize(assemblyContext);


        Module module = systemFactory.createModule();
        module.setName("system.module");

        module.getComponents().add(source);
        module.getComponents().add(target);
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
     * Creates an aggregate runtime configuration
     *
     * @param name             the name of the component
     * @param aggregateContext the containing aggregate context
     * @throws BuilderException
     * @see RuntimeConfiguration
     */
    public static RuntimeConfiguration<InstanceContext> createAggregateConfiguration(String name,
                                                                                     AggregateContext aggregateContext) throws BuilderException {

        Component sc = createAggregateComponent(name);
        SystemComponentContextBuilder builder = new SystemComponentContextBuilder();
        builder.build(sc, aggregateContext);
        return (RuntimeConfiguration<InstanceContext>) sc.getComponentImplementation().getRuntimeConfiguration();
    }

    /**
     * Creates a Java POJO component context
     *
     * @param name                   the name of the context
     * @param implType               the POJO class
     * @param scope                  the component scope
     * @param moduleComponentContext the containing aggregate context
     * @throws NoSuchMethodException if the POJO does not have a default noi-args constructor
     */
    public static JavaComponentContext createPojoContext(String name, Class implType, Scope scope,
                                                         AggregateContext moduleComponentContext) throws NoSuchMethodException {
        SimpleComponent component = createComponent(name, implType, scope);

        Set<Field> fields = JavaIntrospectionHelper.getAllFields(implType);
        Set<Method> methods = JavaIntrospectionHelper.getAllUniqueMethods(implType);
        List<Injector> injectors = new ArrayList();
        EventInvoker initInvoker = null;
        boolean eagerInit = false;
        EventInvoker destroyInvoker = null;
        for (Field field : fields) {
            ComponentName compName = field.getAnnotation(ComponentName.class);
            if (compName != null) {
                Injector injector = new FieldInjector(field, new SingletonObjectFactory(name));
                injectors.add(injector);
            }
            Context context = field.getAnnotation(Context.class);
            if (context != null) {
                Injector injector = new FieldInjector(field, new SingletonObjectFactory(moduleComponentContext));
                injectors.add(injector);
            }
        }
        for (Method method : methods) {
            // FIXME Java5
            Init init = method.getAnnotation(Init.class);
            if (init != null && initInvoker == null) {
                initInvoker = new MethodEventInvoker(method);
                eagerInit = init.eager();
                continue;
            }
            Destroy destroy = method.getAnnotation(Destroy.class);
            if (destroy != null && destroyInvoker == null) {
                destroyInvoker = new MethodEventInvoker(method);
                continue;
            }
            ComponentName compName = method.getAnnotation(ComponentName.class);
            if (compName != null) {
                Injector injector = new MethodInjector(method, new SingletonObjectFactory(name));
                injectors.add(injector);
            }
            Context context = method.getAnnotation(Context.class);
            if (context != null) {
                Injector injector = new MethodInjector(method, new SingletonObjectFactory(moduleComponentContext));
                injectors.add(injector);
            }
        }
        boolean stateless = (scope == Scope.INSTANCE);
        JavaComponentContext context = new JavaComponentContext("foo", new PojoObjectFactory(JavaIntrospectionHelper
                .getDefaultConstructor(implType), null, injectors), eagerInit, initInvoker, destroyInvoker, stateless);
        return context;
    }

    /**
     * Creates a default {@link RuntimeContext} configured with support for Java component implementations
     *
     * @throws ConfigurationException
     */
    public static RuntimeContext createJavaRuntime() throws ConfigurationException {
        RuntimeContext runtime = new RuntimeContextImpl(null, null, MockFactory.createSystemBuilders(), null);
        runtime.start();
        runtime.getSystemContext().registerModelObject(createSystemAggregateComponent(SYSTEM_CHILD));
        SystemAggregateContext ctx = (SystemAggregateContext) runtime.getSystemContext().getContext(SYSTEM_CHILD);
        ctx.registerModelObject(createSystemComponent(JAVA_BUILDER, JavaComponentContextBuilder.class, Scope.MODULE));
        ctx.registerModelObject(createSystemComponent(JAVA_WIRE_BUILDER, JavaTargetWireBuilder.class, Scope.MODULE));
        ctx.fireEvent(EventContext.MODULE_START, null);
        return runtime;
    }

    /**
     * Registers the {@link FooBinding} builders with a given runtime
     *
     * @throws ConfigurationException
     */
    public static RuntimeContext registerFooBinding(RuntimeContext runtime) throws ConfigurationException {
        AggregateContext child = (AggregateContext) runtime.getSystemContext().getContext(MockFactory.SYSTEM_CHILD);
        JavaComponentContextBuilder javaBuilder = (JavaComponentContextBuilder) child.getContext(MockFactory.JAVA_BUILDER)
                .getInstance(null);
        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
        child.registerModelObject(MockFactory.createSystemComponent(FOO_BUILDER, FooBindingBuilder.class, Scope.MODULE));
        child.registerModelObject(MockFactory.createSystemComponent(FOO_WIRE_BUILDER, FooBindingWireBuilder.class, Scope.MODULE));
        // since the child context is already started, we need to manually retrieve the components to init them
        Assert.assertNotNull(child.getContext(FOO_BUILDER).getInstance(null));
        Assert.assertNotNull(child.getContext(FOO_WIRE_BUILDER).getInstance(null));
        return runtime;
    }
}
