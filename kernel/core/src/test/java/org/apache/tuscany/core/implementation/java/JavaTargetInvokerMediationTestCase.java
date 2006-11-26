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

import static org.apache.tuscany.spi.wire.TargetInvoker.NONE;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;

/**
 * Tests invoking on a different interface from the one actually implemented by the target
 *
 * @version $Rev$ $Date$
 */
public class JavaTargetInvokerMediationTestCase extends TestCase {

    private Method hello;

    public void setUp() throws Exception {
        hello = Hello.class.getMethod("hello", String.class);
    }

    public void testMediation() throws Exception {
        Target target = EasyMock.createMock(Target.class);
        EasyMock.expect(target.hello("foo")).andReturn("foo");
        EasyMock.replay(target);
        JavaAtomicComponent component = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(component.getTargetInstance()).andReturn(target);
        EasyMock.replay(component);
        JavaTargetInvoker invoker = new JavaTargetInvoker(hello, component, null, null, null);
        assertEquals("foo", invoker.invokeTarget("foo", NONE));
    }

    public interface Hello {
        String hello(String message) throws Exception;
    }

    private interface Target {
        String hello(String message);
    }
}
