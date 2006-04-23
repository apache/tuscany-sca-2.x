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
package org.apache.tuscany.core.extension;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.builder.impl.EntryPointContextFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.service.WireFactoryService;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Service;
import org.osoa.sca.annotations.Init;

/**
 * A base class for a {@link ContextFactoryBuilder} that creates {@link org.apache.tuscany.core.context.EntryPointContext}s
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class EntryPointBuilderSupport implements ContextFactoryBuilder {

    protected ContextFactoryBuilderRegistry builderRegistry;
    protected WireFactoryService wireService;
    protected MessageFactory messageFactory;

    public EntryPointBuilderSupport() {
    }

    @Init(eager = true)
    public void init() throws Exception {
        builderRegistry.register(this);
    }

    @Autowire
    public void setBuilderRegistry(ContextFactoryBuilderRegistry registry) {
        builderRegistry = registry;
    }

    @Autowire
    public void setWireService(WireFactoryService wireService) {
        this.wireService = wireService;
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
        if (!(object instanceof EntryPoint)) {
            return;
        }
        EntryPoint entryPoint = (EntryPoint) object;
        if (entryPoint.getBindings().size() < 1
                || !(handlesBindingType(entryPoint.getBindings().get(0)))) {
            return;
        }

        EntryPointContextFactory contextFactory = createEntryPointContextFactory(entryPoint, messageFactory);
        ConfiguredService configuredService = entryPoint.getConfiguredService();
        Service service = configuredService.getPort();
        SourceWireFactory wireFactory = wireService.createSourceFactory(entryPoint.getConfiguredReference()).get(0);
        contextFactory.addSourceWireFactory(service.getName(), wireFactory);
        entryPoint.setContextFactory(contextFactory);
    }

    /**
     * Returns true if an extending implementation can process the given binding element
     */
    protected abstract boolean handlesBindingType(Binding binding);

    /**
     * Callback to create the specific <code>ContextFactory</code> type associated with the extending implementation
     * @param entryPoint the entry point being processed
     * @param msgFactory the message factory to be used by <code>EntryPointContext</code> when flowing invocations
     */
    protected abstract EntryPointContextFactory createEntryPointContextFactory(EntryPoint entryPoint, MessageFactory msgFactory);


}
