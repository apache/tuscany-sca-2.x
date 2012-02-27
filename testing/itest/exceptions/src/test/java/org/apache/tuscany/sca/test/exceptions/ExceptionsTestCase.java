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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oasisopen.sca.ServiceRuntimeException;

public class ExceptionsTestCase {

    private static Node node;

    /**
     * Test exception handling over a local interface
     */
    @Test
    public void testLocal() {
        ExceptionHandler exceptionHandler = node.getService(ExceptionHandler.class, "main");
        exceptionHandler.testing();
        assertEquals(ExceptionThrower.SO_THEY_SAY, exceptionHandler.getTheGood());
        assertNotNull(exceptionHandler.getTheBad());
        assertEquals(Checked.class, exceptionHandler.getTheBad().getClass());
        assertSame(ExceptionThrower.BAD, exceptionHandler.getTheBad());
        assertNotNull(exceptionHandler.getTheUgly());
        assertEquals(UnChecked.class, exceptionHandler.getTheUgly().getClass());
        assertSame(ExceptionThrower.UGLY, exceptionHandler.getTheUgly());
        assertEquals(ServiceRuntimeException.class, exceptionHandler.getServiceRuntimeException().getClass());
        assertEquals(ExceptionThrower.SERVICE_RUNTIME_EXCEPTION.getMessage(), exceptionHandler.getServiceRuntimeException().getMessage());
    }

    /**
     * Test exception handling over a remotable interface
     */
    @Test
    public void testRemote() {
        ExceptionHandler exceptionHandler = node.getService(ExceptionHandler.class, "mainRemote");
        exceptionHandler.testing();
        assertEquals(ExceptionThrower.SO_THEY_SAY, exceptionHandler.getTheGood());
        assertNotNull(exceptionHandler.getTheBad());
        assertEquals(Checked.class, exceptionHandler.getTheBad().getClass());
        assertNotSame(ExceptionThrower.BAD, exceptionHandler.getTheBad());
        assertNotNull(exceptionHandler.getTheUgly());
        assertEquals(UnChecked.class, exceptionHandler.getTheUgly().getClass());
        assertEquals(ServiceRuntimeException.class, exceptionHandler.getServiceRuntimeException().getClass());
        assertEquals(ExceptionThrower.SERVICE_RUNTIME_EXCEPTION.getMessage(), exceptionHandler.getServiceRuntimeException().getMessage());

        // [rfeng] We're not in a position to copy non business exceptions
        // assertNotSame(ExceptionThrower.UGLY, exceptionHandler.getTheUgly());

    }
    
    /**
     * Test exception handling over a remote binding
     */
    @Test
    public void testRemoteWS() {
        ExceptionHandler exceptionHandler = node.getService(ExceptionHandler.class, "mainRemoteWS");
        exceptionHandler.testing();
        assertEquals(ExceptionThrower.SO_THEY_SAY, exceptionHandler.getTheGood());
        assertNotNull(exceptionHandler.getTheBad());
        assertEquals(Checked.class, exceptionHandler.getTheBad().getClass());
        assertNotSame(ExceptionThrower.BAD, exceptionHandler.getTheBad());
        assertNotNull(exceptionHandler.getUncheckedException());
        assertEquals(ServiceRuntimeException.class, exceptionHandler.getUncheckedException().getClass());
        assertEquals(ServiceRuntimeException.class, exceptionHandler.getServiceRuntimeException().getClass());
        assertEquals("org.apache.tuscany.sca.interfacedef.util.FaultException: " + ExceptionThrower.SERVICE_RUNTIME_EXCEPTION.getMessage(), exceptionHandler.getServiceRuntimeException().getMessage());
        assertEquals(ServiceRuntimeException.class, exceptionHandler.getBindingException().getClass());
        assertEquals("org.apache.tuscany.sca.interfacedef.util.FaultException: " + ExceptionThrower.SERVICE_RUNTIME_EXCEPTION.getMessage(), exceptionHandler.getServiceRuntimeException().getMessage());
    }

    @BeforeClass
    public static void setUp() throws Exception {
        String location = ContributionLocationHelper.getContributionLocation("ExceptionTest.composite");
        node = NodeFactory.newInstance().createNode("ExceptionTest.composite", new Contribution("c1", location));
        node.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        node.stop();
    }

}
