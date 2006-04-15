package org.apache.tuscany.binding.axis2.builder;

import org.apache.tuscany.binding.axis2.config.ExternalWebServiceContextFactory;
import org.apache.tuscany.binding.axis2.handler.ExternalWebServiceTargetInvoker;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.wire.InvocationConfiguration;
import org.apache.tuscany.core.wire.ProxyFactory;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

@Scope("MODULE")
public class ExternalWebServiceWireBuilder implements WireBuilder {

    private RuntimeContext runtimeContext;

    /**
     * Constructs a new ExternalWebServiceWireBuilder.
     */
    public ExternalWebServiceWireBuilder() {
        super();
    }

    @Autowire
    public void setRuntimeContext(RuntimeContext context) {
        runtimeContext = context;
    }

    @Init(eager=true)
    public void init() {
        runtimeContext.addBuilder(this);
    }

    public void connect(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope, ScopeContext targetScopeContext) throws BuilderConfigException {
        if (!(ExternalWebServiceContextFactory.class.isAssignableFrom(targetType))) {
            return;
        }
        for (InvocationConfiguration sourceInvocationConfig : sourceFactory.getProxyConfiguration().getInvocationConfigurations().values()) {
            
            ExternalWebServiceTargetInvoker invoker = new ExternalWebServiceTargetInvoker(sourceFactory.getProxyConfiguration().getTargetName(), sourceInvocationConfig.getMethod(), targetScopeContext);
            
            // if (downScope) {
            // // the source scope is shorter than the target, so the invoker can cache the target instance
            // invoker.setCacheable(true);
            // } else {
            // invoker.setCacheable(false);
            // }
            sourceInvocationConfig.setTargetInvoker(invoker);
        }

    }

    public void completeTargetChain(ProxyFactory targetFactory, Class targetType, ScopeContext targetScopeContext)
            throws BuilderConfigException {
        //TODO implement
    }

}
