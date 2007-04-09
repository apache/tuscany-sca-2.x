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
package org.apache.tuscany.spi.component;

import java.net.URI;

import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.EventFilter;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.event.TrueFilter;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class AbstractSCAObjectTestCase extends TestCase {

    public void testFireListener() throws Exception {
        SCAObject object = new TestSCAObject(new URI("foo"));
        Event event = new TestEvent();
        RuntimeEventListener listener = EasyMock.createMock(RuntimeEventListener.class);
        listener.onEvent(EasyMock.same(event));
        EasyMock.expectLastCall();
        EasyMock.replay(listener);
        object.addListener(listener);
        object.publish(event);
    }

    public void testRemoveListener() throws Exception {
        SCAObject object = new TestSCAObject(new URI("foo"));
        Event event = new TestEvent();
        RuntimeEventListener listener = EasyMock.createMock(RuntimeEventListener.class);
        EasyMock.replay(listener);
        object.addListener(listener);
        object.removeListener(listener);
        object.publish(event);
    }

    public void testFalseFilterListener() throws Exception {
        SCAObject object = new TestSCAObject(new URI("foo"));
        Event event = new TestEvent();
        RuntimeEventListener listener = EasyMock.createMock(RuntimeEventListener.class);
        EasyMock.replay(listener);
        object.addListener(new FalseFilter(), listener);
        object.publish(event);
    }

    public void testTrueFilterListener() throws Exception {
        SCAObject object = new TestSCAObject(new URI("foo"));
        Event event = new TestEvent();
        RuntimeEventListener listener = EasyMock.createMock(RuntimeEventListener.class);
        listener.onEvent(EasyMock.same(event));
        EasyMock.expectLastCall();
        EasyMock.replay(listener);
        object.addListener(new TrueFilter(), listener);
        object.publish(event);
    }

    public void testToString() throws Exception {
        SCAObject object = new TestSCAObject(new URI("foo"));
        assertNotNull(object.toString());
    }

    private class TestSCAObject extends AbstractSCAObject {
        public TestSCAObject(URI name) {
            super(name);
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



