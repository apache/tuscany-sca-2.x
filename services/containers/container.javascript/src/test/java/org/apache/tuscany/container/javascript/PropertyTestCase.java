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

import junit.framework.TestCase;

import org.apache.tuscany.container.javascript.rhino.RhinoScript;

/**
 * Tests for component properties
 */
public class PropertyTestCase extends TestCase {

    private static final String SCRIPT = "function greet(name){ return property; }";

    private RhinoScript implClass;

    /**
     * Tests injecting a simple property type on a Javascript implementation instance
     */
    public void testPropertyInjection() throws Exception {
        /*CompositeScopeContainer scope = new CompositeScopeContainer(null);
        scope.start();
        List<Class<?>> serviceBindings = new ArrayList<Class<?>>();
        serviceBindings.add(Greeting.class);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("property", "bar");
        WireService wireService = ArtifactFactory.createWireService();
        JavaScriptComponent<Greeting> context = new JavaScriptComponent<Greeting>("source", implClass, serviceBindings, properties, null, scope, wireService, null);
        scope.register(context);
        Greeting greeting = context.getServiceInstance();
        assertEquals("bar", greeting.greet("foo"));
        scope.stop();*/
    }

    protected void setUp() throws Exception {
        super.setUp();
        implClass = new RhinoScript("test", SCRIPT);
    }
}
