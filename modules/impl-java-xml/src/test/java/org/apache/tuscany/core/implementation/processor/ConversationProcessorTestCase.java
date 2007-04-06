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
package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.processor.ConversationProcessor;
import org.apache.tuscany.implementation.java.processor.InvalidConversationalImplementation;
import org.osoa.sca.annotations.ConversationAttributes;
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Scope;

/**
 * @version $Rev$ $Date$
 */
public class ConversationProcessorTestCase extends TestCase {
    private ConversationProcessor processor = new ConversationProcessor();

    public void testMaxIdleTime() throws Exception {
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        processor.visitClass(FooMaxIdle.class, type);
        assertEquals(10000L, type.getMaxIdleTime());
        assertEquals(-1, type.getMaxAge());
    }

    public void testMaxAge() throws Exception {
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        processor.visitClass(FooMaxAge.class, type);
        assertEquals(10000L, type.getMaxAge());
        assertEquals(-1, type.getMaxIdleTime());
    }

    public void testBadFooBoth() throws Exception {
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        try {
            processor.visitClass(BadFooBoth.class, type);
            fail();
        } catch (InvalidConversationalImplementation e) {
            // expected
        }
    }

    public void testImplicitScope() throws Exception {
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        processor.visitClass(ImplicitFooScope.class, type);
        assertEquals(org.apache.tuscany.implementation.java.impl.Scope.CONVERSATION, type.getScope());
    }

    public void testBadFooScope() throws Exception {
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        try {
            processor.visitClass(BadFooScope.class, type);
            fail();
        } catch (InvalidConversationalImplementation e) {
            // expected
        }
    }

    public void testJustConversation() throws Exception {
        // TODO do we want these semantics
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        processor.visitClass(FooJustConversation.class, type);
        assertEquals(org.apache.tuscany.implementation.java.impl.Scope.CONVERSATION, type.getScope());
        assertEquals(-1, type.getMaxAge());
        assertEquals(-1, type.getMaxIdleTime());
    }

    public void testSetConversationIDField() throws Exception {
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        Field field = FooWithConversationIDField.class.getDeclaredField("conversationID");
        processor.visitField(field, type);
        assertNotNull(type.getConversationIDMember());
        assertEquals(field, type.getConversationIDMember());
    }

    public void testSetConversationIDMethod() throws Exception {
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        Method method = FooWithConversationIDMethod.class.getDeclaredMethods()[0];
        processor.visitMethod(method, type);
        assertNotNull(type.getConversationIDMember());
        assertEquals(method, type.getConversationIDMember());
    }

    @Scope("CONVERSATION")
    @ConversationAttributes(maxIdleTime = "10 seconds")
    private class FooMaxIdle {
    }

    @Scope("CONVERSATION")
    @ConversationAttributes(maxAge = "10 seconds")
    private class FooMaxAge {
    }

    @Scope("CONVERSATION")
    @ConversationAttributes(maxAge = "10 seconds", maxIdleTime = "10 seconds")
    private class BadFooBoth {
    }

    @ConversationAttributes(maxAge = "10 seconds")
    private class ImplicitFooScope {
    }

    @Scope("STATELESS")
    @ConversationAttributes(maxAge = "10 seconds")
    private class BadFooScope {
    }

    @ConversationAttributes
    private class FooJustConversation {
    }

    private class FooWithConversationIDField {
        @ConversationID
        private String conversationID;
    }

    private class FooWithConversationIDMethod {
        @ConversationID
        void setConversationID(String conversationID) {
        }
    }
}
