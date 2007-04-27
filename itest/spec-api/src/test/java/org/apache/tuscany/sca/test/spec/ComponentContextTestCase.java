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
package org.apache.tuscany.sca.test.spec;

import junit.framework.TestCase;

import org.apache.tuscany.host.embedded.SCARuntime;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

public class ComponentContextTestCase extends TestCase {

    public void testCreateSelfReference() {
    	
    	ComponentContext context = SCARuntime.getComponentContext("MyService");        
        ServiceReference<MyService> service = context.createSelfReference(MyService.class);
        MyService myService = service.getService();
        
        assertNotNull(myService);
        assertEquals("RTP", myService.getLocation());
        assertEquals("2006", myService.getYear());
        
    }

    protected void setUp() throws Exception {
        SCARuntime.start("CompositeTest.composite");       
   }

    protected void tearDown() throws Exception {
    	SCARuntime.stop();
    }
}
