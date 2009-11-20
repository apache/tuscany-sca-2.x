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

import static org.junit.Assert.assertEquals;

import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.JavaScopeImpl;
import org.junit.Before;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class ScopeProcessorTestCase {

    private JavaImplementationFactory javaImplementationFactory;

    @Test
    public void testCompositeScope() throws IntrospectionException {
        ScopeProcessor processor = new ScopeProcessor(new DefaultAssemblyFactory());
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();

        processor.visitClass(Composite.class, type);
        assertEquals(JavaScopeImpl.COMPOSITE, type.getJavaScope());
    }

    @Test
    public void testStatelessScope() throws IntrospectionException {
        ScopeProcessor processor = new ScopeProcessor(new DefaultAssemblyFactory());
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        processor.visitClass(Stateless.class, type);
        assertEquals(JavaScopeImpl.STATELESS, type.getJavaScope());
    }

    @Test
    public void testNoScope() throws IntrospectionException {
        ScopeProcessor processor = new ScopeProcessor(new DefaultAssemblyFactory());
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        processor.visitClass(None.class, type);
        assertEquals(JavaScopeImpl.STATELESS, type.getJavaScope());
    }

    @Before
    public void setUp() throws Exception {
        javaImplementationFactory = new DefaultJavaImplementationFactory();
    }

    @org.oasisopen.sca.annotation.Scope("COMPOSITE")
    private class Composite {
    }

    @org.oasisopen.sca.annotation.Scope("STATELESS")
    private class Stateless {
    }

    private class None {
    }

}
