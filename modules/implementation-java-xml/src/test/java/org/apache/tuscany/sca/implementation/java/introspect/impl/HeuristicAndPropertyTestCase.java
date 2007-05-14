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

import java.lang.reflect.Constructor;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.impl.JavaConstructorImpl;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.introspect.DefaultJavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.sca.interfacedef.java.introspect.ExtensibleJavaInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.osoa.sca.annotations.Property;

/**
 * @version $Rev$ $Date$
 */
public class HeuristicAndPropertyTestCase extends TestCase {

    private PropertyProcessor propertyProcessor;
    private HeuristicPojoProcessor heuristicProcessor;
    private AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
    private JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory();

    /**
     * Verifies the property and heuristic processors don't collide
     */
    @SuppressWarnings("unchecked")
    public void testPropertyProcessorWithHeuristicProcessor() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor ctor = Foo.class.getConstructor(String.class);
        type.setConstructor(new JavaConstructorImpl(ctor));
        propertyProcessor.visitConstructorParameter(type.getConstructor().getParameters()[0], type);
        heuristicProcessor.visitEnd(Foo.class, type);
        assertEquals(1, type.getProperties().size());
        assertEquals("foo", type.getProperties().get(0).getName());
    }

    protected void setUp() throws Exception {
        super.setUp();
        JavaInterfaceIntrospectorExtensionPoint visitors = new DefaultJavaInterfaceIntrospectorExtensionPoint();
        ExtensibleJavaInterfaceIntrospector introspector = new ExtensibleJavaInterfaceIntrospector(new DefaultJavaInterfaceFactory(), visitors);
        propertyProcessor = new PropertyProcessor(assemblyFactory);
        heuristicProcessor = new HeuristicPojoProcessor(assemblyFactory, new DefaultJavaInterfaceFactory(), introspector);
    }

    public static class Foo {
        public Foo(@Property(name = "foo")
        String prop) {
        }
    }

}
