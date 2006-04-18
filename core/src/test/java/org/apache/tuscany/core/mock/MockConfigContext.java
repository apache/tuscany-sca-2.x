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
package org.apache.tuscany.core.mock;

import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.impl.AssemblyVisitorImpl;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.model.assembly.AssemblyObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A mock configuration context
 *
 * @version $Rev$ $Date$
 */
public class MockConfigContext implements ConfigurationContext {

    private List<ContextFactoryBuilder> builders = new ArrayList<ContextFactoryBuilder>();

    public MockConfigContext(List<ContextFactoryBuilder> builders) {
        this.builders = builders;
    }

    public void build(AssemblyObject model) throws BuilderConfigException {
        AssemblyVisitorImpl visitor = new AssemblyVisitorImpl(builders);
        visitor.start(model);
    }

    public void connect(SourceWireFactory sourceFactory, TargetWireFactory targetFactory, Class targetType, boolean downScope,
            ScopeContext targetScopeContext) throws BuilderConfigException {
    }

    public void completeTargetChain(TargetWireFactory targetFactory, Class targetType, ScopeContext targetScopeContext) throws BuilderConfigException {
    }

}
