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

import org.apache.tuscany.api.SCARuntime;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

public class CompositeServiceReferenceTestCase extends TestCase {
    private MyTotalService myService1;
    private MyTotalService myService2;
    private MyTotalService myService3;
    private MyTotalService myService4;
    private MyTotalService myService5;
    private CompositeContext context;

    public void testPropertyWithServiceFromRecursive() {
        assertEquals("CARY", myService1.getLocation());
        assertEquals("2007", myService1.getYear());
    }

    public void testPropertyWithServiceInCompositeFromRecursive() {
        assertEquals("CARY", myService2.getLocation());
        assertEquals("2007", myService2.getYear());

    }

    public void testPropertyWithServiceInCompositeFromComponent() {
        assertEquals("CARY", myService3.getLocation());
        assertEquals("2007", myService3.getYear());
    }

    public void testServiceFromRecursive() {
        assertNotSame(myService1.nextHoliday(), myService1.nextHolidayByDate(new Date()));
        assertEquals(myService1.getHolidays()[0], myService1.getHolidaysByYear(2007)[0]);

    }

    public void testServiceReferenceFromRecursive() {
        assertEquals(myService2.getHolidays()[0], myService2.getHolidaysByYear(2007)[0]);
        assertNotSame(myService2.nextHoliday(), myService2.nextHolidayByDate(new Date()));

    }

    public void testServiceReferenceFromRecursiveUseService() {
        assertNotSame(myService4.nextHoliday(), myService4.nextHolidayByDate(new Date()));
        assertEquals(myService4.getHolidays()[0], myService4.getHolidaysByYear(2007)[0]);
    }

    public void testServiceReferenceFromComponent() {
        assertEquals(myService3.getHolidays()[0], myService3.getHolidaysByYear(2007)[0]);
        assertNotSame(myService3.nextHoliday(), myService3.nextHolidayByDate(new Date()));

    }

    public void testServiceReferenceFromComponentUseService() {
        assertNotSame(myService5.nextHoliday(), myService5.nextHolidayByDate(new Date()));
        assertEquals(myService5.getHolidays()[0], myService5.getHolidaysByYear(2007)[0]);
    }

    protected void setUp() throws Exception {
    	SCARuntime.start("CompositeTest.composite");
        context = CurrentCompositeContext.getContext();
        myService1 = context.locateService(MyTotalService.class, "MyTotalServiceFromRecursive");
        myService2 = context.locateService(MyTotalService.class, "MyTotalServiceInCompositeWithRecursive");
        myService3 = context.locateService(MyTotalService.class, "MyTotalServiceInCompositeWithComponentService");
        myService4 = context.locateService(MyTotalService.class, "MyTotalServiceInCompositeWithRecursiveUseService");
        myService5 =
            context.locateService(MyTotalService.class, "MyTotalServiceInCompositeWithComponentServiceUseService");
    }

    protected void tearDown() throws Exception {
    	SCARuntime.stop();
    }
}
