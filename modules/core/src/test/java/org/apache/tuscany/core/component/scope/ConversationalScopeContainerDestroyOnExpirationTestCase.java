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

import junit.framework.TestCase;

import org.apache.tuscany.core.component.SimpleWorkContext;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.services.store.Store;
import org.apache.tuscany.spi.services.store.StoreExpirationEvent;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * @version $Rev$ $Date$
 */
public class ConversationalScopeContainerDestroyOnExpirationTestCase extends TestCase {
    private Store store;
    private RuntimeEventListener listener;
    private WorkContext context;
    private InstanceWrapper wrapper;
    private AtomicComponent component;

    /**
     * Verifies the scope container registers a callback listener for component instance destroy events when a
     * conversational instance expires
     */
    public void testDestroyNotification() throws Exception {
        store.addListener(EasyMock.isA(RuntimeEventListener.class));
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>(){
            public Object answer() throws Throwable {
                listener = (RuntimeEventListener) EasyMock.getCurrentArguments()[0];
                return null;
            }
        });
        wrapper.stop();
        EasyMock.replay(store);
        EasyMock.replay(wrapper);

        new ConversationalScopeContainer(store, context, null);
        listener.onEvent(new StoreExpirationEvent(this, component, wrapper));
        EasyMock.verify(store);
        EasyMock.verify(wrapper);
    }

    protected void setUp() throws Exception {
        super.setUp();
        store = EasyMock.createMock(Store.class);
        wrapper = EasyMock.createMock(InstanceWrapper.class);
        component = EasyMock.createMock(AtomicComponent.class);
        context = new SimpleWorkContext();
    }
}
