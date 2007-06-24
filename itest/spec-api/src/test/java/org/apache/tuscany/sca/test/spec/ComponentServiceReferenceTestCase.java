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

import org.apache.tuscany.sca.host.embedded.SCADomain;

public class ComponentServiceReferenceTestCase extends TestCase {
    private MyTotalService myService;
    private SCADomain domain;

    public void testDefaultProperty() {
        assertEquals("NC", myService.getLocation());
        assertEquals("2007", myService.getYear());

    }

    public void testDefaultService() {
        assertNotSame(myService.nextHoliday(), myService.nextHolidayByDate(new Date()));
        assertEquals(myService.getHolidays()[0], myService.getHolidaysByYear(2007)[0]);

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

    protected void setUp() throws Exception {
    	domain = SCADomain.newInstance("CompositeTest.composite");
        myService = domain.getService(MyTotalService.class, "MyTotalService");
    }

    protected void tearDown() throws Exception {
    	domain.close();
    }
}
