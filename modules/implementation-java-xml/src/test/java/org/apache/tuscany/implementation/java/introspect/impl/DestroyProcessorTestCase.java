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

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.osoa.sca.annotations.Destroy;

/**
 * @version $Rev$ $Date$
 */
public class DestroyProcessorTestCase extends TestCase {
    
    private AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
    private JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory(new DefaultAssemblyFactory());

    public void testDestroy() throws Exception {
        DestroyProcessor processor = new DestroyProcessor(assemblyFactory);
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Method method = Foo.class.getMethod("destroy");
        processor.visitMethod(method, type);
        assertNotNull(type.getDestroyMethod());
    }

    public void testBadDestroy() throws Exception {
        DestroyProcessor processor = new DestroyProcessor(assemblyFactory);
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Method method = Bar.class.getMethod("badDestroy", String.class);
        try {
            processor.visitMethod(method, type);
            fail();
        } catch (IllegalDestructorException e) {
            // expected
        }
    }

    public void testTwoDestroy() throws Exception {
        DestroyProcessor processor = new DestroyProcessor(assemblyFactory);
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Method method = Bar.class.getMethod("destroy");
        Method method2 = Bar.class.getMethod("destroy2");
        processor.visitMethod(method, type);
        try {
            processor.visitMethod(method2, type);
            fail();
        } catch (DuplicateDestructorException e) {
            // expected
        }
    }


    private class Foo {

        @Destroy
        public void destroy() {
        }
    }


    private class Bar {

        @Destroy
        public void destroy() {
        }

        @Destroy
        public void destroy2() {
        }

        @Destroy
        public void badDestroy(String foo) {
        }


    }
}
