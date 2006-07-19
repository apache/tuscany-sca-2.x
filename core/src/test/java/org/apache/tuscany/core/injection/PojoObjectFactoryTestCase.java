/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.injection;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.ArrayList;

import org.apache.tuscany.spi.ObjectFactory;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class PojoObjectFactoryTestCase extends TestCase {

    private Constructor<Foo> ctor;

    public void testConstructorInjection() throws Exception {
        List<ObjectFactory> initializers = new ArrayList<ObjectFactory>();
        initializers.add(new SingletonObjectFactory<String>("foo"));
        PojoObjectFactory<Foo> factory = new PojoObjectFactory<Foo>(ctor, initializers);
        Foo foo = factory.getInstance();
        assertEquals("foo", foo.foo);
    }

    public void testConstructorInitializerInjection() throws Exception {
        PojoObjectFactory<Foo> factory = new PojoObjectFactory<Foo>(ctor);
        factory.setInitializerFactory(0, new SingletonObjectFactory<String>("foo"));
        Foo foo = factory.getInstance();
        assertEquals("foo", foo.foo);
    }

    protected void setUp() throws Exception {
        super.setUp();
        ctor = Foo.class.getConstructor(String.class);
    }

    private static class Foo {

        private String foo;

        public Foo(String foo) {
            this.foo = foo;
        }
    }
}
