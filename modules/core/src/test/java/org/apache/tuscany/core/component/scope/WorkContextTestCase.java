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

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.WorkContext;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class WorkContextTestCase extends TestCase {

    public void testSetCurrentAtomicComponent() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        AtomicComponent component = EasyMock.createNiceMock(AtomicComponent.class);
        AtomicComponent component2 = EasyMock.createNiceMock(AtomicComponent.class);
        ctx.setCurrentAtomicComponent(component);
        assertEquals(component, ctx.getCurrentAtomicComponent());
        ctx.setCurrentAtomicComponent(component2);
        assertEquals(component2, ctx.getCurrentAtomicComponent());
    }

    public void testNonSetCurrentAtomicComponent() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        assertNull(ctx.getCurrentAtomicComponent());
    }

    public void testIndentifier() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        Object id = new Object();
        ctx.setIdentifier(this, id);
        assertEquals(id, ctx.getIdentifier(this));
    }

    public void testClearIndentifier() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        Object id = new Object();
        ctx.setIdentifier(this, id);
        ctx.clearIdentifier(this);
        assertNull(ctx.getIdentifier(this));
    }

    public void testClearIndentifiers() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        Object id = new Object();
        Object id2 = new Object();
        ctx.setIdentifier(id, id);
        ctx.setIdentifier(id2, id2);
        ctx.clearIdentifiers();
        assertNull(ctx.getIdentifier(id));
        assertNull(ctx.getIdentifier(id2));
    }

    public void testClearNonExistentIndentifier() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ctx.clearIdentifier(this);
    }

    public void testNullIndentifier() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        Object id = new Object();
        ctx.setIdentifier(this, id);
        ctx.clearIdentifier(null);
        assertEquals(id, ctx.getIdentifier(this));
    }

    public void testNoIndentifier() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        assertNull(ctx.getIdentifier(this));
    }

    public void testSetGetCorrelationId() {
        WorkContext context = new WorkContextImpl();
        context.setCorrelationId("msg-005");
        assertEquals(context.getCorrelationId(), "msg-005");
        context.setCorrelationId(null);
        assertNull(context.getCorrelationId());
    }

    public void testSetGetCorrelationIdInNewThread() throws InterruptedException {
        WorkContext context = new WorkContextImpl();
        context.setCorrelationId("msg-005");
        assertEquals(context.getCorrelationId(), "msg-005");
        context.setIdentifier("TX", "002");
        ChildThread t = new ChildThread(context);
        t.start();
        t.join();
        assertTrue(t.passed);
        context.setCorrelationId(null);
        assertNull(context.getCorrelationId());
    }

    public void testCurrentAtomicComponentDoesNotPropagateToChildThread() throws InterruptedException {
        // NOTE should behaviour be to propagate?
        WorkContext context = new WorkContextImpl();
        context.setCurrentAtomicComponent(EasyMock.createNiceMock(AtomicComponent.class));
        TestCurrentAtomicComponentChildThread t = new TestCurrentAtomicComponentChildThread(context);
        t.start();
        t.join();
        assertTrue(t.passed);
        context.setCurrentAtomicComponent(null);
        assertNull(context.getCurrentAtomicComponent());
    }

    private static final class ChildThread extends Thread {
        private WorkContext context;
        private boolean passed = true;

        private ChildThread(WorkContext context) {
            this.context = context;
        }

        @Override
        public void run() {
            try {
                assertNull(context.getCorrelationId());
                assertEquals("002", context.getIdentifier("TX"));
            } catch (AssertionFailedError e) {
                passed = false;
            }
        }

    }

    private static final class TestCurrentAtomicComponentChildThread extends Thread {
        private WorkContext context;
        private boolean passed = true;

        private TestCurrentAtomicComponentChildThread(WorkContext context) {
            this.context = context;
        }

        @Override
        public void run() {
            try {
                assertNull(context.getCurrentAtomicComponent());
            } catch (AssertionFailedError e) {
                passed = false;
            }
        }

    }


}
