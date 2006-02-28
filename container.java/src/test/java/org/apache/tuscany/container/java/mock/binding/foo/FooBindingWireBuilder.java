package org.apache.tuscany.container.java.mock.binding.foo;

import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;

public class FooBindingWireBuilder implements WireBuilder {

    public FooBindingWireBuilder() {
        super();
    }

    public void connect(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope,
            ScopeContext targetScopeContext) throws BuilderConfigException {
        if (!FooExternalServiceRuntimeConfiguration.class.isAssignableFrom(targetType)) {
            return;
        }
        for (InvocationConfiguration sourceInvocationConfig : sourceFactory.getProxyConfiguration().getInvocationConfigurations()
                .values()) {
            FooESTargetInvoker invoker = new FooESTargetInvoker(sourceFactory.getProxyConfiguration().getTargetName()
                    .getPartName(), targetScopeContext);
            sourceInvocationConfig.setTargetInvoker(invoker);
            // if (downScope) {
            // // the source scope is shorter than the target, so the invoker can cache the target instance
            // invoker.setCacheable(true);
            // } else {
            // invoker.setCacheable(false);
            // }
        }

    }

    public void completeTargetChain(ProxyFactory targetFactory, Class targetType, ScopeContext targetScopeContext)
            throws BuilderConfigException {
        // TODO implement
    }

}
