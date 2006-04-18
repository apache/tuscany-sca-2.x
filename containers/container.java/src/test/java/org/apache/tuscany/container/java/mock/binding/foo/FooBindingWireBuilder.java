package org.apache.tuscany.container.java.mock.binding.foo;

import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.wire.SourceInvocationConfiguration;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.osoa.sca.annotations.Init;

public class FooBindingWireBuilder implements WireBuilder {

    public FooBindingWireBuilder() {
        super();
    }
    
    private RuntimeContext runtimeContext;

    @Autowire
    public void setRuntimeContext(RuntimeContext context) {
        runtimeContext = context;
    }

    @Init(eager=true)
    public void init() {
        runtimeContext.addBuilder(this);
    }


    public void connect(SourceWireFactory sourceFactory, TargetWireFactory targetFactory, Class targetType, boolean downScope,
            ScopeContext targetScopeContext) throws BuilderConfigException {
        if (!FooExternalServiceContextFactory.class.isAssignableFrom(targetType)) {
            return;
        }
        for (SourceInvocationConfiguration sourceInvocationConfig : sourceFactory.getProxyConfiguration().getInvocationConfigurations()
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

    public void completeTargetChain(TargetWireFactory targetFactory, Class targetType, ScopeContext targetScopeContext)
            throws BuilderConfigException {
        // TODO implement
    }

}
