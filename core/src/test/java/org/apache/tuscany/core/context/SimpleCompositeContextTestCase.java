/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.context;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;

/**
 * @version $Rev$ $Date$
 */
public class SimpleCompositeContextTestCase extends TestCase implements RuntimeEventListener {
    private SimpleCompositeContext context;
    private List<Event> events;

    public void testRegisterChild() {
        SimpleCompositeContext child = new SimpleCompositeContext("child");
        context.registerContext(child);
        assertSame(context, child.getParent());
        assertSame(child, context.getContext("child"));
        try {
            context.registerContext(child);
            fail();
        } catch (IllegalStateException e) {
            // ok
        }
    }

    public void testStartEventSent() {
        context.addListener(this);
        context.start();
        assertEquals(1, events.size());
        Event event = events.get(0);
        assertTrue(event instanceof ModuleStart);
        assertSame(context, event.getSource());
    }

    public void testStopEventSent() {
        context.start();
        context.addListener(this);
        context.stop();
        assertEquals(1, events.size());
        Event event = events.get(0);
        assertTrue(event instanceof ModuleStop);
        assertSame(context, event.getSource());
    }

    public void testDuplicateNameException() {
        SimpleCompositeContext child1 = new SimpleCompositeContext("child");
        SimpleCompositeContext child2 = new SimpleCompositeContext("child");
        context.registerContext(child1);
        try {
            context.registerContext(child2);
            fail();
        } catch (DuplicateNameException e) {
            // ok
        }
    }

    public void onEvent(Event event) {
        events.add(event);
    }

    protected void setUp() throws Exception {
        super.setUp();
        context = new SimpleCompositeContext("parent");
        events = new ArrayList<Event>();
    }
}
