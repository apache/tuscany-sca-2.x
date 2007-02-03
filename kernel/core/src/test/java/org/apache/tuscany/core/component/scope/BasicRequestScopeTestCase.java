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

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
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
        RequestScopeContainer scopeContainer = new RequestScopeContainer(null, null);
        scopeContainer.start();
        AtomicComponent component = createComponent(scopeContainer);
        // start the request
        RequestScopeInitDestroyComponent o1 =
            (RequestScopeInitDestroyComponent) scopeContainer.getInstance(component);
        assertTrue(o1.isInitialized());
        assertFalse(o1.isDestroyed());
        RequestScopeInitDestroyComponent o2 =
            (RequestScopeInitDestroyComponent) scopeContainer.getInstance(component);
        assertSame(o1, o2);
        scopeContainer.onEvent(new RequestEnd(this));
        assertTrue(o1.isDestroyed());
        scopeContainer.stop();
    }

    public void testGetAssociatedInstance() throws Exception {
        RequestScopeContainer scopeContainer = new RequestScopeContainer(null, null);
        scopeContainer.start();
        AtomicComponent component = createComponent(scopeContainer);
        // start the request
        scopeContainer.getInstance(component);
        scopeContainer.getAssociatedInstance(component);
        scopeContainer.stop();
    }

    public void testGetAssociatedInstanceNonExistent() throws Exception {
        RequestScopeContainer scopeContainer = new RequestScopeContainer(null, null);
        scopeContainer.start();
        AtomicComponent component = createComponent(scopeContainer);
        // start the request
        try {
            scopeContainer.getAssociatedInstance(component);
            fail();
        } catch (TargetNotFoundException e) {
            // expected
        }
        scopeContainer.stop();
    }

    public void testRequestIsolation() throws Exception {
        RequestScopeContainer scopeContainer = new RequestScopeContainer(null, null);
        scopeContainer.start();

        AtomicComponent component = createComponent(scopeContainer);

        RequestScopeInitDestroyComponent o1 =
            (RequestScopeInitDestroyComponent) scopeContainer.getInstance(component);
        assertTrue(o1.isInitialized());
        scopeContainer.onEvent(new RequestEnd(this));
        assertTrue(o1.isDestroyed());

        RequestScopeInitDestroyComponent o2 =
            (RequestScopeInitDestroyComponent) scopeContainer.getInstance(component);
        assertNotSame(o1, o2);
        scopeContainer.onEvent(new RequestEnd(this));
        assertTrue(o2.isDestroyed());
        scopeContainer.stop();
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

    private AtomicComponent createComponent(ScopeContainer scopeContainer) {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setInstanceFactory(factory);
        configuration.setInitInvoker(initInvoker);
        configuration.setDestroyInvoker(destroyInvoker);
        try {
            configuration.setName(new URI("foo"));
        } catch (URISyntaxException e) {
            // will not happen
        }
        SystemAtomicComponentImpl component = new SystemAtomicComponentImpl(configuration);
        component.setScopeContainer(scopeContainer);
        component.start();
        return component;
    }
}
