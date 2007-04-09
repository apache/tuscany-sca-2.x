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
import java.net.URI;

import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.integration.mock.MockFactory;
import org.apache.tuscany.core.wire.jdk.JDKInvocationHandler;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;
import org.easymock.EasyMock;
import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;

/**
 * Verifies conversational resources are cleaned up if the maximum age is exceeded
 *
 * @version $Rev$ $Date$
 */
public abstract class ConversationMaxAgeExpireTestCase extends AbstractConversationTestCase {
    protected AtomicComponent target;
    private JDKInvocationHandler handler;
    private FooImpl targetInstance;
    private Method operation1;
    private Method operation2;
    private final Object mutex = new Object();

    public void testConversationExpire() throws Throwable {
/*
        workContext.setIdentifier(org.apache.tuscany.spi.model.Scope.CONVERSATION, "12345A");
        // start the conversation
        handler.invoke(operation1, null);
        // verify the instance was persisted
        assertEquals(targetInstance, ((InstanceWrapper)store.readRecord(target, "12345A")).getInstance());
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
*/
    }


    protected void setUp() throws Exception {
        super.setUp();
        createRuntime();
        store.setReaperInterval(10);
        initializeRuntime();

        targetInstance = EasyMock.createMock(FooImpl.class);
        targetInstance.operation1();
        targetInstance.operation2();
        targetInstance.end();
        EasyMock.replay(targetInstance);
        target = createTarget();
        // create source component mock
        JavaAtomicComponent source = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(source.getUri()).andReturn(URI.create("source")).atLeastOnce();
        EasyMock.replay(source);

        Wire wire = MockFactory.createWire("foo", Foo.class);
        for (InvocationChain chain : wire.getInvocationChains().values()) {
            chain.setTargetInvoker(target.createTargetInvoker("target", chain.getOperation(), false));
        }
        handler = new JDKInvocationHandler(Foo.class, wire, workContext);
        operation1 = Foo.class.getMethod("operation1");
        operation2 = Foo.class.getMethod("operation2");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        container.stop();
        store.destroy();
    }

    private AtomicComponent createTarget() throws Exception {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setName(new URI("target"));
        configuration.setMaxAge(50);
        Constructor<FooImpl> ctor = FooImpl.class.getConstructor();
        configuration.setInstanceFactory(new MockPojoFactory(ctor));
        configuration.setImplementationClass(FooImpl.class);
        JavaAtomicComponent component = new JavaAtomicComponent(configuration);
        component.setScopeContainer(container);
        component.start();
        return component;
    }

    private class MockPojoFactory extends PojoObjectFactory<FooImpl> {
        public MockPojoFactory(Constructor<FooImpl> ctr) {
            super(ctr);
        }

        public FooImpl getInstance() throws ObjectCreationException {
            return targetInstance;
        }
    }

    @Conversational
    public static interface Foo {

        void operation1();

        void operation2();

        @EndsConversation
        void end();

    }

    public static class FooImpl implements Foo {

        public void operation1() {
        }

        public void operation2() {
        }

        @EndsConversation
        public void end() {
        }
    }
}
