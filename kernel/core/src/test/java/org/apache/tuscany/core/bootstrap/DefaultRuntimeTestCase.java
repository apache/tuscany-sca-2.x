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
package org.apache.tuscany.core.bootstrap;

import org.apache.tuscany.spi.component.CompositeComponent;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class DefaultRuntimeTestCase extends TestCase {

    public void testLifecycleInitialization() {
        DefaultRuntime runtime = new DefaultRuntime();
        CompositeComponent app = EasyMock.createMock(CompositeComponent.class);
        app.start();
        app.stop();
        EasyMock.replay(app);
        CompositeComponent system = EasyMock.createMock(CompositeComponent.class);
        system.start();
        system.stop();
        EasyMock.replay(system);
        runtime.setSystemComponent(system);
        runtime.setRootComponent(app);
        runtime.start();
        runtime.stop();
        EasyMock.verify(system);
        EasyMock.verify(app);
    }

    public void testLifecycleInitializationNoAppAnSystemComposites() {
        DefaultRuntime runtime = new DefaultRuntime();
        runtime.start();
        runtime.stop();
    }

}
