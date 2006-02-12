/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.container.java.builder;

import org.apache.tuscany.container.java.config.JavaComponentRuntimeConfiguration;
import org.apache.tuscany.container.java.handler.ScopedJavaComponentInvoker;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.model.types.java.JavaOperationType;
import org.osoa.sca.annotations.Scope;

/**
 * Completes a wire to a Java-based target component by adding a scoped java invoker to the source chain
 * 
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class JavaTargetWireBuilder implements WireBuilder {

    public JavaTargetWireBuilder() {
    }

    public void wire(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope, ScopeContext targetScopeContext)
            throws BuilderConfigException {
        if (!(JavaComponentRuntimeConfiguration.class.isAssignableFrom(targetType))) {
            return;
        }
//        for (ProxyFactory sourceFactory : ((Map<String, ProxyFactory>) source.getSourceProxyFactories()).values()) {
            for (InvocationConfiguration sourceInvocationConfig : sourceFactory.getProxyConfiguration()
                    .getInvocationConfigurations().values()) {
                ScopedJavaComponentInvoker invoker = new ScopedJavaComponentInvoker(sourceFactory.getProxyConfiguration()
                        .getTargetName(), ((JavaOperationType) sourceInvocationConfig.getOperationType()).getJavaMethod(),
                        targetScopeContext);
                if (downScope) {
                    invoker.setCacheable(true);
                } else {
                    invoker.setCacheable(false);
                }
                sourceInvocationConfig.setTargetInvoker(invoker);
            }
//        }

    }
}
