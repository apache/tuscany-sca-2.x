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
package org.apache.tuscany.core.component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class SystemSingletonAtomicComponentTestCase extends TestCase {

    public void testGetInstance() throws TargetException {
        JavaServiceContract<Foo> contract = new JavaServiceContract<Foo>(Foo.class) {

        };
        FooImpl foo = new FooImpl();
        SystemSingletonAtomicComponent<Foo, FooImpl> component =
            new SystemSingletonAtomicComponent<Foo, FooImpl>(URI.create("foo"), contract, foo);
        assertEquals(foo, component.getTargetInstance());
    }

    public void testGetInstanceMultipleServices() throws TargetException {
        FooImpl foo = new FooImpl();
        List<JavaServiceContract<?>> services = new ArrayList<JavaServiceContract<?>>();
        services.add(new JavaServiceContract<Foo>(Foo.class) {
        });
        services.add(new JavaServiceContract<Bar>(Bar.class) {
        });
        SystemSingletonAtomicComponent<Foo, FooImpl> component =
            new SystemSingletonAtomicComponent<Foo, FooImpl>(URI.create("foo"), services, foo);
        assertEquals(foo, component.getTargetInstance());
    }

    public void testOptimizable() {
        JavaServiceContract<Foo> contract = new JavaServiceContract<Foo>(Foo.class) {
        };
        FooImpl foo = new FooImpl();
        SystemSingletonAtomicComponent<Foo, FooImpl> component =
            new SystemSingletonAtomicComponent<Foo, FooImpl>(URI.create("foo"), contract, foo);
        assertTrue(component.isOptimizable());
    }


    protected void setUp() throws Exception {
        super.setUp();
    }

    private interface Foo {

    }

    private interface Bar {

    }

    private class FooImpl implements Foo, Bar {

    }
}
