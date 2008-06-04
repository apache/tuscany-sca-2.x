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

import org.apache.tuscany.sca.host.embedded.SCADomain;

public class ExceptionsTestCase extends TestCase {

    private SCADomain domain;

    /**
     * Test exception handling over a local interface
     */
    public void testLocal() {
        ExceptionHandler exceptionHandler = domain.getService(ExceptionHandler.class, "main");
        exceptionHandler.testing();
        assertEquals(ExceptionThrower.SO_THEY_SAY, exceptionHandler.getTheGood() );
        assertNotNull(exceptionHandler.getTheBad());
        assertEquals( Checked.class, exceptionHandler.getTheBad().getClass());
        assertSame(ExceptionThrower.BAD, exceptionHandler.getTheBad());
        assertNotNull(exceptionHandler.getTheUgly());
        assertEquals( UnChecked.class, exceptionHandler.getTheUgly().getClass());
        assertSame(ExceptionThrower.UGLY, exceptionHandler.getTheUgly());
    }
    
    /**
     * Test exception handling over a remotable interface
     */
    public void testRemote() {
        ExceptionHandler exceptionHandler = domain.getService(ExceptionHandler.class, "mainRemote");
        exceptionHandler.testing();
        assertEquals(ExceptionThrower.SO_THEY_SAY, exceptionHandler.getTheGood() );
        assertNotNull(exceptionHandler.getTheBad());
        assertEquals( Checked.class, exceptionHandler.getTheBad().getClass());
        assertNotSame(ExceptionThrower.BAD, exceptionHandler.getTheBad());
        assertNotNull(exceptionHandler.getTheUgly());
        assertEquals( UnChecked.class, exceptionHandler.getTheUgly().getClass());
        
        // [rfeng] We're not in a position to copy non business exceptions
        // assertNotSame(ExceptionThrower.UGLY, exceptionHandler.getTheUgly());

    }


    @Override
    protected void setUp() throws Exception {
    	domain = SCADomain.newInstance("ExceptionTest.composite");
    }
    
    @Override
    protected void tearDown() throws Exception {
    	domain.close();
    }
    
}
