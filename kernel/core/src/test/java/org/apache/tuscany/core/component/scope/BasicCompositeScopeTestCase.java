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

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetNotFoundException;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.mock.component.CompositeScopeInitDestroyComponent;

/**
 * @version $$Rev$$ $$Date$$
 */
public class BasicCompositeScopeTestCase extends TestCase {

    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private PojoObjectFactory<?> factory;

    public void testLifecycleManagement() throws Exception {
        CompositeScopeContainer scopeContext = new CompositeScopeContainer(null);
        scopeContext.start();
        AtomicComponent component = createComponent(scopeContext);
        // start the request
        CompositeScopeInitDestroyComponent o1 =
            (CompositeScopeInitDestroyComponent) scopeContext.getInstance(component);
        assertTrue(o1.isInitialized());
        assertFalse(o1.isDestroyed());
        CompositeScopeInitDestroyComponent o2 =
            (CompositeScopeInitDestroyComponent) scopeContext.getInstance(component);
        assertEquals(o1, o2);
        scopeContext.onEvent(new CompositeStop(this, null));
        assertTrue(o1.isDestroyed());
        scopeContext.stop();
    }


    public void testGetAssociatedInstance() throws Exception {
        CompositeScopeContainer scopeContext = new CompositeScopeContainer(null);
        scopeContext.start();
        AtomicComponent component = createComponent(scopeContext);
        // start the request
        scopeContext.getInstance(component);
        scopeContext.getAssociatedInstance(component);
    }

    public void testGetAssociatedInstanceNonExistent() throws Exception {
        CompositeScopeContainer scopeContext = new CompositeScopeContainer(null);
        scopeContext.start();
        AtomicComponent component = createComponent(scopeContext);
        // start the request
        try {
            scopeContext.getAssociatedInstance(component);
            fail();
        } catch (TargetNotFoundException e) {
            // expected
        }
    }

    public void testCompositeIsolation() throws Exception {
        CompositeScopeContainer scopeContext = new CompositeScopeContainer(null);
        scopeContext.start();

        AtomicComponent component = createComponent(scopeContext);

        CompositeScopeInitDestroyComponent o1 =
            (CompositeScopeInitDestroyComponent) scopeContext.getInstance(component);
        assertTrue(o1.isInitialized());
        assertFalse(o1.isDestroyed());

        CompositeScopeInitDestroyComponent o2 =
            (CompositeScopeInitDestroyComponent) scopeContext.getInstance(component);
        assertSame(o1, o2);
        scopeContext.onEvent(new CompositeStop(this, null));
        assertTrue(o1.isDestroyed());
        scopeContext.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new PojoObjectFactory<CompositeScopeInitDestroyComponent>(
            CompositeScopeInitDestroyComponent.class.getConstructor((Class[]) null));
        initInvoker = new MethodEventInvoker<Object>(CompositeScopeInitDestroyComponent.class.getMethod(
            "init", (Class[]) null));
        destroyInvoker = new MethodEventInvoker<Object>(CompositeScopeInitDestroyComponent.class.getMethod(
            "destroy", (Class[]) null));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private AtomicComponent createComponent(ScopeContainer scopeContainer) {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scopeContainer);
        configuration.addServiceInterface(CompositeScopeInitDestroyComponent.class);
        configuration.setInstanceFactory(factory);
        configuration.setInitInvoker(initInvoker);
        configuration.setDestroyInvoker(destroyInvoker);
        configuration.setName("foo");
        SystemAtomicComponentImpl context = new SystemAtomicComponentImpl(configuration);
        context.start();
        return context;
    }
}
