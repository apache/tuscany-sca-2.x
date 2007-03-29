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

package org.apache.tuscany.api;

import junit.framework.TestCase;

import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

import calculator.AddService;
import calculator.CalculatorService;

/**
 * @version $Rev$ $Date$
 */
public class SCARuntimeTestCase extends TestCase {
    /**
     * @throws java.lang.Exception
     */
    protected void setUp() throws Exception {
        SCARuntime.start("calculator.composite");
    }

    public void testStart() throws Exception {
        ComponentContext context = SCARuntime.getComponentContext("CalculatorServiceComponent");
        assertNotNull(context);
        ServiceReference<CalculatorService> self = context.createSelfReference(CalculatorService.class);
        assertEquals(2.0, self.getService().multiply(1.0, 2.0));
        AddService service = context.getService(AddService.class, "addService");
        assertEquals(3.0, service.add(1.0, 2.0));
    }

    /**
     * @throws java.lang.Exception
     */
    protected void tearDown() throws Exception {
        SCARuntime.stop();
    }

}
