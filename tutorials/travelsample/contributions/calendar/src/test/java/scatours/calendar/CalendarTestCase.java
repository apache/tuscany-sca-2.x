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
package scatours.calendar;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tuscanyscatours.calendar.Calendar;

/**
 * This shows how to test the Calculator service component.
 */
public class CalendarTestCase {

    private SCANode node;

    @Before
    public void startNode() throws Exception {
        node =
            SCANodeFactory.newInstance().createSCANode("calendar.composite",
                                                       new SCAContribution("calendar", "./target/classes"),
                                                       new SCAContribution("calendar-test", "./target/test-classes"));
        node.start();
    }

    @Test
    public void testCalendar() throws Exception {
        Calendar calendar = ((SCAClient)node).getService(Calendar.class, "Calendar");
        System.out.println(calendar.getEndDate("07/10/96 04:05", 3));
    }

    @After
    public void stopNode() throws Exception {
        node.stop();
    }
}
