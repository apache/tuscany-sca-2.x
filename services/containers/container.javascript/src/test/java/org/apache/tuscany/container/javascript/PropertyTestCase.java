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
package org.apache.tuscany.container.javascript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tuscany.container.javascript.mock.Greeting;
import org.apache.tuscany.container.javascript.rhino.RhinoScript;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.test.ArtifactFactory;

/**
 * Tests for component properties
 */
public class PropertyTestCase extends TestCase {

    private static final String SCRIPT = "function greet(name){ return property; }";

    private RhinoScript implClass;

    /**
     * Tests injecting a simple property type on a Groovy implementation instance
     */
    public void testPropertyInjection() throws Exception {
//        ModuleScopeContainer scope = new ModuleScopeContainer(null);
//        scope.start();
//        List<Class<?>> services = new ArrayList<Class<?>>();
//        services.add(Greeting.class);
//        Map<String, Object> properties = new HashMap<String, Object>();
//        properties.put("property", "bar");
//        WireService wireService = ArtifactFactory.createWireService();
//        JavaScriptComponent<Greeting> context = new JavaScriptComponent<Greeting>("source", implClass, services, properties, null, scope, wireService, null);
//        scope.register(context);
//        Greeting greeting = context.getServiceInstance();
//        assertEquals("bar", greeting.greet("foo"));
//        scope.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        implClass = new RhinoScript("test", SCRIPT);
    }
}
