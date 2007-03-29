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

package org.apache.tuscany.runtime.embedded;

import java.net.URI;

import junit.framework.TestCase;

import org.osoa.sca.ComponentContext;

import calculator.AddService;

/**
 * @version $Rev$ $Date$
 */
public class SimpleRuntimeImplTestCase extends TestCase {
    private SimpleRuntime runtime;

    /**
     * @throws java.lang.Exception
     */
    protected void setUp() throws Exception {
        SimpleRuntimeInfo runtimeInfo = new SimpleRuntimeInfoImpl(getClass().getClassLoader(), "application.composite");
        runtime = new SimpleRuntimeImpl(runtimeInfo);
        runtime.start();
    }

    public void testStart() throws Exception {
        ComponentContext context = runtime.getComponentContext(URI
            .create("sca://root.application/default/CalculatorServiceComponent"));
        assertNotNull(context);
        AddService service = context.getService(AddService.class, "addService");
        assertEquals(3.0, service.add(1.0, 2.0));
    }

    /**
     * @throws java.lang.Exception
     */
    protected void tearDown() throws Exception {
        runtime.destroy();
    }

}
