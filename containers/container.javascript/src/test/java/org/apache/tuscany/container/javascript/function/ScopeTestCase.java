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
package org.apache.tuscany.container.javascript.function;

import helloworld.HelloWorldService;

import org.apache.tuscany.test.SCATestCase;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

/**
 * This shows how to test the HelloWorld service component.
 */
public class ScopeTestCase extends SCATestCase {

    private CompositeContext context;

    protected void setUp() throws Exception {
        addExtension("JavaScriptContainer", getClass().getClassLoader().getResource("META-INF/sca/default.scdl"));
        setApplicationSCDL("org/apache/tuscany/container/javascript/function/scopeTest.scdl");
        super.setUp();

        context = CurrentCompositeContext.getContext();
    }

    public void testStateless() throws Exception {
        HelloWorldService statelessService = context.locateService(HelloWorldService.class, "StatelessComponent");
        assertEquals("1", statelessService.sayHello(""));
        assertEquals("2", statelessService.sayHello(""));
    }

    public void testRequestState() throws Exception {
        HelloWorldService requestService = context.locateService(HelloWorldService.class, "RequestComponent");
        assertEquals("1", requestService.sayHello(""));
        // TODO: scopes don't work yet, everything seems to use Module scope
        // assertEquals("1", requestService.sayHello(""));
    }
}
