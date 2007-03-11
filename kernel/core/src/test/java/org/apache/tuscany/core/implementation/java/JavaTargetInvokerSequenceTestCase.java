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
package org.apache.tuscany.core.implementation.java;

import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class JavaTargetInvokerSequenceTestCase extends TestCase {

    /**
     * Verifies an invocation marked as non-conversational has an existing or new instance returned
     */
    public void testNoSequence() throws Exception {
        Foo foo = EasyMock.createMock(Foo.class);
        foo.invoke();
        EasyMock.replay(foo);
        JavaAtomicComponent component = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(component.getTargetInstance()).andReturn(foo);
        EasyMock.expect(component.getScope()).andReturn(Scope.CONVERSATION);
        EasyMock.replay(component);
        JavaTargetInvoker invoker = new JavaTargetInvoker(Foo.class.getMethod("invoke"), component, null, null);
        Message msg = new MessageImpl();
        msg.setConversationSequence(TargetInvoker.NONE);
        invoker.invoke(msg);
        EasyMock.verify(foo);
        EasyMock.verify(component);
    }

    /**
     * Verifies that an invocation marked as starting a conversation has a new instance returned
     */
    public void testStartSequence() throws Exception {
        Foo foo = EasyMock.createMock(Foo.class);
        foo.invoke();
        EasyMock.replay(foo);
        JavaAtomicComponent component = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(component.getTargetInstance()).andReturn(foo);
        EasyMock.expect(component.getScope()).andReturn(Scope.CONVERSATION);
        EasyMock.replay(component);
        JavaTargetInvoker invoker = new JavaTargetInvoker(Foo.class.getMethod("invoke"), component, null, null);
        Message msg = new MessageImpl();
        msg.setConversationSequence(TargetInvoker.START);
        invoker.invoke(msg);
        EasyMock.verify(foo);
        EasyMock.verify(component);
    }

    /**
     * Verifies that an invocation marked as continuing a conversation has an associated instance returned
     */
    public void testContinueSequence() throws Exception {
        Foo foo = EasyMock.createMock(Foo.class);
        foo.invoke();
        EasyMock.replay(foo);
        JavaAtomicComponent component = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(component.getAssociatedTargetInstance()).andReturn(foo);
        EasyMock.expect(component.getScope()).andReturn(Scope.CONVERSATION);
        EasyMock.replay(component);
        JavaTargetInvoker invoker = new JavaTargetInvoker(Foo.class.getMethod("invoke"), component, null, null);
        Message msg = new MessageImpl();
        msg.setConversationSequence(TargetInvoker.CONTINUE);
        invoker.invoke(msg);
        EasyMock.verify(foo);
        EasyMock.verify(component);
    }

    /**
     * Verifies that an invocation marked as ending a conversation has an associated instance returned and it is removed
     * following the dispatch to the instance
     */
    public void testEndSequence() throws Exception {
        Foo foo = EasyMock.createMock(Foo.class);
        foo.invoke();
        EasyMock.replay(foo);
        JavaAtomicComponent component = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(component.getAssociatedTargetInstance()).andReturn(foo);
        EasyMock.expect(component.getScope()).andReturn(Scope.CONVERSATION);
        component.removeInstance();
        component.destroy(EasyMock.eq(foo));
        EasyMock.replay(component);
        JavaTargetInvoker invoker = new JavaTargetInvoker(Foo.class.getMethod("invoke"), component, null, null);
        Message msg = new MessageImpl();
        msg.setConversationSequence(TargetInvoker.END);
        invoker.invoke(msg);
        EasyMock.verify(foo);
        EasyMock.verify(component);
    }


    private interface Foo {
        void invoke();
    }
}
