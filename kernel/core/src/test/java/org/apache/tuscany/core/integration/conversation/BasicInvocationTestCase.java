/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tuscany.core.integration.conversation;

import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.EndConversation;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.services.store.StoreMonitor;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.OutboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.scope.ConversationalScopeContainerImpl;
import org.apache.tuscany.core.services.store.memory.MemoryStore;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.wire.ConversationWirePostProcessor;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.easymock.classextension.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class BasicInvocationTestCase extends TestCase {
    private ScopeContainer container;
    private MemoryStore store;
    private WorkContext workContext;
    private JavaInterfaceProcessorRegistryImpl registry;
    private ServiceContract contract;
    private JavaAtomicComponent component;
    private ConversationWirePostProcessor processor;

    public void testConversationStartContinueEnd() throws Exception {
        OutboundWire owire = new OutboundWireImpl();
        // start, continue, end
    }


    protected void setUp() throws Exception {
        super.setUp();
        workContext = new WorkContextImpl();
        registry = new JavaInterfaceProcessorRegistryImpl();
        contract = registry.introspect(Foo.class);
        processor = new ConversationWirePostProcessor();
        store = new MemoryStore(EasyMock.createNiceMock(StoreMonitor.class));
        store.init();
        container = new ConversationalScopeContainerImpl(store, workContext);
        container.start();
        component = EasyMock.createMock(JavaAtomicComponent.class);

    }

    protected void tearDown() throws Exception {
        super.tearDown();
        container.stop();
        store.destroy();
    }

    @Scope("CONVERSATION")
    private interface Foo {

        void operation1();
        void operation2();

        @EndConversation
        void end();

    }
}
