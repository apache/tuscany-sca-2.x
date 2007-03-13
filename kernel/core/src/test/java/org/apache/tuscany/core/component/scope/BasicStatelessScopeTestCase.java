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

import java.net.URI;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetNotFoundException;

/**
 * Unit tests for the composite scope container
 *
 * @version $Rev$ $Date$
 */
public class BasicStatelessScopeTestCase extends TestCase {
    private AtomicComponent component;
    private InstanceWrapper wrapper;
    private ScopeContainer scopeContainer;

    public void testInstanceManagement() throws Exception {
        InstanceWrapper wrapper2 = EasyMock.createNiceMock(InstanceWrapper.class);
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper).andReturn(wrapper2);
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        assertSame(wrapper2, scopeContainer.getWrapper(component));
        EasyMock.verify(component, wrapper);
    }

    public void testGetAssociatedInstance() throws Exception {
        URI uri = URI.create("oops");
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        EasyMock.expect(component.getUri()).andReturn(uri);
        EasyMock.replay(component, wrapper);

        assertSame(wrapper, scopeContainer.getWrapper(component));
        try {
            // always throws an exception, which is the semantic for stateless implementations
            scopeContainer.getAssociatedWrapper(component);
            fail();
        } catch (TargetNotFoundException e) {
            assertEquals(uri.toString(), e.getMessage());
            EasyMock.verify(component, wrapper);
        }
    }

    protected void setUp() throws Exception {
        super.setUp();

        component = EasyMock.createNiceMock(AtomicComponent.class);
        wrapper = EasyMock.createNiceMock(InstanceWrapper.class);

        scopeContainer = new StatelessScopeContainer(null, null);
        scopeContainer.start();
        EasyMock.replay(component);
        scopeContainer.register(null, component);
        EasyMock.reset(component);
    }
}
