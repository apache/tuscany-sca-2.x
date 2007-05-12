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
package org.apache.tuscany.implementation.java.introspect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.tuscany.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class DefaultJavaClassIntrospectorTestCase extends TestCase {

    public void testRegister() throws Exception {
        DefaultJavaClassIntrospectorExtensionPoint visitors = new DefaultJavaClassIntrospectorExtensionPoint();
        JavaClassVisitor extension = EasyMock.createNiceMock(JavaClassVisitor.class);
        visitors.addClassVisitor(extension);
    }

    public void testUnegister() throws Exception {
        DefaultJavaClassIntrospectorExtensionPoint visitors = new DefaultJavaClassIntrospectorExtensionPoint();
        JavaClassVisitor extension = EasyMock.createNiceMock(JavaClassVisitor.class);
        visitors.addClassVisitor(extension);
        visitors.removeClassVisitor(extension);
    }

    @SuppressWarnings("unchecked")
    public void testWalk() throws Exception {
        DefaultJavaClassIntrospectorExtensionPoint visitors = new DefaultJavaClassIntrospectorExtensionPoint();
        JavaClassVisitor extension = EasyMock.createMock(JavaClassVisitor.class);
        extension.visitClass(EasyMock.eq(Bar.class), EasyMock.isA(JavaImplementation.class));
        extension.visitConstructor(EasyMock.isA(Constructor.class), EasyMock.isA(JavaImplementation.class));
        extension.visitMethod(EasyMock.isA(Method.class), EasyMock.isA(JavaImplementation.class));
        extension.visitField(EasyMock.isA(Field.class), EasyMock.isA(JavaImplementation.class));
        extension.visitSuperClass(EasyMock.isA(Class.class), EasyMock.isA(JavaImplementation.class));
        extension.visitEnd(EasyMock.isA(Class.class), EasyMock.isA(JavaImplementation.class));

        // mock.expects(once()).method("visitClass");
        // mock.expects(once()).method("visitMethod");
        // mock.expects(once()).method("visitField");
        // mock.expects(once()).method("visitConstructor");
        // mock.expects(once()).method("visitSuperClass");
        // mock.expects(once()).method("visitEnd");
        EasyMock.replay(extension);
        visitors.addClassVisitor(extension);
        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory();
        ExtensibleJavaClassIntrospector introspector = new ExtensibleJavaClassIntrospector(visitors);
        introspector.introspect(Bar.class, javaImplementationFactory.createJavaImplementation());
        EasyMock.verify(extension);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    private class Baz {

    }

    private class Bar extends Baz {

        protected String bar;

        public Bar() {
        }

        public void bar() {
        }

    }

}
