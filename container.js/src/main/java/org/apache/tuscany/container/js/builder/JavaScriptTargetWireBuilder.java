package org.apache.tuscany.container.js.builder;

import java.lang.reflect.Method;

import org.apache.tuscany.container.js.config.JavaScriptComponentRuntimeConfiguration;
import org.apache.tuscany.container.js.rhino.RhinoTargetInvoker;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

/**
 * Responsible for bridging source- and target-side invocations chains when the target type is a JavaScript
 * implementation
 * 
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class JavaScriptTargetWireBuilder implements WireBuilder {

    private RuntimeContext runtimeContext;

    @Autowire
    public void setRuntimeContext(RuntimeContext context) {
        runtimeContext = context;
    }

    public JavaScriptTargetWireBuilder() {
    }

    @Init(eager=true)
    public void init() {
        runtimeContext.addBuilder(this);
    }

    public void connect(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope,
            ScopeContext targetScopeContext) throws BuilderConfigException {
        if (!(JavaScriptComponentRuntimeConfiguration.class.isAssignableFrom(targetType))) {
            return;
        }
        for (InvocationConfiguration sourceInvocationConfig : sourceFactory.getProxyConfiguration().getInvocationConfigurations()
                .values()) {
            Method method = sourceInvocationConfig.getMethod();
            RhinoTargetInvoker invoker = new RhinoTargetInvoker(sourceFactory.getProxyConfiguration().getTargetName()
                    .getPartName(), method.getName(), targetScopeContext);
            if (downScope) {
                // the source scope is shorter than the target, so the invoker can cache the target instance
                // invoker.setCacheable(true);
            } else {
                // invoker.setCacheable(false);
            }
            sourceInvocationConfig.setTargetInvoker(invoker);
        }
    }

    public void completeTargetChain(ProxyFactory targetFactory, Class targetType, ScopeContext targetScopeContext)
            throws BuilderConfigException {
        if (!(JavaScriptComponentRuntimeConfiguration.class.isAssignableFrom(targetType))) {
            return;
        }
        for (InvocationConfiguration targetInvocationConfig : targetFactory.getProxyConfiguration().getInvocationConfigurations()
                .values()) {
            Method method = targetInvocationConfig.getMethod();
            RhinoTargetInvoker invoker = new RhinoTargetInvoker(targetFactory.getProxyConfiguration().getTargetName()
                    .getPartName(), method.getName(), targetScopeContext);
            targetInvocationConfig.setTargetInvoker(invoker);
        }
    }

}
