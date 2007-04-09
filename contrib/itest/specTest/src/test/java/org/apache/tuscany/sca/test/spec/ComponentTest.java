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

import org.osoa.sca.annotations.Reference;

import junit.framework.TestCase;

public class ComponentTest extends TestCase {
    @Reference
    public MyService myService;
    @Reference
    public MyServiceByDate myServiceByDate;
    @Reference
    public MyListService myListService;
    @Reference
    public MyListServiceByYear myListServiceByYear;
    @Reference
    public MyService myNCService;
    @Reference
    public MyListService myListServiceFor2006;

    public void testDefaultProperty() {
        assertEquals("RTP", myService.getLocation());
        assertEquals("2006", myService.getYear());

    }

    public void testDefaultService() {
        assertEquals(myService.nextHoliday(), myServiceByDate.nextHoliday(new Date()));
        assertEquals(myListService.getHolidays()[0], myListServiceByYear.getHolidays(2006)[0]);

    }

    public void testOverrideProperty() {
        assertEquals("NC", myNCService.getLocation());
        assertEquals("2007", myNCService.getYear());
    }

    public void testServiceWithOverrideProperty() {
        assertFalse(myNCService.nextHoliday() == myService.nextHoliday());
        assertEquals(myListServiceFor2006.getHolidays()[0], myListServiceByYear.getHolidays(2006)[0]);

    }

    public void testContext() {
        assertNotNull("Service component name is null", myService.getComponentName());
        assertNotNull("service context is null", myService.getContext());

        System.out.println("Service component name :" + myService.getComponentName());
        System.out.println("service context :" + myService.getContext());
    }
}
