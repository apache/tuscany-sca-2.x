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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class TuscanyRuntimeExceptionTestCase extends TestCase {
    private static final Throwable CAUSE = new Throwable("Cause");
    private static final String MESSAGE = "Message";
    private static final String IDENTIFIER = "IDENTIFIER";
    private static final String CONTEXT1 = "CONTEXT1";
    private static final String CONTEXT2 = "CONTEXT2";

    public void testNoArgConstructor() {
        TuscanyRuntimeException ex = new DummyException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
        assertNull(ex.getIdentifier());
        assertTrue(ex.returnContextNames().isEmpty());
    }

    public void testMessageConstructor() {
        TuscanyRuntimeException ex = new DummyException(MESSAGE);
        assertSame(MESSAGE, ex.getMessage());
        assertNull(ex.getCause());
        assertNull(ex.getIdentifier());
        assertTrue(ex.returnContextNames().isEmpty());
    }

    public void testThrowableConstructor() {
        TuscanyRuntimeException ex = new DummyException(CAUSE);
        assertEquals(CAUSE.getClass().getName() + ": " + CAUSE.getMessage(), ex.getMessage());
        assertSame(CAUSE, ex.getCause());
        assertNull(ex.getIdentifier());
        assertTrue(ex.returnContextNames().isEmpty());
    }

    public void testMessageThrowableConstructor() {
        TuscanyRuntimeException ex = new DummyException(MESSAGE, CAUSE);
        assertSame(MESSAGE, ex.getMessage());
        assertSame(CAUSE, ex.getCause());
        assertNull(ex.getIdentifier());
        assertTrue(ex.returnContextNames().isEmpty());
    }

    public void testIdentifier() {
        TuscanyRuntimeException ex = new DummyException(MESSAGE);
        ex.setIdentifier(IDENTIFIER);
        assertSame(IDENTIFIER, ex.getIdentifier());
        assertEquals(MESSAGE + " [" + IDENTIFIER + ']', ex.getMessage());
    }

    public void testContextStack() {
        TuscanyRuntimeException ex = new DummyException(MESSAGE);
        List<String> contexts = new ArrayList<String>();
        contexts.add(CONTEXT1);
        ex.addContextName(CONTEXT1);
        assertEquals(contexts, ex.returnContextNames());
        contexts.add(CONTEXT2);
        ex.addContextName(CONTEXT2);
        assertEquals(contexts, ex.returnContextNames());
    }

    public void testContextMessageWithNoIdentifier() {
        TuscanyRuntimeException ex = new DummyException(MESSAGE);
        ex.addContextName(CONTEXT1);
        ex.addContextName(CONTEXT2);
        assertEquals("Message\nContext stack trace: [CONTEXT2][CONTEXT1]", ex.getMessage());
    }


    public void testContextMessageWithIdentifier() {
        TuscanyRuntimeException ex = new DummyException(MESSAGE);
        ex.setIdentifier(IDENTIFIER);
        ex.addContextName(CONTEXT1);
        ex.addContextName(CONTEXT2);
        assertEquals("Message [IDENTIFIER]\nContext stack trace: [CONTEXT2][CONTEXT1]", ex.getMessage());
    }

    public static class DummyException extends TuscanyRuntimeException {
        public DummyException() {
        }

        public DummyException(String message) {
            super(message);
        }

        public DummyException(String message, Throwable cause) {
            super(message, cause);
        }

        public DummyException(Throwable cause) {
            super(cause);
        }
    }
}
