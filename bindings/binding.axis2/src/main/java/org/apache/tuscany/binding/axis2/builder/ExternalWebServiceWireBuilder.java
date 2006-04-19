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
package org.apache.tuscany.binding.axis2.builder;

import org.apache.tuscany.binding.axis2.config.ExternalWebServiceContextFactory;
import org.apache.tuscany.binding.axis2.handler.ExternalWebServiceTargetInvoker;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.wire.InvocationConfiguration;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetWireFactory;
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

    @SuppressWarnings("deprecation")
    @Init(eager = true)
    public void init() {
        runtimeContext.addBuilder(this);
    }

    public void connect(SourceWireFactory<?> sourceFactory,
                        TargetWireFactory<?> targetFactory,
                        Class targetType, boolean downScope,
                        ScopeContext targetScopeContext) throws BuilderConfigException {
        
        if (!(ExternalWebServiceContextFactory.class.isAssignableFrom(targetType))) {
            return;
        }
        for (InvocationConfiguration sourceInvocationConfig
            : sourceFactory.getConfiguration().getInvocationConfigurations().values()) {
            
            ExternalWebServiceTargetInvoker invoker
                = new ExternalWebServiceTargetInvoker(sourceFactory.getConfiguration().getTargetName(),
                                                      sourceInvocationConfig.getMethod(),
                                                      targetScopeContext);
            
            // if (downScope) {
            // // the source scope is shorter than the target, so the invoker can cache the target instance
            // invoker.setCacheable(true);
            // } else {
            // invoker.setCacheable(false);
            // }
            sourceInvocationConfig.setTargetInvoker(invoker);
        }

    }

    public void completeTargetChain(TargetWireFactory<?> targetFactory,
                                    Class targetType,
                                    ScopeContext targetScopeContext)
        throws BuilderConfigException {
        
        //TODO implement
    }

}
