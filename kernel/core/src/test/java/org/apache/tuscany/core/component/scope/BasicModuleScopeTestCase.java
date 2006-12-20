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
package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.SystemAtomicComponent;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.component.WorkContext;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.mock.component.ModuleScopeInitDestroyComponent;

/**
 * @version $$Rev$$ $$Date$$
 */
public class BasicModuleScopeTestCase extends TestCase {

    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private PojoObjectFactory<?> factory;

    public void testLifecycleManagement() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        CompositeScopeContainer scopeContext = new CompositeScopeContainer(null);
        scopeContext.start();
        SystemAtomicComponent component = createComponent(scopeContext);
        // start the request
        ModuleScopeInitDestroyComponent o1 =
            (ModuleScopeInitDestroyComponent) scopeContext.getInstance(component);
        assertTrue(o1.isInitialized());
        assertFalse(o1.isDestroyed());
        ModuleScopeInitDestroyComponent o2 =
            (ModuleScopeInitDestroyComponent) scopeContext.getInstance(component);
        assertEquals(o1, o2);
        scopeContext.onEvent(new CompositeStop(this, null));
        assertTrue(o1.isDestroyed());
        scopeContext.stop();
    }


    public void testGetAssociatedInstance() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        CompositeScopeContainer scopeContext = new CompositeScopeContainer(null);
        scopeContext.start();
        SystemAtomicComponent component = createComponent(scopeContext);
        // start the request
        scopeContext.getInstance(component);
        scopeContext.getAssociatedInstance(component);
    }

    public void testGetAssociatedInstanceNonExistent() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        CompositeScopeContainer scopeContext = new CompositeScopeContainer(null);
        scopeContext.start();
        SystemAtomicComponent component = createComponent(scopeContext);
        // start the request
        try {
            scopeContext.getAssociatedInstance(component);
            fail();
        } catch (TargetNotFoundException e) {
            // expected
        }
    }

    public void testModuleIsolation() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        CompositeScopeContainer scopeContext = new CompositeScopeContainer(null);
        scopeContext.start();

        SystemAtomicComponent component = createComponent(scopeContext);

        ModuleScopeInitDestroyComponent o1 =
            (ModuleScopeInitDestroyComponent) scopeContext.getInstance(component);
        assertTrue(o1.isInitialized());
        assertFalse(o1.isDestroyed());

        ModuleScopeInitDestroyComponent o2 =
            (ModuleScopeInitDestroyComponent) scopeContext.getInstance(component);
        assertSame(o1, o2);
        scopeContext.onEvent(new CompositeStop(this, null));
        assertTrue(o1.isDestroyed());
        scopeContext.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new PojoObjectFactory<ModuleScopeInitDestroyComponent>(
            ModuleScopeInitDestroyComponent.class.getConstructor((Class[]) null));
        initInvoker = new MethodEventInvoker<Object>(ModuleScopeInitDestroyComponent.class.getMethod(
            "init", (Class[]) null));
        destroyInvoker = new MethodEventInvoker<Object>(ModuleScopeInitDestroyComponent.class.getMethod(
            "destroy", (Class[]) null));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private SystemAtomicComponent createComponent(ScopeContainer scopeContainer) {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scopeContainer);
        configuration.addServiceInterface(ModuleScopeInitDestroyComponent.class);
        configuration.setInstanceFactory(factory);
        configuration.setInitInvoker(initInvoker);
        configuration.setDestroyInvoker(destroyInvoker);
        configuration.setName("foo");
        SystemAtomicComponentImpl context = new SystemAtomicComponentImpl(configuration);
        context.start();
        return context;
    }
}
