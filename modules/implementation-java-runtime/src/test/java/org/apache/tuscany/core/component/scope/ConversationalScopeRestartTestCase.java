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

import java.lang.reflect.Constructor;
import java.net.URI;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.services.store.StoreMonitor;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.ConversationEnd;
import org.apache.tuscany.core.component.event.ConversationStart;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.services.store.memory.MemoryStore;
import org.easymock.EasyMock;

/**
 * Verifies the scope container properly disposes resources and can be restarted
 *
 * @version $$Rev: 471111 $$ $$Date: 2006-11-03 23:06:48 -0500 (Fri, 03 Nov 2006) $$
 */
public abstract class ConversationalScopeRestartTestCase extends TestCase {

    public void testRestart() throws Exception {
        StoreMonitor monitor = EasyMock.createMock(StoreMonitor.class);
        monitor.start(EasyMock.isA(String.class));
        monitor.stop(EasyMock.isA(String.class));
        MemoryStore store = new MemoryStore(monitor);
        WorkContext ctx = new WorkContextImpl();
        ConversationalScopeContainer scope = new ConversationalScopeContainer(store, ctx, null);
        scope.start();
        MethodEventInvoker<Object> initInvoker = new MethodEventInvoker<Object>(
            ConversationalScopeRestartTestCase.InitDestroyOnce.class.getMethod("init"));
        MethodEventInvoker<Object> destroyInvoker =
            new MethodEventInvoker<Object>(InitDestroyOnce.class.getMethod("destroy"));
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setInitInvoker(initInvoker);
        configuration.setDestroyInvoker(destroyInvoker);
        Constructor<InitDestroyOnce> ctr = InitDestroyOnce.class.getConstructor((Class<?>[]) null);
        configuration.setInstanceFactory(new PojoObjectFactory<InitDestroyOnce>(ctr));
        configuration.setName(new URI("InitDestroy"));
        AtomicComponent component = new SystemAtomicComponentImpl(configuration);
        component.setScopeContainer(scope);
        component.start();

        String conversation = "conv";
        ctx.setIdentifier(Scope.CONVERSATION, conversation);
        scope.onEvent(new ConversationStart(this, conversation));
        Object instance = component.getTargetInstance();
        assertSame(instance, component.getTargetInstance());

        scope.onEvent(new ConversationEnd(this, conversation));
        scope.stop();
        component.stop();

        scope.start();
        scope.onEvent(new ConversationStart(this, conversation));
        component.start();
        //assertNotSame(instance, context.getServiceInstance());
        scope.onEvent(new ConversationEnd(this, conversation));
        scope.stop();
        component.stop();
    }

    public static class InitDestroyOnce {

        private boolean initialized;
        private boolean destroyed;

        public InitDestroyOnce() {
        }

        public void init() {
            if (!initialized) {
                initialized = true;
            } else {
                fail("Scope did not clean up properly - Init called more than once");
            }
        }

        public void destroy() {
            if (!destroyed) {
                destroyed = true;
            } else {
                fail("Scope did not clean up properly - Destroyed called more than once");
            }
        }

    }
}
