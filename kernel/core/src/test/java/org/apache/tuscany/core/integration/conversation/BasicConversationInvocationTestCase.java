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

import java.lang.reflect.Constructor;
import java.util.Map;

import org.osoa.sca.annotations.EndConversation;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Operation;
import static org.apache.tuscany.spi.model.Scope.CONVERSATION;
import org.apache.tuscany.spi.services.store.StoreMonitor;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WirePostProcessorRegistry;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.builder.WirePostProcessorRegistryImpl;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.scope.ConversationalScopeContainerImpl;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.integration.mock.MockFactory;
import org.apache.tuscany.core.services.store.memory.MemoryStore;
import org.apache.tuscany.core.wire.ConversationWirePostProcessor;
import org.easymock.classextension.EasyMock;

/**
 * Provides fine-grained integration-level testing for conversational invocation sequences using a partial runtime
 *
 * @version $Rev$ $Date$
 */
public class BasicConversationInvocationTestCase extends TestCase {
    private ScopeContainer container;
    private MemoryStore store;
    private WorkContext workContext;
    private ConnectorImpl connector;
    private OutboundWire owire;
    private JavaAtomicComponent target;
    private Foo targetInstance;


    /**
     * Verifies start, continue and end conversation invocations are processed properly. Checks that a target instance
     * is properly instantiated and persisted in the store. Additionally verifies that invocations are dispatched to a
     * target instance, and that start, continue, and end operations are performed correctly. Finally, verfies that the
     * target instance is removed from the store when the conversation ends.
     *
     * @throws Exception
     */
    public void testConversationStartContinueEnd() throws Exception {
        workContext.setIdentifier(CONVERSATION, "12345A");
        // start the conversation
        for (Map.Entry<Operation<?>, OutboundInvocationChain> entry : owire.getInvocationChains().entrySet()) {
            if ("operation1".equals(entry.getKey().getName())) {
                MessageImpl msg = new MessageImpl();
                msg.setTargetInvoker(entry.getValue().getTargetInvoker());
                entry.getValue().getHeadInterceptor().invoke(msg);
            }
        }
        // verify the instance was persisted
        assertEquals(targetInstance, store.readRecord(target, "12345A"));
        // continue the conversation
        for (Map.Entry<Operation<?>, OutboundInvocationChain> entry : owire.getInvocationChains().entrySet()) {
            if ("operation2".equals(entry.getKey().getName())) {
                MessageImpl msg = new MessageImpl();
                msg.setTargetInvoker(entry.getValue().getTargetInvoker());
                entry.getValue().getHeadInterceptor().invoke(msg);
            }
        }
        // verify the instance was persisted
        assertEquals(targetInstance, store.readRecord(target, "12345A"));
        // end the conversation
        for (Map.Entry<Operation<?>, OutboundInvocationChain> entry : owire.getInvocationChains().entrySet()) {
            if ("end".equals(entry.getKey().getName())) {
                MessageImpl msg = new MessageImpl();
                msg.setTargetInvoker(entry.getValue().getTargetInvoker());
                entry.getValue().getHeadInterceptor().invoke(msg);
            }
        }
        workContext.clearIdentifier(CONVERSATION);
        EasyMock.verify(targetInstance);
        // verify the store has removed the instance
        assertNull(store.readRecord(target, "12345A"));
    }


    protected void setUp() throws Exception {
        super.setUp();
        bootRuntime();
        targetInstance = EasyMock.createMock(Foo.class);
        targetInstance.operation1();
        targetInstance.operation2();
        targetInstance.end();
        EasyMock.replay(targetInstance);
        // create target component mock
        target = createAtomicComponent();
        // create source component mock
        JavaAtomicComponent source = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(source.getName()).andReturn("source");
        EasyMock.replay(source);

        owire = MockFactory.createOutboundWire("foo", Foo.class);
        owire.setContainer(source);
        owire.setTargetName(new QualifiedName("foo/bar"));
        InboundWire iwire = MockFactory.createInboundWire("foo", Foo.class);
        iwire.setContainer(target);
        connector.connect(owire, iwire, false);

    }

    protected void tearDown() throws Exception {
        super.tearDown();
        container.stop();
        store.destroy();
    }

    private void bootRuntime() {
        workContext = new WorkContextImpl();
        WirePostProcessorRegistry processorRegistry = new WirePostProcessorRegistryImpl();
        ConversationWirePostProcessor processor = new ConversationWirePostProcessor();
        processor.setRegistry(processorRegistry);
        processor.init();
        connector = new ConnectorImpl(null, processorRegistry, null, workContext);
        store = new MemoryStore(EasyMock.createNiceMock(StoreMonitor.class));
        store.init();
        container = new ConversationalScopeContainerImpl(store, workContext);
        container.start();

    }

    private JavaAtomicComponent createAtomicComponent() throws Exception {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setName("target");
        configuration.setScopeContainer(container);
        configuration.setInstanceFactory(new MockPojoFactory(Object.class.getConstructor()));
        JavaAtomicComponent component = new JavaAtomicComponent(configuration);
        component.start();
        return component;
    }

    private class MockPojoFactory extends PojoObjectFactory<Object> {
        public MockPojoFactory(Constructor<Object> ctr) {
            super(ctr);
        }

        public Foo getInstance() throws ObjectCreationException {
            return targetInstance;
        }
    }

    @Scope("CONVERSATION")
    public static interface Foo {

        void operation1();

        void operation2();

        @EndConversation
        void end();

    }

}
