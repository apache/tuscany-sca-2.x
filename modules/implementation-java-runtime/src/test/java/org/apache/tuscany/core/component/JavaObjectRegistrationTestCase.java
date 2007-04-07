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

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.component.DuplicateNameException;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class JavaObjectRegistrationTestCase extends TestCase {
    private ComponentManager componentManager;

    public void testRegistration() throws Exception {
        MockComponent instance = new MockComponent();
        URI uri = URI.create("foo");
        JavaServiceContract<MockComponent> contract = new JavaServiceContract<MockComponent>(MockComponent.class) {
        };
        componentManager.registerJavaObject(uri, contract, instance);
        Component component = componentManager.getComponent(URI.create("foo"));
        assertTrue(component instanceof AtomicComponent);
        MockComponent resolvedInstance = (MockComponent) ((AtomicComponent) component).getTargetInstance();
        assertSame(instance, resolvedInstance);
    }

    public void testDuplicateRegistration() throws Exception {
        MockComponent instance = new MockComponent();
        URI uri = URI.create("foo");
        JavaServiceContract<MockComponent> contract = new JavaServiceContract<MockComponent>(MockComponent.class) {
        };
        componentManager.registerJavaObject(uri, contract, instance);
        try {
            componentManager.registerJavaObject(uri, contract, instance);
            fail();
        } catch (DuplicateNameException e) {
            // ok
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        componentManager = new ComponentManagerImpl();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private static class MockComponent {
        public String hello(String message) {
            return message;
        }
    }
}
