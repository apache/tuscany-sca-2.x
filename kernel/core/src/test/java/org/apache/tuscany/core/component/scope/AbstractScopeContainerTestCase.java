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

import org.apache.tuscany.core.component.WorkContextImpl;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class AbstractScopeContainerTestCase extends MockObjectTestCase {

    public void testSetWorkContext() {
        TestContainer container = new TestContainer("foo");
        WorkContext ctx = new WorkContextImpl();
        container.setWorkContext(ctx);
        assertEquals(ctx, container.getWorkContext());

    }

    public void testFireListener() {
        TestContainer container = new TestContainer("foo");
        Mock mock = mock(RuntimeEventListener.class);
        Event event = new TestEvent();
        mock.expects(once()).method("onEvent").with(eq(event));
        RuntimeEventListener listener = (RuntimeEventListener) mock.proxy();
        container.addListener(listener);
        container.publish(event);
    }

    public void testRemoveListener() {
        TestContainer container = new TestContainer("foo");
        Mock mock = mock(RuntimeEventListener.class);
        Event event = new TestEvent();
        mock.expects(never()).method("onEvent").with(eq(event));
        RuntimeEventListener listener = (RuntimeEventListener) mock.proxy();
        container.addListener(listener);
        container.removeListener(listener);
        container.publish(event);
    }

    public void testFalseFilterListener() {
        TestContainer container = new TestContainer("foo");
        Mock mock = mock(RuntimeEventListener.class);
        Event event = new TestEvent();
        mock.expects(never()).method("onEvent").with(eq(event));
        RuntimeEventListener listener = (RuntimeEventListener) mock.proxy();
        container.addListener(new FalseFilter(), listener);
        container.publish(event);
    }

    public void testTrueFilterListener() {
        TestContainer container = new TestContainer("foo");
        Mock mock = mock(RuntimeEventListener.class);
        Event event = new TestEvent();
        mock.expects(once()).method("onEvent").with(eq(event));
        RuntimeEventListener listener = (RuntimeEventListener) mock.proxy();
        container.addListener(new TrueFilter(), listener);
        container.publish(event);
    }

    public void testToString() {
        TestContainer container = new TestContainer("foo");
        assertNotNull(container.toString());
    }

    public void testGetName() {
        TestContainer container = new TestContainer("foo");
        assertEquals("foo", container.getName());
    }


    private class TestContainer extends AbstractScopeContainer {

        public TestContainer(String name) {
            super(name, null);
        }

        protected InstanceWrapper getInstanceWrapper(AtomicComponent component) {
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
