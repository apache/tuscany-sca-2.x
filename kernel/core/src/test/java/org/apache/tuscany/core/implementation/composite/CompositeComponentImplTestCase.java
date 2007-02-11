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
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class CompositeComponentImplTestCase extends TestCase {

    public void testRegisterService() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl(URI.create("foo"), null, null);
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getUri()).andReturn(URI.create("bar")).atLeastOnce();
        EasyMock.replay(service);
        parent.register(service);
        assertNotNull(parent.getChild("bar"));
        EasyMock.verify(service);
    }

    public void testRegisterReference() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl(URI.create("foo"), null, null);
        Reference service = EasyMock.createMock(Reference.class);
        EasyMock.expect(service.getUri()).andReturn(URI.create("bar")).atLeastOnce();
        EasyMock.replay(service);
        parent.register(service);
        assertNotNull(parent.getChild("bar"));
        EasyMock.verify(service);
    }

}
