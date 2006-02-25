package org.apache.tuscany.container.java.mock.binding.foo;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.core.invocation.spi.ProxyFactoryFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.ExternalService;
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
        if (!(object instanceof ExternalService)){
            return;
        }
        ExternalService es = (ExternalService)object;
        if (es.getBindings().size() <1 || !(es.getBindings().get(0) instanceof FooBinding)){
            return;
        }
        FooExternalServiceRuntimeConfiguration rc = new FooExternalServiceRuntimeConfiguration(es.getName(),new FooClientFactory());
        es.getBindings().get(0).setRuntimeConfiguration(rc);
    }

    private class FooClientFactory implements ObjectFactory{

        public Object getInstance() throws ObjectCreationException {
            return new FooClient();
        }
    }
}
