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
package org.apache.tuscany.sca.implementation.java.introspect.impl;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.impl.JavaScopeImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ScopeProcessorTestCase extends TestCase {

    Component parent;
    private JavaImplementationFactory javaImplementationFactory;

    public void testCompositeScope() throws IntrospectionException {
        ScopeProcessor processor = new ScopeProcessor(new DefaultAssemblyFactory());
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();

        processor.visitClass(Composite.class, type);
        assertEquals(JavaScopeImpl.COMPOSITE, type.getJavaScope());
    }

    public void testSessionScope() throws IntrospectionException {
        ScopeProcessor processor = new ScopeProcessor(new DefaultAssemblyFactory());
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        processor.visitClass(Session.class, type);
        assertEquals(JavaScopeImpl.SESSION, type.getJavaScope());
    }

    public void testConversationalScope() throws IntrospectionException {
        ScopeProcessor processor = new ScopeProcessor(new DefaultAssemblyFactory());
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        processor.visitClass(Conversation.class, type);
        assertEquals(JavaScopeImpl.CONVERSATION, type.getJavaScope());
    }

    public void testRequestScope() throws IntrospectionException {
        ScopeProcessor processor = new ScopeProcessor(new DefaultAssemblyFactory());
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        processor.visitClass(Request.class, type);
        assertEquals(JavaScopeImpl.REQUEST, type.getJavaScope());
    }

    public void testStatelessScope() throws IntrospectionException {
        ScopeProcessor processor = new ScopeProcessor(new DefaultAssemblyFactory());
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        processor.visitClass(Stateless.class, type);
        assertEquals(JavaScopeImpl.STATELESS, type.getJavaScope());
    }

    public void testNoScope() throws IntrospectionException {
        ScopeProcessor processor = new ScopeProcessor(new DefaultAssemblyFactory());
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        processor.visitClass(None.class, type);
        assertEquals(JavaScopeImpl.STATELESS, type.getJavaScope());
    }

    protected void setUp() throws Exception {
        super.setUp();
        javaImplementationFactory = new DefaultJavaImplementationFactory();
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
