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
import org.osoa.sca.ComponentContext;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Reference;

public class ComponentServiceReferenceTest extends TestCase {
    @Reference
    public MyTotalService myService;

    @Context
    public ComponentContext context;

    public void testDefaultProperty() {
        assertEquals("NC", myService.getLocation());
        assertEquals("2007", myService.getYear());

    }

    public void testDefaultService() {
        assertNotSame(myService.nextHoliday(), myService.nextHoliday(new Date()));
        assertEquals(myService.getHolidays()[0], myService.getHolidays(2007)[0]);

    }

    public void testMyServiceContext() {
        assertNotNull("Service component name is null", myService.getComponentName());
        assertNotNull("service context is null", myService.getContext());

        System.out.println("Service component name :" + myService.getComponentName());
        System.out.println("service context :" + myService.getContext());

    }

    public void testContext() {
        assertNotNull("service context is null", context);

    }
}
