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
package org.apache.tuscany.core.scope;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.WorkContextImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class WorkContextTestCase extends TestCase {

    public void testSetCurrentAtomicComponent() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        RuntimeComponent component = EasyMock.createNiceMock(RuntimeComponent.class);
        RuntimeComponent component2 = EasyMock.createNiceMock(RuntimeComponent.class);
        ctx.setCurrentComponent(component);
        assertEquals(component, ctx.getCurrentComponent());
        ctx.setCurrentComponent(component2);
        assertEquals(component2, ctx.getCurrentComponent());
    }

    public void testNonSetCurrentAtomicComponent() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        assertNull(ctx.getCurrentComponent());
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
        context.setCurrentComponent(EasyMock.createNiceMock(RuntimeComponent.class));
        TestCurrentAtomicComponentChildThread t = new TestCurrentAtomicComponentChildThread(context);
        t.start();
        t.join();
        assertTrue(t.passed);
        context.setCurrentComponent(null);
        assertNull(context.getCurrentComponent());
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
                assertNull(context.getCurrentComponent());
            } catch (AssertionFailedError e) {
                passed = false;
            }
        }

    }


}
