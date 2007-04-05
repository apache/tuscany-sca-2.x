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

import org.apache.tuscany.core.component.CompositeComponentImpl;
import org.apache.tuscany.spi.component.DuplicateNameException;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Component;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * Verfies children with the same name cannot be registered in the same composite
 *
 * @version $Rev$ $Date$
 */
public class DuplicateRegistrationTestCase extends TestCase {

    public void testDuplicateServiceRegistration() throws Exception {
        Component parent = new CompositeComponentImpl(URI.create("parent"));
        parent.start();

        Service service1 = EasyMock.createMock(Service.class);
        EasyMock.expect(service1.getUri()).andReturn(URI.create("#service")).atLeastOnce();
        service1.stop();
        EasyMock.replay(service1);

        Service service2 = EasyMock.createMock(Service.class);
        EasyMock.expect(service2.getUri()).andReturn(URI.create("#service")).atLeastOnce();
        service2.stop();
        EasyMock.replay(service2);

        parent.register(service2);
        try {
            parent.register(service1);
            fail();
        } catch (DuplicateNameException e) {
            // ok
        }
        parent.stop();
    }

    public void testDuplicateReferenceRegistration() throws Exception {
        Component parent = new CompositeComponentImpl(URI.create("parent"));
        parent.start();

        Reference reference1 = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference1.getUri()).andReturn(URI.create("#reference")).atLeastOnce();
        reference1.stop();
        EasyMock.replay(reference1);

        Reference reference2 = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference2.getUri()).andReturn(URI.create("#reference")).atLeastOnce();
        reference2.stop();
        EasyMock.replay(reference2);

        parent.register(reference2);
        try {
            parent.register(reference1);
            fail();
        } catch (DuplicateNameException e) {
            // ok
        }
        parent.stop();

    }

    public void testDuplicateServiceReferenceRegistration() throws Exception {
        Component parent = new CompositeComponentImpl(URI.create("parent"));
        parent.start();

        Service service1 = EasyMock.createMock(Service.class);
        EasyMock.expect(service1.getUri()).andReturn(URI.create("#child")).atLeastOnce();
        service1.stop();
        EasyMock.replay(service1);

        Reference service2 = EasyMock.createMock(Reference.class);
        EasyMock.expect(service2.getUri()).andReturn(URI.create("#child")).atLeastOnce();
        service2.stop();
        EasyMock.replay(service2);

        parent.register(service2);
        try {
            parent.register(service1);
            fail();
        } catch (DuplicateNameException e) {
            // ok
        }
        parent.stop();

    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
