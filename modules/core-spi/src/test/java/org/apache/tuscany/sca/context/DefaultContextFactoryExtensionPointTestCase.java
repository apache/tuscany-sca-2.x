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
package org.apache.tuscany.sca.context;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;

import org.junit.Assert;
import org.junit.Test;

/**
 * This test case will test the class
 * org.apache.tuscany.sca.context.DefaultContextFactoryExtensionPoint
 *
 * $Date$ $Rev$
 */
public class DefaultContextFactoryExtensionPointTestCase {

    /**
     * Tests adding/getting/removing a factory with no interfaces
     */
    @Test
    public void testFactoryWithNoInterfaces() {
        Object factory = new FactoryWithNoInterfaces();
        Class<?>[] ifaces = {};
        addGetRemoveFactory(factory, ifaces);
    }

    /**
     * Tests adding/getting/removing a factory with one interface
     */
    @Test
    public void testFactoryWithOneInterface() {
        Object factory = new FactoryWithOneInterface();
        Class<?>[] ifaces = { FactoryOneInterface.class };
        addGetRemoveFactory(factory, ifaces);
    }

    /**
     * Tests adding/getting/removing a factory with two interfaces
     */
    @Test
    public void testFactoryWithTwoInterfaces() {
        Object factory = new FactoryWithTwoInterfaces();
        Class<?>[] ifaces = { FactoryTwoInterfacesA.class, FactoryTwoInterfacesB.class };
        addGetRemoveFactory(factory, ifaces);
    }

    /**
     * Tests having multiple factories registered
     */
    @Test
    public void testMultipleFactories() {
        // Create new factories
        FactoryWithOneInterface factory1 = new FactoryWithOneInterface();
        FactoryWithTwoInterfaces factory2 = new FactoryWithTwoInterfaces();

        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();

        // Register the factories
        DefaultContextFactoryExtensionPoint ctxFactory = new DefaultContextFactoryExtensionPoint(registry);
        ctxFactory.addFactory(factory1);
        ctxFactory.addFactory(factory2);

        // Re-get each of the factories
        FactoryOneInterface regotFactory1 = ctxFactory.getFactory(FactoryOneInterface.class);
        Assert.assertNotNull(regotFactory1);
        Assert.assertSame(factory1, regotFactory1);
        FactoryTwoInterfacesA regotFactory2A = ctxFactory.getFactory(FactoryTwoInterfacesA.class);
        Assert.assertNotNull(regotFactory2A);
        Assert.assertSame(factory2, regotFactory2A);
        FactoryTwoInterfacesB regotFactory2B = ctxFactory.getFactory(FactoryTwoInterfacesB.class);
        Assert.assertNotNull(regotFactory1);
        Assert.assertSame(factory2, regotFactory2B);
    }

    /**
     * Tests passing in null to addFactory()
     */
    @Test
    public void testAddingNullFactory() {

        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        DefaultContextFactoryExtensionPoint ctxFactory = new DefaultContextFactoryExtensionPoint(registry);
        try {
            ctxFactory.addFactory(null);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // As expected
        }
    }

    /**
     * Test passing in null to removeFactory()
     */
    @Test
    public void testRemovingNullFactory() {
        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        DefaultContextFactoryExtensionPoint ctxFactory = new DefaultContextFactoryExtensionPoint(registry);
        try {
            ctxFactory.removeFactory(null);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // As expected
        }
    }

    /**
     * Test passing in null to getFactory()
     */
    @Test
    public void testGetNullFactory() {
        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        DefaultContextFactoryExtensionPoint ctxFactory = new DefaultContextFactoryExtensionPoint(registry);
        try {
            ctxFactory.getFactory(null);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // As expected
        }
    }

    /**
     * Utility method for testing adding and removing a factory
     *
     * @param factory The factory class to test
     * @param factoryInterfaces The list of interfaces implemented by the factory
     */
    private void addGetRemoveFactory(Object factory, Class<?>[] factoryInterfaces) {
        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        DefaultContextFactoryExtensionPoint ctxFactory = new DefaultContextFactoryExtensionPoint(registry);

        // Make sure factory not already present
        for (Class<?> iface : factoryInterfaces) {
            Assert.assertNull(ctxFactory.getFactory(iface));
        }

        // Add the factory
        ctxFactory.addFactory(factory);

        // Make sure we can get the factory recently registered factory
        for (Class<?> iface : factoryInterfaces) {
            Object regot = ctxFactory.getFactory(iface);
            Assert.assertNotNull(regot);
            Assert.assertSame(factory, regot);
        }

        // Remove the factory
        ctxFactory.removeFactory(factory);

        // Make sure factory is no longer registered
        for (Class<?> iface : factoryInterfaces) {
            Assert.assertNull(ctxFactory.getFactory(iface));
        }
    }

    /**
     * Simple factory with no interfaces
     */
    private class FactoryWithNoInterfaces {
    }

    /**
     * Simple interface for the factory with one interface
     */
    private interface FactoryOneInterface {
    }

    /**
     * Simple factory with one interface
     */
    private class FactoryWithOneInterface implements FactoryOneInterface {
    }

    /**
     * Simple interface for the factory with two interfaces
     */
    private interface FactoryTwoInterfacesA {
    }

    /**
     * Simple interface for the factory with two interfaces
     */
    private interface FactoryTwoInterfacesB {
    }

    /**
     * Simple factory with two interfaces
     */
    private class FactoryWithTwoInterfaces implements FactoryTwoInterfacesA, FactoryTwoInterfacesB {
    }
}
