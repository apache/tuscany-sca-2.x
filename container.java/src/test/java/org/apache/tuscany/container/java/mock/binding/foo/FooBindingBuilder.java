package org.apache.tuscany.container.java.mock.binding.foo;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.impl.InvokerInterceptor;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactoryFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

@Scope("MODULE")
public class FooBindingBuilder implements RuntimeConfigurationBuilder {

    private RuntimeContext runtimeContext;

    private ProxyFactoryFactory proxyFactoryFactory;

    private MessageFactory messageFactory;

    private RuntimeConfigurationBuilder referenceBuilder;

    public FooBindingBuilder() {
    }

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

    public void build(AssemblyModelObject object, Context context) throws BuilderException {
        if (!(object instanceof ExternalService)) {
            return;
        }
        ExternalService es = (ExternalService) object;
        if (es.getBindings().size() < 1 || !(es.getBindings().get(0) instanceof FooBinding)) {
            return;
        }

        FooExternalServiceRuntimeConfiguration config = new FooExternalServiceRuntimeConfiguration(es.getName(),
                new FooClientFactory());

        ConfiguredService configuredService = es.getConfiguredService();
        Service service = configuredService.getService();
        ServiceContract serviceContract = service.getServiceContract();
        Map<Method, InvocationConfiguration> iConfigMap = new HashMap();
        ProxyFactory proxyFactory = proxyFactoryFactory.createProxyFactory();
        Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(serviceContract.getInterface());
        for (Method method : javaMethods) {
            InvocationConfiguration iConfig = new InvocationConfiguration(method);
            iConfigMap.put(method, iConfig);
        }
        QualifiedName qName = new QualifiedName(es.getName() + "/" + service.getName());
        ProxyConfiguration pConfiguration = new ProxyConfiguration(qName, iConfigMap, null, messageFactory);
        proxyFactory.setBusinessInterface(serviceContract.getInterface());
        proxyFactory.setProxyConfiguration(pConfiguration);
        config.addTargetProxyFactory(service.getName(), proxyFactory);
        configuredService.setProxyFactory(proxyFactory);
//        if (referenceBuilder != null) {
//            // invoke the reference builder to handle target-side metadata
//            referenceBuilder.build(configuredService, parentContext);
//        }
        // add tail interceptor
        for (InvocationConfiguration iConfig : (Collection<InvocationConfiguration>) iConfigMap.values()) {
            iConfig.addTargetInterceptor(new InvokerInterceptor());
        }

        es.getConfiguredService().setRuntimeConfiguration(config);
    }

    private class FooClientFactory implements ObjectFactory {

        public Object getInstance() throws ObjectCreationException {
            return new FooClient();
        }
    }
}
