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
package org.apache.tuscany.container.spring.impl;

import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.springframework.context.ApplicationContext;

/**
 * Verifies a simple invocation on a Spring bean
 *
 * @version $$Rev$$ $$Date$$
 */
public class SpringInvocationTestCase extends TestCase {

    /**
     * Verifies the invoker can resolve a bean in an application context and call a method l
     */
    public void testInvocation() throws Exception {
        TestBean bean = createMock(TestBean.class);
        bean.test("bar");
        expectLastCall();
        replay(bean);
        ApplicationContext context = createMock(ApplicationContext.class);
        expect(context.getBean("foo")).andReturn(bean);
        replay(context);
        SpringInvoker invoker = new SpringInvoker("foo", TestBean.class.getMethod("test", String.class), context);
        Message msg = new MessageImpl();
        msg.setBody(new String[]{"bar"});
        invoker.invoke(msg);
        verify(context);
        verify(bean);
    }


    private interface TestBean {
        void test(String msg);
    }
}
