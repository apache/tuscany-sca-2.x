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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.message.channel.impl.MessageChannelImpl;
import org.apache.tuscany.model.types.OperationType;
import org.osoa.sca.annotations.Scope;

/**
 * The top-most wire builder configured in a runtime. Responsible for constructing wires from source and target chains,
 * this implementation first bridges the chains and then delegates to any other wire builders.
 * 
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class DefaultWireBuilder implements WireBuilder {

    public DefaultWireBuilder() {
    }

    // other configured wire builders
    private List<WireBuilder> builders = new ArrayList();

    /**
     * Adds a wire builder to delegate to
     */
    public void addWireBuilder(WireBuilder builder) {
        builders.add(builder);
    }

    public void wire(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope,
            ScopeContext targetScopeContext) {
        QualifiedName targetName = sourceFactory.getProxyConfiguration().getTargetName();
        // get the proxy chain for the target
        if (targetFactory != null) {
            // if null, the target side has no interceptors or handlers
            Map<OperationType, InvocationConfiguration> targetInvocationConfigs = targetFactory.getProxyConfiguration()
                    .getInvocationConfigurations();
            for (InvocationConfiguration sourceInvocationConfig : sourceFactory.getProxyConfiguration()
                    .getInvocationConfigurations().values()) {
                // match invocation chains
                InvocationConfiguration targetInvocationConfig = targetInvocationConfigs.get(sourceInvocationConfig
                        .getOperationType());
                // if handler is configured, add that
                if (targetInvocationConfig.getRequestHandlers() != null) {
                    sourceInvocationConfig.addTargetRequestChannel(new MessageChannelImpl(targetInvocationConfig
                            .getRequestHandlers()));
                    sourceInvocationConfig.addTargetResponseChannel(new MessageChannelImpl(targetInvocationConfig
                            .getResponseHandlers()));
                } else {
                    // no handlers, just connect interceptors
                    sourceInvocationConfig.addTargetInterceptor(targetInvocationConfig.getTargetInterceptor());
                }
            }
        }
        // delegate to other wire builders
        for (WireBuilder builder : builders) {
            builder.wire(sourceFactory, targetFactory, targetType, downScope, targetScopeContext);
        }
        // signal that wire build process is complete
        boolean optimizable = true;
        for (InvocationConfiguration sourceInvocationConfig : sourceFactory.getProxyConfiguration().getInvocationConfigurations()
                .values()) {
            sourceInvocationConfig.build();
            // TODO optimize if no proxy needed using NullProxyFactory
        }
        //TODO initialize here?
        //try {
        //    sourceFactory.initialize();
        //} catch (Exception e) {
         //   e.printStackTrace(); FIXME
        //}
        //End init here
    }

}
