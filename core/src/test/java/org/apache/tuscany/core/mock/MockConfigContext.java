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

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.builder.impl.AssemblyVisitor;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.model.assembly.Extensible;

/**
 *  A mock configuration context
 * 
 * @version $Rev$ $Date$
 */
public class MockConfigContext implements ConfigurationContext {

    private List<RuntimeConfigurationBuilder> builders = new ArrayList();

    public MockConfigContext(List<RuntimeConfigurationBuilder> builders) {
        this.builders=builders;
    }

    public void configure(Extensible model) throws ConfigurationException {
    }

    public void build(AggregateContext parent, Extensible model) throws BuilderConfigException {
        AssemblyVisitor visitor = new AssemblyVisitor(parent, builders);
        visitor.start(model);
    }

    public void wire(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope, ScopeContext targetScopeContext) throws BuilderConfigException {
    }

}
