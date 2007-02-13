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
package org.apache.tuscany.core.implementation;

import java.net.URI;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class PojoComponentContextImplTestCase extends TestCase {
    private PojoAtomicComponent component;
    private PojoComponentContextImpl context;

    public void testURI() {
        URI uri = URI.create("foo");
        EasyMock.expect(component.getUri()).andReturn(uri);
        EasyMock.replay(component);
        assertEquals(uri.toString(), context.getURI());
        EasyMock.verify(component);
    }

    public void testGetProperty() {
        String name = "foo";
        String value = "bar";
        EasyMock.expect(component.getProperty(name)).andReturn(value);
        EasyMock.replay(component);
        assertSame(value, context.getProperty(String.class, name));
        EasyMock.verify(component);
    }

    public void testGetPropertyThatIsIncompatible() {
        String name = "foo";
        String value = "bar";
        EasyMock.expect(component.getProperty(name)).andReturn(value);
        EasyMock.replay(component);
        try {
            context.getProperty(Integer.class, name);
            fail();
        } catch (ClassCastException e) {
            // expected
        }
        EasyMock.verify(component);
    }

    public void testGetPropertyThatIsSubclass() {
        String name = "foo";
        String value = "bar";
        EasyMock.expect(component.getProperty(name)).andReturn(value);
        EasyMock.replay(component);
        assertSame(value, context.getProperty(Object.class, name));
        EasyMock.verify(component);
    }

    protected void setUp() throws Exception {
        super.setUp();
        component = EasyMock.createMock(PojoAtomicComponent.class);
        context = new PojoComponentContextImpl(component);
    }
}
