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
package org.apache.tuscany.sca.core.event;

import java.net.URI;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class EventTestCase extends TestCase {
    private URI uri = URI.create("foo");

    public void testCompositeStart() {
        ComponentStart event = new ComponentStart(this, uri);
        assertEquals(uri, event.getComponentURI());
    }

    public void testCompositeStop() {
        ComponentStop event = new ComponentStop(this, uri);
        assertEquals(uri, event.getComponentURI());
    }

    public void testHttpSessionStart() {
        Object id = new Object();
        HttpSessionStart event = new HttpSessionStart(this, id);
        assertEquals(this, event.getSource());
        assertEquals(id, event.getSessionID());
    }

    public void testHttpSessionEnd() {
        Object id = new Object();
        HttpSessionEnd event = new HttpSessionEnd(this, id);
        assertEquals(this, event.getSource());
        assertEquals(id, event.getSessionID());
    }

    public void testRequestStart() {
        RequestStart event = new RequestStart(this);
        assertEquals(this, event.getSource());
    }

    public void testReequestEnd() {
        RequestEnd event = new RequestEnd(this);
        assertEquals(this, event.getSource());
    }


    @Override
    protected void setUp() throws Exception {
    }
}
