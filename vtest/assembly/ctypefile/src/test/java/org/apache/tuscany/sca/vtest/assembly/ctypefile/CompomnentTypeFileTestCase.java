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

package org.apache.tuscany.sca.vtest.assembly.ctypefile;

import junit.framework.Assert;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class CompomnentTypeFileTestCase {

    protected static String compositeName = "typefile.composite";
    protected static AService aService = null;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            ServiceFinder.init(compositeName);
            aService = ServiceFinder.getService(AService.class, "AComponent/AService");
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
     * Lines 435-439:
     * <p>
     * Step two covers the cases where introspection of the implementation is
     * not possible or where it does not provide complete information and it
     * involves looking for an SCA component type file. Component type
     * information found in the component type file must be compatible with the
     * equivalent information found from inspection of the implementation.
     * <p>
     * Lines 441-444:
     * <p>
     * In the ideal case, the component type information is determined by
     * inspecting the implementation, for example as code annotations. The
     * component type file provides a mechanism for the provision of component
     * type information for implementation types where the information cannot be
     * determined by inspecting the implementation.
     */
    @Test
    public void typeFile1() throws Exception {
        Assert.assertSame("SomeStateFromB", aService.getState());
    }

    /**
     * Lines 439-449:
     * <p>
     * The component type file can specify partial information, with the
     * remainder being derived from the implementation.
     * <p>
     * The first test makes use of the reference to b which is provided by the
     * type file. The second test makes use of the reference to b2 which is
     * provided via annotation
     */
    @Test
    public void typeFile2() throws Exception {
        Assert.assertSame("SomeStateFromB", aService.getState());
        Assert.assertSame("SomeStateFromB", aService.getState2());
    }

    /**
     * Lines 450-451:
     * <p>
     * The componentType element can contain Service elements, Reference
     * elements and Property elements.
     */
    @Test
    public void typeFile3() throws Exception {
        Assert.assertEquals("SomePropertyValue", aService.getBProperty());
    }

}
