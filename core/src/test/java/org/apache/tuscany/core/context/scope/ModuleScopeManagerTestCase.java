/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.core.context.scope;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.InstanceContextFactory;
import org.apache.tuscany.core.context.Lifecycle;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.core.context.impl.AbstractLifecycle;

/**
 * @version $Rev$ $Date$
 */
public class ModuleScopeManagerTestCase extends TestCase {
    private ModuleScopeManager scopeManager;
    private MockInstanceFactory factory1;

    public void testLifecycle() {
        InstanceContext instanceContext = scopeManager.getInstance(factory1);
        assertSame(factory1.instanceContext, instanceContext);
        assertEquals(Lifecycle.STARTED, instanceContext.getLifecycleState());
        scopeManager.stop();
        assertEquals(Lifecycle.STOPPED, instanceContext.getLifecycleState());
    }

    public void testCaching() {
        InstanceContext instanceContext = scopeManager.getInstance(factory1);
        assertSame(factory1.instanceContext, instanceContext);
        InstanceContext instanceContext2 = scopeManager.getInstance(factory1);
        assertSame(instanceContext, instanceContext2);
    }

    protected void setUp() throws Exception {
        super.setUp();
        scopeManager = new ModuleScopeManager();
        factory1 = new MockInstanceFactory();
    }

    private class MockInstanceFactory implements InstanceContextFactory {
        private MockInstanceContext instanceContext;

        public InstanceContext createContext() throws TargetException {
            instanceContext = new MockInstanceContext();
            return instanceContext;
        }
    }

    private class MockInstanceContext extends AbstractLifecycle implements InstanceContext {
        public Object getInstance() {
            return null;
        }
    }
}
