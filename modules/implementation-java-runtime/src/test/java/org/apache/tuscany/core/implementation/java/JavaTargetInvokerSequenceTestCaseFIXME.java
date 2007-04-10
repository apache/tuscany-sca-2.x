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

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class JavaTargetInvokerSequenceTestCaseFIXME extends TestCase {
    private Method method;
    private Foo foo;
    private JavaAtomicComponent component;
    private ScopeContainer scopeContainer;
    private InstanceWrapper wrapper;
    private WorkContext workContext;
    private Object contextId;
    private JavaTargetInvoker invoker;
    private Message msg;


    protected void setUp() throws Exception {
        super.setUp();
        method = Foo.class.getMethod("invoke");
        foo = EasyMock.createMock(Foo.class);
        foo.invoke();
        EasyMock.replay(foo);

        contextId = new Object();
        workContext = EasyMock.createMock(WorkContext.class);
        EasyMock.expect(workContext.getIdentifier(Scope.CONVERSATION)).andStubReturn(contextId);
        EasyMock.replay(workContext);

        msg = new MessageImpl();
        msg.setWorkContext(workContext);

        component = EasyMock.createMock(JavaAtomicComponent.class);
        scopeContainer = EasyMock.createMock(ScopeContainer.class);
        wrapper = EasyMock.createMock(InstanceWrapper.class);
        EasyMock.expect(wrapper.getInstance()).andReturn(foo);
        EasyMock.replay(wrapper);

        EasyMock.expect(scopeContainer.getScope()).andStubReturn(Scope.CONVERSATION);
        EasyMock.replay(scopeContainer);
        invoker = new JavaTargetInvoker(method, component, scopeContainer);
        EasyMock.reset(scopeContainer);
        EasyMock.expect(scopeContainer.getScope()).andStubReturn(Scope.CONVERSATION);
    }

    /**
     * Verifies an invocation marked as non-conversational has an existing or new instance returned
     */
    public void testNoSequence() throws Exception {
        EasyMock.expect(scopeContainer.getWrapper(component, contextId)).andReturn(wrapper);
        scopeContainer.returnWrapper(component, wrapper, contextId);
        EasyMock.replay(component);
        EasyMock.replay(scopeContainer);
        msg.setConversationSequence(TargetInvoker.NONE);
        invoker.invoke(msg);
        EasyMock.verify(foo);
        EasyMock.verify(component);
        EasyMock.verify(scopeContainer);
        EasyMock.verify(wrapper);
    }

    /**
     * Verifies that an invocation marked as starting a conversation has a new instance returned
     */
    public void testStartSequence() throws Exception {
        EasyMock.replay(component);
        EasyMock.expect(scopeContainer.getWrapper(component, contextId)).andReturn(wrapper);
        scopeContainer.returnWrapper(component, wrapper, contextId);
        EasyMock.replay(scopeContainer);
        msg.setConversationSequence(TargetInvoker.START);
        invoker.invoke(msg);
        EasyMock.verify(foo);
        EasyMock.verify(component);
        EasyMock.verify(scopeContainer);
        EasyMock.verify(wrapper);
    }

    /**
     * Verifies that an invocation marked as continuing a conversation has an associated instance returned
     */
    public void testContinueSequence() throws Exception {
        EasyMock.replay(component);
        EasyMock.expect(scopeContainer.getAssociatedWrapper(component, contextId)).andReturn(wrapper);
        scopeContainer.returnWrapper(component, wrapper, contextId);
        EasyMock.replay(scopeContainer);
        msg.setConversationSequence(TargetInvoker.CONTINUE);
        invoker.invoke(msg);
        EasyMock.verify(foo);
        EasyMock.verify(component);
        EasyMock.verify(scopeContainer);
        EasyMock.verify(wrapper);
    }

    /**
     * Verifies that an invocation marked as ending a conversation has an associated instance returned and it is removed
     * following the dispatch to the instance
     */
    public void testEndSequence() throws Exception {
        EasyMock.replay(component);
        EasyMock.expect(scopeContainer.getAssociatedWrapper(component, contextId)).andReturn(wrapper);
        scopeContainer.returnWrapper(component, wrapper, contextId);
        scopeContainer.remove(component);
        EasyMock.replay(scopeContainer);
        msg.setConversationSequence(TargetInvoker.END);
        invoker.invoke(msg);
        EasyMock.verify(foo);
        EasyMock.verify(component);
        EasyMock.verify(scopeContainer);
        EasyMock.verify(wrapper);
    }


    private interface Foo {
        void invoke();
    }
}
