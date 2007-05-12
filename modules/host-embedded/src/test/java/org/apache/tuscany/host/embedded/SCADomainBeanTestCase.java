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

package org.apache.tuscany.host.embedded;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomainBean;
import org.osoa.sca.ServiceReference;

import crud.CRUD;

/**
 * @version $Rev$ $Date$
 */
public class SCADomainBeanTestCase extends TestCase {

    private SCADomainBean domain;
    
    protected void setUp() throws Exception {
        domain = new SCADomainBean();
        domain.setDeployableComposites("crud.composite");
    }

    public void testStart() throws Exception {
        ServiceReference<CRUD> serviceReference = domain.getServiceReference(CRUD.class, "CRUDServiceComponent");
        assertNotNull(serviceReference);
        CRUD service = serviceReference.getService();
        String id = service.create("ABC");
        Object result = service.retrieve(id);
        assertEquals("ABC", result);
        service.update(id, "EFG");
        result = service.retrieve(id);
        assertEquals("EFG", result);
        service.delete(id);
        result = service.retrieve(id);
        assertNull(result);
    }

    /**
     * @throws java.lang.Exception
     */
    protected void tearDown() throws Exception {
        domain.close();
    }

}
