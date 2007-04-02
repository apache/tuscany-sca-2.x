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
package org.apache.tuscany.container.script;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import org.easymock.IAnswer;
import org.easymock.classextension.EasyMock;

public class ScriptInvokerTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testInvokeTarget() throws Exception {
        ScriptInstance instance = EasyMock.createMock(ScriptInstance.class);
        instance.invokeTarget(EasyMock.eq("operation"), (Object[]) EasyMock.notNull());
        EasyMock.expectLastCall().andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                assertEquals(2, EasyMock.getCurrentArguments().length);
                assertEquals("operation", EasyMock.getCurrentArguments()[0]);
                return "hello";
            }
        });

        EasyMock.replay(instance);
        ScriptComponent component = EasyMock.createMock(ScriptComponent.class);
        EasyMock.expect(component.getTargetInstance()).andReturn(instance);
        EasyMock.replay(component);
        ScriptTargetInvoker invoker = new ScriptTargetInvoker("operation", component);
        assertEquals("hello", invoker.invokeTarget(new Object[]{"petra"}, TargetInvoker.NONE));
        EasyMock.verify(instance);
        EasyMock.verify(component);
    }

    @SuppressWarnings("unchecked")
    public void testInvokeTargetException() throws Exception {
        ScriptInstance instance = EasyMock.createMock(ScriptInstance.class);
        instance.invokeTarget(EasyMock.eq("operation"), (Object[]) EasyMock.notNull());
        EasyMock.expectLastCall().andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                throw new RuntimeException();
            }
        });

        EasyMock.replay(instance);
        ScriptComponent component = EasyMock.createMock(ScriptComponent.class);
        EasyMock.expect(component.getTargetInstance()).andReturn(instance);
        EasyMock.replay(component);
        ScriptTargetInvoker invoker = new ScriptTargetInvoker("operation", component);
        try {
            invoker.invokeTarget(new Object[]{"petra"}, TargetInvoker.NONE);
            fail();
        } catch (InvocationTargetException e) {
            // expected
        }
        EasyMock.verify(instance);
        EasyMock.verify(component);
    }

}
