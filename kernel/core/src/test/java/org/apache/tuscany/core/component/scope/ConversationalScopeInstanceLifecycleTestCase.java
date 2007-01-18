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
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.services.store.StoreMonitor;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.services.store.memory.MemoryStore;
import org.easymock.EasyMock;

/**
 * Lifecycle unit tests for the conversational scope container
 *
 * @version $Rev: 451895 $ $Date: 2006-10-02 02:58:18 -0400 (Mon, 02 Oct 2006) $
 */
public class ConversationalScopeInstanceLifecycleTestCase extends TestCase {

    public void testInitRemove() throws Exception {
        StoreMonitor monitor = EasyMock.createMock(StoreMonitor.class);
        monitor.start(EasyMock.isA(String.class));
        monitor.stop(EasyMock.isA(String.class));
        MemoryStore store = new MemoryStore(monitor);
        WorkContext ctx = new WorkContextImpl();
        ConversationalScopeContainer scope = new ConversationalScopeContainer(store, ctx, null);
        scope.start();

        Foo comp = new Foo();
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.createInstance()).andReturn(comp);
        component.init(EasyMock.eq(comp));
        EasyMock.expect(component.getMaxAge()).andReturn(1L).anyTimes();
        component.addListener(EasyMock.isA(RuntimeEventListener.class));
        EasyMock.replay(component);
        scope.register(component);
        String convID = "ConvID";
        ctx.setIdentifier(Scope.CONVERSATION, convID);
        assertNotNull(scope.getInstance(component));
        // expire
        scope.remove(component);
        scope.stop();
        EasyMock.verify(component);
        scope.stop();
    }

    private class Foo {

    }

}
