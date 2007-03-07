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
package org.apache.tuscany.core.implementation.java;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.model.Scope;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;

public class JavaTargetInvokerTestCase extends TestCase {

    public JavaTargetInvokerTestCase(String arg0) {
        super(arg0);
    }

    public void testInvoke() throws Exception {
        Method echoMethod = Echo.class.getDeclaredMethod("echo", String.class);
        JavaAtomicComponent component = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(component.getTargetInstance()).andReturn(new Echo());
        EasyMock.expect(component.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.replay(component);
        JavaTargetInvoker invoker = new JavaTargetInvoker(echoMethod, component, null);
        invoker.setCacheable(false);
        assertEquals("foo", invoker.invokeTarget("foo", JavaTargetInvoker.NONE));
        EasyMock.verify(component);
    }

    public static class Echo {
        public String echo(String message) throws Exception {
            return message;
        }

    }

}
