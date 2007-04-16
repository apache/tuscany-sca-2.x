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

import java.util.Date;

import junit.framework.TestCase;

import org.apache.tuscany.host.embedded.SCARuntime;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;
import org.osoa.sca.RequestContext;

public class ComponentTestCase extends TestCase {
    private MyService myService;
    private MyServiceByDate myServiceByDate;
    private MyListService myListService;
    private MyListServiceByYear myListServiceByYear;
    private MyService myNCService;
    private MyListService myListServiceFor2006;

    private CompositeContext context;

    public void testDefaultProperty() {
        assertEquals("RTP", myService.getLocation());
        assertEquals("2006", myService.getYear());

    }

    public void testDefaultService() {
        assertEquals(myService.nextHoliday(), myServiceByDate.nextHolidayByDate(new Date()));
        assertEquals(myListService.getHolidays()[0], myListServiceByYear.getHolidaysByYear(2006)[0]);

    }

    public void testOverrideProperty() {
        assertEquals("NC", myNCService.getLocation());
        assertEquals("2007", myNCService.getYear());
    }

    public void testServiceWithOverrideProperty() {
        assertFalse(myNCService.nextHoliday() == myService.nextHoliday());
        assertEquals(myListServiceFor2006.getHolidays()[0], myListServiceByYear.getHolidaysByYear(2006)[0]);

    }

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

    private void test(CompositeContext context) {
        assertNotNull("composite name is null", context.getName());
        assertNotNull("composite URI is null", context.getURI());

        System.out.println("composite name :" + context.getName());
        System.out.println("composite URI:" + context.getURI());

        if (context.getRequestContext() == null)
            System.out.println("Request context:" + context.getRequestContext());
        else
            display(context.getRequestContext());
    }

    private void display(RequestContext context) {
        System.out.println("\tService name:" + context.getServiceName());
        System.out.println("\tSecurity subject:" + context.getSecuritySubject());
        //System.out.println("\tService reference:" + (Object)context.getServiceReference());

    }

    protected void setUp() throws Exception {
        SCARuntime.start("CompositeTest.composite");
        context = CurrentCompositeContext.getContext();
        myService = context.locateService(MyService.class, "MyService");
        myServiceByDate = context.locateService(MyServiceByDate.class, "MyServiceByDate");
        myListService = context.locateService(MyListService.class, "MyListService");
        myListServiceByYear = context.locateService(MyListServiceByYear.class, "MyListServiceByYear");
        myNCService = context.locateService(MyService.class, "MyNCService");
        myListServiceFor2006 = context.locateService(MyListService.class, "MyListServiceFor2006");
    }

    protected void tearDown() throws Exception {
    	SCARuntime.stop();
    }
}
