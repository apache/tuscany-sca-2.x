package org.apache.tuscany.binding.axis.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.binding.axis.assembly.WebServiceBinding;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.impl.InvokerInterceptor;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactoryFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

/**
 * Builds runtime configurations for component implementations that map to
 * {@link org.apache.tuscany.binding.axis.assembly.WebServiceBinding}. The logical model is then decorated with the
 * runtime configuration.
 * 
 * @see org.apache.tuscany.core.builder.RuntimeConfiguration
 * 
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class ExternalWebServiceContextBuilder implements RuntimeConfigurationBuilder<AggregateContext> {
    
    private RuntimeContext runtimeContext;
    private ProxyFactoryFactory proxyFactoryFactory;
    private MessageFactory messageFactory;
    private RuntimeConfigurationBuilder referenceBuilder;

    @Init(eager=true)
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
    public void setReferenceBuilder(RuntimeConfigurationBuilder builder) {
        this.referenceBuilder = builder;
    }

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public ExternalWebServiceContextBuilder() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void build(AssemblyModelObject modelObject, AggregateContext parentContext) throws BuilderException {
        if (!(modelObject instanceof ExternalService)) {
            return;
        }
        ExternalService externalService = (ExternalService) modelObject;
        List<Binding> bindings=externalService.getBindings();
        if (!bindings.isEmpty() && bindings.get(0) instanceof WebServiceBinding) {
            WebServiceBinding wsBinding = (WebServiceBinding) bindings.get(0);

            // FIXME scope
            Scope scope = Scope.MODULE;
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
                ExternalWebServiceRuntimeConfiguration config = new ExternalWebServiceRuntimeConfiguration(name, JavaIntrospectionHelper
                        .getDefaultConstructor(implClass), eagerInit, initInvoker, destroyInvoker, scope);
                component.getComponentImplementation().setRuntimeConfiguration(config);

                // create target-side invocation chains for each service offered by the implementation
                for (ConfiguredService configuredService : component.getConfiguredServices()) {
                    Service service = configuredService.getService();
                    ServiceContract serviceContract = service.getServiceContract();
                    Map<Method, InvocationConfiguration> iConfigMap = new HashMap();
                    ProxyFactory proxyFactory = proxyFactoryFactory.createProxyFactory();
                    Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(serviceContract.getInterface());
                    for (Method method : javaMethods) {
                        InvocationConfiguration iConfig = new InvocationConfiguration(method);
                        iConfigMap.put(method, iConfig);
                    }
                    QualifiedName qName = new QualifiedName(component.getName() + "/" + service.getName());
                    ProxyConfiguration pConfiguration = new ProxyConfiguration(qName, iConfigMap, null, messageFactory);
                    proxyFactory.setBusinessInterface(serviceContract.getInterface());
                    proxyFactory.setProxyConfiguration(pConfiguration);
                    config.addTargetProxyFactory(service.getName(), proxyFactory);
                    configuredService.setProxyFactory(proxyFactory);
                    if (referenceBuilder != null) {
                        // invoke the reference builder to handle target-side metadata
                        referenceBuilder.build(configuredService, parentContext);
                    }
                    // add tail interceptor
                    for (InvocationConfiguration iConfig : (Collection<InvocationConfiguration>) iConfigMap.values()) {
                        iConfig.addTargetInterceptor(new InvokerInterceptor());
                    }

                }

            } catch (BuilderException e) {
                e.addContextName(externalService.getName());
                e.addContextName(parentContext.getName());
                throw e;
            }
        }
    }

}
