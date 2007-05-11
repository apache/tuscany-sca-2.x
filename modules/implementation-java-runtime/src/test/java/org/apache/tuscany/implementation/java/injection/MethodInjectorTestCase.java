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

import org.apache.tuscany.implementation.java.injection.MethodInjector;
import org.apache.tuscany.implementation.java.injection.SingletonObjectFactory;
import org.apache.tuscany.sca.spi.ObjectCreationException;
import org.apache.tuscany.sca.spi.ObjectFactory;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class MethodInjectorTestCase extends TestCase {
    private Method fooMethod;
    private Method privateMethod;
    private Method exceptionMethod;

    public void testIllegalArgument() throws Exception {
        ObjectFactory<Object> factory = new SingletonObjectFactory<Object>(new Object());
        MethodInjector<Foo> injector = new MethodInjector<Foo>(fooMethod, factory);
        try {
            injector.inject(new Foo());
            fail();
        } catch (ObjectCreationException e) {
            // expected
        }
    }

    public void testException() throws Exception {
        ObjectFactory<Object> factory = new SingletonObjectFactory<Object>("foo");
        MethodInjector<Foo> injector = new MethodInjector<Foo>(exceptionMethod, factory);
        try {
            injector.inject(new Foo());
            fail();
        } catch (RuntimeException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        fooMethod = Foo.class.getMethod("foo", String.class);
        privateMethod = Foo.class.getDeclaredMethod("hidden", String.class);
        exceptionMethod = Foo.class.getDeclaredMethod("exception", String.class);

    }

    private class Foo {

        public void foo(String bar) {
        }

        private void hidden(String bar) {
        }

        public void exception(String bar) {
            throw new RuntimeException();
        }

    }
}
