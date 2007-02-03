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
package org.apache.tuscany.spi.extension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.net.URI;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class AtomicComponentExtensionTestCase extends TestCase {
    
    public void testIsEagerInit() throws Exception {
        TestExtension ext = new TestExtension();
        ext.isEagerInit();
    }

    public void testPrepare() throws Exception {
        TestExtension ext = new TestExtension();
        Operation<Type> operation = new Operation<Type>("foo", null, null, null);
        InboundInvocationChain chain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(chain.getOperation()).andReturn(operation);
        chain.prepare();
        chain.setTargetInvoker(EasyMock.isA(TargetInvoker.class));
        EasyMock.replay(chain);

        Map<Operation<?>, InboundInvocationChain> chains = new HashMap<Operation<?>, InboundInvocationChain>();
        chains.put(operation, chain);
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getInvocationChains()).andReturn(chains);
        EasyMock.expect(wire.getUri()).andReturn(URI.create("Service")).atLeastOnce();
        EasyMock.replay(wire);

        ext.addInboundWire(wire);
        ext.prepare();

        EasyMock.verify(chain);
        EasyMock.verify(wire);

    }

    public void testInit() throws Exception {
        TestExtension ext = new TestExtension();
        ext.init(null);
    }

    public void testDestroy() throws Exception {
        TestExtension ext = new TestExtension();
        ext.destroy(null);
    }

    public void testInboundWire() throws Exception {
        TestExtension ext = new TestExtension();
        ext.getInboundWire(null);
    }

    public void testRemoveInstance() throws Exception {
        ScopeContainer container = EasyMock.createMock(ScopeContainer.class);
        EasyMock.expect(container.getScope()).andReturn(Scope.COMPOSITE);
        container.remove(EasyMock.isA(AtomicComponentExtension.class));
        EasyMock.replay(container);
        TestExtension ext = new TestExtension(container);
        ext.removeInstance();
        EasyMock.verify(container);
    }

    private class TestExtension extends AtomicComponentExtension {
        public TestExtension() {
            super(URI.create("_foo"), null, null, null, null, null, 0);
        }

        public TestExtension(ScopeContainer scopeContainer) {
            super(URI.create("_foo"), null, null, null, null, null, 0);
            setScopeContainer(scopeContainer);
        }

        public Object createInstance() throws ObjectCreationException {
            return null;
        }

        public Object getTargetInstance() throws TargetResolutionException {
            return null;
        }

        public TargetInvoker createTargetInvoker(String targetName, Operation operation, InboundWire callbackWire) {
            return new TargetInvoker() {

                public Object invokeTarget(final Object payload, final short sequence)
                    throws InvocationTargetException {
                    return null;
                }

                public Message invoke(Message msg) throws InvocationRuntimeException {
                    return null;
                }

                public boolean isCacheable() {
                    return false;
                }

                public void setCacheable(boolean cacheable) {

                }

                public boolean isOptimizable() {
                    return false;
                }

                public Object clone() throws CloneNotSupportedException {
                    return super.clone();
                }
            };
        }

    }
}
