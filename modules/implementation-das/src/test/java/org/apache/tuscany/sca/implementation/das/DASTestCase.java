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

package org.apache.tuscany.sca.implementation.das;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

import commonj.sdo.DataObject;

/**
 * Tests the DAS service
 * 
 * @version $Rev$ $Date$
 */
public class DASTestCase extends TestCase {

    private SCADomain scaDomain;
    private DAS dasService;
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    protected void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("das.composite");
        dasService = scaDomain.getService(DAS.class, "DASServiceComponent");

    }

    /**
     * @throws java.lang.Exception
     */
    @Override
    protected void tearDown() throws Exception {
        scaDomain.close();
    }

    
    public void testExecuteCommand() throws Exception {
        String commandName = "all companies";
         
        DataObject resultRoot= dasService.executeCommand(commandName);
        assertNotNull(resultRoot);    
        assertEquals(3, resultRoot.getList("COMPANY").size());
    }
    
    public void testExecuteCommandWithFilter() throws Exception {
        String commandName = "all companies";
         
        DataObject resultRoot= dasService.executeCommand(commandName, "COMPANY[1]");
        assertNotNull(resultRoot);        
    }


}
