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
package org.apache.tuscany.core.component.event;

import org.apache.tuscany.spi.component.CompositeComponent;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class EventTestCase extends TestCase {

    private CompositeComponent component;

    public void testCompositeStart() {
        CompositeStart event = new CompositeStart(this, component);
        assertEquals(component, event.getComposite());
    }

    public void testCompositeStop() {
        CompositeStop event = new CompositeStop(this, component);
        assertEquals(component, event.getComposite());
    }

    public void testHttpSessionStart() {
        Object id = new Object();
        HttpSessionEvent event = new HttpSessionStart(this, id);
        assertEquals(this, event.getSource());
        assertEquals(id, event.getId());
    }

    public void testHttpSessionEnd() {
        Object id = new Object();
        HttpSessionEvent event = new HttpSessionEnd(this, id);
        assertEquals(this, event.getSource());
        assertEquals(id, event.getId());
    }

    public void testRequestStart() {
        RequestStart event = new RequestStart(this);
        assertEquals(this, event.getSource());
    }

    public void testReequestEnd() {
        RequestEnd event = new RequestEnd(this);
        assertEquals(this, event.getSource());
    }


    protected void setUp() throws Exception {
        super.setUp();
        component = EasyMock.createNiceMock(CompositeComponent.class);
    }
}
