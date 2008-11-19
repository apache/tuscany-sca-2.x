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

package org.apache.tuscany.sca.host.embedded;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.test.extension.TestService;
import org.osoa.sca.ServiceReference;



/**
 * Test creation of an SCADomainBean and invocation of a service.
 * 
 * @version $Rev$ $Date$
 */
public class SCADomainBeanTestCase extends TestCase {

    private SCADomainBean domain;
    
    @Override
    protected void setUp() throws Exception {
        domain = new SCADomainBean();
        domain.setDeployableComposites("test.composite");
    }

    public void testInvoke() throws Exception {
        ServiceReference<TestService> serviceReference = domain.getServiceReference(TestService.class, "TestServiceComponent");
        assertNotNull(serviceReference);
        TestService service = serviceReference.getService();
        String result = service.ping("Bob");
        assertEquals("Hello Bob", result);
    }

    @Override
    protected void tearDown() throws Exception {
        domain.close();
    }

}
