package org.apache.tuscany.container.java.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.container.java.config.JavaComponentRuntimeConfiguration;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.NoAccessorException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.builder.impl.ProxyObjectFactory;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FactoryInitException;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.SDOObjectFactory;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.MethodHashMap;
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.impl.InvokerInterceptor;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactoryFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.apache.tuscany.model.assembly.SimpleComponent;
import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

import commonj.sdo.DataObject;

/**
 * Builds runtime configurations for component implementations that map to
 * {@link org.apache.tuscany.container.java.assembly.JavaImplementation}. The logical model is then decorated with the
 * runtime configuration.
 * 
 * @see org.apache.tuscany.core.builder.RuntimeConfiguration
 * 
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class JavaComponentContextBuilder implements RuntimeConfigurationBuilder<AggregateContext> {

    private RuntimeContext runtimeContext;

    private ProxyFactoryFactory proxyFactoryFactory;

    private MessageFactory messageFactory;

    /* the top-level builder responsible for evaluating policies */
    private RuntimeConfigurationBuilder policyBuilder;

    @Init(eager = true)
    public void init() {
        runtimeContext.addBuilder(this);
    }

    /**
     * @param runtimeContext The runtimeContext to set.
     */
    @Autowire
    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    /**
     * Sets the factory used to construct proxies implmementing the business interface required by a reference
     */
    @Autowire
    public void setProxyFactoryFactory(ProxyFactoryFactory factory) {
        this.proxyFactoryFactory = factory;
    }

    /**
     * Sets the factory used to construct invocation messages
     * 
     * @param msgFactory
     */
    @Autowire
    public void setMessageFactory(MessageFactory msgFactory) {
        this.messageFactory = msgFactory;
    }

    /**
     * Sets a builder responsible for creating source-side and target-side invocation chains for a reference. The
     * reference builder may be hierarchical, containing other child reference builders that operate on specific
     * metadata used to construct and invocation chain.
     * 
     * @see org.apache.tuscany.core.builder.impl.HierarchicalBuilder
     */
    public void setPolicyBuilder(RuntimeConfigurationBuilder builder) {
        this.policyBuilder = builder;
    }

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public JavaComponentContextBuilder() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void build(AssemblyModelObject modelObject, AggregateContext parentContext) throws BuilderException {
        if (!(modelObject instanceof SimpleComponent)) {
            return;
        }
        SimpleComponent component = (SimpleComponent) modelObject;
        if (component.getComponentImplementation() instanceof JavaImplementation) {
            JavaImplementation javaImpl = (JavaImplementation) component.getComponentImplementation();
            // FIXME scope
            Scope scope = component.getComponentImplementation().getComponentType().getServices().get(0).getServiceContract()
                    .getScope();
            Class implClass = null;
            Set<Field> fields;
            Set<Method> methods;
            try {
                implClass = javaImpl.getImplementationClass();
                fields = JavaIntrospectionHelper.getAllFields(implClass);
                methods = JavaIntrospectionHelper.getAllUniqueMethods(implClass);
                String name = component.getName();
                Constructor ctr = implClass.getConstructor((Class[]) null);

                List<Injector> injectors = new ArrayList();

                EventInvoker initInvoker = null;
                boolean eagerInit = false;
                EventInvoker destroyInvoker = null;
                // FIXME this should be run as part of the LCM load
                for (Field field : fields) {
                    ComponentName compName = field.getAnnotation(ComponentName.class);
                    if (compName != null) {
                        Injector injector = new FieldInjector(field, new SingletonObjectFactory(name));
                        injectors.add(injector);
                    }
                    Context context = field.getAnnotation(Context.class);
                    if (context != null) {
                        Injector injector = new FieldInjector(field, new SingletonObjectFactory(parentContext));
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
                    // @spec - should we allow the same method to have @init and
                    // @destroy?
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
                        Injector injector = new MethodInjector(method, new SingletonObjectFactory(parentContext));
                        injectors.add(injector);
                    }
                }
                // handle properties
                List<ConfiguredProperty> configuredProperties = component.getConfiguredProperties();
                // FIXME should return empty properties - does it?
                if (configuredProperties != null) {
                    for (ConfiguredProperty property : configuredProperties) {
                        Injector injector = createPropertyInjector(property, fields, methods);
                        injectors.add(injector);
                    }
                }
                JavaComponentRuntimeConfiguration config = new JavaComponentRuntimeConfiguration(name, JavaIntrospectionHelper
                        .getDefaultConstructor(implClass), eagerInit, initInvoker, destroyInvoker, scope);
                component.getComponentImplementation().setRuntimeConfiguration(config);

                // create target-side invocation chains for each service offered by the implementation
                for (ConfiguredService configuredService : component.getConfiguredServices()) {
                    Service service = configuredService.getService();
                    ServiceContract serviceContract = service.getServiceContract();
                    Map<Method, InvocationConfiguration> iConfigMap = new MethodHashMap();
                    ProxyFactory proxyFactory = proxyFactoryFactory.createProxyFactory();
                    Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(serviceContract.getInterface());
                    for (Method method : javaMethods) {
                        InvocationConfiguration iConfig = new InvocationConfiguration(method);
                        iConfigMap.put(method, iConfig);
                    }
                    QualifiedName qName = new QualifiedName(component.getName() + "/" + service.getName());
                    ProxyConfiguration pConfiguration = new ProxyConfiguration(qName, iConfigMap, serviceContract.getInterface().getClassLoader(), messageFactory);
                    proxyFactory.setBusinessInterface(serviceContract.getInterface());
                    proxyFactory.setProxyConfiguration(pConfiguration);
                    config.addTargetProxyFactory(service.getName(), proxyFactory);
                    configuredService.setProxyFactory(proxyFactory);
                    if (policyBuilder != null) {
                        // invoke the reference builder to handle target-side metadata
                        policyBuilder.build(configuredService, parentContext);
                    }
                    // add tail interceptor
                    for (InvocationConfiguration iConfig : (Collection<InvocationConfiguration>) iConfigMap.values()) {
                        iConfig.addTargetInterceptor(new InvokerInterceptor());
                    }

                }

                // handle references
                List<ConfiguredReference> configuredReferences = component.getConfiguredReferences();
                if (configuredReferences != null) {
                    for (ConfiguredReference reference : configuredReferences) {
                        ProxyFactory proxyFactory = proxyFactoryFactory.createProxyFactory();
                        ServiceContract serviceContract = reference.getReference().getServiceContract();
                        Map<Method, InvocationConfiguration> iConfigMap = new HashMap();
                        Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(serviceContract.getInterface());
                        for (Method method : javaMethods) {
                            InvocationConfiguration iConfig = new InvocationConfiguration(method);
                            iConfigMap.put(method, iConfig);
                        }
                        String targetCompName = reference.getTargetConfiguredServices().get(0).getAggregatePart().getName();
                        String targetSerivceName = reference.getTargetConfiguredServices().get(0).getService().getName();

                        QualifiedName qName = new QualifiedName(targetCompName + "/" + targetSerivceName);
                        // QualifiedName qName = new QualifiedName(reference.getAggregatePart().getName() + "/"
                        // + reference.getPort().getName());
                        ProxyConfiguration pConfiguration = new ProxyConfiguration(qName, iConfigMap, null, messageFactory);
                        proxyFactory.setBusinessInterface(serviceContract.getInterface());
                        proxyFactory.setProxyConfiguration(pConfiguration);
                        config.addSourceProxyFactory(reference.getReference().getName(), proxyFactory);
                        reference.setProxyFactory(proxyFactory);
                        if (policyBuilder != null) {
                            // invoke the reference builder to handle metadata associated with the reference
                            policyBuilder.build(reference, parentContext);
                        }
                        Injector injector = createReferenceInjector(reference.getReference().getName(), proxyFactory, fields,
                                methods);
                        injectors.add(injector);
                    }
                }
                config.setSetters(injectors);
            } catch (BuilderException e) {
                e.addContextName(component.getName());
                e.addContextName(parentContext.getName());
                throw e;
            } catch (NoSuchMethodException e) {
                BuilderConfigException ce = new BuilderConfigException("Class does not have a no-arg constructor", e);
                ce.setIdentifier(implClass.getName());
                ce.addContextName(component.getName());
                ce.addContextName(parentContext.getName());
                throw ce;
            }
        }
    }

    // ----------------------------------
    // Private methods
    // ----------------------------------

    /**
     * Creates an <code>Injector</code> for component properties
     */
    private Injector createPropertyInjector(ConfiguredProperty property, Set<Field> fields, Set<Method> methods)
            throws NoAccessorException {
        Object value = property.getValue();
        String propName = property.getProperty().getName();
        // @FIXME is this how to get property type of object
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
        // FIXME support types other than String
        if (value instanceof DataObject) {
            if (field != null) {
                injector = new FieldInjector(field, new SDOObjectFactory((DataObject) value));
            } else {
                injector = new MethodInjector(method, new SDOObjectFactory((DataObject) value));
            }
        } else if (JavaIntrospectionHelper.isImmutable(type)) {
            if (field != null) {
                injector = new FieldInjector(field, new SingletonObjectFactory(value));
            } else {
                injector = new MethodInjector(method, new SingletonObjectFactory(value));
            }
        }
        return injector;

    }

    /**
     * Creates an <code>Injector</code> for service references
     */
    private Injector createReferenceInjector(String refName, ProxyFactory proxyFactory, Set<Field> fields, Set<Method> methods)
            throws NoAccessorException, BuilderConfigException {
        Method method = null;
        Field field = JavaIntrospectionHelper.findClosestMatchingField(refName, proxyFactory.getBusinessInterface(), fields);
        if (field == null) {
            method = JavaIntrospectionHelper.findClosestMatchingMethod(refName,
                    new Class[] { proxyFactory.getBusinessInterface() }, methods);
            if (method == null) {
                throw new NoAccessorException(refName);
            }
        }
        Injector injector;
        try {
            if (field != null) {
                injector = new FieldInjector(field, new ProxyObjectFactory(proxyFactory));
            } else {
                injector = new MethodInjector(method, new ProxyObjectFactory(proxyFactory));
            }
        } catch (FactoryInitException e) {
            BuilderConfigException ce = new BuilderConfigException("Error configuring reference", e);
            ce.setIdentifier(refName);
            throw ce;
        }
        return injector;

    }

}
