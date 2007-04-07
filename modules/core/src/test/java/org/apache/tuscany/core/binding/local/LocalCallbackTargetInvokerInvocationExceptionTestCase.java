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
package org.apache.tuscany.core.binding.local;

import junit.framework.TestCase;

import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.WireImpl;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.impl.OperationImpl;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.Wire;

/**
 * @version $Rev$ $Date$
 */
public class LocalCallbackTargetInvokerInvocationExceptionTestCase extends TestCase {

    /**
     * Verfies an InvocationTargetException thrown when invoking the target is propagated to the client correctly and
     * the originating error is unwrapped
     */
    public void testThrowableTargetInvocation() throws Exception {
        Operation operation = new OperationImpl();
        operation.setName("echo");
        Interceptor head = new ErrorInterceptor();
        InvocationChain chain = new InvocationChainImpl(operation);
        chain.addInterceptor(head);
        Wire wire = new WireImpl();
        wire.addCallbackInvocationChain(operation, chain);
        LocalCallbackTargetInvoker invoker = new LocalCallbackTargetInvoker(operation, wire);
        Message msg = new MessageImpl();
        msg.setBody("foo");
        Message response = invoker.invoke(msg);
        assertTrue(response.isFault());
        Object body = response.getBody();
        assertTrue(SomeException.class.equals(body.getClass()));
    }

    private class SomeException extends Exception {

    }

    private class ErrorInterceptor implements Interceptor {

        public Message invoke(Message msg) {
            msg.setBodyWithFault(new SomeException());
            return msg;
        }

        public void setNext(Interceptor next) {

        }

        public Interceptor getNext() {
            return null;
        }

        public boolean isOptimizable() {
            return false;
        }
    }


}
