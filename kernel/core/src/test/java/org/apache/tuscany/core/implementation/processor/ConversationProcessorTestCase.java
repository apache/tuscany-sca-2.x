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

import org.osoa.sca.annotations.Conversation;
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class ConversationProcessorTestCase extends TestCase {
    private ConversationProcessor processor = new ConversationProcessor();

    public void testMaxIdleTime() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(null, FooMaxIdle.class, type, null);
        assertEquals(10000L, type.getMaxIdleTime());
        assertEquals(-1, type.getMaxAge());
    }

    public void testMaxAge() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(null, FooMaxAge.class, type, null);
        assertEquals(10000L, type.getMaxAge());
        assertEquals(-1, type.getMaxIdleTime());
    }

    public void testBadFooBoth() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        try {
            processor.visitClass(null, BadFooBoth.class, type, null);
            fail();
        } catch (InvalidConversationalImplementation e) {
            // expected
        }
    }

    public void testImplicitScope() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(null, ImplicitFooScope.class, type, null);
        assertEquals(org.apache.tuscany.spi.model.Scope.CONVERSATION, type.getImplementationScope());
    }

    public void testBadFooScope() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        try {
            processor.visitClass(null, BadFooScope.class, type, null);
            fail();
        } catch (InvalidConversationalImplementation e) {
            // expected
        }
    }

    public void testJustConversation() throws Exception {
        // TODO do we want these semantics
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(null, FooJustConversation.class, type, null);
        assertEquals(org.apache.tuscany.spi.model.Scope.CONVERSATION, type.getImplementationScope());
        assertEquals(-1, type.getMaxAge());
        assertEquals(-1, type.getMaxIdleTime());
    }
    
    public void testSetConversationIDField() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Field field = FooWithConversationIDField.class.getDeclaredField("conversationID");
        processor.visitField(null, field, type, null);
        assertNotNull(type.getConversationIDMember());
        assertEquals(field, type.getConversationIDMember());
    }

    public void testSetConversationIDMethod() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Method method = FooWithConversationIDMethod.class.getDeclaredMethods()[0];
        processor.visitMethod(null, method, type, null);
        assertNotNull(type.getConversationIDMember());
        assertEquals(method, type.getConversationIDMember());
    }

    @Scope("CONVERSATION")
    @Conversation(maxIdleTime = "10 seconds")
    private class FooMaxIdle {
    }

    @Scope("CONVERSATION")
    @Conversation(maxAge = "10 seconds")
    private class FooMaxAge {
    }

    @Scope("CONVERSATION")
    @Conversation(maxAge = "10 seconds", maxIdleTime = "10 seconds")
    private class BadFooBoth {
    }

    @Conversation(maxAge = "10 seconds")
    private class ImplicitFooScope {
    }

    @Scope("STATELESS")
    @Conversation(maxAge = "10 seconds")
    private class BadFooScope {
    }

    @Conversation
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
