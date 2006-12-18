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
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.EventFilter;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.event.TrueFilter;
import org.apache.tuscany.spi.model.Scope;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class AbstractScopeContainerTestCase extends TestCase {

    public void testFireListener() {
        TestContainer container = new TestContainer("foo");
        RuntimeEventListener listener = EasyMock.createMock(RuntimeEventListener.class);
        Event event = new TestEvent();
        listener.onEvent(EasyMock.eq(event));
        EasyMock.replay(listener);
        container.addListener(listener);
        container.publish(event);
        EasyMock.verify(listener);
    }

    public void testRemoveListener() {
        TestContainer container = new TestContainer("foo");
        RuntimeEventListener listener = EasyMock.createMock(RuntimeEventListener.class);
        EasyMock.replay(listener);
        Event event = new TestEvent();
        container.addListener(listener);
        container.removeListener(listener);
        container.publish(event);
        EasyMock.verify(listener);
    }

    public void testFalseFilterListener() {
        TestContainer container = new TestContainer("foo");
        RuntimeEventListener listener = EasyMock.createMock(RuntimeEventListener.class);
        Event event = new TestEvent();
        EasyMock.replay(listener);
        container.addListener(new FalseFilter(), listener);
        container.publish(event);
        EasyMock.verify(listener);
    }

    public void testTrueFilterListener() {
        TestContainer container = new TestContainer("foo");
        RuntimeEventListener listener = EasyMock.createMock(RuntimeEventListener.class);
        Event event = new TestEvent();
        listener.onEvent(EasyMock.eq(event));
        EasyMock.replay(listener);
        container.addListener(new TrueFilter(), listener);
        container.publish(event);
        EasyMock.verify(listener);
    }

    public void testToString() {
        TestContainer container = new TestContainer("foo");
        assertNotNull(container.toString());
    }

    private class TestContainer extends AbstractScopeContainer {

        public TestContainer(String name) {
            super(null, null);
        }

        protected InstanceWrapper getInstanceWrapper(AtomicComponent component, boolean create) {
            return null;
        }

        public Scope getScope() {
            return null;
        }

        public void register(AtomicComponent component) {

        }

        public void onEvent(Event event) {

        }

        public WorkContext getWorkContext() {
            return super.getWorkContext();
        }
    }

    private class TestEvent implements Event {
        public Object getSource() {
            return null;
        }
    }

    private class FalseFilter implements EventFilter {

        public boolean match(Event event) {
            return false;
        }
    }


}
