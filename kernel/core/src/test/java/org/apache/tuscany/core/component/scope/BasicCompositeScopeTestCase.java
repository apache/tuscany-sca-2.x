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
import org.apache.tuscany.spi.component.TargetResolutionException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class BasicCompositeScopeTestCase extends TestCase {

    private CompositeScopeContainer scopeContext;
    private AtomicComponent component;
    private InstanceWrapper wrapper;

    public void testWrapperCreation() throws Exception {
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContext.getWrapper(component));
        EasyMock.verify(component, wrapper);
    }

    public void testWrapperRetrieve() throws Exception {
        // first create a wrapper in the context's cache
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContext.getWrapper(component));
        EasyMock.verify(component, wrapper);
        EasyMock.reset(component, wrapper);

        // fetch again and check that the component and wrapper are not called
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContext.getWrapper(component));
        EasyMock.verify(component, wrapper);
    }

    public void testAssociatedWrapperRetrieve() throws Exception {
        // first create a wrapper in the context's cache
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContext.getWrapper(component));
        EasyMock.verify(component, wrapper);
        EasyMock.reset(component, wrapper);

        // fetch again and check that the component and wrapper are not called
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContext.getAssociatedWrapper(component));
        EasyMock.verify(component, wrapper);
    }

    public void testGetAssociatedInstanceNonExistent() throws Exception {
        URI uri = URI.create("oops");
        EasyMock.expect(component.getUri()).andReturn(uri);
        EasyMock.replay(component, wrapper);
        try {
            scopeContext.getAssociatedWrapper(component);
            fail();
        } catch (TargetResolutionException e) {
            assertEquals(uri.toString(), e.getMessage());
        }
        EasyMock.verify(component, wrapper);
    }

    public void testWrapperReturn() throws Exception{
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContext.getWrapper(component));
        scopeContext.returnWrapper(component, wrapper);
        EasyMock.verify(component, wrapper);
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        component = EasyMock.createNiceMock(AtomicComponent.class);
        wrapper = EasyMock.createNiceMock(InstanceWrapper.class);

        scopeContext = new CompositeScopeContainer(null);
        scopeContext.start();
        scopeContext.register(component);
    }
}
