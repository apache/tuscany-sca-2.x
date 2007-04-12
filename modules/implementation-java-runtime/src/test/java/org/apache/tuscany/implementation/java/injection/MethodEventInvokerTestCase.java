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
package org.apache.tuscany.implementation.java.injection;

import java.lang.reflect.Method;

import org.apache.tuscany.implementation.java.injection.MethodEventInvoker;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class MethodEventInvokerTestCase extends TestCase {
    private Method privateMethod;
    private Method exceptionMethod;

    public void testIllegalAccess() throws Exception {
        MethodEventInvoker<MethodEventInvokerTestCase.Foo> injector = new MethodEventInvoker<Foo>(privateMethod);
        try {
            injector.invokeEvent(new Foo());
            fail();
        } catch (AssertionError e) {
            // expected
        }
    }

    public void testException() throws Exception {
        MethodEventInvoker<MethodEventInvokerTestCase.Foo> injector = new MethodEventInvoker<Foo>(exceptionMethod);
        try {
            injector.invokeEvent(new Foo());
            fail();
        } catch (RuntimeException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        privateMethod = MethodEventInvokerTestCase.Foo.class.getDeclaredMethod("hidden");
        exceptionMethod = MethodEventInvokerTestCase.Foo.class.getDeclaredMethod("exception");

    }

    private class Foo {

        public void foo() {
        }

        private void hidden() {
        }

        public void exception() {
            throw new RuntimeException();
        }

    }
}
