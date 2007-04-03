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

import junit.framework.TestCase;

import org.apache.tuscany.api.SCARuntime;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

public class ComponentServiceReferenceListTestCase extends TestCase {
    private MyListService myListService;
    private MyListServiceByYear myListServiceByYear;

    private CompositeContext context;

    public void testDefaultProperty() {
        assertEquals("2007", myListService.getYear());

    }

    public void testDefaultService() {
        assertEquals(myListService.getHolidays()[0], myListServiceByYear.getHolidaysByYear(2007)[0]);

    }

    protected void setUp() throws Exception {
    	SCARuntime.start("CompositeTest.composite");
        context = CurrentCompositeContext.getContext();
        myListService = context.locateService(MyListService.class, "MyNewListService");
        myListServiceByYear = context.locateService(MyListServiceByYear.class, "MyNewListService");
    }

    protected void tearDown() throws Exception {
    	SCARuntime.stop();
    }
}
