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
package org.apache.tuscany.core.wire;

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class ConversationSequenceInterceptorTestCase extends TestCase {

    public void testStartSequence() {
        Interceptor next = new MockStartInterceptor();
        ConversationSequenceInterceptor interceptor = new ConversationSequenceInterceptor();
        interceptor.setNext(next);
        Message msg = new MessageImpl();
        interceptor.invoke(msg);
    }

    public void testContinueSequence() {
        Interceptor next = new MockStartInterceptor();
        Interceptor next2 = new MockContinueInterceptor();
        ConversationSequenceInterceptor interceptor = new ConversationSequenceInterceptor();
        // set up conversation
        interceptor.setNext(next);
        Message msg = new MessageImpl();
        interceptor.invoke(msg);
        //  swap interceptor to test continue
        interceptor.setNext(next2);
        interceptor.invoke(msg);
    }

    private class MockStartInterceptor implements Interceptor {

        public Message invoke(Message msg) {
            assertEquals(TargetInvoker.START, msg.getConversationSequence());
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

    private class MockContinueInterceptor implements Interceptor {

        public Message invoke(Message msg) {
            assertEquals(TargetInvoker.CONTINUE, msg.getConversationSequence());
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
