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
    private CompositeContext context = null;

    
    protected void setUp() throws Exception {
        URL base = getClass().getResource("/META-INF/sca/ruby.system.scdl");
        addExtension("RubyContainer", new URL(base, "default.scdl"));
        setApplicationSCDL(getClass().getResource("helloworld.scdl"));
        super.setUp();

        context = CurrentCompositeContext.getContext();
        helloWorldService = context.locateService(HelloWorldService.class, "HelloWorldRubyComponent");
         
        //helloWorldService = context.locateService(HelloWorldService.class, "HelloWorldJavaReference");
    }

    public void testHelloWorldWithClass() throws Exception {
        assertEquals(helloWorldService.sayHello("petra"), "Hey Howdy from Java Reference petra");
        //System.out.println(helloWorldService.sayHello("petra")); 
    }
    
    public void testHelloWorldGlobal() throws Exception {
        assertEquals(helloWorldService.sayHello("artep"), "Hey Howdy from Java Reference artep");
        //System.out.println(helloWorldService.sayHello("artep"));
    }
    
    public void testHelloWorldProperty() throws Exception {
        HelloWorldService helloWorldService = context.locateService(HelloWorldService.class, "HelloWorldProperty");
        assertEquals(helloWorldService.sayHello("petra"), "Namaskaar petra");
        //System.out.println(helloWorldService.sayHello("petra"));
    }

    public void testHelloWorldPropertyDefault() throws Exception {
        HelloWorldService helloWorldService = context.locateService(HelloWorldService.class, "HelloWorldPropertyDefault");
        assertEquals(helloWorldService.sayHello("petra"), "Bow Wow petra");
        //System.out.println(helloWorldService.sayHello("petra"));
    }

    protected void tearDown() throws Exception {
       super.tearDown();
    }
}
