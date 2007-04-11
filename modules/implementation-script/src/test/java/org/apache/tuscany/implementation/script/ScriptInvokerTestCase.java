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

package org.apache.tuscany.implementation.script;

import java.lang.reflect.InvocationTargetException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import junit.framework.TestCase;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.easymock.EasyMock;

public class ScriptInvokerTestCase extends TestCase {

    private ScriptEngine engine;
    private ScopeContainer mockScopeContainer;

    public void testInvokeTarget() throws ScriptException, InvocationTargetException {
//        engine.eval("function foo(s) {return 'hi ' + s; }");
//        ScriptInvoker invoker = new ScriptInvoker("foo", null, mockScopeContainer, null);
//        assertEquals("hi petra", invoker.invokeTarget(engine, new Object[] {"petra"}));
    }

    @Override
    public void setUp() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByExtension("js");
        mockScopeContainer = EasyMock.createNiceMock(ScopeContainer.class);
    }

}
