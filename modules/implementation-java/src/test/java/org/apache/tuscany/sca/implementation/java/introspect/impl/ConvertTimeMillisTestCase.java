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
package org.apache.tuscany.sca.implementation.java.introspect.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class ConvertTimeMillisTestCase {

	@Test
    public void testConvertSeconds() throws Exception {
        assertEquals(10000L, MockProcessor.convertTimeMillis("10 seconds"));
        assertEquals(10000L, MockProcessor.convertTimeMillis("10 SECONDS"));
        try {
        	MockProcessor.convertTimeMillis("10seconds");
            fail();
        } catch (NumberFormatException e) {
            // expected
        }
    }

    @Test
    public void testConvertMinutes() throws Exception {
        assertEquals(600000L, MockProcessor.convertTimeMillis("10 minutes"));
        assertEquals(600000L, MockProcessor.convertTimeMillis("10 MINUTES"));
        try {
        	MockProcessor.convertTimeMillis("10minutes");
            fail();
        } catch (NumberFormatException e) {
            // expected
        }
    }

    @Test
    public void testConvertHours() throws Exception {
        assertEquals(36000000L, MockProcessor.convertTimeMillis("10 hours"));
        assertEquals(36000000L, MockProcessor.convertTimeMillis("10 HOURS"));
        try {
        	MockProcessor.convertTimeMillis("10hours");
            fail();
        } catch (NumberFormatException e) {
            // expected
        }
    }

    @Test
    public void testConvertDays() throws Exception {
        assertEquals(864000000L, MockProcessor.convertTimeMillis("10 days"));
        assertEquals(864000000L, MockProcessor.convertTimeMillis("10 DAYS"));
        try {
        	MockProcessor.convertTimeMillis("10days");
            fail();
        } catch (NumberFormatException e) {
            // expected
        }
    }

    @Test
    public void testConvertYears() throws Exception {
        assertEquals(315569260000L, MockProcessor.convertTimeMillis("10 years"));
        assertEquals(315569260000L, MockProcessor.convertTimeMillis("10 YEARS"));
        try {
        	MockProcessor.convertTimeMillis("10years");
            fail();
        } catch (NumberFormatException e) {
            // expected
        }
    }

    @Test
    public void testConvertDefault() throws Exception {
        assertEquals(10000L, MockProcessor.convertTimeMillis("10 "));
        assertEquals(10000L, MockProcessor.convertTimeMillis("10"));
    }

    @Test
    public void testInvalid() throws Exception {
        try {
        	MockProcessor.convertTimeMillis("foo");
            fail();
        } catch (NumberFormatException e) {
            // expected
        }
    }

    private class MockProcessor extends ConversationProcessor {
        
        public MockProcessor() {
            super(new DefaultAssemblyFactory());
        }
    }
}
