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
package org.apache.tuscany.sca.test.exceptions;

import junit.framework.TestCase;

import org.apache.tuscany.host.embedded.SCADomain;

public class IntraCompositeTestCase extends TestCase {

    private SCADomain domain;
    private ExceptionHandler exceptionHandler;

    public void testALL() {
        exceptionHandler.testing();
        assertEquals(ExceptionThrower.SO_THEY_SAY, exceptionHandler.getTheGood() );
        assertNotNull(exceptionHandler.getTheBad());
        assertEquals( Checked.class, exceptionHandler.getTheBad().getClass());
        assertNotNull(exceptionHandler.getTheUgly());
        assertEquals( UnChecked.class, exceptionHandler.getTheUgly().getClass());

    }

    @Override
    protected void setUp() throws Exception {
    	domain = SCADomain.newInstance("ExceptionTest.composite");
        exceptionHandler = domain.getService(ExceptionHandler.class, "main");
    }
    
    @Override
    protected void tearDown() throws Exception {
    	domain.close();
    }
    
}
