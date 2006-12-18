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

import junit.framework.TestCase;
import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.mock.component.RequestScopeInitDestroyComponent;

/**
 * @version $$Rev$$ $$Date$$
 */
public class BasicRequestScopeTestCase extends TestCase {
    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private PojoObjectFactory<?> factory;

    public void testLifecycleManagement() throws Exception {
        RequestScopeContainer scopeContext = new RequestScopeContainer(null, null);
        scopeContext.start();
        SystemAtomicComponent component = createComponent(scopeContext);
        // start the request
        RequestScopeInitDestroyComponent o1 =
            (RequestScopeInitDestroyComponent) scopeContext.getInstance(component);
        assertTrue(o1.isInitialized());
        assertFalse(o1.isDestroyed());
        RequestScopeInitDestroyComponent o2 =
            (RequestScopeInitDestroyComponent) scopeContext.getInstance(component);
        assertSame(o1, o2);
        scopeContext.onEvent(new RequestEnd(this));
        assertTrue(o1.isDestroyed());
        scopeContext.stop();
    }

    public void testGetAssociatedInstance() throws Exception {
        RequestScopeContainer scopeContext = new RequestScopeContainer(null, null);
        scopeContext.start();
        SystemAtomicComponent component = createComponent(scopeContext);
        // start the request
        scopeContext.getInstance(component);
        scopeContext.getAssociatedInstance(component);
        scopeContext.stop();
    }

    public void testGetAssociatedInstanceNonExistent() throws Exception {
        RequestScopeContainer scopeContext = new RequestScopeContainer(null, null);
        scopeContext.start();
        SystemAtomicComponent component = createComponent(scopeContext);
        // start the request
        try {
            scopeContext.getAssociatedInstance(component);
            fail();
        } catch (TargetNotFoundException e) {
            // expected
        }
        scopeContext.stop();
    }

    public void testRequestIsolation() throws Exception {
        RequestScopeContainer scopeContext = new RequestScopeContainer(null, null);
        scopeContext.start();

        SystemAtomicComponent component = createComponent(scopeContext);

        RequestScopeInitDestroyComponent o1 =
            (RequestScopeInitDestroyComponent) scopeContext.getInstance(component);
        assertTrue(o1.isInitialized());
        scopeContext.onEvent(new RequestEnd(this));
        assertTrue(o1.isDestroyed());

        RequestScopeInitDestroyComponent o2 =
            (RequestScopeInitDestroyComponent) scopeContext.getInstance(component);
        assertNotSame(o1, o2);
        scopeContext.onEvent(new RequestEnd(this));
        assertTrue(o2.isDestroyed());
        scopeContext.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new PojoObjectFactory<RequestScopeInitDestroyComponent>(
            RequestScopeInitDestroyComponent.class.getConstructor((Class[]) null));
        initInvoker = new MethodEventInvoker<Object>(
            RequestScopeInitDestroyComponent.class.getMethod("init", (Class[]) null));
        destroyInvoker = new MethodEventInvoker<Object>(
            RequestScopeInitDestroyComponent.class.getMethod("destroy", (Class[]) null));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private SystemAtomicComponent createComponent(ScopeContainer scopeContainer) {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scopeContainer);
        configuration.addServiceInterface(RequestScopeInitDestroyComponent.class);
        configuration.setInstanceFactory(factory);
        configuration.setInitInvoker(initInvoker);
        configuration.setDestroyInvoker(destroyInvoker);
        configuration.setName("foo");
        SystemAtomicComponentImpl component = new SystemAtomicComponentImpl(configuration);
        scopeContainer.register(component);
        return component;
    }
}
