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
package org.apache.tuscany.core.implementation.processor;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.model.Scope;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ScopeProcessorTestCase extends TestCase {

    Component parent;

    public void testCompositeScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();

        processor.visitClass(Composite.class, type, null);
        assertEquals(Scope.COMPOSITE, type.getImplementationScope());
    }

    public void testSessionScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(Session.class, type, null);
        assertEquals(Scope.SESSION, type.getImplementationScope());
    }

    public void testConversationalScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(Conversation.class, type, null);
        assertEquals(Scope.CONVERSATION, type.getImplementationScope());
    }

    public void testRequestScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(Request.class, type, null);
        assertEquals(Scope.REQUEST, type.getImplementationScope());
    }

    public void testSystemScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(System.class, type, null);
        assertEquals(Scope.SYSTEM, type.getImplementationScope());
    }

    public void testStatelessScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(Stateless.class, type, null);
        assertEquals(Scope.STATELESS, type.getImplementationScope());
    }

    public void testNoScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(None.class, type, null);
        assertEquals(Scope.STATELESS, type.getImplementationScope());
    }

    protected void setUp() throws Exception {
        super.setUp();
        parent = EasyMock.createNiceMock(Component.class);
    }

    @org.osoa.sca.annotations.Scope("COMPOSITE")
    private class Composite {
    }

    @org.osoa.sca.annotations.Scope("SESSION")
    private class Session {
    }

    @org.osoa.sca.annotations.Scope("CONVERSATION")
    private class Conversation {
    }

    @org.osoa.sca.annotations.Scope("REQUEST")
    private class Request {
    }

    @org.osoa.sca.annotations.Scope("SYSTEM")
    private class System {
    }

    @org.osoa.sca.annotations.Scope("STATELESS")
    private class Stateless {
    }

    private class None {
    }

}
