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
package org.apache.tuscany.core.implementation.composite;

import java.net.URI;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.event.RuntimeEventListener;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.event.ComponentStart;
import org.apache.tuscany.core.component.event.ComponentStop;
import org.easymock.EasyMock;

/**
 * @version $$Rev$$ $$Date$$
 */
public class CompositeLifecycleTestCase extends TestCase {

    public void testRestart() throws Exception {
        RuntimeEventListener listener = EasyMock.createMock(RuntimeEventListener.class);
        listener.onEvent(EasyMock.isA(ComponentStart.class));
        listener.onEvent(EasyMock.isA(ComponentStop.class));
        listener.onEvent(EasyMock.isA(ComponentStart.class));
        listener.onEvent(EasyMock.isA(ComponentStop.class));
        EasyMock.replay(listener);
        CompositeComponent composite = new CompositeComponentImpl(URI.create("foo"), null, null, null);
        composite.addListener(listener);
        composite.start();
        composite.stop();
        composite.start();
        composite.stop();
        EasyMock.verify(listener);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
