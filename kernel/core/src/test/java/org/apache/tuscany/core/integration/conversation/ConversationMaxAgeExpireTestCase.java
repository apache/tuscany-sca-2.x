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
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.integration.mock.MockFactory;
import org.easymock.classextension.EasyMock;

/**
 * Verifies conversational resources are cleaned up if the maximum age is exceeded
 *
 * @version $Rev$ $Date$
 */
public class ConversationMaxAgeExpireTestCase extends AbstractConversationTestCase {
    private OutboundWire owire;
    private ConversationMaxAgeExpireTestCase.Foo targetInstance;
    private final Object mutex = new Object();

    public void testConversationExpire() throws Exception {
        workContext.setIdentifier(org.apache.tuscany.spi.model.Scope.CONVERSATION, "12345A");
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
        synchronized (mutex) {
            mutex.wait(100);
        }
        // verify the instance was expired
        assertNull(store.readRecord(target, "12345A"));

        // continue the conversation - should throw an error
        for (Map.Entry<Operation<?>, OutboundInvocationChain> entry : owire.getInvocationChains().entrySet()) {
            if ("operation2".equals(entry.getKey().getName())) {
                MessageImpl msg = new MessageImpl();
                msg.setTargetInvoker(entry.getValue().getTargetInvoker());
                try {
                    entry.getValue().getHeadInterceptor().invoke(msg);
                    fail();
                } catch (TargetNotFoundException e) {
                    // expected
                }
            }
        }
    }


    protected void setUp() throws Exception {
        super.setUp();
        createRuntime();
        store.setReaperInterval(10);
        initializeRuntime();

        targetInstance = EasyMock.createMock(ConversationMaxAgeExpireTestCase.Foo.class);
        targetInstance.operation1();
        targetInstance.operation2();
        targetInstance.end();
        EasyMock.replay(targetInstance);
        target = createMaxIdleTimeAtomicComponent();
        // create source component mock
        JavaAtomicComponent source = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(source.getName()).andReturn("source");
        EasyMock.replay(source);

        owire = MockFactory.createOutboundWire("foo", ConversationMaxAgeExpireTestCase.Foo.class);
        owire.setContainer(source);
        owire.setTargetName(new QualifiedName("foo/bar"));
        InboundWire iwire = MockFactory.createInboundWire("foo", ConversationMaxAgeExpireTestCase.Foo.class);
        iwire.setContainer(target);
        connector.connect(owire, iwire, false);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        container.stop();
        store.destroy();
    }

    private JavaAtomicComponent createMaxIdleTimeAtomicComponent() throws Exception {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setName("target");
        configuration.setMaxAge(50);
        configuration.setScopeContainer(container);
        Constructor<Object> ctor = Object.class.getConstructor();
        configuration.setInstanceFactory(new ConversationMaxAgeExpireTestCase.MockPojoFactory(ctor));
        JavaAtomicComponent component = new JavaAtomicComponent(configuration);
        component.start();
        return component;
    }

    private class MockPojoFactory extends PojoObjectFactory<Object> {
        public MockPojoFactory(Constructor<Object> ctr) {
            super(ctr);
        }

        public ConversationMaxAgeExpireTestCase.Foo getInstance() throws ObjectCreationException {
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
