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
package org.apache.tuscany.container.ruby;

import junit.framework.TestCase;

import org.apache.tuscany.container.ruby.rubyscript.RubyScript;

/**
 * Tests for invoker JavaScriptComponents
 */
public class ScriptInvokeTestCase extends TestCase {

    private static final String SCRIPT = "def greet(name) \n return name \n end \n";

    private RubyScript rubyScript;

    /**
     * Tests the invocation of a Groovy "script" as opposed to a class
     */
    public void testBasicScriptInvocation() throws Exception {
//        ModuleScopeContainer scope = new ModuleScopeContainer(null);
//        scope.start();
//        List<Class<?>> serviceBindings = new ArrayList<Class<?>>();
//        serviceBindings.add(Greeting.class);
//        RubyComponent<Greeting> context = new RubyComponent<Greeting>("source", rhinoScript, serviceBindings, new HashMap<String, Object>(),
//                null, scope, ArtifactFactory.createWireService(), null);
//        scope.register(context);
//        Greeting object = (Greeting) context.getServiceInstance();
//        assertEquals("foo", object.greet("foo"));
//        scope.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        rubyScript = new RubyScript("test", SCRIPT);
    }
}
