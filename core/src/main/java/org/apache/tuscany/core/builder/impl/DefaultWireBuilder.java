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

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.wire.TargetWireFactory;
import org.apache.tuscany.core.builder.HierarchicalWireBuilder;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.core.wire.SourceInvocationConfiguration;
import org.apache.tuscany.spi.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.impl.InvokerInterceptor;
import org.apache.tuscany.core.wire.impl.MessageChannelImpl;

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

    public void connect(SourceWireFactory<?> sourceFactory, TargetWireFactory<?> targetFactory, Class targetType, boolean downScope,
                        ScopeContext targetScopeContext) {
        // get the proxy chain for the target
        if (targetFactory != null) {
            // if null, the target side has no interceptors or handlers
            Map<Method, TargetInvocationConfiguration> targetInvocationConfigs = targetFactory.getConfiguration().getInvocationConfigurations();
            for (SourceInvocationConfiguration sourceInvocationConfig : sourceFactory.getConfiguration()
                    .getInvocationConfigurations().values()) {
                // match wire chains
                TargetInvocationConfiguration targetInvocationConfig = targetInvocationConfigs.get(sourceInvocationConfig.getMethod());
                if (targetInvocationConfig == null){
                    BuilderConfigException e= new BuilderConfigException("Incompatible source and target interface types for reference");
                    //FIXME xcv
                    e.setIdentifier(sourceFactory.getConfiguration().getReferenceName());
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
                    if (targetInvocationConfig.getHeadInterceptor() == null) {
                        BuilderConfigException e = new BuilderConfigException("No target handler or interceptor for operation");
                        e.setIdentifier(targetInvocationConfig.getMethod().getName());
                        throw e;
                    }
                    //xcv if (!(sourceInvocationConfig.getLastTargetInterceptor() instanceof InvokerInterceptor && targetInvocationConfig
                    if (!(sourceInvocationConfig.getTailInterceptor() instanceof InvokerInterceptor && targetInvocationConfig
                            .getHeadInterceptor() instanceof InvokerInterceptor)) {
                        // check that we do not have the case where the only interceptors are invokers since we just need one
                        sourceInvocationConfig.setTargetInterceptor(targetInvocationConfig.getHeadInterceptor());
                    }
                }
            }
        }
        // delegate to other wire builders
        for (WireBuilder builder : builders) {
            builder.connect(sourceFactory, targetFactory, targetType, downScope, targetScopeContext);
        }
        // signal that wire buildSource process is complete
        for (SourceInvocationConfiguration sourceInvocationConfig : sourceFactory.getConfiguration().getInvocationConfigurations()
                .values()) {
            sourceInvocationConfig.build();
            // TODO optimize if no proxy needed using NullWireFactory
        }
    }

    public void completeTargetChain(TargetWireFactory targetFactory, Class targetType, ScopeContext targetScopeContext)
            throws BuilderConfigException {
        // delegate to other wire builders
        for (WireBuilder builder : builders) {
            builder.completeTargetChain(targetFactory, targetType, targetScopeContext);
        }
    }

}
