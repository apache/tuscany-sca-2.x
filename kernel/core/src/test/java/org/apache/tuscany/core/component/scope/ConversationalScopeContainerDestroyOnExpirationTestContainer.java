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
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.EventFilter;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.services.store.RecoveryListener;
import org.apache.tuscany.spi.services.store.Store;
import org.apache.tuscany.spi.services.store.StoreExpirationEvent;
import org.apache.tuscany.spi.services.store.StoreReadException;
import org.apache.tuscany.spi.services.store.StoreWriteException;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ConversationalScopeContainerDestroyOnExpirationTestContainer extends TestCase {
    private ScopeContainer container;
    private TestStore store;
    private AtomicComponent component;

    /**
     * Verifies the scope container registers a callback listener for component instance destroy events when a
     * conversational instance expires
     */
    public void testDestroyNotification() throws Exception {
        store.getListener().onEvent(new StoreExpirationEvent(this, component, new Object()));
        EasyMock.verify(component);
    }

    protected void setUp() throws Exception {
        super.setUp();
        component = EasyMock.createMock(AtomicComponent.class);
        component.destroy(EasyMock.isA(Object.class));
        EasyMock.replay(component);
        store = new TestStore();
        WorkContext context = new WorkContextImpl();
        container = new ConversationalScopeContainer(store, context, null);
        container.start();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        container.stop();
    }

    private class TestStore implements Store {
        private RuntimeEventListener listener;

        public RuntimeEventListener getListener() {
            return listener;
        }

        public void insertRecord(SCAObject owner, String id, Object object, long expiration)
            throws StoreWriteException {

        }

        public void updateRecord(SCAObject owner, String id, Object object, long expiration)
            throws StoreWriteException {

        }

        public Object readRecord(SCAObject owner, String id) throws StoreReadException {
            return null;
        }

        public void removeRecord(SCAObject owner, String id) throws StoreWriteException {

        }

        public void removeRecords() throws StoreWriteException {

        }

        public void recover(RecoveryListener listener) throws StoreReadException {

        }

        public void publish(Event object) {

        }

        public void addListener(RuntimeEventListener listener) {
            this.listener = listener;
        }

        public void addListener(EventFilter filter, RuntimeEventListener listener) {

        }

        public void removeListener(RuntimeEventListener listener) {

        }
    }

}
