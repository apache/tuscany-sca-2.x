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

import junit.framework.TestCase;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.interfacedef.java.impl.DefaultJavaFactory;
import org.apache.tuscany.spi.component.TargetException;

/**
 * @version $Rev$ $Date$
 */
public class SingletonAtomicComponentTestCase extends TestCase {
    private <S> ComponentService createContract(Class<S> type) {
        AssemblyFactory factory = new DefaultAssemblyFactory();
        ComponentService contract = factory.createComponentService();
        JavaInterface javaInterface = new DefaultJavaFactory().createJavaInterface();
        javaInterface.setJavaClass(type);
        contract.setInterface(javaInterface);
        return contract;
    }

    public void testGetInstance() throws TargetException {
        ComponentService contract = createContract(Foo.class);
        FooImpl foo = new FooImpl();
        SingletonAtomicComponent<Foo> component = new SingletonAtomicComponent<Foo>(URI.create("foo"), contract, foo);
        assertEquals(foo, component.getTargetInstance());
    }

    public void testGetInstanceMultipleServices() throws TargetException {
        FooImpl foo = new FooImpl();
        ComponentService contract1 = createContract(Foo.class);
        ComponentService contract2 = createContract(Bar.class);

        List<ComponentService> services = new ArrayList<ComponentService>();
        services.add(contract1);
        services.add(contract2);
        SingletonAtomicComponent<Foo> component = new SingletonAtomicComponent<Foo>(URI.create("foo"), services, foo);
        assertEquals(foo, component.getTargetInstance());
    }

    public void testOptimizable() {
        ComponentService contract = createContract(Foo.class);
        FooImpl foo = new FooImpl();
        SingletonAtomicComponent<Foo> component = new SingletonAtomicComponent<Foo>(URI.create("foo"), contract, foo);
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
