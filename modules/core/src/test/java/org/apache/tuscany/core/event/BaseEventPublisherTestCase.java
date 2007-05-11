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
package org.apache.tuscany.core.event;


import junit.framework.TestCase;

import org.apache.tuscany.sca.event.Event;
import org.apache.tuscany.sca.event.EventFilter;
import org.apache.tuscany.sca.event.EventPublisher;
import org.apache.tuscany.sca.event.RuntimeEventListener;
import org.apache.tuscany.sca.event.TrueFilter;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class BaseEventPublisherTestCase extends TestCase {
    EventPublisher publisher;

    public void testFireListener() {
        Event event = new TestEvent();
        RuntimeEventListener listener = EasyMock.createMock(RuntimeEventListener.class);
        listener.onEvent(EasyMock.same(event));
        EasyMock.expectLastCall();
        EasyMock.replay(listener);
        publisher.addListener(listener);
        publisher.publish(event);
        EasyMock.verify(listener);
    }

    public void testRemoveListener() {
        Event event = new TestEvent();
        RuntimeEventListener listener = EasyMock.createMock(RuntimeEventListener.class);
        EasyMock.replay(listener);
        publisher.addListener(listener);
        publisher.removeListener(listener);
        publisher.publish(event);
        EasyMock.verify(listener);
    }

    public void testFalseFilterListener() {
        Event event = new TestEvent();
        RuntimeEventListener listener = EasyMock.createMock(RuntimeEventListener.class);
        EasyMock.replay(listener);
        publisher.addListener(new FalseFilter(), listener);
        publisher.publish(event);
        EasyMock.verify(listener);
    }

    public void testTrueFilterListener() {
        Event event = new TestEvent();
        RuntimeEventListener listener = EasyMock.createMock(RuntimeEventListener.class);
        listener.onEvent(EasyMock.same(event));
        EasyMock.expectLastCall();
        EasyMock.replay(listener);
        publisher.addListener(new TrueFilter(), listener);
        publisher.publish(event);
        EasyMock.verify(listener);
    }

    protected void setUp() throws Exception {
        super.setUp();
        publisher = new BaseEventPublisher() {
        };
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
