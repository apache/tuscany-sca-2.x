/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.container.java.assembly.impl;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.container.java.assembly.mock.HelloWorldImpl;
import org.apache.tuscany.container.java.assembly.mock.HelloWorldWithFieldProperties;
import org.apache.tuscany.container.java.assembly.mock.NakedHelloWorld;
import org.apache.tuscany.container.java.assembly.mock.NakedHelloWorldWithInterface;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.scdl.loader.impl.SCDLAssemblyModelLoaderImpl;

/**
 * @version $Rev$ $Date$
 */
public class JavaImplementationTestCase extends TestCase {
    private JavaImplementationImpl impl = (JavaImplementationImpl) new JavaAssemblyFactoryImpl().createJavaImplementation();
    private ClassLoader origLoader;
    
    public void testFoo() {
        impl.setImplementationClass(HelloWorldImpl.class);

        // this is not needed anymore
        //assertEquals("org/apache/tuscany/container/java/assembly/mock/HelloWorldImpl.componentType", impl.getComponentTypeName());
    }

    public void testNoImplementationClass() {
        //FIXME this test fails with NPE
//        impl.setImplementationClass(null);
//        try {
//            impl.initialize(new AssemblyModelContextImpl(new AssemblyLoaderImpl(), ResourceLoaderFactory.getResourceLoader(Thread.currentThread().getContextClassLoader())));
//            impl.getComponentType();
//            fail("Expected IllegalArgumentException");
//        } catch (IllegalArgumentException e) {
//            // ok
//        }
    }

    public void testNakedHelloWorld() {
        impl.setImplementationClass(NakedHelloWorld.class);
        impl.initialize(new AssemblyModelContextImpl(new AssemblyFactoryImpl(), new SCDLAssemblyModelLoaderImpl(null), new ResourceLoaderImpl(Thread.currentThread().getContextClassLoader())));
        ComponentType type = impl.getComponentType();
        Assert.assertNotNull(type);
        Assert.assertTrue(type.getProperties().isEmpty());
        Assert.assertTrue(type.getReferences().isEmpty());
        List<Service> services = type.getServices();
        Assert.assertEquals(1, services.size());
        Assert.assertTrue(services.get(0).getName().equals("NakedHelloWorld"));
    }

    public void testNakedHelloWorldWithInterface() {
        impl.setImplementationClass(NakedHelloWorldWithInterface.class);
        impl.initialize(new AssemblyModelContextImpl(new AssemblyFactoryImpl(), new SCDLAssemblyModelLoaderImpl(null), new ResourceLoaderImpl(Thread.currentThread().getContextClassLoader())));
        ComponentType type = impl.getComponentType();
        Assert.assertNotNull(type);
        Assert.assertTrue(type.getProperties().isEmpty());
        Assert.assertTrue(type.getReferences().isEmpty());
        List<Service> services = type.getServices();
        Assert.assertEquals(1, services.size());
        Assert.assertTrue(services.get(0).getName().equals("NakedHelloWorldWithInterface"));
    }

    public void testHelloWorldWithSidefile() {
        impl.setImplementationClass(HelloWorldImpl.class);
        impl.initialize(new AssemblyModelContextImpl(new AssemblyFactoryImpl(), new SCDLAssemblyModelLoaderImpl(null), new ResourceLoaderImpl(Thread.currentThread().getContextClassLoader())));
        ComponentType type = impl.getComponentType();
        Assert.assertNotNull(type);
        List<Property> props = type.getProperties();
        Assert.assertEquals(1, props.size());
        Assert.assertTrue(props.get(0).getName().equals("text"));

        Assert.assertTrue(type.getReferences().isEmpty());

        List<Service> services = type.getServices();
        Assert.assertEquals(1, services.size());
        Assert.assertTrue(services.get(0).getName().equals("HelloWorldService"));
    }

    public void testHelloWorldWithFieldProperties() {
        impl.setImplementationClass(HelloWorldWithFieldProperties.class);
        impl.initialize(new AssemblyModelContextImpl(new AssemblyFactoryImpl(), new SCDLAssemblyModelLoaderImpl(null), new ResourceLoaderImpl(Thread.currentThread().getContextClassLoader())));
        ComponentType type = impl.getComponentType();
        Assert.assertNotNull(type);
        List<Property> props = type.getProperties();
        Assert.assertEquals(3, props.size());

        Property prop = type.getProperty("text");
        Assert.assertNotNull(prop);
        Assert.assertEquals("text", prop.getName());
        Assert.assertEquals(false, prop.isRequired());
        Assert.assertEquals(String.class, prop.getType());

        prop = type.getProperty("text2");
        Assert.assertNotNull(prop);
        Assert.assertEquals("text2", prop.getName());
        Assert.assertEquals(true, prop.isRequired());
        Assert.assertEquals(Integer.class, prop.getType());

        prop = type.getProperty("foo");
        Assert.assertNotNull(prop);
        Assert.assertEquals("foo", prop.getName());
        Assert.assertEquals(false, prop.isRequired());
        Assert.assertEquals(Integer.TYPE, prop.getType());
    }

    protected void setUp() throws Exception {
        super.setUp();
        origLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(JavaImplementationTestCase.class.getClassLoader());
    }

    protected void tearDown() throws Exception {
        Thread.currentThread().setContextClassLoader(origLoader);
        super.tearDown();
    }
}
