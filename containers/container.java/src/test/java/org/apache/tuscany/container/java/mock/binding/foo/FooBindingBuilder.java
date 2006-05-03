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
package org.apache.tuscany.container.java.mock.binding.foo;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.extension.EntryPointContextFactory;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.service.WireFactoryService;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Service;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

/**
 * Creates a <code>ContextFactoryBuilder</code> for an entry point or external service configured with the {@link FooBinding}
 *
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class FooBindingBuilder implements ContextFactoryBuilder {
    private ContextFactoryBuilderRegistry builderRegistry;

    private MessageFactory messageFactory;

    private WireFactoryService wireFactoryService;

    public FooBindingBuilder(WireFactoryService wireFactoryService) {
        this.wireFactoryService = wireFactoryService;
    }

    public FooBindingBuilder() {
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.register(this);
    }

    @Autowire
    public void setBuilderRegistry(ContextFactoryBuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

    @Autowire
    public void setWireFactoryService(WireFactoryService wireFactoryService) {
        this.wireFactoryService = wireFactoryService;
    }

    
    /**
     * Sets the factory used to construct wire messages
     *
     * @param msgFactory
     */
    @Autowire
    public void setMessageFactory(MessageFactory msgFactory) {
        this.messageFactory = msgFactory;
    }

    public void build(AssemblyObject object) throws BuilderException {
        if (object instanceof EntryPoint) {
            EntryPoint ep = (EntryPoint) object;
            if (ep.getBindings().size() < 1 || !(ep.getBindings().get(0) instanceof FooBinding)) {
                return;
            }
            EntryPointContextFactory contextFactory = new FooEntryPointContextFactory(ep.getName(), messageFactory);
            ConfiguredService configuredService = ep.getConfiguredService();
            Service service = configuredService.getPort();
            SourceWireFactory wireFactory = wireFactoryService.createSourceFactory(ep.getConfiguredReference()).get(0);
            contextFactory.addSourceWireFactory(service.getName(), wireFactory);
            ep.setContextFactory(contextFactory);
        } else if (object instanceof ExternalService) {
            ExternalService es = (ExternalService) object;
            if (es.getBindings().size() < 1 || !(es.getBindings().get(0) instanceof FooBinding)) {
                return;
            }
            FooExternalServiceContextFactory contextFactory = new FooExternalServiceContextFactory(es.getName(),
                    new FooClientFactory());
            ConfiguredService configuredService = es.getConfiguredService();
            Service service = configuredService.getPort();
            TargetWireFactory wireFactory = wireFactoryService.createTargetFactory(configuredService);
            contextFactory.addTargetWireFactory(service.getName(), wireFactory);
            es.setContextFactory(contextFactory);
        }
    }

    private static class FooClientFactory implements ObjectFactory {

        public Object getInstance() throws ObjectCreationException {
            return new FooClient();
        }
    }
}
