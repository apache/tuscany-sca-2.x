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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.databinding.extension.SimpleTypeMapperExtension;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorService;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.InteractionScope;
import org.apache.tuscany.spi.model.ServiceContract;

import junit.framework.TestCase;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;

/**
 * @version $Rev$ $Date$
 */
public class ImplementationProcessorServiceTestCase extends TestCase {

    private ImplementationProcessorService implService =
        new ImplementationProcessorServiceImpl(new JavaInterfaceProcessorRegistryImpl());

    public void testCreateConversationalService() throws Exception {
        JavaMappedService service = implService.createService(Foo.class);
        assertTrue(Foo.class.equals(service.getServiceContract().getInterfaceClass()));
        assertTrue(service.isRemotable());
        assertEquals(InteractionScope.CONVERSATIONAL, service.getServiceContract().getInteractionScope());
        ServiceContract serviceContract = service.getServiceContract();
        assertTrue(Bar.class.equals(serviceContract.getCallbackClass()));
        assertTrue("ImplementationProcessorServiceTestCase$Bar".equals(serviceContract.getCallbackName()));
    }

    public void testCreateDefaultService() throws Exception {
        JavaMappedService service = implService.createService(Baz.class);
        assertTrue(Baz.class.equals(service.getServiceContract().getInterfaceClass()));
        assertTrue(!service.isRemotable());
        assertEquals(InteractionScope.NONCONVERSATIONAL, service.getServiceContract().getInteractionScope());
    }

    public void testProcessParamProperty() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<PropertyClass> ctor = PropertyClass.class.getConstructor(int.class);
        Annotation[] paramAnnotations = ctor.getParameterAnnotations()[0];
        List<String> injectionNames = new ArrayList<String>();
        String[] names = new String[]{"foo"};
        implService.processParam(int.class, paramAnnotations, names, 0, type, injectionNames);
        org.apache.tuscany.spi.model.Property<?> property = type.getProperties().get("foo");
        assertEquals(int.class, property.getJavaType());
        assertEquals(SimpleTypeMapperExtension.XSD_INT, property.getXmlType());
    }


    @Callback(Bar.class)
    @Remotable
    @Scope("CONVERSATION")
    public interface Foo {

    }

    public interface Bar {

    }

    public interface Baz {

    }

    public static class PropertyClass {
        private int foo;

        public PropertyClass(@Property(name = "foo") int foo) {
            this.foo = foo;
        }

        public int getFoo() {
            return foo;
        }
    }
}
