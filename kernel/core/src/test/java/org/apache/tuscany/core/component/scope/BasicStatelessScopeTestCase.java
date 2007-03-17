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
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.model.Scope;

/**
 * Unit tests for the composite scope container
 *
 * @version $Rev$ $Date$
 */
public class BasicStatelessScopeTestCase<T> extends TestCase {
    private StatelessScopeContainer<String, String> scopeContainer;
    private String contextId;
    private IMocksControl control;
    private AtomicComponent<T> component;
    private InstanceWrapper<T> wrapper;

    public void testCorrectScope() {
        assertEquals(Scope.STATELESS, scopeContainer.getScope());
    }

    public void testInstanceCreation() throws Exception {
        @SuppressWarnings("unchecked")
        InstanceWrapper<T> wrapper2 = control.createMock(InstanceWrapper.class);

        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        wrapper.start();
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper2);
        wrapper2.start();
        control.replay();

        assertSame(wrapper, scopeContainer.getWrapper(component, contextId));
        assertSame(wrapper2, scopeContainer.getWrapper(component, contextId));
        control.verify();
    }

    public void testGetAssociatedInstance() throws Exception {
        control.replay();
        try {
            // always throws an exception, which is the semantic for stateless implementations
            scopeContainer.getAssociatedWrapper(component, contextId);
            fail();
        } catch (UnsupportedOperationException e) {
            // ok
        }
        control.verify();
    }

    public void testReturnWrapper() throws Exception {
        wrapper.stop();
        control.replay();
        scopeContainer.returnWrapper(component, wrapper, contextId);
        control.verify();
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        scopeContainer = new StatelessScopeContainer<String, String>(null);
        contextId = "context";

        control = EasyMock.createStrictControl();
        component = control.createMock(AtomicComponent.class);
        wrapper = control.createMock(InstanceWrapper.class);
    }
}
