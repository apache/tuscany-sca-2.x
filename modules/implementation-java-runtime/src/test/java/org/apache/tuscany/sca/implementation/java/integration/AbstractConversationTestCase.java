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
package org.apache.tuscany.sca.implementation.java.integration;

import junit.framework.TestCase;

import org.apache.tuscany.sca.core.RuntimeComponent;
import org.apache.tuscany.sca.core.scope.ConversationalScopeContainer;
import org.apache.tuscany.sca.core.store.MemoryStore;
import org.apache.tuscany.sca.scope.ScopeContainer;
import org.apache.tuscany.sca.store.StoreMonitor;
import org.easymock.EasyMock;

/**
 * Provides helper methods for setting up a partial runtime for conversational test cases.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractConversationTestCase extends TestCase {
    protected ScopeContainer container;
    protected MemoryStore store;
    protected RuntimeComponent component;

    protected void createRuntime() {
        store = new MemoryStore(EasyMock.createNiceMock(StoreMonitor.class));
        component = EasyMock.createMock(RuntimeComponent.class);
        container = new ConversationalScopeContainer(store, component);
    }

    protected void initializeRuntime() {
        store.init();
        container.start();
    }

}
