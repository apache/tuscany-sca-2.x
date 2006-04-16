/**
 *
 *  Copyright 2005 BEA Systems Inc.
 *  Copyright 2005 International Business Machines Corporation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.system.context;

import junit.framework.TestCase;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.SystemCompositeContext;
import org.apache.tuscany.core.context.DuplicateNameException;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.runtime.RuntimeContextImpl;

/**
 * @version $Rev$ $Date$
 */
public class SystemObjectRegistrationTestCase extends TestCase {
    private RuntimeContext runtime;
    private SystemCompositeContext systemContext;

    public void testRegistration() throws ConfigurationException {
        MockComponent instance = new MockComponent();
        systemContext.registerJavaObject("foo", MockComponent.class, instance);
        assertSame(instance, systemContext.getContext("foo").getInstance(null));
    }

    public void testDuplicateRegistration() throws ConfigurationException {
        MockComponent instance = new MockComponent();
        systemContext.registerJavaObject("foo", MockComponent.class, instance);
        try {
            systemContext.registerJavaObject("foo", MockComponent.class, instance);
            fail();
        } catch (DuplicateNameException e) {
            // ok
        }
    }

    public void testAutowireToObject() throws ConfigurationException {
        MockComponent instance = new MockComponent();
        systemContext.registerJavaObject("foo", MockComponent.class, instance);
        assertSame(instance, systemContext.resolveInstance(MockComponent.class));
        assertNull(systemContext.resolveExternalInstance(MockComponent.class));
    }

    protected void setUp() throws Exception {
        super.setUp();
        runtime = new RuntimeContextImpl();
        runtime.start();
        systemContext = runtime.getSystemContext();
        systemContext.publish(new ModuleStart(this));
    }

    protected void tearDown() throws Exception {
        runtime.stop();
        super.tearDown();
    }

    private static class MockComponent {
        public String hello(String message) {
            return message;
        }
    }
}
