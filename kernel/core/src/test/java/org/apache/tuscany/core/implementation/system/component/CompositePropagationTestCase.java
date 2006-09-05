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
package org.apache.tuscany.core.implementation.system.component;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import org.apache.tuscany.core.mock.component.Source;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * Tests registering arbitrarily deep child composite contexts
 *
 * @version $Rev$ $Date$
 */
public class CompositePropagationTestCase extends TestCase {

    private SystemCompositeComponent parent;
    private SystemCompositeComponent child2;

    public void testLifecyclePropagation() throws NoSuchMethodException {
        parent.start();
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        SystemAtomicComponent component = createMock(SystemAtomicComponent.class);
        expect(component.getName()).andReturn("source").anyTimes();
        component.stop();
        expect(component.getServiceInterfaces()).andReturn(interfaces);
        replay(component);
        child2.register(component);
        parent.stop();
        verify(component);
    }


    protected void setUp() throws Exception {
        super.setUp();
        parent = new SystemCompositeComponentImpl("parent", null, null, null, null);
        SystemCompositeComponent child1 = new SystemCompositeComponentImpl("child1", parent, null, null, null);
        child2 = new SystemCompositeComponentImpl("child2", child1, null, null, null);
        child1.register(child2);
        parent.register(child1);
    }

    protected void tearDown() throws Exception {
        parent.stop();
        super.tearDown();
    }

}
