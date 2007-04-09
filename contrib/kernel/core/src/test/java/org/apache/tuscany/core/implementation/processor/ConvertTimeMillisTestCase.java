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
package org.apache.tuscany.core.implementation.processor;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class ConvertTimeMillisTestCase extends TestCase {
    private MockProcessor registy;

    public void testConvertSeconds() throws Exception {
        assertEquals(10000L, registy.convertTimeMillis("10 seconds"));
        assertEquals(10000L, registy.convertTimeMillis("10 SECONDS"));
        try {
            registy.convertTimeMillis("10seconds");
            fail();
        } catch (NumberFormatException e) {
            // expected
        }
    }

    public void testConvertMinutes() throws Exception {
        assertEquals(600000L, registy.convertTimeMillis("10 minutes"));
        assertEquals(600000L, registy.convertTimeMillis("10 MINUTES"));
        try {
            registy.convertTimeMillis("10minutes");
            fail();
        } catch (NumberFormatException e) {
            // expected
        }
    }

    public void testConvertHours() throws Exception {
        assertEquals(36000000L, registy.convertTimeMillis("10 hours"));
        assertEquals(36000000L, registy.convertTimeMillis("10 HOURS"));
        try {
            registy.convertTimeMillis("10hours");
            fail();
        } catch (NumberFormatException e) {
            // expected
        }
    }

    public void testConvertDays() throws Exception {
        assertEquals(864000000L, registy.convertTimeMillis("10 days"));
        assertEquals(864000000L, registy.convertTimeMillis("10 DAYS"));
        try {
            registy.convertTimeMillis("10days");
            fail();
        } catch (NumberFormatException e) {
            // expected
        }
    }

    public void testConvertYears() throws Exception {
        assertEquals(315569260000L, registy.convertTimeMillis("10 years"));
        assertEquals(315569260000L, registy.convertTimeMillis("10 YEARS"));
        try {
            registy.convertTimeMillis("10years");
            fail();
        } catch (NumberFormatException e) {
            // expected
        }
    }

    public void testConvertDefault() throws Exception {
        assertEquals(10000L, registy.convertTimeMillis("10 "));
        assertEquals(10000L, registy.convertTimeMillis("10"));
    }

    public void testInvalid() throws Exception {
        try {
            registy.convertTimeMillis("foo");
            fail();
        } catch (NumberFormatException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        registy = new MockProcessor();
    }

    private class MockProcessor extends ConversationProcessor {

        @Override
        protected long convertTimeMillis(String expr) throws NumberFormatException {
            return super.convertTimeMillis(expr);
        }
    }
}
