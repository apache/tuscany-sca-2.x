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
package org.apache.tuscany.container.ruby.function;

import java.net.URL;

import helloworld.HelloWorldService;

import org.apache.tuscany.test.SCATestCase;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

/**
 * This shows how to test the HelloWorld service component.
 */
public class HelloWorldTestCase extends SCATestCase {

    private HelloWorldService helloWorldService;

    private HelloWorldService introspectableService;
    
    private HelloWorldService e4xHelloWorldService;

    protected void setUp() throws Exception {
        URL base = getClass().getResource("/org/apache/tuscany/container/ruby/RubyComponent.class");
        addExtension("RubyContainer", new URL(base, "../../../../../META-INF/sca/default.scdl"));
        setApplicationSCDL(getClass().getResource("helloworld.scdl"));
        super.setUp();

        CompositeContext context = CurrentCompositeContext.getContext();
        helloWorldService = context.locateService(HelloWorldService.class, "HelloWorldComponent");
    }

    public void testHelloWorldWithClass() throws Exception {
        assertEquals(helloWorldService.sayHello("petra"), "Hello to petra from the Ruby World!");
        //System.out.println(helloWorldService.sayHello("petra"));
    }
    
    public void testHelloWorldGlobal() throws Exception {
        assertEquals(helloWorldService.sayHello("artep"), "Hello to artep from the Ruby World!");
        //System.out.println(helloWorldService.sayHello("artep"));
    }
}
