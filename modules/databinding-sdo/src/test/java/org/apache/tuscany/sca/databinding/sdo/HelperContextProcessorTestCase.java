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

package org.apache.tuscany.sca.databinding.sdo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.impl.JavaResourceImpl;
import org.apache.tuscany.sca.implementation.java.introspect.DefaultJavaClassIntrospectorExtensionPoint;

import commonj.sdo.helper.HelperContext;

/**
 * @version $Rev$ $Date$
 */
public class HelperContextProcessorTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testProcessor() throws Exception {
        HelperContextRegistry registry = new HelperContextRegistryImpl();
        HelperContextProcessor processor = new HelperContextProcessor(new DefaultAssemblyFactory(), registry);
        
        JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory(new DefaultJavaClassIntrospectorExtensionPoint());
        JavaImplementation componentType = javaImplementationFactory.createJavaImplementation();
        componentType.setJavaClass(FooImpl.class);
        for (Field f : FooImpl.class.getDeclaredFields()) {
            processor.visitField(f, componentType);

        }
        for (Method m : FooImpl.class.getMethods()) {
            processor.visitMethod(m, componentType);
        }

        JavaResourceImpl r1 = (JavaResourceImpl)componentType.getResources().get("context");
        assertNotNull(r1);
        JavaResourceImpl r2 = (JavaResourceImpl)componentType.getResources().get("context2");
        assertNotNull(r2);
//        HelperContext c1 = (HelperContext)r1.getObjectFactory().getInstance();
//        HelperContext c2 = (HelperContext)r2.getObjectFactory().getInstance();
//        assertSame(c1, c2);
    }

    private static class FooImpl {
        @org.apache.tuscany.sca.databinding.sdo.api.HelperContext
        protected HelperContext context2;

        private HelperContext context;

        @org.apache.tuscany.sca.databinding.sdo.api.HelperContext
        public void setContext(HelperContext context1) {
            this.context = context1;
        }
    }
}
