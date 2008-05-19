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

package org.apache.tuscany.sca.vtest.javaapi.annotations.oneway;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test class tests the Service annotation described in section 1.2.1 and
 * 1.8.17
 */
public class OneWayAnnotationTestCase {

    protected static String compositeName = "oneway.composite";
    protected static AService aService = null;
    protected static BService bService = null;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            ServiceFinder.init(compositeName);
            aService = ServiceFinder.getService(AService.class, "AComponent");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {

        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }

    /**
     * Line 384,385,386:<br>
     * <p>
     * Any method that returns "void" and has no declared exceptions may be
     * marked with an "@OneWay" annotation. This means that the method is
     * non-blocking and communication with the service provider may use a
     * binding that buffers the requests and sends it at some later time.
     * <p>
     * Line 1319, 1320:<br>
     * <p>
     * The "@OneWay" annotation type is used to annotate a Java interface method
     * to indicate that invocations will be dispatched in a non-blocking fashion
     * as described in the section on Asynchronous Programming.<br>
     * <p>
     * The serviceMethod on A is annotated with "@OneWay". The A implementation
     * delegates to b's method which includes a 2 seconds delay. So, this will
     * fail if the call to A is blocking.
     */
    @Test(timeout = 500)
    public void atOneWay1() throws Exception {
        aService.setNameOneWay("Some Name");
    }

}
