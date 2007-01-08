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
import java.lang.reflect.Method;

import org.osoa.sca.annotations.EndConversation;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.integration.mock.MockFactory;
import org.apache.tuscany.core.wire.jdk.JDKOutboundInvocationHandler;
import org.easymock.classextension.EasyMock;

/**
 * Verifies conversational resources are cleaned up if the maximum idle time is exceeded
 *
 * @version $Rev$ $Date$
 */
public class ConversationIdleExpireTestCase extends AbstractConversationTestCase {
    private JDKOutboundInvocationHandler handler;
    private OutboundWire owire;
    private Foo targetInstance;
    private Method operation1;
    private Method operation2;
    private final Object mutex = new Object();

    public void testConversationExpire() throws Throwable {
        workContext.setIdentifier(org.apache.tuscany.spi.model.Scope.CONVERSATION, "12345A");
        // start the conversation
        handler.invoke(operation1, null);
        // verify the instance was persisted
        assertEquals(targetInstance, store.readRecord(target, "12345A"));
        synchronized (mutex) {
            mutex.wait(100);
        }
        // verify the instance was expired
        assertNull(store.readRecord(target, "12345A"));
        // continue the conversation - should throw an error
        try {
            handler.invoke(operation2, null);
            fail();
        } catch (TargetNotFoundException e) {
            // expected
        }
    }


    protected void setUp() throws Exception {
        super.setUp();
        createRuntime();
        store.setReaperInterval(10);
        initializeRuntime();

        targetInstance = EasyMock.createMock(Foo.class);
        targetInstance.operation1();
        targetInstance.operation2();
        targetInstance.end();
        EasyMock.replay(targetInstance);
        target = createMaxIdleTimeAtomicComponent();
        // create source component mock
        JavaAtomicComponent source = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(source.getName()).andReturn("source").atLeastOnce();
        EasyMock.expect(source.isSystem()).andReturn(false).atLeastOnce();
        EasyMock.replay(source);

        owire = MockFactory.createOutboundWire("foo", Foo.class);
        owire.setContainer(source);
        owire.setTargetName(new QualifiedName("foo/bar"));
        InboundWire iwire = MockFactory.createInboundWire("foo", Foo.class);
        iwire.setContainer(target);
        connector.connect(owire, iwire, false);
        handler = new JDKOutboundInvocationHandler(Foo.class, owire, workContext);
        operation1 = Foo.class.getMethod("operation1");
        operation2 = Foo.class.getMethod("operation2");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        container.stop();
        store.destroy();
    }

    private JavaAtomicComponent createMaxIdleTimeAtomicComponent() throws Exception {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setName("target");
        configuration.setMaxIdleTime(50);
        configuration.setInstanceFactory(new MockPojoFactory(Object.class.getConstructor()));
        JavaAtomicComponent component = new JavaAtomicComponent(configuration);
        component.setScopeContainer(container);
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
