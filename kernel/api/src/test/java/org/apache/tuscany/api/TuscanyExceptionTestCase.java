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
package org.apache.tuscany.api;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class TuscanyExceptionTestCase extends TestCase {
    private static final Throwable CAUSE = new Throwable("Cause");
    private static final String IDENTIFIER = "IDENTIFIER";
    private static final String MESSAGE = "Message";

    public void testNoArgConstructor() {
        TuscanyException ex = new DummyException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
        assertNull(ex.getIdentifier());
    }

    public void testMessageConstructor() {
        TuscanyException ex = new DummyException(MESSAGE);
        assertEquals(MESSAGE, ex.getMessage());
        assertNull(ex.getCause());
        assertNull(ex.getIdentifier());
    }

    public void testAppendBaseMessage() {
        TuscanyException ex = new DummyException(MESSAGE, IDENTIFIER);
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.appendBaseMessage(pw);
        assertEquals("Message [IDENTIFIER]", writer.toString());
    }

    public void testAppendBaseMessageNoIdentifier() {
        TuscanyException ex = new DummyException(MESSAGE);
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.appendBaseMessage(pw);
        assertEquals("Message", writer.toString());
    }

    public void testThrowableConstructor() {
        TuscanyException ex = new DummyException(CAUSE);
        assertEquals(CAUSE.getClass().getName() + ": " + CAUSE.getMessage(), ex.getMessage());
        assertEquals(CAUSE, ex.getCause());
        assertNull(ex.getIdentifier());
    }

    public void testMessageThrowableConstructor() {
        TuscanyException ex = new DummyException(MESSAGE, CAUSE);
        assertEquals(MESSAGE, ex.getMessage());
        assertEquals(CAUSE, ex.getCause());
        assertNull(ex.getIdentifier());
    }

    public void testGetMessage() throws Exception {
        TuscanyException e = new DummyException();
        e.getMessage();
    }

    public void testFullMessage() throws Exception {
        TuscanyException e = new DummyException("message", "foo");
        e.getMessage();
    }

    public static class DummyException extends TuscanyException {

        public DummyException() {
        }

        public DummyException(String message) {
            super(message);
        }


        public DummyException(String message, String identifier) {
            super(message, identifier);
        }

        public DummyException(String message, Throwable cause) {
            super(message, cause);
        }

        public DummyException(Throwable cause) {
            super(cause);
        }
    }
}
