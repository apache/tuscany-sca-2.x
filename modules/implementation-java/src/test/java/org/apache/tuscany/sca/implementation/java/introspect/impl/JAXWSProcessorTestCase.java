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

import static org.apache.tuscany.sca.implementation.java.introspect.impl.ModelHelper.getService;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.jws.WebService;

import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.interfacedef.InvalidCallbackException;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.junit.Before;
import org.junit.Test;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.annotation.Callback;
import org.oasisopen.sca.annotation.Service;

/**
 * @version $Rev: 826368 $ $Date: 2009-10-18 08:22:23 +0100 (Sun, 18 Oct 2009) $
 */
public class JAXWSProcessorTestCase {
    private JAXWSProcessor processor;
    private JavaImplementationFactory javaImplementationFactory;

    @Before
    public void setUp() throws Exception {
        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        processor = new JAXWSProcessor(new DefaultAssemblyFactory(), new DefaultJavaInterfaceFactory(registry));
        javaImplementationFactory = new DefaultJavaImplementationFactory();
    }

    @Test
    public void testWebServiceName() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        processor.visitClass(Foo1Impl.class, type);
        org.apache.tuscany.sca.assembly.Service service = getService(type, "Foo1");
        assertNotNull(service);
    }
    
    @Test
    public void testWebServiceEP() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        processor.visitClass(Foo2Impl.class, type);
        org.apache.tuscany.sca.assembly.Service service = getService(type, "Foo2");
        assertNotNull(service);
    }    

    @WebService(name="Foo1")
    private static class Foo1Impl{
        public String doSomething(String aParam){
            return null;
        }
    }
    
    private interface Foo2 {
        public String doSomething(String aParam);
    }
    
    @WebService(name="Foo2", endpointInterface="Foo2")
    private static class Foo2Impl{
        public String doSomething(String aParam){
            return null;
        }
    }    
    

}
