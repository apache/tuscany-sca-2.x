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
package org.apache.tuscany.interfacedef.java.introspection.impl;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.interfacedef.Interface;
import org.apache.tuscany.interfacedef.InvalidOperationException;
import org.apache.tuscany.interfacedef.Operation;
import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;

/**
 * @version $Rev$ $Date$
 */
public class ConversationalIntrospectionTestCase extends TestCase {
    private AssemblyFactory factory = new DefaultAssemblyFactory();
    private JavaInterfaceProcessorRegistryImpl registry = new JavaInterfaceProcessorRegistryImpl();

    private Operation getOperation(Interface i, String name) {
        for (Operation op : i.getOperations()) {
            if (op.getName().equals(name)) {
                return op;
            }
        }
        return null;
    }

    public void testServiceContractConversationalInformationIntrospection() throws Exception {
        Interface i = registry.introspect(Foo.class);
        assertNotNull(i);
        assertTrue(i.isConversational());
        Operation.ConversationSequence seq = getOperation(i, "operation").getConversationSequence();
        assertEquals(Operation.ConversationSequence.CONVERSATION_CONTINUE, seq);
        seq = getOperation(i, "endOperation").getConversationSequence();
        assertEquals(Operation.ConversationSequence.CONVERSATION_END, seq);
    }

    public void testBadServiceContract() throws Exception {
        try {
            registry.introspect(BadFoo.class);
            fail();
        } catch (InvalidOperationException e) {
            // expected
        }
    }

    public void testNonConversationalInformationIntrospection() throws Exception {
        Interface i = registry.introspect(NonConversationalFoo.class);
        assertFalse(i.isConversational());
        Operation.ConversationSequence seq = getOperation(i, "operation")
            .getConversationSequence();
        assertEquals(Operation.ConversationSequence.NO_CONVERSATION, seq);
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
