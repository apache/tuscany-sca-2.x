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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

import java.util.Date;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ComponentTestCase {
    private static MyService myService;
    private static MyServiceByDate myServiceByDate;
    private static MyListService myListService;
    private static MyListServiceByYear myListServiceByYear;
    private static MyService myNCService;
    private static MyListService myListServiceFor2006;

    private static SCADomain domain;

    @Test
    public void testDefaultProperty() {
        assertEquals("RTP", myService.getLocation());
        assertEquals("2006", myService.getYear());

    }

    @Test
    public void testDefaultService() {
        assertEquals(myService.nextHoliday(), myServiceByDate.nextHolidayByDate(new Date()));
        assertEquals(myListService.getHolidays()[0], myListServiceByYear.getHolidaysByYear(2006)[0]);

    }

    @Test
    public void testOverrideProperty() {
        assertEquals("NC", myNCService.getLocation());
        assertEquals("2007", myNCService.getYear());
    }

    @Test
    public void testServiceWithOverrideProperty() {
        assertFalse(myNCService.nextHoliday() == myService.nextHoliday());
        assertEquals(myListServiceFor2006.getHolidays()[0], myListServiceByYear.getHolidaysByYear(2006)[0]);

    }

    @Test
    public void testContext() {
        //FIXME TUSCANY-1174 - Need support for @ComponentName
        /*
        assertNotNull("Service component name is null", myService.getComponentName());
        assertNotNull("service context is null", myService.getContext());

        System.out.println("Service component name :" + myService.getComponentName());
        System.out.println("service context :" + myService.getContext());

        test(context);
        */
    }

    @BeforeClass
    public static void init() throws Exception {
        domain = SCADomain.newInstance("CompositeTest.composite");
        myService = domain.getService(MyService.class, "MyService");
        myServiceByDate = domain.getService(MyServiceByDate.class, "MyServiceByDate");
        myListService = domain.getService(MyListService.class, "MyListService");
        myListServiceByYear = domain.getService(MyListServiceByYear.class, "MyListServiceByYear");
        myNCService = domain.getService(MyService.class, "MyNCService");
        myListServiceFor2006 = domain.getService(MyListService.class, "MyListServiceFor2006");
    }

    @AfterClass
    public static void destroy() throws Exception {
        domain.close();
    }
    
}
