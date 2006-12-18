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

import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.ConversationEnd;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.mock.component.ConversationalScopeInitDestroyComponent;
import org.apache.tuscany.core.services.store.memory.MemoryStore;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.SystemAtomicComponent;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.services.store.StoreMonitor;
import org.easymock.EasyMock;

import junit.framework.TestCase;

/**
 * @version $$Rev: 471111 $$ $$Date: 2006-11-03 23:06:48 -0500 (Fri, 03 Nov 2006) $$
 */
public class BasicConversationalScopeTestCase extends TestCase {

    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private PojoObjectFactory<?> factory;

    public void testLifecycleManagement() throws Exception {
        StoreMonitor monitor = EasyMock.createMock(StoreMonitor.class);
        monitor.start(EasyMock.isA(String.class));
        monitor.stop(EasyMock.isA(String.class));
        MemoryStore store = new MemoryStore(monitor);
        WorkContext workContext = new WorkContextImpl();
        ConversationalScopeContainer scopeContext = new ConversationalScopeContainer(store, workContext, null);
        scopeContext.start();
        SystemAtomicComponent atomicContext = createContext(scopeContext);
        // start the request
        String conversation = "conv";
        workContext.setIdentifier(Scope.CONVERSATION, conversation);
        ConversationalScopeInitDestroyComponent o1 =
            (ConversationalScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        //assertTrue(o1.isInitialized());
        assertFalse(o1.isDestroyed());
        ConversationalScopeInitDestroyComponent o2 =
            (ConversationalScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertSame(o1, o2);
        scopeContext.onEvent(new ConversationEnd(this, conversation));
        //assertTrue(o1.isDestroyed());
        scopeContext.stop();
    }

    public void testModuleIsolation() throws Exception {
        StoreMonitor monitor = EasyMock.createMock(StoreMonitor.class);
        monitor.start(EasyMock.isA(String.class));
        monitor.stop(EasyMock.isA(String.class));
        MemoryStore store = new MemoryStore(monitor);
        WorkContext workContext = new WorkContextImpl();
        ConversationalScopeContainer scopeContext = new ConversationalScopeContainer(store, workContext, null);
        scopeContext.start();

        SystemAtomicComponent atomicContext = createContext(scopeContext);

        String conversation1 = "conv";
        workContext.setIdentifier(Scope.CONVERSATION, conversation1);
        ConversationalScopeInitDestroyComponent o1 =
            (ConversationalScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        //assertTrue(o1.isInitialized());
        assertFalse(o1.isDestroyed());

        String conversation2 = "conv2";
        workContext.setIdentifier(Scope.CONVERSATION, conversation2);
        ConversationalScopeInitDestroyComponent o2 =
            (ConversationalScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertNotSame(o1, o2);

        scopeContext.onEvent(new ConversationEnd(this, conversation1));
        //assertTrue(o1.isDestroyed());
        assertFalse(o2.isDestroyed());
        scopeContext.onEvent(new ConversationEnd(this, conversation2));
        //assertTrue(o2.isDestroyed());
        scopeContext.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new PojoObjectFactory<ConversationalScopeInitDestroyComponent>(
                ConversationalScopeInitDestroyComponent.class.getConstructor((Class[]) null));
        initInvoker = new MethodEventInvoker<Object>(
                ConversationalScopeInitDestroyComponent.class.getMethod("init", (Class[]) null));
        destroyInvoker = new MethodEventInvoker<Object>(
                ConversationalScopeInitDestroyComponent.class.getMethod("destroy", (Class[]) null));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private SystemAtomicComponent createContext(ScopeContainer scopeContainer) {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scopeContainer);
        configuration.addServiceInterface(ConversationalScopeInitDestroyComponent.class);
        configuration.setInstanceFactory(factory);
        configuration.setInitInvoker(initInvoker);
        configuration.setDestroyInvoker(destroyInvoker);
        configuration.setName("foo");
        SystemAtomicComponentImpl context = new SystemAtomicComponentImpl(configuration);
        context.start();
        return context;
    }
}
