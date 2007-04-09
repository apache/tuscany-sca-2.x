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
package org.apache.tuscany.core.idl.java;

import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;

import org.apache.tuscany.spi.idl.InvalidConversationalOperationException;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class ConversationalIntrospectionTestCase extends TestCase {
    private JavaInterfaceProcessorRegistryImpl registry = new JavaInterfaceProcessorRegistryImpl();

    public void testServiceContractConversationalInformationIntrospection() throws Exception {
        JavaServiceContract<Foo> contract = registry.introspect(Foo.class);
        assertTrue(contract.isConversational());
        int seq = contract.getOperations().get("operation").getConversationSequence();
        assertEquals(Operation.CONVERSATION_CONTINUE, seq);
        seq = contract.getOperations().get("endOperation").getConversationSequence();
        assertEquals(Operation.CONVERSATION_END, seq);
    }

    public void testBadServiceContract() throws Exception {
        try {
            registry.introspect(BadFoo.class);
            fail();
        } catch (InvalidConversationalOperationException e) {
            //expected
        }
    }

    public void testNonConversationalInformationIntrospection() throws Exception {
        JavaServiceContract<NonConversationalFoo> contract = registry.introspect(NonConversationalFoo.class);
        assertFalse(contract.isConversational());
        int seq = contract.getOperations().get("operation").getConversationSequence();
        assertEquals(Operation.NO_CONVERSATION, seq);
    }

    @Conversational
    private interface Foo {
        void operation();

        @EndsConversation
        void endOperation();
    }

    private interface BadFoo {
        void operation();

        @EndsConversation
        void endOperation();
    }

    private interface NonConversationalFoo {
        void operation();
    }

}
