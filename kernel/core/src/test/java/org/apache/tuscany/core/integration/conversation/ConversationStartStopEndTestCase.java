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
import static org.apache.tuscany.spi.model.Scope.CONVERSATION;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.integration.mock.MockFactory;
import org.apache.tuscany.core.wire.jdk.JDKOutboundInvocationHandler;
import org.easymock.classextension.EasyMock;

/**
 * Verifies start, continue and end conversation invocations are processed properly. Checks that a target instance is
 * properly instantiated and persisted in the store. Additionally verifies that invocations are dispatched to a target
 * instance, and that start, continue, and end operations are performed correctly. Finally, verfies that the target
 * instance is removed from the store when the conversation ends.
 *
 * @version $Rev$ $Date$
 */
public class ConversationStartStopEndTestCase extends AbstractConversationTestCase {
    private OutboundWire owire;
    private Foo targetInstance;
    private JDKOutboundInvocationHandler handler;
    private Method operation1;
    private Method operation2;
    private Method endOperation;

    public void testConversationStartContinueEnd() throws Throwable {
        workContext.setIdentifier(CONVERSATION, "12345A");
        // start the conversation
        handler.invoke(operation1, null);
        // verify the instance was persisted
        assertEquals(targetInstance, store.readRecord(target, "12345A"));
        // continue the conversation
        handler.invoke(operation2, null);
        // verify the instance was persisted
        assertEquals(targetInstance, store.readRecord(target, "12345A"));
        // end the conversation
        handler.invoke(endOperation, null);
        workContext.clearIdentifier(CONVERSATION);
        EasyMock.verify(targetInstance);
        // verify the store has removed the instance
        assertNull(store.readRecord(target, "12345A"));
    }


    protected void setUp() throws Exception {
        super.setUp();
        createRuntime();
        initializeRuntime();
        targetInstance = EasyMock.createMock(Foo.class);
        targetInstance.operation1();
        targetInstance.operation2();
        targetInstance.end();
        EasyMock.replay(targetInstance);
        // create target component mock
        target = createAtomicComponent();
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
        endOperation = Foo.class.getMethod("end");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        container.stop();
        store.destroy();
    }

    private JavaAtomicComponent createAtomicComponent() throws Exception {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setName("target");
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
