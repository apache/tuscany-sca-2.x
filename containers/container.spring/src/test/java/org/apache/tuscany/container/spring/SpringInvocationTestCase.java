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
package org.apache.tuscany.container.spring;

import junit.framework.TestCase;
import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.container.spring.mock.TestBeanImpl;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Verifies a simple invocation on a Spring bean
 *
 * @version $$Rev$$ $$Date$$
 */
public class SpringInvocationTestCase extends TestCase {

    public void testSpringInvocation() throws Exception {
        ConfigurableApplicationContext ctx = createMock(ConfigurableApplicationContext.class);
        expect(ctx.getBean("foo")).andStubReturn(new TestBeanImpl());
        replay(ctx);
        SpringInvoker invoker = new SpringInvoker("foo", TestBean.class.getMethod("echo", String.class), ctx);
        assertEquals("call foo", invoker.invokeTarget(new String[]{"call foo"}));
    }
}
