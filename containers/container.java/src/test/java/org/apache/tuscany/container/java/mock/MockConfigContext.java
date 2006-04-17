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
package org.apache.tuscany.container.java.mock;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.builder.impl.AssemblyVisitorImpl;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.wire.ProxyFactory;
import org.apache.tuscany.model.assembly.AssemblyObject;

/**
 *  A mock configuration context
 * 
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 */
public class MockConfigContext implements ConfigurationContext {

    private List<ContextFactoryBuilder> builders;

    private DefaultWireBuilder wireBuilder = new DefaultWireBuilder();

    public MockConfigContext(List<ContextFactoryBuilder> builders, List<WireBuilder> wireBuilders) {
        this.builders = (builders == null) ? new ArrayList(1) : builders;
        if (wireBuilders != null){
            for (WireBuilder builder : wireBuilders) {
                wireBuilder.addWireBuilder(builder);
            }
        }
    }

    public void build(AssemblyObject model) throws BuilderConfigException {
        AssemblyVisitorImpl visitor = new AssemblyVisitorImpl(builders);
        visitor.start(model);
    }

    public void connect(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope,
                        ScopeContext targetScopeContext) throws BuilderConfigException {
        wireBuilder.connect(sourceFactory, targetFactory, targetType, downScope, targetScopeContext);
    }

    public void completeTargetChain(ProxyFactory targetFactory, Class targetType, ScopeContext targetScopeContext) throws BuilderConfigException {
        wireBuilder.completeTargetChain(targetFactory, targetType, targetScopeContext);
    }

}
