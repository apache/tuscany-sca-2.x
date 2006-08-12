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

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.model.Scope;

import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class ScopeProcessorTestCase extends MockObjectTestCase {

    CompositeComponent parent;

    public void testModuleScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(parent, Module.class, type, null);
        assertEquals(Scope.MODULE, type.getImplementationScope());
    }

    public void testSessionScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(parent, Session.class, type, null);
        assertEquals(Scope.SESSION, type.getImplementationScope());
    }

    public void testRequestScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(parent, Request.class, type, null);
        assertEquals(Scope.REQUEST, type.getImplementationScope());
    }

    public void testCompositeScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(parent, Composite.class, type, null);
        assertEquals(Scope.COMPOSITE, type.getImplementationScope());
    }

    public void testStatelessScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(parent, Stateless.class, type, null);
        assertEquals(Scope.STATELESS, type.getImplementationScope());
    }

    public void testNoScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(parent, None.class, type, null);
        assertEquals(Scope.STATELESS, type.getImplementationScope());
    }

    protected void setUp() throws Exception {
        super.setUp();
        Mock mock = mock(CompositeComponent.class);
        parent = (CompositeComponent) mock.proxy();
    }

    @org.osoa.sca.annotations.Scope("MODULE")
    private class Module {
    }

    @org.osoa.sca.annotations.Scope("SESSION")
    private class Session {
    }

    @org.osoa.sca.annotations.Scope("REQUEST")
    private class Request {
    }

    @org.osoa.sca.annotations.Scope("COMPOSITE")
    private class Composite {
    }

    @org.osoa.sca.annotations.Scope("STATELESS")
    private class Stateless {
    }

    private class None {
    }

}
