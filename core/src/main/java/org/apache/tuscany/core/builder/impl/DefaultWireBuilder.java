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
package org.apache.tuscany.core.builder.impl;

import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.HierarchicalWireBuilder;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.impl.InvokerInterceptor;
import org.apache.tuscany.core.invocation.impl.MessageChannelImpl;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The top-most <code>WireBuilder</code> configured in a runtime. Responsible for constructing wires from source and target chains,
 * this implementation first bridges the chains and then delegates to any other wire builders.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultWireBuilder implements HierarchicalWireBuilder {

    // collection configured wire builders
    private List<WireBuilder> builders = new ArrayList<WireBuilder>();

    public DefaultWireBuilder() {
    }

    /**
     * Adds a wire builder to delegate to
     */
    public void addWireBuilder(WireBuilder builder) {
        builders.add(builder);
    }

    public void setWireBuilders(List<WireBuilder> builders) {
        builders.addAll(builders);
    }

    public void connect(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope,
            ScopeContext targetScopeContext) {
        // get the proxy chain for the target
        if (targetFactory != null) {
            // if null, the target side has no interceptors or handlers
            Map<Method, InvocationConfiguration> targetInvocationConfigs = targetFactory.getProxyConfiguration()
                    .getInvocationConfigurations();
            for (InvocationConfiguration sourceInvocationConfig : sourceFactory.getProxyConfiguration()
                    .getInvocationConfigurations().values()) {
                // match invocation chains
                InvocationConfiguration targetInvocationConfig = targetInvocationConfigs.get(sourceInvocationConfig.getMethod());
                if (targetInvocationConfig == null){
                    BuilderConfigException e= new BuilderConfigException("Incompatible source and target interface types for reference");
                    e.setIdentifier(sourceFactory.getProxyConfiguration().getReferenceName());
                    throw e;
                }
                // if handler is configured, add that
                if (targetInvocationConfig.getRequestHandlers() != null) {
                    sourceInvocationConfig.setTargetRequestChannel(new MessageChannelImpl(targetInvocationConfig
                            .getRequestHandlers()));
                    sourceInvocationConfig.setTargetResponseChannel(new MessageChannelImpl(targetInvocationConfig
                            .getResponseHandlers()));
                } else {
                    // no handlers, just connect interceptors
                    if (targetInvocationConfig.getTargetInterceptor() == null) {
                        BuilderConfigException e = new BuilderConfigException("No target handler or interceptor for operation");
                        e.setIdentifier(targetInvocationConfig.getMethod().getName());
                        throw e;
                    }
                    if (!(sourceInvocationConfig.getLastTargetInterceptor() instanceof InvokerInterceptor && targetInvocationConfig
                            .getTargetInterceptor() instanceof InvokerInterceptor)) {
                        // check that we do not have the case where the only interceptors are invokers since we just need one
                        sourceInvocationConfig.addTargetInterceptor(targetInvocationConfig.getTargetInterceptor());
                    }
                }
            }
        }
        // delegate to other wire builders
        for (WireBuilder builder : builders) {
            builder.connect(sourceFactory, targetFactory, targetType, downScope, targetScopeContext);
        }
        // signal that wire build process is complete
        for (InvocationConfiguration sourceInvocationConfig : sourceFactory.getProxyConfiguration().getInvocationConfigurations()
                .values()) {
            sourceInvocationConfig.build();
            // TODO optimize if no proxy needed using NullProxyFactory
        }
    }

    public void completeTargetChain(ProxyFactory targetFactory, Class targetType, ScopeContext targetScopeContext)
            throws BuilderConfigException {
        // delegate to other wire builders
        for (WireBuilder builder : builders) {
            builder.completeTargetChain(targetFactory, targetType, targetScopeContext);
        }
    }

}
