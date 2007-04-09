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
import org.osoa.sca.annotations.Reference;

public class CompositeServiceReferenceTest extends TestCase {
    @Reference
    public MyTotalService myService1;
    @Reference
    public MyTotalService myService2;
    @Reference
    public MyTotalService myService3;
    @Reference
    public MyTotalService myService4;
    @Reference
    public MyTotalService myService5;

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
        assertNotSame(myService1.nextHoliday(), myService1.nextHoliday(new Date()));
        assertEquals(myService1.getHolidays()[0], myService1.getHolidays(2007)[0]);

    }

    public void testServiceReferenceFromRecursive() {
        assertEquals(myService2.getHolidays()[0], myService2.getHolidays(2007)[0]);
        assertNotSame(myService2.nextHoliday(), myService2.nextHoliday(new Date()));

    }

    public void testServiceReferenceFromRecursiveUseService() {
        assertNotSame(myService4.nextHoliday(), myService4.nextHoliday(new Date()));
        assertEquals(myService4.getHolidays()[0], myService4.getHolidays(2007)[0]);
    }

    public void testServiceReferenceFromComponent() {
        assertEquals(myService3.getHolidays()[0], myService3.getHolidays(2007)[0]);
        assertNotSame(myService3.nextHoliday(), myService3.nextHoliday(new Date()));

    }

    public void testServiceReferenceFromComponentUseService() {
        assertNotSame(myService5.nextHoliday(), myService5.nextHoliday(new Date()));
        assertEquals(myService5.getHolidays()[0], myService5.getHolidays(2007)[0]);
    }
}
