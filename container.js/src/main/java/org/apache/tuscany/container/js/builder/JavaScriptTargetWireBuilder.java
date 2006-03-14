/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.container.js.builder;

import java.lang.reflect.Method;

import org.apache.tuscany.container.js.config.JavaScriptContextFactory;
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
        if (!(JavaScriptContextFactory.class.isAssignableFrom(targetType))) {
            return;
        }
        for (InvocationConfiguration sourceInvocationConfig : sourceFactory.getProxyConfiguration().getInvocationConfigurations()
                .values()) {
            Method method = sourceInvocationConfig.getMethod();
            String serviceName = sourceFactory.getProxyConfiguration().getTargetName().getPartName();
            RhinoTargetInvoker invoker = new RhinoTargetInvoker(serviceName, method, targetScopeContext);
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
        if (!(JavaScriptContextFactory.class.isAssignableFrom(targetType))) {
            return;
        }
        for (InvocationConfiguration targetInvocationConfig : targetFactory.getProxyConfiguration().getInvocationConfigurations()
                .values()) {
            Method method = targetInvocationConfig.getMethod();
            String serviceName = targetFactory.getProxyConfiguration().getTargetName().getPartName();
            RhinoTargetInvoker invoker = new RhinoTargetInvoker(serviceName, method, targetScopeContext);
            targetInvocationConfig.setTargetInvoker(invoker);
        }
    }

}
