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
package org.apache.tuscany.implementation.java.introspect.impl;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.impl.Scope;
import org.apache.tuscany.implementation.java.introspect.ProcessingException;
import org.apache.tuscany.implementation.java.introspect.impl.ScopeProcessor;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ScopeProcessorTestCase extends TestCase {

    Component parent;

    public void testCompositeScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();

        processor.visitClass(Composite.class, type);
        assertEquals(Scope.COMPOSITE, type.getScope());
    }

    public void testSessionScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        processor.visitClass(Session.class, type);
        assertEquals(Scope.SESSION, type.getScope());
    }

    public void testConversationalScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        processor.visitClass(Conversation.class, type);
        assertEquals(Scope.CONVERSATION, type.getScope());
    }

    public void testRequestScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        processor.visitClass(Request.class, type);
        assertEquals(Scope.REQUEST, type.getScope());
    }

    public void testSystemScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        processor.visitClass(System.class, type);
        assertEquals(Scope.SYSTEM, type.getScope());
    }

    public void testStatelessScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        processor.visitClass(Stateless.class, type);
        assertEquals(Scope.STATELESS, type.getScope());
    }

    public void testNoScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        JavaImplementationDefinition type =
            new JavaImplementationDefinition();
        processor.visitClass(None.class, type);
        assertEquals(Scope.STATELESS, type.getScope());
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
