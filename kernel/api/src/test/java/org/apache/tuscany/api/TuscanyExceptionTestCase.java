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

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class TuscanyExceptionTestCase extends TestCase {
    private static final Throwable cause = new Throwable("Cause");
    private static final String message = "Message";
    private static final String identifier = "identifier";
    private static final String context1 = "context1";
    private static final String context2 = "context2";

    public void testNoArgConstructor() {
        TuscanyException ex = new DummyException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
        assertNull(ex.getIdentifier());
        assertTrue(ex.returnContextNames().isEmpty());
    }

    public void testMessageConstructor() {
        TuscanyException ex = new DummyException(message);
        assertSame(message, ex.getMessage());
        assertNull(ex.getCause());
        assertNull(ex.getIdentifier());
        assertTrue(ex.returnContextNames().isEmpty());
    }

    public void testThrowableConstructor() {
        TuscanyException ex = new DummyException(cause);
        assertEquals(cause.getClass().getName() + ": " + cause.getMessage(), ex.getMessage());
        assertSame(cause, ex.getCause());
        assertNull(ex.getIdentifier());
        assertTrue(ex.returnContextNames().isEmpty());
    }

    public void testMessageThrowableConstructor() {
        TuscanyException ex = new DummyException(message, cause);
        assertSame(message, ex.getMessage());
        assertSame(cause, ex.getCause());
        assertNull(ex.getIdentifier());
        assertTrue(ex.returnContextNames().isEmpty());
    }

    public void testIdentifier() {
        TuscanyException ex = new DummyException(message);
        ex.setIdentifier(identifier);
        assertSame(identifier, ex.getIdentifier());
        assertEquals(message + " [" + identifier + ']', ex.getMessage());
    }

    public void testContextStack() {
        TuscanyException ex = new DummyException(message);
        ArrayList<String> contexts = new ArrayList<String>();
        contexts.add(context1);
        ex.addContextName(context1);
        assertEquals(contexts, ex.returnContextNames());
        contexts.add(context2);
        ex.addContextName(context2);
        assertEquals(contexts, ex.returnContextNames());
    }

    public void testContextMessageWithNoIdentifier() {
        TuscanyException ex = new DummyException(message);
        ex.addContextName(context1);
        ex.addContextName(context2);
        assertEquals("Message\nContext stack trace: [context2][context1]", ex.getMessage());
    }


    public void testContextMessageWithIdentifier() {
        TuscanyException ex = new DummyException(message);
        ex.setIdentifier(identifier);
        ex.addContextName(context1);
        ex.addContextName(context2);
        assertEquals("Message [identifier]\nContext stack trace: [context2][context1]", ex.getMessage());
    }

    public static class DummyException extends TuscanyException {
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
