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
package org.apache.tuscany.sca.core.wire;

import junit.framework.TestCase;

import org.apache.tuscany.interfacedef.impl.OperationImpl;
import org.apache.tuscany.sca.core.invocation.InvocationChainImpl;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

/**
 * @version $Rev$ $Date$
 */
public class InvocationChainImplTestCase extends TestCase {

    public void testInsertAtEnd() throws Exception {
        InvocationChain chain = new InvocationChainImpl(new OperationImpl("foo"));
        Interceptor inter2 = new MockInterceptor();
        Interceptor inter1 = new MockInterceptor();
        chain.addInterceptor(inter1);
        chain.addInterceptor(inter2);
        Interceptor head = (Interceptor)chain.getHeadInvoker();
        assertEquals(inter1, head);
        assertEquals(inter2, head.getNext());
        assertEquals(inter2, chain.getTailInvoker());

    }

    private class MockInterceptor implements Interceptor {

        private Invoker next;

        public Message invoke(Message msg) {
            return null;
        }

        public void setNext(Invoker next) {
            this.next = next;
        }

        public Invoker getNext() {
            return next;
        }

    }

}
