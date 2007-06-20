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

package org.apache.tuscany.sca.implementation.data;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.implementation.data.DATA;

import commonj.sdo.DataObject;

/**
 * Tests the DAS service
 * 
 * @version $Rev$ $Date$
 */
public class DATATestCase extends TestCase {
    private SCADomain scaDomain;
    private DATA dataService;
    
    /**
     * @throws java.lang.Exception
     */
    protected void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("data.composite");
        dataService = scaDomain.getService(DATA.class, "DataComponent");
    }

    /**
     * @throws java.lang.Exception
     */
    protected void tearDown() throws Exception {
        scaDomain.close();
    }

    
    public void testExecuteCommand() throws Exception {
        Integer companyID = new Integer(51);
         
        DataObject resultRoot= dataService.get(companyID.toString());
        assertNotNull(resultRoot);  
        
        //verify we got back the right row
        assertEquals(companyID, resultRoot.get("COMPANY[1]/ID"));
    }

}
