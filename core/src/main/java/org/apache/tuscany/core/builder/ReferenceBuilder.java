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
package org.apache.tuscany.core.builder;

import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactoryFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

/**
 * Builds the source and target sides of wires for a component
 * 
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class ReferenceBuilder implements RuntimeConfigurationBuilder<AggregateContext> {

    private ProxyFactoryFactory factory;

    private RuntimeContext runtimeContext;

    private AggregateContext parentContext;

    private AssemblyModelObject modelObject;

    private MessageFactory msgFactory;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public ReferenceBuilder() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    @Autowire
    public void setRuntimeContext(RuntimeContext ctx) {
        runtimeContext = ctx;
    }

    @Init(eager = true)
    public void init() {
        runtimeContext.addBuilder(this);
    }

    public void setModelObject(AssemblyModelObject modelObject) {
        this.modelObject = modelObject;
    }

    public void setParentContext(AggregateContext context) {
        parentContext = context;
    }

    public void build() throws BuilderException {
        if (!(modelObject instanceof ConfiguredReference) && (!(modelObject instanceof ConfiguredService))) {
            return; // FIXME support external service
        }
        if (modelObject instanceof ConfiguredReference) {
            ConfiguredReference configuredReference = (ConfiguredReference) modelObject;
            ProxyFactory proxyFactory = (ProxyFactory) configuredReference.getProxyFactory();
            // Do some magic here
        } else {
            ConfiguredService configuredService = (ConfiguredService) modelObject;
            ProxyFactory proxyFactory = (ProxyFactory) configuredService.getProxyFactory();
            // Do some magic here
        }
    }
}
