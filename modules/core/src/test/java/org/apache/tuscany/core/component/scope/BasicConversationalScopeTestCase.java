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
import org.apache.tuscany.core.services.store.memory.MemoryStore;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.services.store.StoreMonitor;
import org.easymock.EasyMock;

/**
 * @version $$Rev: 471111 $$ $$Date: 2006-11-03 23:06:48 -0500 (Fri, 03 Nov 2006) $$
 */
public class BasicConversationalScopeTestCase extends TestCase {
    private AtomicComponent component;
    private InstanceWrapper wrapper;
    private ScopeContainer scopeContainer;
    private WorkContext workContext;

/*
    public void testLifecycleManagement() throws Exception {
        // start the request
        String conversation = "conv";
        workContext.setIdentifier(Scope.CONVERSATION, conversation);

        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        wrapper.start();
        // FIXME shouldn't stop be called when the component is removed?
//        wrapper.stop();
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        assertSame(wrapper, scopeContainer.getWrapper(component));
        scopeContainer.remove(component);
        EasyMock.verify(component, wrapper);
    }
*/

/*
    public void testCoversationIsolation() throws Exception {
        String conversation1 = "conv";
        String conversation2 = "conv2";

        InstanceWrapper wrapper2 = EasyMock.createStrictMock(InstanceWrapper.class);
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        wrapper.start();
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper2);
        wrapper2.start();
        EasyMock.replay(component, wrapper, wrapper2);
        workContext.setIdentifier(Scope.CONVERSATION, conversation1);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        workContext.setIdentifier(Scope.CONVERSATION, conversation2);
        assertSame(wrapper2, scopeContainer.getWrapper(component));
        EasyMock.verify(component, wrapper);
    }
*/
    public void testX() {
}

    protected void setUp() throws Exception {
        super.setUp();
        component = EasyMock.createStrictMock(AtomicComponent.class);
        EasyMock.expect(component.getMaxAge()).andStubReturn(-1);
        EasyMock.expect(component.getMaxIdleTime()).andStubReturn(-1);
        wrapper = EasyMock.createStrictMock(InstanceWrapper.class);

        StoreMonitor monitor = EasyMock.createMock(StoreMonitor.class);
        monitor.start(EasyMock.isA(String.class));
        monitor.stop(EasyMock.isA(String.class));
        MemoryStore store = new MemoryStore(monitor);
        workContext = new SimpleWorkContext();
        scopeContainer = new ConversationalScopeContainer(store, workContext);
        scopeContainer.start();
    }
}
