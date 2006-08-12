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

import org.apache.tuscany.spi.component.AtomicComponent;

import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Verifies an atomic component can be resolved from its parent
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemCompositeComponentResolutionTestCase extends MockObjectTestCase {

    public void testComponentResolution() throws NoSuchMethodException {
        SystemCompositeComponent parent = new SystemCompositeComponentImpl("foo", null, null, null);
        parent.start();
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        Source originalSource = new SourceImpl();
        Mock mock = mock(SystemAtomicComponent.class);
        mock.stubs().method("getName").will(returnValue("source"));
        mock.stubs().method("getServiceInstance").will(returnValue(originalSource));
        mock.stubs().method("getServiceInterfaces").will(returnValue(interfaces));
        SystemAtomicComponent context = (SystemAtomicComponent) mock.proxy();
        parent.register(context);
        AtomicComponent ctx = (AtomicComponent) parent.getChild("source");
        Source source = (Source) ctx.getServiceInstance();
        assertNotNull(source);
    }


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
