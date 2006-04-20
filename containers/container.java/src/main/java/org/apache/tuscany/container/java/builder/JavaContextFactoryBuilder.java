package org.apache.tuscany.container.java.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.container.java.config.JavaContextFactory;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.NoAccessorException;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.builder.impl.ArrayMultiplicityObjectFactory;
import org.apache.tuscany.core.builder.impl.HierarchicalBuilder;
import org.apache.tuscany.core.builder.impl.ListMultiplicityObjectFactory;
import org.apache.tuscany.core.builder.impl.ProxyObjectFactory;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.injection.ContextObjectFactory;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.SDOObjectFactory;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.wire.MethodHashMap;
import org.apache.tuscany.core.wire.impl.InvokerInterceptor;
import org.apache.tuscany.core.wire.ProxyFactoryFactory;
import org.apache.tuscany.core.wire.WireSourceConfiguration;
import org.apache.tuscany.core.wire.WireTargetConfiguration;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.core.wire.SourceInvocationConfiguration;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.AtomicComponent;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Multiplicity;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

import commonj.sdo.DataObject;

/**
 * Builds context factories for component implementations that map to
 * {@link org.apache.tuscany.container.java.assembly.JavaImplementation}. The logical model is then decorated with the
 * runtime configuration.
 * 
 * @see org.apache.tuscany.core.builder.ContextFactory
 * 
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class JavaContextFactoryBuilder implements ContextFactoryBuilder {
    private ContextFactoryBuilderRegistry builderRegistry;

    private ProxyFactoryFactory proxyFactoryFactory;

    private MessageFactory messageFactory;

    /* the top-level builder responsible for evaluating policies */
    private HierarchicalBuilder policyBuilder = new HierarchicalBuilder();


    public JavaContextFactoryBuilder(ProxyFactoryFactory proxyFactoryFactory, MessageFactory messageFactory) {
        this.proxyFactoryFactory = proxyFactoryFactory;
        this.messageFactory = messageFactory;
    }

    public JavaContextFactoryBuilder() {
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.register(this);
    }

    @Autowire
    public void setBuilderRegistry(ContextFactoryBuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

    /**
     * Sets the factory used to construct proxies implmementing the business interface required by a reference
     */
    @Autowire
    public void setProxyFactoryFactory(ProxyFactoryFactory factory) {
        this.proxyFactoryFactory = factory;
    }

    /**
     * Sets the factory used to construct wire messages
     * 
     * @param msgFactory
     */
    @Autowire
    public void setMessageFactory(MessageFactory msgFactory) {
        this.messageFactory = msgFactory;
    }

    /**
     * Adds a builder responsible for creating source-side and target-side wire chains for a reference. The
     * reference builder may be hierarchical, containing other child reference builders that operate on specific
     * metadata used to construct and wire chain.
     */
    public void addPolicyBuilder(ContextFactoryBuilder builder) {
        policyBuilder.addBuilder(builder);
    }

    public void build(AssemblyObject modelObject) throws BuilderException {
        if (!(modelObject instanceof AtomicComponent)) {
            return;
        }
        AtomicComponent component = (AtomicComponent) modelObject;
        if (component.getImplementation() instanceof JavaImplementation) {
            JavaImplementation javaImpl = (JavaImplementation) component.getImplementation();
            List<Service> services = component.getImplementation().getComponentInfo().getServices();
            Scope previous = null;
            Scope scope = Scope.INSTANCE;
            for (Service service : services) {
                // calculate and validate the scope of the component; ensure that all service scopes are the same unless
                // a scope is stateless
                Scope current = service.getServiceContract().getScope();
                if (previous != null && current != null && current != previous
                        && (current != Scope.INSTANCE && previous != Scope.INSTANCE)) {
                    BuilderException e = new BuilderConfigException("Incompatible scopes specified for services on component");
                    e.setIdentifier(component.getName());
                    throw e;
                }
                if (scope != null && current != Scope.INSTANCE) {
                    scope = current;
                }
            }
            Class implClass = null;
            Set<Field> fields;
            Set<Method> methods;
            JavaContextFactory contextFactory;
            try {
                implClass = javaImpl.getImplementationClass();
                fields = JavaIntrospectionHelper.getAllFields(implClass);
                methods = JavaIntrospectionHelper.getAllUniqueMethods(implClass);
                String name = component.getName();
                contextFactory = new JavaContextFactory(name, JavaIntrospectionHelper
                        .getDefaultConstructor(implClass), scope);
                
                List<Injector> injectors = new ArrayList<Injector>();

                EventInvoker<Object> initInvoker = null;
                boolean eagerInit = false;
                EventInvoker<Object> destroyInvoker = null;
                ContextObjectFactory contextObjectFactory = new ContextObjectFactory(contextFactory);
                for (Field field : fields) {
                    ComponentName compName = field.getAnnotation(ComponentName.class);
                    if (compName != null) {
                        Injector injector = new FieldInjector(field, new SingletonObjectFactory<Object>(name));
                        injectors.add(injector);
                    }
                    Context context = field.getAnnotation(Context.class);
                    if (context != null) {
                        Injector injector = new FieldInjector(field, contextObjectFactory);
                        injectors.add(injector);
                    }
                }
                for (Method method : methods) {
                    Init init = method.getAnnotation(Init.class);
                    if (init != null && initInvoker == null) {
                        initInvoker = new MethodEventInvoker<Object>(method);
                        eagerInit = init.eager();
                        continue;
                    }
                    // @spec - should we allow the same method to have @init and @destroy?
                    Destroy destroy = method.getAnnotation(Destroy.class);
                    if (destroy != null && destroyInvoker == null) {
                        destroyInvoker = new MethodEventInvoker<Object>(method);
                        continue;
                    }
                    ComponentName compName = method.getAnnotation(ComponentName.class);
                    if (compName != null) {
                        Injector injector = new MethodInjector(method, new SingletonObjectFactory<Object>(name));
                        injectors.add(injector);
                    }
                    Context context = method.getAnnotation(Context.class);
                    if (context != null) {
                        Injector injector = new MethodInjector(method, contextObjectFactory);
                        injectors.add(injector);
                    }
                }
                // handle properties
                List<ConfiguredProperty> configuredProperties = component.getConfiguredProperties();
                if (configuredProperties != null) {
                    for (ConfiguredProperty property : configuredProperties) {
                        Injector injector = createPropertyInjector(property, fields, methods);
                        injectors.add(injector);
                    }
                }
                component.setContextFactory(contextFactory);

                // create target-side wire chains for each service offered by the implementation
                for (ConfiguredService configuredService : component.getConfiguredServices()) {
                    Service service = configuredService.getPort();
                    ServiceContract serviceContract = service.getServiceContract();
                    Map<Method, TargetInvocationConfiguration> iConfigMap = new MethodHashMap<TargetInvocationConfiguration>();
                    TargetWireFactory proxyFactory = proxyFactoryFactory.createTargetWireFactory();
                    Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(serviceContract.getInterface());
                    for (Method method : javaMethods) {
                        TargetInvocationConfiguration iConfig = new TargetInvocationConfiguration(method);
                        iConfigMap.put(method, iConfig);
                    }
                    QualifiedName qName = new QualifiedName(component.getName() + QualifiedName.NAME_SEPARATOR
                            + service.getName());
                    WireTargetConfiguration wireConfiguration = new WireTargetConfiguration(qName, iConfigMap, serviceContract.getInterface()
                            .getClassLoader(), messageFactory);
                    proxyFactory.setBusinessInterface(serviceContract.getInterface());
                    proxyFactory.setConfiguration(wireConfiguration);
                    contextFactory.addTargetProxyFactory(service.getName(), proxyFactory);
                    configuredService.setProxyFactory(proxyFactory);
                    if (policyBuilder != null) {
                        // invoke the reference builder to handle target-side metadata
                        policyBuilder.build(configuredService);
                    }
                    // add tail interceptor
                    for (TargetInvocationConfiguration iConfig : iConfigMap.values()) {
                        iConfig.addInterceptor(new InvokerInterceptor());
                    }

                }

                // create injectors for references
                List<ConfiguredReference> configuredReferences = component.getConfiguredReferences();
                if (configuredReferences != null) {
                    for (ConfiguredReference reference : configuredReferences) {
                        Injector injector = createReferenceInjector(contextFactory, reference, fields, methods);
                        injectors.add(injector);
                    }
                }
                
                contextFactory.setSetters(injectors);
                contextFactory.setEagerInit(eagerInit);
                contextFactory.setInitInvoker(initInvoker);
                contextFactory.setDestroyInvoker(destroyInvoker);
            } catch (BuilderException e) {
                e.addContextName(component.getName());
                throw e;
            } catch (NoSuchMethodException e) {
                BuilderConfigException ce = new BuilderConfigException("Class does not have a no-arg constructor", e);
                ce.setIdentifier(implClass.getName());
                ce.addContextName(component.getName());
                throw ce;
            }
        }
    }

    /**
     * Creates an <code>Injector</code> for component properties
     */
    private Injector createPropertyInjector(ConfiguredProperty property, Set<Field> fields, Set<Method> methods)
            throws NoAccessorException {
        Object value = property.getValue();
        String propName = property.getProperty().getName();
        Class type = value.getClass();

        // There is no efficient way to do this
        Method method = null;
        Field field = JavaIntrospectionHelper.findClosestMatchingField(propName, type, fields);
        if (field == null) {
            method = JavaIntrospectionHelper.findClosestMatchingMethod(propName, new Class[] { type }, methods);
            if (method == null) {
                throw new NoAccessorException(propName);
            }
        }
        Injector injector = null;
        if (value instanceof DataObject) {
            if (field != null) {
                injector = new FieldInjector(field, new SDOObjectFactory((DataObject) value));
            } else {
                injector = new MethodInjector(method, new SDOObjectFactory((DataObject) value));
            }
        } else if (JavaIntrospectionHelper.isImmutable(type)) {
            if (field != null) {
                injector = new FieldInjector(field, new SingletonObjectFactory<Object>(value));
            } else {
                injector = new MethodInjector(method, new SingletonObjectFactory<Object>(value));
            }
        }
        return injector;

    }

    /**
     * Creates proxy factories that represent target(s) of a reference and an <code>Injector</code> responsible for
     * injecting them into the reference
     */
    private Injector createReferenceInjector(JavaContextFactory config, ConfiguredReference reference,
            Set<Field> fields, Set<Method> methods) {

        // iterate through the targets
        List<ObjectFactory> objectFactories = new ArrayList<ObjectFactory>();
        String refName = reference.getPort().getName();
        Class refClass = reference.getPort().getServiceContract().getInterface();
        for (ConfiguredService configuredService : reference.getTargetConfiguredServices()) {
            String targetCompName = configuredService.getPart().getName();
            String targetSerivceName = configuredService.getPort().getName();
            QualifiedName qName = new QualifiedName(targetCompName + QualifiedName.NAME_SEPARATOR + targetSerivceName);

            SourceWireFactory proxyFactory = proxyFactoryFactory.createSourceWireFactory();
            Class interfaze = reference.getPort().getServiceContract().getInterface();
            Map<Method, SourceInvocationConfiguration> iConfigMap = new HashMap<Method, SourceInvocationConfiguration>();
            Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(interfaze);
            for (Method method : javaMethods) {
                SourceInvocationConfiguration iConfig = new SourceInvocationConfiguration(method);
                iConfigMap.put(method, iConfig);
            }

            WireSourceConfiguration pConfiguration = new WireSourceConfiguration(refName, qName, iConfigMap, interfaze.getClassLoader(),
                    messageFactory);
            proxyFactory.setBusinessInterface(interfaze);
            proxyFactory.setConfiguration(pConfiguration);
            config.addSourceProxyFactory(reference.getPort().getName(), proxyFactory);
            configuredService.setProxyFactory(proxyFactory);
            if (policyBuilder != null) {
                // invoke the reference builder to handle metadata associated with the reference
                policyBuilder.build(reference);
            }
            objectFactories.add(new ProxyObjectFactory(proxyFactory));
        }
        boolean multiplicity = reference.getPort().getMultiplicity() == Multiplicity.ONE_N
                || reference.getPort().getMultiplicity() == Multiplicity.ZERO_N;
        return createInjector(refName, refClass, multiplicity, objectFactories, fields, methods);

    }

    /**
     * Creates an <code>Injector</code> for a set of object factories associated with a reference.
     */
    private Injector createInjector(String refName, Class refClass, boolean multiplicity, List<ObjectFactory> objectFactories,
            Set<Field> fields, Set<Method> methods) throws NoAccessorException, BuilderConfigException {
        Field field;
        Method method = null;
        if (multiplicity) {
            // since this is a multiplicity, we cannot match on business interface type, so scan through the fields,
            // matching on name and List or Array
            field = JavaIntrospectionHelper.findMultiplicityFieldByName(refName, fields);
            if (field == null) {
                // No fields found. Again, since this is a multiplicity, we cannot match on business interface type, so
                // scan through the fields, matching on name and List or Array
                method = JavaIntrospectionHelper.findMultiplicityMethodByName(refName, methods);
                if (method == null) {
                    throw new NoAccessorException(refName);
                }
            }
            Injector injector;
            // for multiplicities, we need to inject the reference proxy or proxies using an object factory
            // which first delegates to create the proxies and then returns them in the appropriate List or array type
            if (field != null) {
                if (field.getType().isArray()) {
                    injector = new FieldInjector(field, new ArrayMultiplicityObjectFactory(refClass, objectFactories));
                } else {
                    injector = new FieldInjector(field, new ListMultiplicityObjectFactory(objectFactories));
                }
            } else {
                if (method.getParameterTypes()[0].isArray()) {
                    injector = new MethodInjector(method, new ArrayMultiplicityObjectFactory(refClass, objectFactories));
                } else {
                    injector = new MethodInjector(method, new ListMultiplicityObjectFactory(objectFactories));
                }
            }
            return injector;
        } else {
            field = JavaIntrospectionHelper.findClosestMatchingField(refName, refClass, fields);
            if (field == null) {
                method = JavaIntrospectionHelper.findClosestMatchingMethod(refName, new Class[] { refClass }, methods);
                if (method == null) {
                    throw new NoAccessorException(refName);
                }
            }
            Injector injector;
            if (field != null) {
                injector = new FieldInjector(field, objectFactories.get(0));
            } else {
                injector = new MethodInjector(method, objectFactories.get(0));
            }
            return injector;
        }
    }
}
