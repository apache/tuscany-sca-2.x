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

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class InstanceWrapperTestCase extends TestCase {

    public void testExceptionInit() throws Exception {
        AtomicComponent component = getComponent();
        InstanceWrapper wrapper = new InstanceWrapperImpl(component, new Object());
        try {
            wrapper.start();
            fail();
        } catch (SomeException e) {
            // expected
        }
        assertFalse(wrapper.isStarted());
        EasyMock.verify(component);
    }

    public void testNonStart() throws Exception {
        AtomicComponent comp = EasyMock.createNiceMock(AtomicComponent.class);  // class-level one has an expects
        InstanceWrapper wrapper = new InstanceWrapperImpl(comp, new Object());
        try {
            wrapper.getInstance();
            fail();
        } catch (AssertionError e) {
            // expected
        }
    }

    private AtomicComponent getComponent() throws Exception {
        // do not use setUp() since we do not need this in all testcases
        AtomicComponent comp = EasyMock.createMock(AtomicComponent.class);
        comp.init(EasyMock.isA(Object.class));
        EasyMock.expectLastCall().andThrow(new SomeException());
        EasyMock.replay(comp);
        return comp;
    }

    private class SomeException extends RuntimeException {
    }
}
