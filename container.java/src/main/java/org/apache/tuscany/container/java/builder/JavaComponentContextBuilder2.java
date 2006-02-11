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
import org.apache.tuscany.core.builder.ProxyObjectFactory;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
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
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.impl.InvokerInterceptor;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactoryFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Interface;
import org.apache.tuscany.model.assembly.ScopeEnum;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.SimpleComponent;
import org.apache.tuscany.model.types.OperationType;
import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

import commonj.sdo.DataObject;

/**
 * Decorates components whose implementation type is a
 * {@link org.apache.tuscany.container.java.assembly.JavaImplementation} with the appropriate runtime configuration
 * 
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 */
public class JavaComponentContextBuilder2 implements RuntimeConfigurationBuilder<AggregateContext> {

    private String name;

    private final List<Injector> setters = new ArrayList();

    private AggregateContext parentContext;

    private AssemblyModelObject modelObject;

    private ProxyFactoryFactory factory;

    @Autowire
    public void setProxyFactoryFactory(ProxyFactoryFactory factory) {
        this.factory = factory;
    }

    private MessageFactory msgFactory;

    @Autowire
    public void setMessageFactory(MessageFactory msgFactory) {
        this.msgFactory = msgFactory;
    }

    private RuntimeConfigurationBuilder referenceBuilder;

    @Autowire
    public void setReferenceBuilder(RuntimeConfigurationBuilder builder) {
        this.referenceBuilder = builder;
    }

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public JavaComponentContextBuilder2() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void setModelObject(AssemblyModelObject modelObject) {
        this.modelObject = modelObject;
    }

    public void setParentContext(AggregateContext context) {
        parentContext = context;
    }

    public void build() throws BuilderException {
        if (!(modelObject instanceof SimpleComponent)) {
            return;
        }
        SimpleComponent component = (SimpleComponent) modelObject;
        if (component.getComponentImplementation() instanceof JavaImplementation) {
            JavaImplementation javaImpl = (JavaImplementation) component.getComponentImplementation();
            // FIXME scope
            ScopeEnum scope = component.getComponentImplementation().getServices().get(0).getInterfaceContract().getScope();
            Class implClass = null;
            Set<Field> fields;
            Set<Method> methods;
            try {
                implClass = JavaIntrospectionHelper.loadClass(javaImpl.getClass_());
                fields = JavaIntrospectionHelper.getAllFields(implClass);
                methods = JavaIntrospectionHelper.getAllUniqueMethods(implClass);
                name = component.getName();
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
                        .getDefaultConstructor(implClass), eagerInit, initInvoker, destroyInvoker, scope.getValue());
                component.getComponentImplementation().setRuntimeConfiguration(config);

                // create chains for handling incoming requests
                for (ConfiguredService configuredService : component.getConfiguredServices()) {
                    Service service = configuredService.getService();
                    Interface interfaze = service.getInterfaceContract();
                    Map<OperationType, InvocationConfiguration> iConfigMap = new HashMap();
                    ProxyFactory proxyFactory = factory.createProxyFactory();
                    // FIXME we pass null for scopes since ProxyConfiguration requires scopes - this should be removed
                    for (OperationType type : interfaze.getInterfaceType().getOperationTypes()) {
                        InvocationConfiguration iConfig = new InvocationConfiguration(type);
                        iConfigMap.put(type, iConfig);
                    }
                    // @FIXME hardcode separator
                    QualifiedName qName = new QualifiedName(configuredService.getPart().getName() + "/"
                            + configuredService.getPort().getName());
                    ProxyConfiguration pConfiguration = new ProxyConfiguration(qName, iConfigMap, null, null, msgFactory);
                    proxyFactory.setBusinessInterface(interfaze.getInterfaceType().getInstanceClass());
                    proxyFactory.setProxyConfiguration(pConfiguration);
                    config.addTargetProxyFactory(service.getName(), proxyFactory);
                    configuredService.setProxyFactory(proxyFactory);
                    // invoke another builder to add interceptors, etc.
                    referenceBuilder.setParentContext(parentContext);
                    referenceBuilder.setModelObject(configuredService);
                    referenceBuilder.build();
                    // add tail interceptor
                    for (InvocationConfiguration iConfig : (Collection<InvocationConfiguration>) iConfigMap.values()) {
                        iConfig.addTargetInterceptor(new InvokerInterceptor());
                        //iConfig.build();
                    }

                }

                // handle references
                List<ConfiguredReference> configuredReferences = component.getConfiguredReferences();
                if (configuredReferences != null) {
                    for (ConfiguredReference reference : configuredReferences) {
                        ProxyFactory proxyFactory = factory.createProxyFactory();
                        Interface interfaze = reference.getReference().getInterfaceContract();
                        Map<OperationType, InvocationConfiguration> iConfigMap = new HashMap();
                        for (OperationType type : interfaze.getInterfaceType().getOperationTypes()) {
                            InvocationConfiguration iConfig = new InvocationConfiguration(type);
                            iConfigMap.put(type, iConfig);
                        }

                        /*
                         * FIXME we pass null for scopes since ProxyConfiguration requires scopes - this should be
                         * removed from the constructor
                         */
                        QualifiedName qName = new QualifiedName(reference.getPart().getName() + "/"
                                + reference.getPort().getName());
                        ProxyConfiguration pConfiguration = new ProxyConfiguration(qName, iConfigMap, null, null, msgFactory);
                        proxyFactory.setBusinessInterface(interfaze.getInterfaceType().getInstanceClass());
                        proxyFactory.setProxyConfiguration(pConfiguration);
                        config.addSourceProxyFactory(reference.getReference().getName(), proxyFactory);
                        reference.setProxyFactory(proxyFactory);
                        // invoke another builder to add interceptors, etc.
                        referenceBuilder.setParentContext(parentContext);
                        referenceBuilder.setModelObject(reference);
                        referenceBuilder.build();
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
            } catch (ClassNotFoundException e) {
                BuilderException be = new BuilderConfigException(e);
                be.addContextName(component.getName());
                be.addContextName(parentContext.getName());
                throw be;
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
