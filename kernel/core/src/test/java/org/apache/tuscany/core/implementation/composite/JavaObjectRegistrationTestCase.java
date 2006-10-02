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
package org.apache.tuscany.core.implementation.composite;

import javax.naming.ConfigurationException;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.DuplicateNameException;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;

/**
 * @version $Rev$ $Date$
 */
public class JavaObjectRegistrationTestCase extends TestCase {
    private CompositeComponent composite;

    public void testRegistration() throws Exception {
        MockComponent instance = new MockComponent();
        composite.registerJavaObject("foo", MockComponent.class, instance);
        MockComponent resolvedInstance = (MockComponent) composite.getSystemChild("foo").getServiceInstance();
        assertSame(instance, resolvedInstance);
    }

    public void testDuplicateRegistration() throws ConfigurationException {
        MockComponent instance = new MockComponent();
        composite.registerJavaObject("foo", MockComponent.class, instance);
        try {
            composite.registerJavaObject("foo", MockComponent.class, instance);
            fail();
        } catch (DuplicateNameException e) {
            // ok
        }
    }

    public void testSystemAutowireToObject() {
        MockComponent instance = new MockComponent();
        composite.registerJavaObject("foo", MockComponent.class, instance);
        assertSame(instance, composite.resolveSystemInstance(MockComponent.class));
        assertNull(composite.resolveSystemExternalInstance(MockComponent.class));
    }

    public void testApplicationAutowireToObject() {
        MockComponent instance = new MockComponent();
        composite.registerJavaObject("foo", MockComponent.class, instance);
        assertNull(composite.resolveInstance(MockComponent.class));
        assertNull(composite.resolveExternalInstance(MockComponent.class));
    }

    protected void setUp() throws Exception {
        super.setUp();
        composite = new CompositeComponentImpl(null, null, null, null);
        composite.start();
        composite.publish(new CompositeStart(this, null));
    }

    protected void tearDown() throws Exception {
        composite.publish(new CompositeStop(this, null));
        composite.stop();
        super.tearDown();
    }

    private static class MockComponent {
        public String hello(String message) {
            return message;
        }
    }
}
