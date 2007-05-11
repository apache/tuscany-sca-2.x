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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

import org.apache.tuscany.sca.core.invocation.NonBlockingInterceptor;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageImpl;
import org.apache.tuscany.sca.scope.Scope;
import org.apache.tuscany.sca.spi.component.WorkContext;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * @version $Rev$ $Date$
 */
public class NonBlockingInterceptorTestCase extends TestCase {

    public void testInvoke() throws Exception {
        WorkScheduler scheduler = createMock(WorkScheduler.class);
        scheduler.scheduleWork(isA(Runnable.class));
        expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                Runnable runnable = (Runnable) getCurrentArguments()[0];
                runnable.run();
                return null;
            }
        });
        replay(scheduler);
        WorkContext context = createMock(WorkContext.class);
        String convID = "convID";
        EasyMock.expect(context.getIdentifier(Scope.CONVERSATION)).andReturn(convID);
        context.setCorrelationId(null);
        context.setIdentifier(Scope.CONVERSATION, convID);
        EasyMock.replay(context);
        Message msg = new MessageImpl();
        Interceptor next = EasyMock.createMock(Interceptor.class);
        EasyMock.expect(next.invoke(EasyMock.eq(msg))).andReturn(msg);
        EasyMock.replay(next);
        Interceptor interceptor = new NonBlockingInterceptor(scheduler, context, next);
        interceptor.invoke(msg);
        verify(context);
        verify(next);
    }

}
