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
package org.apache.tuscany.container.java.mock.binding.foo;

import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.wire.SourceInvocationConfiguration;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.core.wire.TargetInvoker;
import org.apache.tuscany.core.wire.TargetWireFactory;
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

    @Init(eager = true)
    public void init() {
        runtimeContext.addBuilder(this);
    }


    public void connect(SourceWireFactory sourceFactory, TargetWireFactory targetFactory, Class targetType, boolean downScope,
                        ScopeContext targetScopeContext) throws BuilderConfigException {
        if (!FooExternalServiceContextFactory.class.isAssignableFrom(targetType)) {
            return;
        }
        for (SourceInvocationConfiguration sourceInvocationConfig : sourceFactory.getConfiguration().getInvocationConfigurations()
                .values()) {
            FooExternalServiceTargetInvoker invoker = new FooExternalServiceTargetInvoker(sourceFactory.getConfiguration().getTargetName()
                    .getPartName());
            sourceInvocationConfig.setTargetInvoker(invoker);
        }

    }

    public void completeTargetChain(TargetWireFactory targetFactory, Class targetType, ScopeContext targetScopeContext)
            throws BuilderConfigException {
        if (FooExternalServiceContextFactory.class.isAssignableFrom(targetType)) {
            for (TargetInvocationConfiguration targetInvocationConfig : targetFactory.getConfiguration().getInvocationConfigurations()
                    .values()) {
                TargetInvoker invoker = new FooExternalServiceTargetInvoker(targetFactory.getConfiguration().getTargetName().getQualifiedName());
                targetInvocationConfig.setTargetInvoker(invoker);
            }

        }
    }

}
