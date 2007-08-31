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

import org.apache.tuscany.sca.core.invocation.CallbackInterfaceInterceptor;
import org.apache.tuscany.sca.core.invocation.MessageFactoryImpl;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Message;
import org.easymock.EasyMock;
import org.osoa.sca.NoRegisteredCallbackException;
import org.osoa.sca.ServiceReference;

/**
 * @version $Rev$ $Date$
 */
public class CallbackInterfaceInterceptorTestCase extends TestCase {

    public void testHasCallbackObject() {
        CallbackInterfaceInterceptor interceptor = new CallbackInterfaceInterceptor();
        Interceptor next = EasyMock.createMock(Interceptor.class);
        EasyMock.expect(next.invoke(EasyMock.isA(Message.class))).andReturn(null);
        EasyMock.replay(next);
        interceptor.setNext(next);
        Message msg = new MessageFactoryImpl().createMessage();
        ServiceReference callableReference = EasyMock.createMock(ServiceReference.class);
        EasyMock.expect(callableReference.getCallback()).andReturn(new Object());
        EasyMock.replay(callableReference);
        msg.setCallableReference(callableReference);
        interceptor.invoke(msg);
        EasyMock.verify(next);
    }

    public void testNoCallbackObject() {
        CallbackInterfaceInterceptor interceptor = new CallbackInterfaceInterceptor();
        Message msg = new MessageFactoryImpl().createMessage();
        ServiceReference callableReference = EasyMock.createMock(ServiceReference.class);
        EasyMock.expect(callableReference.getCallback()).andReturn(null);
        EasyMock.replay(callableReference);
        msg.setCallableReference(callableReference);
        try {
            interceptor.invoke(msg);
            fail();
        } catch (NoRegisteredCallbackException e) {
            // expected
        }
    }

}
