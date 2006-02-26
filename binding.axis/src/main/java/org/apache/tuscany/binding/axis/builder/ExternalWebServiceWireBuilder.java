package org.apache.tuscany.binding.axis.builder;

import org.apache.tuscany.binding.axis.config.ExternalWebServiceRuntimeConfiguration;
import org.apache.tuscany.binding.axis.handler.ExternalWebServiceTargetInvoker;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;

public class ExternalWebServiceWireBuilder implements WireBuilder {

    /**
     * Constructs a new ExternalWebServiceWireBuilder.
     */
    public ExternalWebServiceWireBuilder() {
        super();
    }

    public void connect(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope, ScopeContext targetScopeContext) throws BuilderConfigException {
        if (!(ExternalWebServiceRuntimeConfiguration.class.isAssignableFrom(targetType))) {
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
